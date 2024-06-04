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

                XmlElement xmlElement = GenUtils.xmlElement(ss(m.getName(), prefix), "id", statementId);
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

    private static String ss(String name, String prefix) {
        if (name.startsWith(prefix + "Insert")) {
            return "insert";
        }
        if (name.startsWith(prefix + "Delete")) {
            return "delete";
        }
        if (name.startsWith(prefix + "Update")) {
            return "update";
        }
        if (name.startsWith(prefix + "Count")) {
            return "select";
        }
        if (name.startsWith(prefix + "Select")) {
            return "select";
        }
        if (name.startsWith(prefix + "Sql")) {
            return "sql";
        }
        throw new IllegalArgumentException("name 命名错误");
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
         * 删除 Example 的 import
         */
        interfaze.getImportedTypes().remove(new FullyQualifiedJavaType(introspectedTable.getExampleType()));

        /*
         * 删除不用的 Mapper 方法
         */
        List<String> keepIdList = Arrays.asList(
                introspectedTable.getInsertSelectiveStatementId());
        for (Iterator<Method> it = interfaze.getMethods().iterator(); it.hasNext(); ) {
            Method e = it.next();

            boolean keep = false;
            for (String keepId : keepIdList) {
                if (e.getName().equals(keepId)) {
                    keep = true;
                    break;
                }
            }
            if (keep) {
                continue;
            }

            log.info("删除 {} client: {}", introspectedTable.getTableConfiguration().getTableName(), e.getName());

            it.remove();
        }

        /*
         * 添加自定义的 Mapper 方法
         */
        for (java.lang.reflect.Method m : Runner.getSortedMethod(runner.getClass())) {
            String prefix = Runner.CLIENT_METHOD_PREFIX;
            if (m.getName().startsWith(prefix)) {
                String methodName = GenUtils.firstCharToLower(m.getName().substring(prefix.length()));

                log.info("生成 {} client: {}", introspectedTable.getTableConfiguration().getTableName(), methodName);

                Method method = new Method(methodName);
                method.setAbstract(true);
                try {
                    m.invoke(runner, interfaze, method, introspectedTable);
                    interfaze.addMethod(method);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }
}
