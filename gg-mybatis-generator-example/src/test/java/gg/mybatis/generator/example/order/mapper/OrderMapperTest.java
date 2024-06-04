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

package gg.mybatis.generator.example.order.mapper;

import gg.mybatis.generator.common.domain.BaseEntity;
import gg.mybatis.generator.common.sql.DefaultWhere;
import gg.mybatis.generator.common.util.SqlUtils;
import gg.mybatis.generator.example.Application;
import gg.mybatis.generator.example.order.model.Order;
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
public class OrderMapperTest {
    private static final String PREFIX_ORDER_NO = "orderNo ";
    private static final String PREFIX_ADDRESS = "address ";
    private static final int PREFIX_STATE = 0;
    private static final long PREFIX_USER_ID = 1000L;

    @Autowired
    private OrderMapper orderMapper;

    @Before
    public void setup() {
        orderMapper.deleteByWhere(null);

        long count = orderMapper.countByWhere(null, null, null);
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

    private Order test_insertSelective_core(int suffix, long expectCount) {
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
        //      orderNo 不变
        //      address 不变
        //      state 不变
        //      userId 不变
        //      createTime != null
        //      updateTime != null
        //      isDeleted = 0
        Order order = new Order();
        order.setId(null);
        order.setOrderNo(PREFIX_ORDER_NO + suffix);
        order.setAddress(PREFIX_ADDRESS + suffix);
        order.setState(PREFIX_STATE + suffix);
        order.setUserId(PREFIX_USER_ID + suffix);
        order.setCreateTime(null);
        order.setUpdateTime(null);
        order.setIsDeleted(null);

        int rows = orderMapper.insertSelective(order);
        Assert.assertEquals(rows, 1);

        Assert.assertNotNull(order.getId());
        Assert.assertEquals(order.getOrderNo(), PREFIX_ORDER_NO + suffix);
        Assert.assertEquals(order.getAddress(), PREFIX_ADDRESS + suffix);
        Assert.assertEquals(order.getState().longValue(), PREFIX_STATE + suffix);
        Assert.assertEquals(order.getUserId().longValue(), PREFIX_USER_ID + suffix);
        Assert.assertNull(order.getCreateTime());
        Assert.assertNull(order.getUpdateTime());
        Assert.assertNull(order.getIsDeleted());

        long count = orderMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, expectCount);

        Order savedOrder = assertEqualDBValue(order, false, false, false);

        return savedOrder;
    }

    private Order assertEqualDBValue(Order order, boolean testCreateTime, boolean testUpdateTime, boolean testIsDeleted) {
        Order savedOrder = orderMapper.selectById(order.getId(), null);
        Assert.assertNotNull(savedOrder);
        Assert.assertEquals(order.getId(), savedOrder.getId());
        Assert.assertEquals(order.getOrderNo(), savedOrder.getOrderNo());
        Assert.assertEquals(order.getAddress(), savedOrder.getAddress());
        Assert.assertEquals(order.getState(), savedOrder.getState());
        Assert.assertEquals(order.getUserId(), savedOrder.getUserId());
        Assert.assertNotNull(savedOrder.getCreateTime());
        if (testCreateTime) {
            Assert.assertEquals(order.getCreateTime(), savedOrder.getCreateTime());
        }
        Assert.assertNotNull(savedOrder.getUpdateTime());
        if (testUpdateTime) {
            Assert.assertEquals(order.getUpdateTime(), savedOrder.getUpdateTime());
        }
        Assert.assertEquals(savedOrder.getIsDeleted().intValue(), 0);
        if (testIsDeleted) {
            Assert.assertEquals(order.getIsDeleted(), savedOrder.getIsDeleted());
        }
        return savedOrder;
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
        int rows = orderMapper.deleteById(id_1);
        Assert.assertEquals(rows, 1);

        // 查询 id_1 记录 = null
        Order order = orderMapper.selectById(id_1, null);
        Assert.assertNull(order);

        // 查询 id_2 记录 != null
        order = orderMapper.selectById(id_2, null);
        Assert.assertNotNull(order);

        // 总数 = 1
        long count = orderMapper.countByWhere(null, null, null);
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
        int rows = orderMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(Order.ID_long).eq(id_1));
        Assert.assertEquals(rows, 1);

