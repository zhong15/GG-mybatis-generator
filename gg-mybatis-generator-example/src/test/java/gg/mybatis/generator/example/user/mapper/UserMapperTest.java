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

import gg.mybatis.generator.common.domain.BaseEntity;
import gg.mybatis.generator.common.sql.DefaultWhere;
import gg.mybatis.generator.common.util.SqlUtils;
import gg.mybatis.generator.example.Application;
import gg.mybatis.generator.example.user.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Transactional
@SpringBootTest(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMapperTest {
    private static final String PREFIX_EMAIL = "email ";
    private static final String PREFIX_NICKNAME = "nickname ";
    private static final String PREFIX_PASSWORD = "password ";
    private static final byte PREFIX_IS_ENABLE = 0;

    @Autowired
    private UserMapper userMapper;

    @Before
    public void setup() {
        userMapper.deleteByWhere(null);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, 0);
    }

    @Test
    public void test_insertSelective() {
        // 测试用例：插入 2 条数据
        // 期望结果：2 条数据 ID 都不为 null 且不同
        final Long id_1 = test_insertSelective_core(1, 1).getId();
        Assert.assertNotNull(id_1);

        final Long id_2 = test_insertSelective_core(2, 2).getId();
        Assert.assertNotNull(id_2);

        Assert.assertNotEquals(id_1, id_2);
    }

    private User test_insertSelective_core(int suffix, long expectCount) {
        // T row

        // 测试用例：插入 1 条数据
        // 期望结果：
        //      插入 1 条数据
        //      返回 ID 不为空
        //      createTime = null
        //      updateTime = null
        //      isDeleted = null
        //      数据总数变为 1
        //      根据 ID 查询该条数据：
        //      id 不变
        //      email 不变
        //      nickname 不变
        //      password 不变
        //      isEnable 不变
        //      createTime != null
        //      updateTime != null
        User user = new User();
        user.setId(null);
        user.setEmail(PREFIX_EMAIL + suffix);
        user.setNickname(PREFIX_NICKNAME + suffix);
        user.setPassword(PREFIX_PASSWORD + suffix);
        user.setIsEnable((byte) (PREFIX_IS_ENABLE + suffix));
        user.setCreateTime(null);
        user.setUpdateTime(null);

        int rows = userMapper.insertSelective(user);
        Assert.assertEquals(rows, 1);

        Assert.assertNotNull(user.getId());
        Assert.assertEquals(user.getEmail(), PREFIX_EMAIL + suffix);
        Assert.assertEquals(user.getNickname(), PREFIX_NICKNAME + suffix);
        Assert.assertEquals(user.getPassword(), PREFIX_PASSWORD + suffix);
        Assert.assertEquals(user.getIsEnable().intValue(), PREFIX_IS_ENABLE + suffix);
        Assert.assertNull(user.getCreateTime());
        Assert.assertNull(user.getUpdateTime());

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, expectCount);

        User savedUser = assertEqualDBValue(user, false, false, false);

        return savedUser;
    }

    private User assertEqualDBValue(User user, boolean testCreateTime, boolean testUpdateTime, boolean testIsDeleted) {
        User savedUser = userMapper.selectById(user.getId(), null);
        Assert.assertNotNull(savedUser);
        Assert.assertEquals(user.getId(), savedUser.getId());
        Assert.assertEquals(user.getEmail(), savedUser.getEmail());
        Assert.assertEquals(user.getNickname(), savedUser.getNickname());
        Assert.assertEquals(user.getPassword(), savedUser.getPassword());
        Assert.assertEquals(user.getIsEnable(), savedUser.getIsEnable());
        Assert.assertNotNull(savedUser.getCreateTime());
        if (testCreateTime) {
            Assert.assertEquals(user.getCreateTime(), savedUser.getCreateTime());
        }
        Assert.assertNotNull(savedUser.getUpdateTime());
        if (testUpdateTime) {
            Assert.assertEquals(user.getUpdateTime(), savedUser.getUpdateTime());
        }
        return savedUser;
    }

    @Test
    public void test_deleteById() {
        // Long id

        // 测试用例：插入 2 条数据，然后根据 ID 删除第 1 条数据
        // 期望结果：
        //      插入第 1 条数据：获取 ID_1
        //      插入第 2 条数据：获取 ID_2
        //      根据 ID_1 删除第 1 条数据：总数 1
        //      根据 ID_1 查询第 1 条数据：不存在
        //      根据 ID_2 查询第 2 条数据：存在

        // 第 1 次插入
        final long id_1 = test_insertSelective_core(1, 1).getId();

        // 第 2 次插入
        final long id_2 = test_insertSelective_core(2, 2).getId();

        // 执行删除 id_1
        int rows = userMapper.deleteById(id_1);
        Assert.assertEquals(rows, 1);

        // 查询 id_1 记录 = null
        User user = userMapper.selectById(id_1, null);
        Assert.assertNull(user);

        // 查询 id_2 记录 != null
        user = userMapper.selectById(id_2, null);
        Assert.assertNotNull(user);

        // 总数 = 1
        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, 1L);
    }

    @Test
    public void test_deleteByWhere() {
        // Where where

        // 测试用例：插入 2 条数据，然后根据 ID 删除第 1 条数据
        // 期望结果：
        //      插入第 1 条数据：获取 ID_1
        //      插入第 2 条数据：获取 ID_2
        //      根据 ID_1 删除第 1 条数据：总数 1
        //      根据 ID_1 查询第 1 条数据：不存在
        //      根据 ID_2 查询第 2 条数据：存在

        // 第 1 次插入
        final long id_1 = test_insertSelective_core(1, 1).getId();

        // 第 2 次插入
        final long id_2 = test_insertSelective_core(2, 2).getId();

        // 执行删除 id_1
        int rows = userMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(User.ID_long).eq(id_1));
        Assert.assertEquals(rows, 1);

        // 查询 id_1 记录 = null
        User user = userMapper.selectById(id_1, null);
        Assert.assertNull(user);

        // 查询 id_2 记录 != null
        user = userMapper.selectById(id_2, null);
        Assert.assertNotNull(user);

        // 总数 = 1
        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, 1L);
    }

    @Test
    public void test_updateById() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：null, 指定列：null
        // 期望结果：报错 MyBatisSystemException
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);
        try {
            int rows = userMapper.updateById(null, null);
            Assert.fail("报错了");
        } catch (MyBatisSystemException e) {
        }
    }

    @Test
    public void test_updateById_2() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, email, 指定列：null
        // 期望结果：
        //      第 1 个数据：email 更新，updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        user1.setEmail(user1.getEmail() + " update");

        User updateUser1 = new User();
        updateUser1.setId(user1.getId());
        updateUser1.setEmail(user1.getEmail());
        int rows = userMapper.updateById(updateUser1, null);
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateById_3() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, 指定列：nickname
        // 期望结果：
        //      第 1 个数据：nickname = null，updateTime 更新，其它列不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        User updateUser1 = new User();
        updateUser1.setId(user1.getId());
        int rows = userMapper.updateById(updateUser1, Arrays.asList(User.NICKNAME_str));
        Assert.assertEquals(rows, 1);

        user1.setNickname(null);
        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateById_4() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, email，指定列：nickname
        // 期望结果：
        //      第 1 个数据：email 更新，nickname = null，updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        user1.setEmail(user1.getEmail() + " update");

        User updateUser1 = new User();
        updateUser1.setId(user1.getId());
        updateUser1.setEmail(user1.getEmail());
        int rows = userMapper.updateById(updateUser1, Arrays.asList(User.NICKNAME_str));
        Assert.assertEquals(rows, 1);

        user1.setNickname(null);
        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateByWhere() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：null, 指定列：null, 第 1 个数据 ID
        // 期望结果：
        //      更新报错 MyBatisSystemException
        //      第 1 条数据：不变
        //      第 2 条数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        try {
            int rows = userMapper.updateByWhere(null, null, new DefaultWhere().col(User.ID_long).eq(user1.getId()));
            Assert.fail("报错列");
        } catch (MyBatisSystemException e) {
        }

        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateByWhere_2() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：email, 指定列：null, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：email 更新，updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        user1.setEmail(user1.getEmail() + " update");

        User updateUser1 = new User();
        updateUser1.setId(user1.getId());
        updateUser1.setEmail(user1.getEmail());
        int rows = userMapper.updateByWhere(updateUser1, null, new DefaultWhere().col(User.ID_long).eq(user1.getId()));
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateByWhere_3() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：null, 指定列：nickname, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：nickname = null，updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        int rows = userMapper.updateByWhere(new User(), Arrays.asList(User.NICKNAME_str), new DefaultWhere().col(User.ID_long).eq(user1.getId()));
        Assert.assertEquals(rows, 1);

        user1.setNickname(null);
        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateByWhere_4() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：email, 指定列：nickname, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：email 更新，nickname = null, updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        user1.setEmail(user1.getEmail() + " update");

        User updateUser1 = new User();
        updateUser1.setId(user1.getId());
        updateUser1.setEmail(user1.getEmail());
        int rows = userMapper.updateByWhere(updateUser1, Arrays.asList(User.NICKNAME_str), new DefaultWhere().col(User.ID_long).eq(user1.getId()));
        Assert.assertEquals(rows, 1);

        user1.setNickname(null);
        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, null, null
        // 期望结果：
        //      更新报错 BadSqlGrammarException
        //      第 1 个数据：不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        try {
            int rows = userMapper.updateColumnValueById(user1.getId(), null, null);
            Assert.fail("报错了");
        } catch (BadSqlGrammarException e) {
        }

        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_2() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, nickname, null
        // 期望结果：
        //      第 1 个数据：nickname = null, updateTime 更新，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        int rows = userMapper.updateColumnValueById(user1.getId(), User.NICKNAME_str, null);
        Assert.assertEquals(rows, 1);

        user1.setNickname(null);
        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_3() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, null, "xxxUpdate"
        // 期望结果：
        //      更新报错 BadSqlGrammarException
        //      第 1 个数据：不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        try {
            int rows = userMapper.updateColumnValueById(user1.getId(), null, "xxxUpdate");
            Assert.fail("报错了");
        } catch (BadSqlGrammarException e) {
        }

        assertEqualDBValue(user1, true, true, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_4() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, nickname, "xxxUpdate"
        // 期望结果：
        //      第 1 个数据：nickname = "xxxUpdate"，其它不变
        //      第 2 个数据：不变
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        user1.setNickname(user1.getNickname() + "_update");
        int rows = userMapper.updateColumnValueById(user1.getId(), User.NICKNAME_str, user1.getNickname());
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(user1, true, false, true);

        assertEqualDBValue(user2, true, true, true);
    }

    @Test
    public void test_selectById() {
        // @Param("id") Long id, @Param("columnList") List<String> columnList

        // 测试用例：插入 2 条数据，执行查询：第 1 个数据 ID, null
        // 期望结果：查出第 1 条数据所有列
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        User user1_1 = userMapper.selectById(user1.getId(), null);

        Assert.assertEquals(user1, user1_1);
    }

    @Test
    public void test_selectById_2() {
        // @Param("id") Long id, @Param("columnList") List<String> columnList

        // 测试用例：插入 2 条数据，执行查询：第 1 个数据 ID, id email
        // 期望结果：查出第 1 条数据 id email，其它列为 null
        User user1 = test_insertSelective_core(1, 1);
        User user2 = test_insertSelective_core(2, 2);

        User user1_1 = userMapper.selectById(user1.getId(), Arrays.asList(User.ID_long, User.EMAIL_str));

        User user1_2 = new User();
        user1_2.setId(user1.getId());
        user1_2.setEmail(user1.getEmail());
        Assert.assertEquals(user1_1, user1_2);
    }

    @Test
    public void test_selectByWhere() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：distinct null, columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        List<User> list = userMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    private static final <T extends BaseEntity> void assertCountDistinctId(List<T> list, long expectCount) {
        long count = list.stream()
                .filter(Objects::nonNull)
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        Assert.assertEquals(count, expectCount);
    }

    @Test
    public void test_selectByWhere_2() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：distinct true, columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        List<User> list = userMapper.selectByWhere(true, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWhere_3() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 email，nickname 都相同，执行查询：distinct null, columnList：email, nickname
        // 期望结果：
        //      查出总数 = 10
        //      每个查出的值和插入的值一致，且只查出 email, nickname
        final int total = 10;
        Map<String, User> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getEmail(), user);
        }
        Assert.assertEquals(map.size(), total - 1);
        User repeatUser = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatUser.getEmail()).getNickname(), repeatUser.getNickname());

        List<String> columnList = Arrays.asList(User.EMAIL_str, User.NICKNAME_str);
        List<Function<User, ?>> funcList = Arrays.asList(User::getEmail, User::getNickname);
        List<User> list = userMapper.selectByWhere(null, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        for (User user : list) {
            Assert.assertNotNull(user);
            assertSelectColumn(user, columnList);
            assertEqual(user, map.get(user.getEmail()), funcList);
        }
    }

    private static void assertSelectColumn(User user, List<String> columnList) {
        Assert.assertNotNull(user);
        if (columnList.contains(User.ID_long)) {
            Assert.assertNotNull(user.getId());
        } else {
            Assert.assertNull(user.getId());
        }
        if (columnList.contains(User.EMAIL_str)) {
            Assert.assertNotNull(user.getEmail());
        } else {
            Assert.assertNull(user.getEmail());
        }
        if (columnList.contains(User.PASSWORD_str)) {
            Assert.assertNotNull(user.getPassword());
        } else {
            Assert.assertNull(user.getPassword());
        }
        if (columnList.contains(User.IS_ENABLE_byte)) {
            Assert.assertNotNull(user.getIsEnable());
        } else {
            Assert.assertNull(user.getIsEnable());
        }
        if (columnList.contains(User.NICKNAME_str)) {
            Assert.assertNotNull(user.getNickname());
        } else {
            Assert.assertNull(user.getNickname());
        }
        if (columnList.contains(User.CREATE_TIME_date)) {
            Assert.assertNotNull(user.getCreateTime());
        } else {
            Assert.assertNull(user.getCreateTime());
        }
        if (columnList.contains(User.UPDATE_TIME_date)) {
            Assert.assertNotNull(user.getUpdateTime());
        } else {
            Assert.assertNull(user.getUpdateTime());
        }
    }

    private static void assertEqual(User a, User b, List<Function<User, ?>> columnList) {
        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        for (Function<User, ?> func : columnList) {
            Assert.assertEquals(func.apply(a), func.apply(b));
        }
    }

    @Test
    public void test_selectByWhere_4() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 email，nickname 都相同，执行查询：distinct false, columnList：email, nickname
        // 期望结果：
        //      查出总数 = 10
        //      每个查出的值和插入的值一致，且只查出 email, nickname
        final int total = 10;
        Map<String, User> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getEmail(), user);
        }
        Assert.assertEquals(map.size(), total - 1);
        User repeatUser = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatUser.getEmail()).getNickname(), repeatUser.getNickname());

        List<String> columnList = Arrays.asList(User.EMAIL_str, User.NICKNAME_str);
        List<Function<User, ?>> funcList = Arrays.asList(User::getEmail, User::getNickname);
        List<User> list = userMapper.selectByWhere(false, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        for (User user : list) {
            Assert.assertNotNull(user);
            assertSelectColumn(user, columnList);
            assertEqual(user, map.get(user.getEmail()), funcList);
        }
    }

    @Test
    public void test_selectByWhere_5() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 email，nickname 都相同，执行查询：distinct true, columnList email, nickname
        // 期望结果：
        //      查出不重复 email, nickname 总数 = 9
        //      每个查出的值和插入的值一致，且只查出 email, nickname
        final int total = 10;
        Map<String, User> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getEmail(), user);
        }
        Assert.assertEquals(map.size(), total - 1);
        User repeatUser = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatUser.getEmail()).getNickname(), repeatUser.getNickname());

        List<String> columnList = Arrays.asList(User.EMAIL_str, User.NICKNAME_str);
        List<Function<User, ?>> funcList = Arrays.asList(User::getEmail, User::getNickname);
        List<User> list = userMapper.selectByWhere(true, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total - 1);
        for (User user : list) {
            Assert.assertNotNull(user);
            assertSelectColumn(user, columnList);
            assertEqual(user, map.get(user.getEmail()), funcList);
        }
    }

    @Test
    public void test_selectByWhere_6() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      默认排序，mysql 默认无序
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        List<User> list = userMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        long id = 0;
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
//            Assert.assertTrue(id < user.getId());
//            id = user.getId();
        }
    }

    @Test
    public void test_selectByWhere_7() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy "id DESC"
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      排序按 ID 降序
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        List<User> list = userMapper.selectByWhere(null, null, null, SqlUtils.orderBy(User.ID_long, SqlUtils.DESC), null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        Long id = null;
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
            if (id != null) {
                Assert.assertTrue(id > user.getId());
            }
            id = user.getId();
        }
    }

    @Test
    public void test_selectByWhere_8() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset null, rowCount null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        List<User> list = userMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWhere_9() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 1, rowCount null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 1;
        List<User> list = userMapper.selectByWhere(null, null, null, null, offset, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWhere_10() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset null, rowCount 5
        // 期望结果：
        //      查出总数 = 5
        //      查出不重复 ID 总数 = 5
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final int rowCount = 5;
        List<User> list = userMapper.selectByWhere(null, null, null, null, null, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), rowCount);
        assertCountDistinctId(list, rowCount);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWhere_11() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 7, rowCount 5
        // 期望结果：
        //      查出总数 = 3
        //      查出不重复 ID 总数 = 3
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 7;
        final int rowCount = 5;
        List<User> list = userMapper.selectByWhere(null, null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        int count = total - offset > rowCount ? rowCount : (int) (total - offset);
        Assert.assertEquals(list.size(), count);
        assertCountDistinctId(list, count);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWherePageIdIn() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 0;
        final int rowCount = total;
        List<User> list = userMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_selectByWherePageIdIn_2() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：columnList: id, email
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      只查出 id, email
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 0;
        final int rowCount = total;
        List<String> columnList = Arrays.asList(User.ID_long, User.EMAIL_str);
        List<Function<User, ?>> funcList = Arrays.asList(User::getId, User::getEmail);
        List<User> list = userMapper.selectByWherePageIdIn(columnList, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (User user : list) {
            Assert.assertNotNull(user);
            assertSelectColumn(user, columnList);
            assertEqual(user, map.get(user.getId()), funcList);
        }
    }

    @Test
    public void test_selectByWherePageIdIn_3() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      按 mysql 默认排序规则：无序
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 0;
        final int rowCount = total;
        List<User> list = userMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        long id = 0;
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
//            Assert.assertTrue(id < user.getId());
//            id = user.getId();
            System.out.println(user.getId());
        }
    }

    @Test
    public void test_selectByWherePageIdIn_4() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy "id DESC"
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      按 ID 降序
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 0;
        final int rowCount = total;
        List<User> list = userMapper.selectByWherePageIdIn(null, null, SqlUtils.orderBy(User.ID_long, SqlUtils.DESC), offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        Long id = null;
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
            if (id != null) {
                Assert.assertTrue(id > user.getId());
            }
            id = user.getId();
        }
    }

    @Test
    public void test_selectByWherePageIdIn_5() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String userBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 7, rowCount 5
        // 期望结果：
        //      查出总数 = 3
        //      查出不重复 ID 总数 = 3
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, User> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            map.put(user.getId(), user);
        }

        final long offset = 7;
        final int rowCount = 5;
        List<User> list = userMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        int count = total - offset > rowCount ? rowCount : (int) (total - offset);
        Assert.assertEquals(list.size(), count);
        assertCountDistinctId(list, count);
        for (User user : list) {
            Assert.assertNotNull(user);
            Assert.assertEquals(user, map.get(user.getId()));
        }
    }

    @Test
    public void test_countByWhere() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 email, nickname 相同，执行查询：distinct null, columnList null
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }
        User user = test_insertSelective_core(1, total);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_2() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 email, nickname 相同，执行查询：distinct true, columnList null
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }
        User user = test_insertSelective_core(1, total);

        long count = userMapper.countByWhere(true, null, null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_3() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 email, nickname 相同，执行查询：distinct null, columnList: email
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }
        User user = test_insertSelective_core(1, total);

        long count = userMapper.countByWhere(null, Arrays.asList(User.EMAIL_str), null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_4() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 email, nickname 相同，执行查询：distinct false, columnList: email
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }
        User user = test_insertSelective_core(1, total);

        long count = userMapper.countByWhere(false, Arrays.asList(User.EMAIL_str), null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_5() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 email, nickname 相同，执行查询：distinct true, columnList: email
        // 期望结果：
        //      查出总数 = 9
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }
        User user = test_insertSelective_core(1, total);

        long count = userMapper.countByWhere(true, Arrays.asList(User.EMAIL_str), null);
        Assert.assertEquals(count, (long) total - 1);
    }

    @Test
    public void test_sqlWhere() {
        // 测试用例：插入 10 条数据，输入 null
        // 期望结果：
        //      删除总数 10
        //      查询总数 = 0
        final int total = 10;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }

        int rows = userMapper.deleteByWhere(null);
        Assert.assertEquals(rows, total);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, 0L);
    }

    @Test
    public void test_sqlWhere_2() {
        // 测试用例：插入 10 条数据，根据 ID 删除 1 条，输入没有 or
        // 期望结果：
        //      删除总数 1
        //      查询总数 = 9
        final int total = 10;
        long id = 0L;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            id = user.getId();
        }
        int rows = userMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(User.ID_long).eq(id));
        Assert.assertEquals(rows, 1);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, total - 1);
    }

    @Test
    public void test_sqlWhere_3() {
        // 测试用例：插入 10 条数据，根据 ID 删除 2 条，输入在括号外的 or
        // 期望结果：
        //      删除总数 2
        //      查询总数 = 8
        final int total = 10;
        Long id = null;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = user.getId();
            }
        }
        int rows = userMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(User.ID_long).eq(id)
                .or(User.ID_long).eq(id + 1));
        Assert.assertEquals(rows, 2);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, total - 2);
    }

    @Test
    public void test_sqlWhere_4() {
        // 测试用例：插入 10 条数据，根据 ID 删除 2 条，输入在括号内的 or
        // 期望结果：
        //      删除总数 2
        //      查询总数 = 8
        final int total = 10;
        Long id = null;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = user.getId();
            }
        }
        int rows = userMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .open()
                .col(User.ID_long).eq(id)
                .or(User.ID_long).eq(id + 1)
                .close());
        Assert.assertEquals(rows, 2);

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, total - 2);
    }

    @Test
    public void test_sqlWhere2() {
        // 测试用例：插入 10 条数据，查询总数，输入 null
        // 期望结果：
        //      查询总数 = 10
        final int total = 10;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
        }

        long count = userMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_sqlWhere2_2() {
        // 测试用例：插入 10 条数据，查询总数根据 ID，输入没有 or
        // 期望结果：
        //      查询总数 = 1
        final int total = 10;
        long id = 0L;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            id = user.getId();
        }
        long count = userMapper.countByWhere(null, null, new DefaultWhere()
                .col(User.ID_long).eq(id));
        Assert.assertEquals(count, 1L);
    }

    @Test
    public void test_sqlWhere2_3() {
        // 测试用例：插入 10 条数据，根据 2 个 ID 查询总数，输入在括号外的 or
        // 期望结果：
        //      查询总数 = 2
        final int total = 10;
        Long id = null;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = user.getId();
            }
        }
        long count = userMapper.countByWhere(null, null, new DefaultWhere()
                .col(User.ID_long).eq(id)
                .or(User.ID_long).eq(id + 1));
        Assert.assertEquals(count, 2L);
    }

    @Test
    public void test_sqlWhere2_4() {
        // 测试用例：插入 10 条数据，根据 2 个 ID 查询总数，输入在括号内的 or
        // 期望结果：
        //      查询总数 = 2
        final int total = 10;
        Long id = null;
        for (int i = 0; i < total; i++) {
            User user = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = user.getId();
            }
        }
        long count = userMapper.countByWhere(null, null, new DefaultWhere()
                .open()
                .col(User.ID_long).eq(id)
                .or(User.ID_long).eq(id + 1)
                .close());
        Assert.assertEquals(count, 2L);
    }
}
