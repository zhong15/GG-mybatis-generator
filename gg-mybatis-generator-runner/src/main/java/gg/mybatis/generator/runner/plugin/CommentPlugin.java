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
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 添加表注释到类
 *
 * @author Zhong
 * @since 0.0.1
 */
public class CommentPlugin extends PluginAdapter {

  private static Logger log = LoggerFactory.getLogger(CommentPlugin.class);

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public void initialized(IntrospectedTable introspectedTable) {
    if (introspectedTable.getRemarks() != null
        && introspectedTable.getRemarks().trim().length() != 0) {
      return;
    }
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try {
      conn = super.context.getConnection();
      pstmt = conn.prepareStatement(
          "SELECT table_comment FROM information_schema.tables WHERE table_schema = ? AND table_name = ?");
      pstmt.setString(1, getDb());
      pstmt.setString(2, introspectedTable.getTableConfiguration().getTableName());
      rset = pstmt.executeQuery();
      while (rset.next()) {
        String tableComment = rset.getString(1);
        introspectedTable.setRemarks(tableComment);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } finally {
      if (rset != null) {
        try {
          rset.close();
        } catch (SQLException e) {
          log.warn("rset close error", e);
        }
      }
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e) {
          log.warn("pstat close error", e);
        }
      }
      super.context.closeConnection(conn);
    }
  }

  private String getDb() throws NoSuchFieldException, IllegalAccessException {
    Field jdbcConfigField = super.context.getClass()
        .getDeclaredField("jdbcConnectionConfiguration");
    jdbcConfigField.setAccessible(true);
    JDBCConnectionConfiguration jdbcConfig = (JDBCConnectionConfiguration) jdbcConfigField.get(
        super.context);
    String db = jdbcConfig.getConnectionURL().split("/")[3];
    jdbcConfigField.setAccessible(false);
    return db;
  }

  @Override
  public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    if (introspectedTable.getRemarks() != null
        && introspectedTable.getRemarks().trim().length() != 0) {
      topLevelClass.addJavaDocLine("/**");
      topLevelClass.addJavaDocLine(" * " + introspectedTable.getRemarks().trim());
      topLevelClass.addJavaDocLine(" * <p>");
      topLevelClass.addJavaDocLine(
          " * " + introspectedTable.getTableConfiguration().getTableName());
      topLevelClass.addJavaDocLine(" */");
    }
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    if (introspectedTable.getRemarks() != null
        && introspectedTable.getRemarks().trim().length() != 0) {
      interfaze.addJavaDocLine("/**");
      interfaze.addJavaDocLine(" * " + introspectedTable.getRemarks().trim() + " Mapper");
      interfaze.addJavaDocLine(" * <p>");
      interfaze.addJavaDocLine(" * " + introspectedTable.getTableConfiguration().getTableName());
      interfaze.addJavaDocLine(" */");
    }
    return true;
  }
}
