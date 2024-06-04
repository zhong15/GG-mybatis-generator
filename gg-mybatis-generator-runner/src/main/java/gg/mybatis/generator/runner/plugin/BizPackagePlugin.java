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

package gg.mybatis.generator.runner.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 指定生成的包路径
 *
 * @author Zhong
 * @since 0.0.1
 */
public class BizPackagePlugin extends PluginAdapter {
    private static Logger log = LoggerFactory.getLogger(BizPackagePlugin.class);
    private static final String PROP_BASE_BIZ_PACKAGE = "baseBizPackage";
    private static final String PROP_BIZ_PACKAGE = "bizPackage";

    @Override
    public boolean validate(List<String> warnings) {
        String baseBizPackage = properties.getProperty(PROP_BASE_BIZ_PACKAGE);
        return baseBizPackage != null && baseBizPackage.trim().length() != 0;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String baseBizPackage = properties.getProperty(PROP_BASE_BIZ_PACKAGE);
        log.info("baseBizPackage: {}", baseBizPackage);
        String bizPackage = introspectedTable.getTableConfiguration().getProperty(PROP_BIZ_PACKAGE);
        log.info("bizPackage: {}", bizPackage);

        if (bizPackage == null || bizPackage.trim().length() == 0) {
        } else {
            introspectedTable.setMyBatis3XmlMapperPackage("mapper." + bizPackage);
            introspectedTable.setBaseRecordType(baseBizPackage + "." + bizPackage + ".model." + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName());
            introspectedTable.setMyBatis3JavaMapperType(baseBizPackage + "." + bizPackage + ".mapper." + new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getShortName());
        }
    }
}
