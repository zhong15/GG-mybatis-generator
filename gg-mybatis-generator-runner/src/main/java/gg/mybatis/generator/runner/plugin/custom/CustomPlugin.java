/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gg.mybatis.generator.runner.plugin.custom;

import gg.mybatis.generator.common.mapper.BaseMapper;
import gg.mybatis.generator.runner.utils.GenUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class CustomPlugin extends PluginAdapter {
    private static Logger log = LoggerFactory.getLogger(CustomPlugin.class);

    private Runner runner = new DefaultRunner();

    @Override
    public boolean validate(List<String> warnings) {
        runner.init();
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        /*
         * 删除不用的 Mapper 方法
         */
        List<String> keepIdList = Arrays.asList(
                introspectedTable.getBaseResultMapId(),
                introspectedTable.getInsertSelectiveStatementId());

        for (Iterator<VisitableElement> it = document.getRootElement().getElements().iterator(); it.hasNext(); ) {
            VisitableElement e = it.next();
            if (!(e instanceof XmlElement)) {
                continue;
            }

            XmlElement x = (XmlElement) e;
            boolean keep = false;
            for (String keepId : keepIdList) {
                if (GenUtils.attrEqual(x, "id", keepId)) {
                    keep = true;
                    break;
                }
            }
            if (keep) {
                continue;
            }

            log.info("删除 {} sqlMap: {}", introspectedTable.getTableConfiguration().getTableName(), GenUtils.getAttrValue(x, "id"));
            it.remove();
        }

        /*
         * 添加自定义的 Mapper 方法
         */
        for (java.lang.reflect.Method m : Runner.getSortedMethod(runner.getClass())) {
            String prefix = Runner.SQL_MAP_METHOD_PREFIX;
            if (m.getName().startsWith(prefix)) {
                String statementId = GenUtils.firstCharToLower(m.getName().substring(prefix.length()));

                log.info("生成 {} sqlMap: {}", introspectedTable.getTableConfiguration().getTableName(), statementId);

                XmlElement xmlElement = GenUtils.xmlElement(getXmlElementType(m.getName(), prefix), "id", statementId);
                document.getRootElement().addElement(xmlElement);
                try {
                    m.invoke(runner, xmlElement, introspectedTable);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }

    private static String getXmlElementType(String methodName, String methodNamePrefix) {
        if (methodName.startsWith(methodNamePrefix + "Insert")) {
            return "insert";
        }
        if (methodName.startsWith(methodNamePrefix + "Delete")) {
            return "delete";
        }
        if (methodName.startsWith(methodNamePrefix + "Update")) {
            return "update";
        }
        if (methodName.startsWith(methodNamePrefix + "Count")) {
            return "select";
        }
        if (methodName.startsWith(methodNamePrefix + "Select")) {
            return "select";
        }
        if (methodName.startsWith(methodNamePrefix + "Sql")) {
            return "sql";
        }
        throw new IllegalArgumentException("methodName 命名错误：" + methodName + "，前缀：" + methodNamePrefix);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        /*
         * 不生成 Example 类
         */
        return false;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        runner.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        /*
         * 删除、添加 Example 的 import
         */
        interfaze.getImportedTypes().remove(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        interfaze.getImportedTypes().remove(new FullyQualifiedJavaType("java.util.List"));
        interfaze.getImportedTypes().remove(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        FullyQualifiedJavaType baseMapper = new FullyQualifiedJavaType(BaseMapper.class.getName());
        interfaze.getImportedTypes().add(baseMapper);

        /*
         * 继承 BaseMapper 接口
         */
        baseMapper = GenUtils.javaType("BaseMapper", introspectedTable.getBaseRecordType());
        interfaze.addSuperInterface(baseMapper);

        /*
         * 删除不用的 Mapper 方法
         */
        List<String> keepIdList = Arrays.asList(
                introspectedTable.getInsertSelectiveStatementId());
        for (Iterator<Method> it = interfaze.getMethods().iterator(); it.hasNext(); ) {
            Method e = it.next();

            log.info("删除 {} client: {}", introspectedTable.getTableConfiguration().getTableName(), e.getName());

            it.remove();
        }

        return true;
    }
}
