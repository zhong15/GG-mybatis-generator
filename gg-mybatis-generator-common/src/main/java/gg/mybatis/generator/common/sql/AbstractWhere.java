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

package gg.mybatis.generator.common.sql;

import gg.mybatis.generator.common.exception.SqlWhereException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
public abstract class AbstractWhere {
    /**
     * XxxMapper.java 里方法的 Where 参数的注解 @Param 的值
     */
    private String whereParamName = "where";
    /**
     * 左括号的数量
     */
    private int brackets;
    /**
     * 是否整个 WHERE 语句添加括号
     * <p>如果是软删除，且当前 WHERE 语句包含 OR 条件，且 OR 条件不在括号内，则为 true
     */
    private boolean addBrackets;
    /**
     * WHERE 语句
     */
    private StringBuilder whereSql;
    /**
     * Prepare Statement 的参数个数
     * <p>参数按顺序传入，paramNumber 是当前参数的序号，从 0 开始计数递增
     */
    private int paramNumber;
    /**
     * 当前类的实例变量 {@link #paramList} 的名字
     */
    private static final String PARAM_LIST = "paramList";
    /**
     * 存储 Prepare Statement 的参数
     * <p>参数按顺序传入，并有自己的存储下标
     */
    private List<Object> paramList;

    protected void abstractWhereClear() {
        whereParamName = "where";
        brackets = 0;
        addBrackets = false;
        if (whereSql != null) {
            whereSql.setLength(0);
        }
        paramNumber = 0;
        if (paramList != null) {
            paramList.clear();
        }
    }

    protected void noWhereParamName() {
        whereParamName = null;
    }

    @Override
    public String toString() {
        return whereSql == null ? "" : whereSql.toString();
    }

    protected void incrementBrackets() {
        brackets++;
    }

    protected void decrementBrackets() {
        brackets--;
//        if (brackets < 0) {
//            throw new SqlWhereException("缺少左括号");
//        }
    }

    protected void checkAndSetAddBrackets() {
        if (brackets == 0) {
            addBrackets = true;
        }
    }

    protected void appendWhereSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new SqlWhereException("sql is blank");
        }
        if (whereSql == null) {
            whereSql = new StringBuilder();
        }
        if (whereSql.length() == 0
                || whereSql.charAt(whereSql.length() - 1) == '('
                || sql.equals(")")
                || sql.equals(",")) {
        } else {
            whereSql.append(" ");
        }
        whereSql.append(sql);
    }

    protected void appendWhereSqlParam(Object param) {
        if (param == null) {
            throw new SqlWhereException("param is null");
        }

        final int index = paramNumber++;

        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        paramList.add(param);

        if (whereSql == null) {
            whereSql = new StringBuilder();
        }
        if (whereSql.length() == 0
                || whereSql.charAt(whereSql.length() - 1) == '(') {
        } else {
            whereSql.append(" ");
        }
        whereSql.append("#{");
        if (whereParamName != null) {
            whereSql.append(whereParamName).append(".");
        }
        whereSql.append(PARAM_LIST).append("[").append(index).append("]}");
    }
}
