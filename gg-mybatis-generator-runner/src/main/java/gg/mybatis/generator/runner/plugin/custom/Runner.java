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

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;


/**
 * @author Zhong
 * @since 0.0.1
 */
public interface Runner {
    String SQL_MAP_METHOD_PREFIX = "sqlMap";
//    String CLIENT_METHOD_PREFIX = "client";

    static List<java.lang.reflect.Method> getSortedMethod(Class<?> clazz) {
        List<String> nameList = Arrays.asList("Sql", "Delete", "Update", "Select", "Count");
        Map<String, java.lang.reflect.Method> map = new TreeMap<>();
        List<java.lang.reflect.Method> methodList = new ArrayList<>();

        outer:
        for (java.lang.reflect.Method e : clazz.getMethods()) {
            for (String s : nameList) {
                if (/*e.getName().startsWith(Runner.CLIENT_METHOD_PREFIX + s)
                        || */e.getName().startsWith(Runner.SQL_MAP_METHOD_PREFIX + s)) {
                    continue outer;
                }
            }
            map.put(e.getName(), e);
        }
        methodList.addAll(map.values());

        for (String s : nameList) {
            map.clear();
            for (java.lang.reflect.Method e : clazz.getMethods()) {
                if (/*e.getName().startsWith(Runner.CLIENT_METHOD_PREFIX + s)
                        || */e.getName().startsWith(Runner.SQL_MAP_METHOD_PREFIX + s)) {
                    map.put(e.getName(), e);
                }
            }
            methodList.addAll(map.values());
        }

        return methodList;
    }

    void init();

    void modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable);

    void sqlMapSqlWhere(XmlElement element, IntrospectedTable introspectedTable);

    void sqlMapSqlWhere2(XmlElement element, IntrospectedTable introspectedTable);

//    void clientDeleteById(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapDeleteById(XmlElement element, IntrospectedTable introspectedTable);

    void sqlMapDeleteByIdList(XmlElement element, IntrospectedTable introspectedTable);

//    void clientDeleteByWhere(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapDeleteByWhere(XmlElement element, IntrospectedTable introspectedTable);

//    void clientUpdateById(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapUpdateById(XmlElement element, IntrospectedTable introspectedTable);

//    void clientUpdateColumnValueById(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapUpdateColumnValueById(XmlElement element, IntrospectedTable introspectedTable);

//    void clientUpdateByWhere(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapUpdateByWhere(XmlElement element, IntrospectedTable introspectedTable);

//    void clientSelectById(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapSelectById(XmlElement element, IntrospectedTable introspectedTable);

    void sqlMapSelectByIdList(XmlElement element, IntrospectedTable introspectedTable);

//    void clientSelectByWherePageIdIn(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapSelectByWherePageIdIn(XmlElement element, IntrospectedTable introspectedTable);

//    void clientSelectByWhere(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapSelectByWhere(XmlElement element, IntrospectedTable introspectedTable);

//    void clientCountByWhere(Interface interfaze, Method method, IntrospectedTable introspectedTable);

    void sqlMapCountByWhere(XmlElement element, IntrospectedTable introspectedTable);
}