        // 查询 id_1 记录 = null
        Order order = orderMapper.selectById(id_1, null);
        Assert.assertNull(order);

        // 查询 id_2 记录 != null
        order = orderMapper.selectById(id_2, null);
        Assert.assertNotNull(order);

        // 总数 = 1
        long count = orderMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, 1L);
    }

    @Test
    public void test_updateById() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：null, 指定列：null
        // 期望结果：报错 MyBatisSystemException
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);
        try {
            int rows = orderMapper.updateById(null, null);
            Assert.fail("报错了");
        } catch (MyBatisSystemException e) {
        }
    }

    @Test
    public void test_updateById_2() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, orderNo, 指定列：null
        // 期望结果：
        //      第 1 个数据：orderNo 更新，updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        order1.setOrderNo(order1.getOrderNo() + " update");

        Order updateOrder1 = new Order();
        updateOrder1.setId(order1.getId());
        updateOrder1.setOrderNo(order1.getOrderNo());
        int rows = orderMapper.updateById(updateOrder1, null);
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateById_3() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, 指定列：address
        // 期望结果：
        //      第 1 个数据：address = null，updateTime 更新，其它列不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        Order updateOrder1 = new Order();
        updateOrder1.setId(order1.getId());
        int rows = orderMapper.updateById(updateOrder1, Arrays.asList(Order.ADDRESS_str));
        Assert.assertEquals(rows, 1);

        order1.setAddress(null);
        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateById_4() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList

        // 测试用例：插入 2 条数据，更新条件：第 1 个数据 ID, orderNo，指定列：address
        // 期望结果：
        //      第 1 个数据：orderNo 更新，address = null，updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        order1.setOrderNo(order1.getOrderNo() + " update");

        Order updateOrder1 = new Order();
        updateOrder1.setId(order1.getId());
        updateOrder1.setOrderNo(order1.getOrderNo());
        int rows = orderMapper.updateById(updateOrder1, Arrays.asList(Order.ADDRESS_str));
        Assert.assertEquals(rows, 1);

        order1.setAddress(null);
        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateByWhere() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：null, 指定列：null, 第 1 个数据 ID
        // 期望结果：
        //      更新报错 MyBatisSystemException
        //      第 1 条数据：不变
        //      第 2 条数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        try {
            int rows = orderMapper.updateByWhere(null, null, new DefaultWhere().col(Order.ID_long).eq(order1.getId()));
            Assert.fail("报错列");
        } catch (MyBatisSystemException e) {
        }

        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateByWhere_2() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：orderNo, 指定列：null, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：orderNo 更新，updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        order1.setOrderNo(order1.getOrderNo() + " update");

        Order updateOrder1 = new Order();
        updateOrder1.setId(order1.getId());
        updateOrder1.setOrderNo(order1.getOrderNo());
        int rows = orderMapper.updateByWhere(updateOrder1, null, new DefaultWhere().col(Order.ID_long).eq(order1.getId()));
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateByWhere_3() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：null, 指定列：address, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：address = null，updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        int rows = orderMapper.updateByWhere(new Order(), Arrays.asList(Order.ADDRESS_str), new DefaultWhere().col(Order.ID_long).eq(order1.getId()));
        Assert.assertEquals(rows, 1);

        order1.setAddress(null);
        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateByWhere_4() {
        // @Param("row") T row, @Param("setNullColumnList") List<String> setNullColumnList, @Param("where") Where where

        // 测试用例：插入 2 条数据，执行更新：orderNo, 指定列：address, 第 1 个数据 ID
        // 期望结果：
        //      第 1 个数据：orderNo 更新，address = null, updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        order1.setOrderNo(order1.getOrderNo() + " update");

        Order updateOrder1 = new Order();
        updateOrder1.setId(order1.getId());
        updateOrder1.setOrderNo(order1.getOrderNo());
        int rows = orderMapper.updateByWhere(updateOrder1, Arrays.asList(Order.ADDRESS_str), new DefaultWhere().col(Order.ID_long).eq(order1.getId()));
        Assert.assertEquals(rows, 1);

        order1.setAddress(null);
        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, null, null
        // 期望结果：
        //      更新报错 BadSqlGrammarException
        //      第 1 个数据：不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        try {
            int rows = orderMapper.updateColumnValueById(order1.getId(), null, null);
            Assert.fail("报错了");
        } catch (BadSqlGrammarException e) {
        }

        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_2() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, address, null
        // 期望结果：
        //      第 1 个数据：address = null, updateTime 更新，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        int rows = orderMapper.updateColumnValueById(order1.getId(), Order.ADDRESS_str, null);
        Assert.assertEquals(rows, 1);

        order1.setAddress(null);
        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_3() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, null, "xxxUpdate"
        // 期望结果：
        //      更新报错 BadSqlGrammarException
        //      第 1 个数据：不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        try {
            int rows = orderMapper.updateColumnValueById(order1.getId(), null, "xxxUpdate");
            Assert.fail("报错了");
        } catch (BadSqlGrammarException e) {
        }

        assertEqualDBValue(order1, true, true, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_updateColumnValueById_4() {
        // @Param("id") Long id, @Param("column") String column, @Param("value") Object value

        // 测试用例：插入 2 条数据，执行更新：第 1 个 ID, address, "xxxUpdate"
        // 期望结果：
        //      第 1 个数据：address = "xxxUpdate"，其它不变
        //      第 2 个数据：不变
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        order1.setAddress(order1.getAddress() + "_update");
        int rows = orderMapper.updateColumnValueById(order1.getId(), Order.ADDRESS_str, order1.getAddress());
        Assert.assertEquals(rows, 1);

        assertEqualDBValue(order1, true, false, true);

        assertEqualDBValue(order2, true, true, true);
    }

    @Test
    public void test_selectById() {
        // @Param("id") Long id, @Param("columnList") List<String> columnList

        // 测试用例：插入 2 条数据，执行查询：第 1 个数据 ID, null
        // 期望结果：查出第 1 条数据所有列
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        Order order1_1 = orderMapper.selectById(order1.getId(), null);

        Assert.assertEquals(order1, order1_1);
    }

    @Test
    public void test_selectById_2() {
        // @Param("id") Long id, @Param("columnList") List<String> columnList

        // 测试用例：插入 2 条数据，执行查询：第 1 个数据 ID, id orderNo
        // 期望结果：查出第 1 条数据 id orderNo，其它列为 null
        Order order1 = test_insertSelective_core(1, 1);
        Order order2 = test_insertSelective_core(2, 2);

        Order order1_1 = orderMapper.selectById(order1.getId(), Arrays.asList(Order.ID_long, Order.ORDER_NO_str));

        Order order1_2 = new Order();
        order1_2.setId(order1.getId());
        order1_2.setOrderNo(order1.getOrderNo());
        Assert.assertEquals(order1_1, order1_2);
    }

    @Test
    public void test_selectByWhere() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：distinct null, columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        List<Order> list = orderMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
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
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：distinct true, columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        List<Order> list = orderMapper.selectByWhere(true, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWhere_3() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 orderNo，address 都相同，执行查询：distinct null, columnList：orderNo, address
        // 期望结果：
        //      查出总数 = 10
        //      每个查出的值和插入的值一致，且只查出 orderNo, address
        final int total = 10;
        Map<String, Order> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getOrderNo(), order);
        }
        Assert.assertEquals(map.size(), total - 1);
        Order repeatOorder = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatOorder.getOrderNo()).getAddress(), repeatOorder.getAddress());

        List<String> columnList = Arrays.asList(Order.ORDER_NO_str, Order.ADDRESS_str);
        List<Function<Order, ?>> funcList = Arrays.asList(Order::getOrderNo, Order::getAddress);
        List<Order> list = orderMapper.selectByWhere(null, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            assertSelectColumn(order, columnList);
            assertEqual(order, map.get(order.getOrderNo()), funcList);
        }
    }

    private static void assertSelectColumn(Order order, List<String> columnList) {
        Assert.assertNotNull(order);
        if (columnList.contains(Order.ID_long)) {
            Assert.assertNotNull(order.getId());
        } else {
            Assert.assertNull(order.getId());
        }
        if (columnList.contains(Order.ORDER_NO_str)) {
            Assert.assertNotNull(order.getOrderNo());
        } else {
            Assert.assertNull(order.getOrderNo());
        }
        if (columnList.contains(Order.STATE_int)) {
            Assert.assertNotNull(order.getState());
        } else {
            Assert.assertNull(order.getState());
        }
        if (columnList.contains(Order.USER_ID_long)) {
            Assert.assertNotNull(order.getUserId());
        } else {
            Assert.assertNull(order.getUserId());
        }
        if (columnList.contains(Order.ADDRESS_str)) {
            Assert.assertNotNull(order.getAddress());
        } else {
            Assert.assertNull(order.getAddress());
        }
        if (columnList.contains(Order.CREATE_TIME_date)) {
            Assert.assertNotNull(order.getCreateTime());
        } else {
            Assert.assertNull(order.getCreateTime());
        }
        if (columnList.contains(Order.UPDATE_TIME_date)) {
            Assert.assertNotNull(order.getUpdateTime());
        } else {
            Assert.assertNull(order.getUpdateTime());
        }
        if (columnList.contains(Order.IS_DELETED_byte)) {
            Assert.assertNotNull(order.getIsDeleted());
        } else {
            Assert.assertNull(order.getIsDeleted());
        }
    }

    private static void assertEqual(Order a, Order b, List<Function<Order, ?>> columnList) {
        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        for (Function<Order, ?> func : columnList) {
            Assert.assertEquals(func.apply(a), func.apply(b));
        }
    }

    @Test
    public void test_selectByWhere_4() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 orderNo，address 都相同，执行查询：distinct false, columnList：orderNo, address
        // 期望结果：
        //      查出总数 = 10
        //      每个查出的值和插入的值一致，且只查出 orderNo, address
        final int total = 10;
        Map<String, Order> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getOrderNo(), order);
        }
        Assert.assertEquals(map.size(), total - 1);
        Order repeatOorder = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatOorder.getOrderNo()).getAddress(), repeatOorder.getAddress());

        List<String> columnList = Arrays.asList(Order.ORDER_NO_str, Order.ADDRESS_str);
        List<Function<Order, ?>> funcList = Arrays.asList(Order::getOrderNo, Order::getAddress);
        List<Order> list = orderMapper.selectByWhere(false, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            assertSelectColumn(order, columnList);
            assertEqual(order, map.get(order.getOrderNo()), funcList);
        }
    }

    @Test
    public void test_selectByWhere_5() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，其中 2 条数据 orderNo，address 都相同，执行查询：distinct true, columnList orderNo, address
        // 期望结果：
        //      查出不重复 orderNo, address 总数 = 9
        //      每个查出的值和插入的值一致，且只查出 orderNo, address
        final int total = 10;
        Map<String, Order> map = new HashMap<>();
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getOrderNo(), order);
        }
        Assert.assertEquals(map.size(), total - 1);
        Order repeatOorder = test_insertSelective_core(1, total);
        Assert.assertEquals(map.get(repeatOorder.getOrderNo()).getAddress(), repeatOorder.getAddress());

        List<String> columnList = Arrays.asList(Order.ORDER_NO_str, Order.ADDRESS_str);
        List<Function<Order, ?>> funcList = Arrays.asList(Order::getOrderNo, Order::getAddress);
        List<Order> list = orderMapper.selectByWhere(true, columnList, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total - 1);
        for (Order order : list) {
            Assert.assertNotNull(order);
            assertSelectColumn(order, columnList);
            assertEqual(order, map.get(order.getOrderNo()), funcList);
        }
    }

    @Test
    public void test_selectByWhere_6() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      默认排序，mysql 默认无序
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        List<Order> list = orderMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        long id = 0;
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
//            Assert.assertTrue(id < order.getId());
//            id = order.getId();
        }
    }

    @Test
    public void test_selectByWhere_7() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy "id DESC"
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      排序按 ID 降序
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        List<Order> list = orderMapper.selectByWhere(null, null, null, SqlUtils.orderBy(Order.ID_long, SqlUtils.DESC), null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        Long id = null;
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
            if (id != null) {
                Assert.assertTrue(id > order.getId());
            }
            id = order.getId();
        }
    }

    @Test
    public void test_selectByWhere_8() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset null, rowCount null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        List<Order> list = orderMapper.selectByWhere(null, null, null, null, null, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWhere_9() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 1, rowCount null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 1;
        List<Order> list = orderMapper.selectByWhere(null, null, null, null, offset, null);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWhere_10() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset null, rowCount 5
        // 期望结果：
        //      查出总数 = 5
        //      查出不重复 ID 总数 = 5
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final int rowCount = 5;
        List<Order> list = orderMapper.selectByWhere(null, null, null, null, null, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), rowCount);
        assertCountDistinctId(list, rowCount);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWhere_11() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") Long offset, @Param("rowCount") Integer rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 7, rowCount 5
        // 期望结果：
        //      查出总数 = 3
        //      查出不重复 ID 总数 = 3
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 7;
        final int rowCount = 5;
        List<Order> list = orderMapper.selectByWhere(null, null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        int count = total - offset > rowCount ? rowCount : (int) (total - offset);
        Assert.assertEquals(list.size(), count);
        assertCountDistinctId(list, count);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWherePageIdIn() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：columnList null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 0;
        final int rowCount = total;
        List<Order> list = orderMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_selectByWherePageIdIn_2() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：columnList: id, orderNo
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      只查出 id, orderNo
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 0;
        final int rowCount = total;
        List<String> columnList = Arrays.asList(Order.ID_long, Order.ORDER_NO_str);
        List<Function<Order, ?>> funcList = Arrays.asList(Order::getId, Order::getOrderNo);
        List<Order> list = orderMapper.selectByWherePageIdIn(columnList, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        for (Order order : list) {
            Assert.assertNotNull(order);
            assertSelectColumn(order, columnList);
            assertEqual(order, map.get(order.getId()), funcList);
        }
    }

    @Test
    public void test_selectByWherePageIdIn_3() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy null
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      按 mysql 默认排序规则：无序
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 0;
        final int rowCount = total;
        List<Order> list = orderMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        long id = 0;
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
//            Assert.assertTrue(id < order.getId());
//            id = order.getId();
        }
    }

    @Test
    public void test_selectByWherePageIdIn_4() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：orderBy "id DESC"
        // 期望结果：
        //      查出总数 = 10
        //      查出不重复 ID 总数 = 10
        //      每个查出的值和插入的值一致
        //      按 ID 降序
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 0;
        final int rowCount = total;
        List<Order> list = orderMapper.selectByWherePageIdIn(null, null, SqlUtils.orderBy(Order.ID_long, SqlUtils.DESC), offset, rowCount);
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), total);
        assertCountDistinctId(list, total);
        Long id = null;
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
            if (id != null) {
                Assert.assertTrue(id > order.getId());
            }
            id = order.getId();
        }
    }

    @Test
    public void test_selectByWherePageIdIn_5() {
        // @Param("columnList") List<String> columnList, @Param("where") Where where, @Param("orderBy") String orderBy, @Param("offset") long offset, @Param("rowCount") int rowCount

        // 测试用例：插入 10 条数据，执行查询：offset 7, rowCount 5
        // 期望结果：
        //      查出总数 = 3
        //      查出不重复 ID 总数 = 3
        //      每个查出的值和插入的值一致
        final int total = 10;
        Map<Long, Order> map = new HashMap<>();
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
            map.put(order.getId(), order);
        }

        final long offset = 7;
        final int rowCount = 5;
        List<Order> list = orderMapper.selectByWherePageIdIn(null, null, null, offset, rowCount);
        Assert.assertNotNull(list);
        int count = total - offset > rowCount ? rowCount : (int) (total - offset);
        Assert.assertEquals(list.size(), count);
        assertCountDistinctId(list, count);
        for (Order order : list) {
            Assert.assertNotNull(order);
            Assert.assertEquals(order, map.get(order.getId()));
        }
    }

    @Test
    public void test_countByWhere() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 orderNo, address 相同，执行查询：distinct null, columnList null
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }
        Order order = test_insertSelective_core(1, total);

        long count = orderMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_2() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 orderNo, address 相同，执行查询：distinct true, columnList null
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }
        Order order = test_insertSelective_core(1, total);

        long count = orderMapper.countByWhere(true, null, null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_3() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 orderNo, address 相同，执行查询：distinct null, columnList: orderNo
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }
        Order order = test_insertSelective_core(1, total);

        long count = orderMapper.countByWhere(null, Arrays.asList(Order.ORDER_NO_str), null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_4() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 orderNo, address 相同，执行查询：distinct false, columnList: order_no
        // 期望结果：
        //      查出总数 = 10
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }
        Order order = test_insertSelective_core(1, total);

        long count = orderMapper.countByWhere(false, Arrays.asList(Order.ORDER_NO_str), null);
        Assert.assertEquals(count, (long) total);
    }

    @Test
    public void test_countByWhere_5() {
        // @Param("distinct") Boolean distinct, @Param("columnList") List<String> columnList, @Param("where") Where where

        // 测试用例：插入 10 条数据，其中 2 条 orderNo, address 相同，执行查询：distinct true, columnList: order_no
        // 期望结果：
        //      查出总数 = 9
        final int total = 10;
        for (int i = 0; i < total - 1; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }
        Order order = test_insertSelective_core(1, total);

        long count = orderMapper.countByWhere(true, Arrays.asList(Order.ORDER_NO_str), null);
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
            Order order = test_insertSelective_core(i + 1, i + 1);
        }

        int rows = orderMapper.deleteByWhere(null);
        Assert.assertEquals(rows, total);

        long count = orderMapper.countByWhere(null, null, null);
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            id = order.getId();
        }
        int rows = orderMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(Order.ID_long).eq(id));
        Assert.assertEquals(rows, 1);

        long count = orderMapper.countByWhere(null, null, null);
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = order.getId();
            }
        }
        int rows = orderMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .col(Order.ID_long).eq(id)
                .or(Order.ID_long).eq(id + 1));
        Assert.assertEquals(rows, 2);

        long count = orderMapper.countByWhere(null, null, null);
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = order.getId();
            }
        }
        int rows = orderMapper.deleteByWhere(new DefaultWhere()
                .withoutParamAnnotation()
                .open()
                .col(Order.ID_long).eq(id)
                .or(Order.ID_long).eq(id + 1)
                .close());
        Assert.assertEquals(rows, 2);

        long count = orderMapper.countByWhere(null, null, null);
        Assert.assertEquals(count, total - 2);
    }

    @Test
    public void test_sqlWhere2() {
        // 测试用例：插入 10 条数据，查询总数，输入 null
        // 期望结果：
        //      查询总数 = 10
        final int total = 10;
        for (int i = 0; i < total; i++) {
            Order order = test_insertSelective_core(i + 1, i + 1);
        }

        long count = orderMapper.countByWhere(null, null, null);
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            id = order.getId();
        }
        long count = orderMapper.countByWhere(null, null, new DefaultWhere()
                .col(Order.ID_long).eq(id));
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = order.getId();
            }
        }
        long count = orderMapper.countByWhere(null, null, new DefaultWhere()
                .col(Order.ID_long).eq(id)
                .or(Order.ID_long).eq(id + 1));
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
            Order order = test_insertSelective_core(i + 1, i + 1);
            if (id == null) {
                id = order.getId();
            }
        }
        long count = orderMapper.countByWhere(null, null, new DefaultWhere()
                .open()
                .col(Order.ID_long).eq(id)
                .or(Order.ID_long).eq(id + 1)
                .close());
        Assert.assertEquals(count, 2L);
    }
}
