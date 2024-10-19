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
public interface BaseMapper<T extends BaseEntity> {
    /**
     * insert 记录
     *
     * @param row 记录
     * @return 1 如果 insert 成功
     */
    int insertSelective(T row);

    /**
     * 删除根据 ID
     *
     * @param id 主键
     * @return 1 如果删除成功
     */
    int deleteById(Long id);

    /**
     * 删除根据多个 ID
     *
     * @param idList 多个主键
     * @return 非 0 如果删除成功
     */
    int deleteByIdList(List<Long> idList);

    /**
     * 删除根据 Where
     *
     * @param where WHERE 条件，Where 必须先调用 .withoutParamAnnotation()，null 删除所有
     * @return 删除的记录数
     */
    int deleteByWhere(Where where);

    /**
     * 更新根据 ID
     *
     * @param row               记录，row.id 不能为 null
     * @param setNullColumnList SET null 的列，允许 null
     * @return 更新的记录数
     */
    int updateById(@Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList);

    /**
     * 更新根据 Where
     *
     * @param row               记录
     * @param setNullColumnList SET null 的列，允许 null
     * @param where             WHERE 条件，null 更新所有
     * @return 更新的记录数
     */
    int updateByWhere(@Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where);

    /**
     * 更新某字段更新 ID
     *
     * @param id     主键
     * @param column 字段名
     * @param value  值
     * @return 1 如果更新成功
     */
    int updateColumnValueById(@Param("id") Long id, @Param("column") String column, @Param("value") Object value);

    /**
     * 查询根据 ID
     *
     * @param id         主键
     * @param columnList SELECT 的字段，允许 null
     * @return null 如果记录不存在
     */
    T selectById(@Param("id") Long id, @Param("columnList") List<String> columnList);

    /**
     * 查询根据多个 ID
     *
     * @param idList     主键
     * @param columnList SELECT 的字段，允许 null
     * @return null 如果记录不存在
     */
    List<T> selectByIdList(@Param("idList") List<Long> idList, @Param("columnList") List<String> columnList);

    /**
     * 分页查询根据 Where，此方法深度分页效率较低
     *
     * @param distinct   是否去重，只有 columnList 有值才起作用，允许 null
     * @param columnList SELECT 的字段，允许 null
     * @param where      WHERE 条件，null 查询所有
     * @param orderBy    ORDER BY 规则，如："nickname ASC, id DESC"，允许 null
     * @param offset     分页条件 offset，只有 rowCount 有值才起作用，允许 null
     * @param rowCount   分页条件 rowCount，允许 null
     * @return null 如果无匹配记录
     */
    List<T> selectByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where,
                          @Param("orderBy") String orderBy,
                          @Param("offset") Long offset, @Param("rowCount") Integer rowCount);

    /**
     * 分页查询根据 Where
     *
     * @param columnList SELECT 的字段，允许 null
     * @param where      WHERE 条件，null 查询所有
     * @param orderBy    ORDER BY 规则，如："nickname ASC, id DESC"，允许 null
     * @param offset     分页条件 offset，只有 rowCount 有值才起作用
     * @param rowCount   分页条件 rowCount
     * @return null 如果无匹配记录
     */
    List<T> selectByWherePageIdIn(@Param("columnList") List<String> columnList, @Param("where") Where where,
                                  @Param("orderBy") String orderBy,
                                  @Param("offset") long offset, @Param("rowCount") int rowCount);

    /**
     * 查询总数根据 Where
     *
     * @param distinct   是否去重，只有 columnList 有值才起作用，允许 null
     * @param columnList SELECT 的字段，允许 null
     * @param where      WHERE 条件，null 查询所有
     * @return 0 如果无匹配记录
     */
    long countByWhere(@Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where);
}
