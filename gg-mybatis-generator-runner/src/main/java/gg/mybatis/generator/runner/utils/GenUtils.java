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

package gg.mybatis.generator.runner.utils;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Objects;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class GenUtils {
    private GenUtils() {
    }

    public static FullyQualifiedJavaType javaType(String shortName, String... typeArguments) {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(shortName);
        if (typeArguments != null) {
            for (String e : typeArguments) {
                type.addTypeArgument(new FullyQualifiedJavaType(e));
            }
        }
        return type;
    }

    public static boolean updateAttribute(XmlElement element, String attrName, String attrValue) {
        for (int i = 0; i < element.getAttributes().size(); i++) {
            if (element.getAttributes().get(i).getName().equals(attrName)) {
                element.getAttributes().set(i, new Attribute(attrName, attrValue));
                return true;
            }
        }
        return false;
    }

    public static XmlElement xmlElement(String name, String... attrs) {
        XmlElement xmlElement = new XmlElement(name);
        if (attrs == null || attrs.length == 0) {
            return xmlElement;
        }
        if (attrs.length % 2 != 0) {
            throw new IllegalArgumentException("attrs 必须是成对的 [name, value, ...] 形式");
        }
        for (int i = 0; i < attrs.length; i++) {
            xmlElement.addAttribute(new Attribute(attrs[i], attrs[++i]));
        }
        return xmlElement;
    }

    public static TextElement textElement(XmlElement parent, String text) {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException("text 不能为空");
        }
        TextElement textElement = new TextElement(text);
        parent.addElement(textElement);
        return textElement;
    }

    public static Parameter parameter(String simpleClazzName, String name, String annotation) {
        if (annotation == null)
            return new Parameter(new FullyQualifiedJavaType(simpleClazzName), name);
        else
            return new Parameter(new FullyQualifiedJavaType(simpleClazzName), name, annotation);
    }

    public static Parameter parameter(FullyQualifiedJavaType javaType, String name, String annotation) {
        if (annotation == null)
            return new Parameter(javaType, name);
        else
            return new Parameter(javaType, name, annotation);
    }

    public static Parameter parameterAndAnnotation(String simpleClazzName, String name) {
        return new Parameter(new FullyQualifiedJavaType(simpleClazzName), name,
                "@Param(\"" + name + "\")");
    }

    public static Parameter parameterAndAnnotation(FullyQualifiedJavaType javaType, String name) {
        return new Parameter(javaType, name, "@Param(\"" + name + "\")");
    }

    public static String getAttrValue(XmlElement xmlElement, String attrName) {
        if (xmlElement == null) {
            return null;
        }
        if (xmlElement.getAttributes() == null) {
            return null;
        }
        for (Attribute e : xmlElement.getAttributes()) {
            if (Objects.equals(e.getName(), attrName)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static boolean attrEqual(XmlElement xmlElement, String attrName, String attrValue) {
        if (xmlElement == null) {
            return false;
        }
        if (xmlElement.getAttributes() == null) {
            return false;
        }
        for (Attribute e : xmlElement.getAttributes()) {
            if (Objects.equals(e.getName(), attrName)
                    && Objects.equals(e.getValue(), attrValue)) {
                return true;
            }
        }
        return false;
    }

    public static String firstCharToLower(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() <= 1) {
            return s.toLowerCase();
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String firstCharToUpper(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() <= 1) {
            return s.toUpperCase();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static IntrospectedColumn primaryKey(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns().get(0);
    }

    public static String primaryKeyShortJavaType(IntrospectedTable introspectedTable) {
        return primaryKey(introspectedTable).getFullyQualifiedJavaType().getShortName();
    }

    public static String primaryKeyFullJavaType(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
    }

    public static String prettySql(String sql) {
        int level = 0;
        StringBuilder sb = new StringBuilder(sql.length());
        for (String s : sql.split("\n")) {
            s = s.trim();
            boolean open = s.startsWith("<") && !s.startsWith("</");
            boolean close = s.startsWith("</") || s.endsWith("/>");
            if (close) {
                level--;
            }

            sb.append("    ");
            for (int i = 0; i < level; i++) {
                sb.append("  ");
            }
            sb.append(s).append("\n");

            if (open) {
                level++;
            }
        }
        return sb.substring(4, sb.length() - 1);
    }
}
