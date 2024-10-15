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

package gg.mybatis.generator.example.user.model;

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
public class UserTest {
    @Test
    public void test_clear() {
        User user = new User();

        Assert.assertNull(user.getId());
        Assert.assertNull(user.getNickname());
        Assert.assertNull(user.getEmail());
        Assert.assertNull(user.getPassword());
        Assert.assertNull(user.getIsEnable());
        Assert.assertNull(user.getCreateTime());
        Assert.assertNull(user.getUpdateTime());

        user.setId(1L);
        user.setNickname("123");
        user.setEmail("456");
        user.setPassword("789");
        user.setIsEnable((byte) 0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getNickname());
        Assert.assertNotNull(user.getEmail());
        Assert.assertNotNull(user.getPassword());
        Assert.assertNotNull(user.getIsEnable());
        Assert.assertNotNull(user.getCreateTime());
        Assert.assertNotNull(user.getUpdateTime());

        user.clear();

        Assert.assertNull(user.getId());
        Assert.assertNull(user.getNickname());
        Assert.assertNull(user.getEmail());
        Assert.assertNull(user.getPassword());
        Assert.assertNull(user.getIsEnable());
        Assert.assertNull(user.getCreateTime());
        Assert.assertNull(user.getUpdateTime());
    }

    @Test
    public void test_toString() {
        User user = new User();

        user.setId(1L);
        user.setNickname("123");
        user.setEmail("456");
        user.setPassword("789");
        user.setIsEnable((byte) 0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsEnable((byte) 0);

        StringBuilder sb = new StringBuilder();
        sb.append(user.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(user.hashCode());
        sb.append(", id=").append(user.getId());
        sb.append(", nickname=").append(user.getNickname());
        sb.append(", email=").append(user.getEmail());
        sb.append(", password=").append(user.getPassword());
        sb.append(", isEnable=").append(user.getIsEnable());
        sb.append(", createTime=").append(user.getCreateTime());
        sb.append(", updateTime=").append(user.getUpdateTime());
        sb.append(", isDeleted=").append(user.getIsDeleted());
        sb.append(", serialVersionUID=").append(1);
        sb.append("]");

        Assert.assertEquals(user.toString(), sb.toString());
    }

    @Test
    public void test_isColumn() {
        Assert.assertFalse(User.isColumn(null));
        Assert.assertFalse(User.isColumn(""));
        Assert.assertFalse(User.isColumn("   "));
        Assert.assertFalse(User.isColumn("xx"));
        Assert.assertFalse(User.isColumn(" xx "));

        Assert.assertTrue(User.isColumn("id"));
        Assert.assertTrue(User.isColumn("nickname"));
        Assert.assertTrue(User.isColumn("email"));
        Assert.assertTrue(User.isColumn("password"));
        Assert.assertTrue(User.isColumn("is_enable"));
        Assert.assertTrue(User.isColumn("create_time"));
        Assert.assertTrue(User.isColumn("update_time"));

        Assert.assertTrue(User.isColumn("ID"));
        Assert.assertTrue(User.isColumn("NICKNAME"));
        Assert.assertTrue(User.isColumn("EMAIL"));
        Assert.assertTrue(User.isColumn("PASSWORD"));
        Assert.assertTrue(User.isColumn("IS_ENABLE"));
        Assert.assertTrue(User.isColumn("CREATE_TIME"));
        Assert.assertTrue(User.isColumn("UPDATE_TIME"));
    }
}
