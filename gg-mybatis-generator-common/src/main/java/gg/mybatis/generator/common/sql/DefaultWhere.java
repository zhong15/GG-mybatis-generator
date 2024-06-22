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

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class DefaultWhere extends AbstractWhere implements Where {
    @Override
    public Where clear() {
        /*super.*/abstractWhereClear();
        return this;
    }

    @Override
    public Where withoutParamAnnotation() {
        /*super.*/noWhereParamName();
        return this;
    }

    @Override
    public Where open() {
        incrementBrackets();
        appendWhereSql("(");
        return this;
    }

    @Override
    public Where close() {
        decrementBrackets();
        appendWhereSql(")");
        return this;
    }

    @Override
    public Where col(String column) {
        appendWhereSql(column);
        return this;
    }

    @Override
    public Where and() {
        appendWhereSql("AND");
        return this;
    }

    @Override
    public Where and(String column) {
        appendWhereSql("AND");
        appendWhereSql(column);
        return this;
    }

    @Override
    public Where or() {
        checkAndSetAddBrackets();
        appendWhereSql("OR");
        return this;
    }

    @Override
    public Where or(String column) {
        checkAndSetAddBrackets();
        appendWhereSql("OR");
        appendWhereSql(column);
        return this;
    }

    @Override
    public Where isNull() {
        appendWhereSql("IS NULL");
        return this;
    }

    @Override
    public Where isNotNull() {
        appendWhereSql("IS NOT NULL");
        return this;
    }

    @Override
    public Where eq(Object value) {
        appendWhereSql("=");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where ne(Object value) {
        appendWhereSql("<>");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where lt(Object value) {
        appendWhereSql("<");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where lte(Object value) {
        appendWhereSql("<=");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where gt(Object value) {
        appendWhereSql(">");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where gte(Object value) {
        appendWhereSql(">=");
        appendWhereSqlParam(value);
        return this;
    }

    @Override
    public Where between(Object value1, Object value2) {
        appendWhereSql("BETWEEN");
        appendWhereSqlParam(value1);
        appendWhereSql("AND");
        appendWhereSqlParam(value2);
        return this;
    }

    @Override
    public Where in(List<?> list) {
        if (list == null || list.size() == 0) {
            throw new SqlWhereException("list is empty");
        }
        appendWhereSql("IN (");
        for (int i = 0; i < list.size(); i++) {
            appendWhereSqlParam(list.get(i));
            if (i != list.size() - 1) {
                appendWhereSql(",");
            }
        }
        appendWhereSql(")");
        return this;
    }

    @Override
    public Where like(String value) {
        if (StringUtils.isBlank(value)) {
            throw new SqlWhereException("value is blank");
        }
        appendWhereSql("LIKE");
        appendWhereSqlParam(value);
        return this;
    }
}