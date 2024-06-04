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

package gg.mybatis.generator.common.domain;

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
public class BaseEntityTest {
    @Test
    public void test_clear() {
        BaseEntity<Long> x = new BaseEntity<>();

        Assert.assertNull(x.getId());
        Assert.assertNull(x.getCreateTime());
        Assert.assertNull(x.getUpdateTime());

        x.setId(1L);
        x.setCreateTime(new Date());
        x.setUpdateTime(new Date());

        Assert.assertNotNull(x.getId());
        Assert.assertNotNull(x.getCreateTime());
        Assert.assertNotNull(x.getUpdateTime());

        x.clear();

        Assert.assertNull(x.getId());
        Assert.assertNull(x.getCreateTime());
        Assert.assertNull(x.getUpdateTime());
    }
}
