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

package gg.mybatis.generator.example.user.mapper;

import gg.mybatis.generator.common.sql.Where;
import gg.mybatis.generator.example.user.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 Mapper
 * <p>
 * t_user
 */
@Mapper
public interface UserMapper {
    int insertSelective(User row);

    int deleteById(Long id);

    int deleteByWhere(Where where);

    int updateById(@Param("row") User row, @Param("setNullColumnList") List<String> setNullColumnList);

    int updateByWhere(@Param("row") User row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where);

    int updateColumnValueById(@Param("id") Long id, @Param("column") String column, @Param("value") Object value);

    User selectById(@Param("id") Long id, @Param("columnList") List<String> columnList);

    List<User> selectByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount);

    List<User> selectByWherePageIdIn(@Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount);

    long countByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where);
}