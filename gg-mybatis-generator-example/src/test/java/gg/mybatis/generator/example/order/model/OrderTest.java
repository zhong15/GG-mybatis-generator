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

package gg.mybatis.generator.example.order.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

/**
 * @author Zhong
 * @since 0.0.1
 */
@RunWith(JUnit4.class)
public class OrderTest {
    @Test
    public void test_clear() {
        Order order = new Order();

        Assert.assertNull(order.getId());
        Assert.assertNull(order.getOrderNo());
        Assert.assertNull(order.getState());
        Assert.assertNull(order.getUserId());
        Assert.assertNull(order.getAddress());
        Assert.assertNull(order.getCreateTime());
        Assert.assertNull(order.getUpdateTime());
        Assert.assertNull(order.getIsDeleted());

        order.setId(1L);
        order.setOrderNo("123");
        order.setState(456);
        order.setUserId(789L);
        order.setAddress("fff");
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setIsDeleted((byte) 0);

        Assert.assertNotNull(order.getId());
        Assert.assertNotNull(order.getOrderNo());
        Assert.assertNotNull(order.getState());
        Assert.assertNotNull(order.getUserId());
        Assert.assertNotNull(order.getAddress());
        Assert.assertNotNull(order.getCreateTime());
        Assert.assertNotNull(order.getUpdateTime());
        Assert.assertNotNull(order.getIsDeleted());

        order.clear();

        Assert.assertNull(order.getId());
        Assert.assertNull(order.getOrderNo());
        Assert.assertNull(order.getState());
        Assert.assertNull(order.getUserId());
        Assert.assertNull(order.getAddress());
        Assert.assertNull(order.getCreateTime());
        Assert.assertNull(order.getUpdateTime());
        Assert.assertNull(order.getIsDeleted());
    }

    @Test
    public void test_toString() {
        Order order = new Order();

        order.setId(1L);
        order.setOrderNo("123");
        order.setState(456);
        order.setUserId(789L);
        order.setAddress("fff");
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setIsDeleted((byte) 0);

        StringBuilder sb = new StringBuilder();
        sb.append(order.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(order.hashCode());
        sb.append(", id=").append(order.getId());
        sb.append(", orderNo=").append(order.getOrderNo());
        sb.append(", state=").append(order.getState());
        sb.append(", userId=").append(order.getUserId());
        sb.append(", address=").append(order.getAddress());
        sb.append(", createTime=").append(order.getCreateTime());
        sb.append(", updateTime=").append(order.getUpdateTime());
        sb.append(", isDeleted=").append(order.getIsDeleted());
        sb.append(", serialVersionUID=").append(1);
        sb.append("]");

        Assert.assertEquals(order.toString(), sb.toString());
    }

    @Test
    public void test_isColumn() {
        Assert.assertFalse(Order.isColumn(null));
        Assert.assertFalse(Order.isColumn(""));
        Assert.assertFalse(Order.isColumn("   "));
        Assert.assertFalse(Order.isColumn("xx"));
        Assert.assertFalse(Order.isColumn(" xx "));

        Assert.assertTrue(Order.isColumn("id"));
        Assert.assertTrue(Order.isColumn("order_no"));
        Assert.assertTrue(Order.isColumn("state"));
        Assert.assertTrue(Order.isColumn("user_id"));
        Assert.assertTrue(Order.isColumn("address"));
        Assert.assertTrue(Order.isColumn("create_time"));
        Assert.assertTrue(Order.isColumn("update_time"));
        Assert.assertTrue(Order.isColumn("is_deleted"));

        Assert.assertTrue(Order.isColumn("ID"));
        Assert.assertTrue(Order.isColumn("ORDER_NO"));
        Assert.assertTrue(Order.isColumn("STATE"));
        Assert.assertTrue(Order.isColumn("USER_ID"));
        Assert.assertTrue(Order.isColumn("ADDRESS"));
        Assert.assertTrue(Order.isColumn("CREATE_TIME"));
        Assert.assertTrue(Order.isColumn("UPDATE_TIME"));
        Assert.assertTrue(Order.isColumn("IS_DELETED"));
    }
}
