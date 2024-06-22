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

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.1
 */
public interface Where {
    /**
     * 清空状态
     *
     * @return 当前对象
     */
    Where clear();

    /**
     * 设置表示 XxxMapper.java 里方法的 Where 参数没有 @Param 注解
     *
     * @return 当前对象
     */
    Where withoutParamAnnotation();

    /**
     * 添加 "("
     *
     * @return 当前对象
     */
    Where open();

    /**
     * 添加 ")"
     *
     * @return 当前对象
     */
    Where close();

    /**
     * 添加 "column"
     *
     * @return 当前对象
     */
    Where col(String column);

    /**
     * 添加 "AND"
     *
     * @return 当前对象
     */
    Where and();

    /**
     * 添加 "AND column"
     *
     * @return 当前对象
     */
    Where and(String column);

    /**
     * 添加 "OR"
     *
     * @return 当前对象
     */
    Where or();

    /**
     * 添加 "OR column"
     *
     * @return 当前对象
     */
    Where or(String column);

    /**
     * 添加 "IS NULL"
     *
     * @return 当前对象
     */
    Where isNull();

    /**
     * 添加 "IS NOT NULL"
     *
     * @return 当前对象
     */
    Where isNotNull();

    /**
     * 添加 "= ?"
     *
     * @return 当前对象
     */
    Where eq(Object value);

    /**
     * 添加 "<> ?"
     *
     * @return 当前对象
     */
    Where neq(Object value);

    /**
     * 添加 "< ?"
     *
     * @return 当前对象
     */
    Where lt(Object value);

    /**
     * 添加 "<= ?"
     *
     * @return 当前对象
     */
    Where lte(Object value);

    /**
     * 添加 "> ?"
     *
     * @return 当前对象
     */
    Where gt(Object value);

    /**
     * 添加 ">= ?"
     *
     * @return 当前对象
     */
    Where gte(Object value);

    /**
     * 添加 "BETWEEN ? AND ?"
     *
     * @return 当前对象
     */
    Where between(Object value1, Object value2);

    /**
     * 添加 "IN (?, ?, ?)"
     *
     * @return 当前对象
     */
    Where in(List<?> list);

    /**
     * 添加 "LIKE ?"
     *
     * @return 当前对象
     */
    Where like(String value);
}
