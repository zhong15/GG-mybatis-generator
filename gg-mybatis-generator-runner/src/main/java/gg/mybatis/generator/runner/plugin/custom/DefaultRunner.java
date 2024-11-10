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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import gg.mybatis.generator.common.domain.BaseEntity;
import gg.mybatis.generator.common.sql.Where;
import gg.mybatis.generator.runner.utils.GenUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class DefaultRunner implements Runner {
    private static final String WHERE_CLASS_NAME = Where.class.getName();
    private static final String WHERE_CLASS_SIMPLE_NAME = Where.class.getSimpleName();
    private static final String TPL_DIR = "tpl";

    private Configuration cfg;

    @Override
    public void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_22);

        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), TPL_DIR);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    private void tpl(XmlElement element, Map<String, Object> map) {
        StringWriter out = null;
        try {
            String id = GenUtils.getAttrValue(element, "id");
            Template tpl = cfg.getTemplate(id + ".tpl");
            out = new StringWriter();
            tpl.process(map, out);
            GenUtils.textElement(element, GenUtils.prettySql(out.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        /*
         * 检查主键是否是 Long id
         */
        checkModelExistsLongId(introspectedTable);

        /*
         * 添加父类
         */
        List<String> baseEntityFieldList = Arrays.asList("id", "createTime", "updateTime", "isDeleted");
        addSuperClassToModel(topLevelClass, baseEntityFieldList);

        /*
         * 添加字段注释
         */
        addCommentToModelProperty(topLevelClass, introspectedTable);

        /*
         * 添加列名
         */
        addColumnEnumToModel(topLevelClass, introspectedTable);

        /**
         * 添加 clear 方法
         */
        addClearMethodToModel(topLevelClass, introspectedTable, baseEntityFieldList);

        /*
         * 添加 isColumn 方法
         */
        addIsColumnMethodToModel(topLevelClass, introspectedTable);

        // toString() 方法
        addToStringMethodToModel(topLevelClass, baseEntityFieldList);

        // 清理 import
        cleanModelImportClass(topLevelClass);
    }

    private void checkModelExistsLongId(IntrospectedTable introspectedTable) {
        if (!Objects.equals(GenUtils.primaryKeyFullJavaType(introspectedTable), Long.class.getName())) {
            throw new UnsupportedOperationException("主键必须是 " + Long.class.getName());
        }
        if (!Objects.equals(GenUtils.primaryKey(introspectedTable).getActualColumnName().toLowerCase(), "id")) {
            throw new UnsupportedOperationException("主键必须是 id");
        }
    }

    private void addSuperClassToModel(TopLevelClass topLevelClass, List<String> baseEntityFieldList) {
        topLevelClass.addImportedType(BaseEntity.class.getName());
        FullyQualifiedJavaType baseEntity = new FullyQualifiedJavaType(BaseEntity.class.getSimpleName());
        topLevelClass.setSuperClass(baseEntity);

        int removeNumber = 0;
        for (Iterator<Field> it = topLevelClass.getFields().iterator(); it.hasNext(); ) {
            Field e = it.next();
            if (baseEntityFieldList.contains(e.getName())) {
                it.remove();
                removeNumber++;
            }
        }
        if (removeNumber != baseEntityFieldList.size()) {
            throw new UnsupportedOperationException("必须存在字段：" + baseEntityFieldList.stream().collect(Collectors.joining(", ")));
        }
        for (Iterator<Method> it = topLevelClass.getMethods().iterator(); it.hasNext(); ) {
            Method e = it.next();
            for (String s : baseEntityFieldList) {
                if (e.getName().equals("get" + GenUtils.firstCharToUpper(s))
                        || e.getName().equals("set" + GenUtils.firstCharToUpper(s))) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private void addCommentToModelProperty(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Map<String, String> comments = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getJavaProperty, IntrospectedColumn::getRemarks));
        for (Field e : topLevelClass.getFields()) {
            if (comments.get(e.getName()) == null || comments.get(e.getName()).trim().length() == 0) {
                continue;
            }
            e.addJavaDocLine("/**");
            e.addJavaDocLine(" * " + comments.get(e.getName()));
            e.addJavaDocLine(" */");
        }
    }

    private void addColumnEnumToModel(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        final String columnEnum = "ColumnEnum";
        InnerEnum innerEnum = new InnerEnum(columnEnum);
        innerEnum.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addInnerEnum(innerEnum);

        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn e = introspectedTable.getAllColumns().get(i);

            StringBuilder sb = new StringBuilder();
            sb.append("/**\n");
            String javaType = e.getFullyQualifiedJavaType().toString();
            if (javaType.startsWith("java.lang")) {
                javaType = e.getFullyQualifiedJavaType().getShortName();
            }
            if (e.getRemarks() == null || e.getRemarks().trim().length() == 0) {
                sb.append("         * 类型：").append(javaType).append("\n");
            } else {
                sb.append("         * ").append(e.getRemarks().trim()).append("，类型：").append(javaType).append("\n");
            }
            sb.append("         */\n");
            sb.append("        ").append(e.getActualColumnName().toLowerCase());
            innerEnum.addEnumConstant(sb.toString());
        }
    }

    private void addClearMethodToModel(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, List<String> baseEntityFieldList) {
        Method clear = new Method("clear");
        clear.addAnnotation("@Override");
        clear.setVisibility(JavaVisibility.PUBLIC);
        clear.addBodyLine("super.clear();");
        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn e = introspectedTable.getAllColumns().get(i);
            if (baseEntityFieldList.contains(e.getJavaProperty())) {
                clear.addBodyLine("// super.set" + GenUtils.firstCharToUpper(e.getJavaProperty()) + "(null);");
            } else {
                clear.addBodyLine("this." + e.getJavaProperty() + " = null;");
            }
        }
        topLevelClass.addMethod(clear);
    }

    private void addIsColumnMethodToModel(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method isColumn = new Method("isColumn");
        isColumn.setVisibility(JavaVisibility.PUBLIC);
        isColumn.setStatic(true);
        isColumn.setReturnType(GenUtils.javaType("boolean"));
        isColumn.addParameter(GenUtils.parameter("String", "column", null));
        isColumn.addBodyLine("if (column == null || column.trim().length() == 0) {");
        isColumn.addBodyLine("return false;");
        isColumn.addBodyLine("}");
        isColumn.addBodyLine("column = column.toLowerCase();");
        for (int i = 0; i < introspectedTable.getAllColumns().size(); i++) {
            IntrospectedColumn e = introspectedTable.getAllColumns().get(i);
            isColumn.addBodyLine((i == 0 ? "return " : "        || ")
                    + "column.equals(\"" + e.getActualColumnName().toLowerCase() + "\")"
                    + (i == introspectedTable.getAllColumns().size() - 1 ? ";" : ""));
        }
        topLevelClass.addMethod(isColumn);
    }

    private void addToStringMethodToModel(TopLevelClass topLevelClass, List<String> baseEntityFieldList) {
        Method toString = null;
        for (Method e : topLevelClass.getMethods()) {
            if (!e.isStatic() && e.getName().equals("toString") && CollectionUtils.isEmpty(e.getParameters())) {
                toString = e;
                break;
            }
        }
        if (toString != null) {
            for (String e : baseEntityFieldList) {
                String from = "sb.append(\", " + e + "=\").append(" + e + ");";
                String to = "sb.append(\", " + e + "=\").append(super.get" + GenUtils.firstCharToUpper(e) + "());";
                for (int i = 0; i < toString.getBodyLines().size(); i++) {
                    if (toString.getBodyLines().get(i).equals(from)) {
                        toString.getBodyLines().set(i, to);
                        break;
                    }
                }
            }
        }
    }

    private void cleanModelImportClass(TopLevelClass topLevelClass) {
        Set<String> typeSet = new HashSet<>();
        typeSet.add(BaseEntity.class.getName());
        for (Field e : topLevelClass.getFields()) {
            typeSet.add(e.getType().getFullyQualifiedName());
        }
        for (Iterator<FullyQualifiedJavaType> it = topLevelClass.getImportedTypes().iterator(); it.hasNext(); ) {
            FullyQualifiedJavaType type = it.next();
            if (!typeSet.contains(type.getFullyQualifiedName())) {
                it.remove();
            }
        }
    }

    private static String getJavaTypeShortName(String javaType) {
        switch (javaType) {
            case "Byte":
                return "byte";
            case "Short":
                return "short";
            case "Integer":
                return "int";
            case "Long":
                return "long";
            case "Float":
                return "float";
            case "Double":
                return "double";
            case "Character":
                return "char";
            case "String":
                return "str";
            case "Date":
                return "date";
            case "BigDecimal":
                return "bd";
            default:
                return javaType.substring(0, 1).toLowerCase() + javaType.substring(1);
        }
    }

    @Override
    public void sqlMapSqlWhere(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, Object> map = new HashMap<>();
        tpl(element, map);
    }

    @Override
    public void sqlMapSqlWhere2(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, Object> map = new HashMap<>();
        tpl(element, map);
    }

    @Override
    public void sqlMapDeleteById(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("parameterType", GenUtils.primaryKeyFullJavaType(introspectedTable)));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapDeleteByIdList(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("parameterType", List.class.getName()));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapDeleteByWhere(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("parameterType", WHERE_CLASS_NAME));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        tpl(element, map);
    }

    @Override
    public void sqlMapUpdateById(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("allColumns", introspectedTable.getAllColumns().stream()
                .filter(e -> !introspectedTable.getPrimaryKeyColumns().contains(e))
                .collect(Collectors.toList()));
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapUpdateColumnValueById(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapUpdateByWhere(XmlElement element, IntrospectedTable introspectedTable) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("allColumns", introspectedTable.getAllColumns().stream()
                .filter(e -> !introspectedTable.getPrimaryKeyColumns().contains(e))
                .collect(Collectors.toList()));
        tpl(element, map);
    }

    @Override
    public void sqlMapSelectById(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapSelectByIdList(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapSelectByWherePageIdIn(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        map.put("pk", GenUtils.primaryKey(introspectedTable));
        tpl(element, map);
    }

    @Override
    public void sqlMapSelectByWhere(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        tpl(element, map);
    }

    @Override
    public void sqlMapCountByWhere(XmlElement element, IntrospectedTable introspectedTable) {
        element.addAttribute(new Attribute("resultType", "java.lang.Long"));

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", introspectedTable.getTableConfiguration().getTableName());
        tpl(element, map);
    }
}
