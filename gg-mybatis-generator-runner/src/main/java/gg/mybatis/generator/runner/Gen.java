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

package gg.mybatis.generator.runner;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class Gen {
    private static final Logger log = LoggerFactory.getLogger(Gen.class);

    public static void run() {
        final String projectPath = Gen.class.getClassLoader().getResource("").getPath();
        log.info("projectPath: {}", projectPath);

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File(projectPath + "generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = null;
        try {
            config = cp.parseConfiguration(configFile);

            config.getContexts().get(0).setJdbcConnectionConfiguration(getApplicationYmlJdbcConfig());

            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMLParserException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        } finally {
            warnings.forEach(e -> log.warn("Gen warn: {}", e));
        }
    }

    private static JDBCConnectionConfiguration getApplicationYmlJdbcConfig() {
        JDBCConnectionConfiguration config = new JDBCConnectionConfiguration();
        YamlPropertiesFactoryBean yamlProFb = new YamlPropertiesFactoryBean();
        yamlProFb.setResources(new ClassPathResource("application.yml"));
        Properties properties = yamlProFb.getObject();
        config.setDriverClass(properties.get("spring.datasource.driverClassName").toString());
        config.setConnectionURL(properties.get("spring.datasource.url").toString());
        config.setUserId(properties.get("spring.datasource.username").toString());
        config.setPassword(properties.get("spring.datasource.password").toString());
        config.getProperties().setProperty("useInformationSchema", "true");
        return config;
    }
}
