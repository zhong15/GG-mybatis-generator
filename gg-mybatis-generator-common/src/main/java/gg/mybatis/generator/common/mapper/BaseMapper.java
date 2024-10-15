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

package gg.mybatis.generator.common.mapper;

import gg.mybatis.generator.common.domain.BaseEntity;
import gg.mybatis.generator.common.sql.Where;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zhong
 * @since 0.0.2
 */
public interface BaseMapper<T extends BaseEntity<ID>, ID> {
    int insertSelective(T row);

    int deleteById(ID id);

    int deleteByWhere(Where where);

    int updateById(@Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList);

    int updateByWhere(@Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where);

    int updateColumnValueById(@Param("id") ID id, @Param("column") String column, @Param("value") Object value);

    T selectById(@Param("id") ID id, @Param("columnList") List<String> columnList);

    List<T> selectByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount);

    List<T> selectByWherePageIdIn(@Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount);

    long countByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where);
}
