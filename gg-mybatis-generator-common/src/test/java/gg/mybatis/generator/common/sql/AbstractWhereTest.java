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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Zhong
 * @since 0.0.1
 */
@PrepareForTest({DefaultWhere.class})
@RunWith(PowerMockRunner.class)
public class AbstractWhereTest {
    private static final String COLUMN = "name";
    private static final String VALUE = "value";
    private static final String VALUE2 = "value2";
    private static final String OR_OPEN = "(";
    private static final String OR_CLOSE = ")";

    private static void assertSqlWhereException(Consumer<Where> consumer) {
        try {
            consumer.accept(new DefaultWhere());
            Assert.fail("没有出现预期的 " + SqlWhereException.class.getSimpleName() + " 异常");
        } catch (SqlWhereException e) {
            Assert.assertTrue(e instanceof SqlWhereException);
        }
    }

    @Test
    public void checkInitState() {
        // 测试用例：new 一个 DefaultWhere 对象
        // 期望结果：
        //      whereParamName = "where"
        //      brackets = 0
        //      addBrackets = false
        //      whereSql = null
        //      paramNumber = 0
        //      paramList = null
        Where where = new DefaultWhere();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "whereParamName");
            Assert.assertEquals(field.get(where), "where");

            field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), false);

            field = PowerMockito.field(DefaultWhere.class, "whereSql");
            Assert.assertNull(field.get(where));

            field = PowerMockito.field(DefaultWhere.class, "paramList");
            Assert.assertNull(field.get(where));
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void test_abstractWhereClear() {
        // 测试用例：调用方法，使各个全局变量初始值变更
        // 期望结果：
        //      whereParamName = null
        //      brackets = 1
        //      addBrackets = true
        //      whereSql = "(name = #{paramList[0]}) OR name = #{paramList[1]} OR ("
        //      paramNumber = 2
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .withoutParamAnnotation()
                .open()
                .col(COLUMN).eq(VALUE)
                .close()
                .or(COLUMN).eq(VALUE2)
                .or()
                .open();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "whereParamName");
            Assert.assertNull(field.get(where));

            field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 1);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), true);

            field = PowerMockito.field(DefaultWhere.class, "whereSql");
            StringBuilder whereSql = (StringBuilder) field.get(where);
            Assert.assertNotNull(whereSql);
            Assert.assertEquals(whereSql.toString(), OR_OPEN + "(" + COLUMN + " = #{paramList[0]}) OR " + COLUMN + " = #{paramList[1]} OR (" + OR_CLOSE);

            field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertNotNull(paramList);
            Assert.assertEquals(paramList.size(), 2);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：在上一个测试用例执行后，调用 clear() 方法
        // 期望结果：各个值恢复初始状态
        //      whereParamName = "where"
        //      brackets = 0
        //      addBrackets = false
        //      whereSql.length() = 0
        //      paramNumber = 0
        //      paramList.size() = 0
        where.clear();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "whereParamName");
            Assert.assertEquals(field.get(where), "where");

            field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), false);

            field = PowerMockito.field(DefaultWhere.class, "whereSql");
            StringBuilder whereSql = (StringBuilder) field.get(where);
            Assert.assertNotNull(whereSql);
            Assert.assertEquals(whereSql.length(), 0);

            field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertNotNull(paramList);
            Assert.assertEquals(paramList.size(), 0);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 clear() 方法
        // 期望结果：abstractWhereClear() 方法被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "abstractWhereClear");

            where.clear();

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("abstractWhereClear");
        } catch (Exception e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void test_noWhereParamName() {
        // 测试用例：nwe DefaultWhere 对象
        // 期望结果：whereParamName = "where"
        Where where = new DefaultWhere();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "whereParamName");
            Assert.assertEquals(field.get(where), "where");

            // 测试用例：在上一个用例执行后，调用 withoutParamAnnotation()
            // 期望结果：whereParamName = null
            where.withoutParamAnnotation();
            Assert.assertEquals(field.get(where), null);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 withoutParamAnnotation() 方法
        // 期望结果：noWhereParamName() 方法被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "noWhereParamName");

            where.withoutParamAnnotation();

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("noWhereParamName");
        } catch (Exception e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void test_toString() {
        // 测试用例：new DefaultWhere 对象
        // 期望结果：toString() = ""
        Where where = new DefaultWhere();
        Assert.assertEquals(where.toString(), "");

        // 测试用例：在上一个用例执行后，调用 .open().col("name").eq("value").or("name").eq("value2").close()
        // 期望结果：toString() = "(name = #{where.paramList[0]} OR name = #{where.paramList[1]})"
        where.open()
                .col(COLUMN).eq(VALUE)
                .or(COLUMN).eq(VALUE2)
                .close();
        Assert.assertEquals(where.toString(), "(" + COLUMN + " = #{where.paramList[0]} OR " + COLUMN + " = #{where.paramList[1]})");

        // 测试用例：设置 whereSql = "value"
        // 期望结果：toString() = "value"
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "whereSql");
            field.set(where, new StringBuilder(VALUE));
            Assert.assertEquals(where.toString(), VALUE);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void test_incrementBrackets() {
        // 测试用例：new DefaultWhere 对象
        // 期望结果：brackets = 0
        Where where = new DefaultWhere();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            // 测试用例：在上一个用例执行后，调用 open() 方法
            // 期望结果：brackets = 1
            where.open();
            Assert.assertEquals(field.get(where), 1);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 open() 方法
        // 期望结果：incrementBrackets() 方法被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "incrementBrackets");

            where.open();

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("incrementBrackets");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_decrementBrackets() {
        // 测试用例：new DefaultWhere 对象
        // 期望结果：brackets = 0
        Where where = new DefaultWhere();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            // 测试用例：在上一个测试用例执行后，调用 close() 方法
            // 期望结果：brackets = -1
            where.close();
            Assert.assertEquals(field.get(where), -1);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 close() 方法
        // 期望结果：decrementBrackets() 方法被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "decrementBrackets");

            where.close();

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("decrementBrackets");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_checkAndSetAddBrackets() {
        // 测试用例：new DefaultWhere 对象
        // 期望结果：
        //      brackets = 0
        //      addBrackets = false
        Where where = new DefaultWhere();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), false);

            // 测试用例：在上一个测试用例执行后，调用 or() 方法
            // 期望结果：
            //      brackets = 0
            //      addBrackets = true
            where.or();

            field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 0);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), true);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：new DefaultWhere 对象，调用 open() 方法
        // 期望结果：
        //      brackets = 1
        //      addBrackets = false
        where = new DefaultWhere()
                .open();
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 1);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), false);

            // 测试用例：在上一个测试用例执行后，调用 or() 方法
            // 期望结果：
            //      brackets = 1
            //      addBrackets = false
            where.or();

            field = PowerMockito.field(DefaultWhere.class, "brackets");
            Assert.assertEquals(field.get(where), 1);

            field = PowerMockito.field(DefaultWhere.class, "addBrackets");
            Assert.assertEquals(field.get(where), false);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 or() 方法
        // 期望结果：checkAndSetAddBrackets() 方法被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "checkAndSetAddBrackets");

            where.or();

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("checkAndSetAddBrackets");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_appendWhereSql() {
        // 测试用例：传入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(null));

        // 测试用例：传入 ""
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(""));

        // 测试用例：传入 " "
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(" "));

        // 测试用例：whereSql 长度 0，传入 "name"
        // 期望结果：输出 "name"
        Assert.assertEquals(new DefaultWhere().col(COLUMN).toString(), COLUMN);

        // 测试用例：whereSql 初始值 "("，传入 "name"
        // 期望结果：输出 "(name"
        Assert.assertEquals(new DefaultWhere().open().col(COLUMN).toString(), "(" + COLUMN);

        // 测试用例：whereSql 初始值 "name"，传入 ")"
        // 期望结果：输出 "name)"
        Assert.assertEquals(new DefaultWhere().col(COLUMN).close().toString(), COLUMN + ")");

        // 测试用例：whereSql 初始值 "name"，传入 ","
        // 期望结果：输出 "name,"
        Assert.assertEquals(new DefaultWhere().col(COLUMN).col(",").toString(), COLUMN + ",");

        // 测试用例：whereSql 初始值 "AND"，传入 "name"
        // 期望结果：输出 "AND name"
        Assert.assertEquals(new DefaultWhere().and(COLUMN).toString(), "AND " + COLUMN);

        // 测试用例：调用 col(String) 方法
        // 期望结果：appendWhereSql 被调用 1 次
        Where where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "appendWhereSql", COLUMN);

            where.col(COLUMN);

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("appendWhereSql", COLUMN);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_appendWhereSqlParam() {
        // 测试用例：传入"null"
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.eq(null));

        // 测试用例：传入 eq("value").eq("value2")
        // 期望结果：输出
        //      whereSql = "= #{where.paramList[0]} = #{where.paramList[1]}"
        //      paramNumber = 2
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere().eq(VALUE).eq(VALUE2);
        Assert.assertEquals(where.toString(), "= #{where.paramList[0]} = #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertNotNull(paramList);
            Assert.assertEquals(paramList.size(), 2);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：调用 eq(Object) 方法
        // 期望结果：appendWhereSqlParam 被调用 1 次
        where = PowerMockito.spy(new DefaultWhere());
        try {
            PowerMockito.doNothing().when(where, "appendWhereSqlParam", VALUE);

            where.eq(VALUE);

            PowerMockito.verifyPrivate(where, Mockito.times(1)).invoke("appendWhereSqlParam", VALUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
