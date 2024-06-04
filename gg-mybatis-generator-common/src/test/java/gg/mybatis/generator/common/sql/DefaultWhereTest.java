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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Zhong
 * @since 0.0.1
 */
//@PrepareForTest({DefaultWhere.class})
@RunWith(PowerMockRunner.class)
public class DefaultWhereTest {
    private static final String COLUMN = "name";
    private static final String VALUE = "value";
    private static final String VALUE2 = "value2";

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
        // 当前类是子类，无全局变量

        // 测试用例：
        // 期望结果：
    }

    @Test
    public void clear() {
        // 当前类是子类，无全局变量

        // 测试用例：
        // 期望结果：
    }

    @Test
    public void withoutParamAnnotation() {
        // 测试用例：new DefaultWhere
        // 期望结果：whereParamName = "where"
        Where where = new DefaultWhere();
        Field field = PowerMockito.field(DefaultWhere.class, "whereParamName");
        try {
            Assert.assertEquals(field.get(where), "where");
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：在上一个测试用例执行后，调用 withoutParamAnnotation()
        // 期望结果：whereParamName = null
        where.withoutParamAnnotation();
        try {
            Assert.assertNull(field.get(where));
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：在上一个测试用例执行后，调用 col("name").eq("value").or("name").eq("value2")
        // 期望结果：
        //      toString() = "name = #{paramList[0]} OR name = #{paramList[1]}"
        //      paramList = ["value", "value2"]
        where.col(COLUMN).eq(VALUE)
                .or(COLUMN).eq(VALUE2);

        Assert.assertEquals(where.toString(), COLUMN + " = #{paramList[0]} OR " + COLUMN + " = #{paramList[1]}");
        try {
            field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void open() {
        // 测试用例：new DefaultWhere 对象，调用 open() 方法
        // 期望结果：
        //      toString() = "("
        //      brackets = 1
        Where where = new DefaultWhere()
                .open();
        Assert.assertEquals(where.toString(), "(");
        Field field = PowerMockito.field(DefaultWhere.class, "brackets");
        try {
            Assert.assertEquals(field.get(where), 1);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void close() {
        // 测试用例：new DefaultWhere 对象，调用 close() 方法
        // 期望结果：
        //      toString() = ")"
        //      brackets = -1
        Where where = new DefaultWhere()
                .close();
        Assert.assertEquals(where.toString(), ")");
        Field field = PowerMockito.field(DefaultWhere.class, "brackets");
        try {
            Assert.assertEquals(field.get(where), -1);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void col() {
        // String column

        // 测试用例：col(String) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(null));

        // 测试用例：col(String) 输入 ""
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(""));

        // 测试用例：col(String) 输入 " "
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.col(" "));

        // 测试用例：new DefaultWhere 对象，调用 col("name")
        // 期望结果：toString() = "name"
        Assert.assertEquals(new DefaultWhere().col(COLUMN).toString(), COLUMN);
    }

    @Test
    public void and() {
        // 测试用例：new DefaultWhere 对象，调用 and() 方法
        // 期望结果：toString() = "AND"
        Assert.assertEquals(new DefaultWhere().and().toString(), "AND");
    }

    @Test
    public void and2() {
        // String column

        // 测试用例：and(String) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.and(null));

        // 测试用例：and(String) 输入 ""
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.and(""));

        // 测试用例：and(String) 输入 " "
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.and(" "));

        // 测试用例：new DefaultWhere 对象，调用 and("name")
        // 期望结果：toString() = "AND name"
        Assert.assertEquals(new DefaultWhere().and(COLUMN).toString(), "AND " + COLUMN);
    }

    @Test
    public void or() {
        // 测试用例：new DefaultWhere 对象，调用 or() 方法
        // 期望结果：
        //      toString() = "OR"
        //      addBrackets = true
        Where where = new DefaultWhere()
                .or();
        Assert.assertEquals(where.toString(), "OR");
        Field field = PowerMockito.field(DefaultWhere.class, "addBrackets");
        try {
            Assert.assertEquals(field.get(where), true);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：new DefaultWhere 对象，调用 open().or() 方法
        // 期望结果：
        //      toString() = "(OR"
        //      addBrackets = false
        where = new DefaultWhere()
                .open()
                .or();
        Assert.assertEquals(where.toString(), "(OR");
        field = PowerMockito.field(DefaultWhere.class, "addBrackets");
        try {
            Assert.assertEquals(field.get(where), false);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void or2() {
        // String column

        // 测试用例：or(String) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.or(null));

        // 测试用例：or(String) 输入 ""
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.or(""));

        // 测试用例：or(String) 输入 " "
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.or(" "));

        // 测试用例：new DefaultWhere 对象，调用 or("name")
        // 期望结果：
        //      toString() = "OR name"
        //      addBrackets = true
        Where where = new DefaultWhere()
                .or(COLUMN);
        Assert.assertEquals(where.toString(), "OR " + COLUMN);
        Field field = PowerMockito.field(DefaultWhere.class, "addBrackets");
        try {
            Assert.assertEquals(field.get(where), true);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }

        // 测试用例：new DefaultWhere 对象，调用 open().or("name")
        // 期望结果：
        //      toString() = "(OR name"
        //      addBrackets = false
        where = new DefaultWhere()
                .open()
                .or(COLUMN);
        Assert.assertEquals(where.toString(), "(OR " + COLUMN);
        field = PowerMockito.field(DefaultWhere.class, "addBrackets");
        try {
            Assert.assertEquals(field.get(where), false);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void isNull() {
        // 测试用例：调用 isNull()
        // 期望结果：toString() = "IS NULL"
        Assert.assertEquals(new DefaultWhere().isNull().toString(), "IS NULL");
    }

    @Test
    public void isNotNull() {
        // 测试用例：调用 isNotNull()
        // 期望结果：toString() = "IS NOT NULL"
        Assert.assertEquals(new DefaultWhere().isNotNull().toString(), "IS NOT NULL");
    }

    @Test
    public void eq() {
        // Object value

        // 测试用例：调用 eq(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.eq(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").eq("value").or("name").eq("value2")
        // 期望结果：
        //      toString() = "name = #{where.paramList[0]} OR name = #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).eq(VALUE)
                .or(COLUMN).eq(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " = #{where.paramList[0]} OR " + COLUMN + " = #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void ne() {
        // Object value

        // 测试用例：调用 ne(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.ne(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").ne("value").or("name").ne("value2")
        // 期望结果：
        //      toString() = "name <> #{where.paramList[0]} OR name <> #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).ne(VALUE)
                .or(COLUMN).ne(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " <> #{where.paramList[0]} OR " + COLUMN + " <> #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void lt() {
        // Object value

        // 测试用例：调用 lt(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.lt(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").lt("value").or("name").lt("value2")
        // 期望结果：
        //      toString() = "name < #{where.paramList[0]} OR name < #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).lt(VALUE)
                .or(COLUMN).lt(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " < #{where.paramList[0]} OR " + COLUMN + " < #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void le() {
        // Object value

        // 测试用例：调用 le(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.le(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").le("value").or("name").le("value2")
        // 期望结果：
        //      toString() = "name <= #{where.paramList[0]} OR name <= #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).le(VALUE)
                .or(COLUMN).le(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " <= #{where.paramList[0]} OR " + COLUMN + " <= #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void gt() {
        // Object value

        // 测试用例：调用 gt(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.gt(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").gt("value").or("name").gt("value2")
        // 期望结果：
        //      toString() = "name > #{where.paramList[0]} OR name > #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).gt(VALUE)
                .or(COLUMN).gt(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " > #{where.paramList[0]} OR " + COLUMN + " > #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void ge() {
        // Object value

        // 测试用例：调用 ge(Object) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.ge(null));

        // 测试用例：new DefaultWhere 对象，调用 col("name").ge("value").or("name").ge("value2")
        // 期望结果：
        //      toString() = "name >= #{where.paramList[0]} OR name >= #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).ge(VALUE)
                .or(COLUMN).ge(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " >= #{where.paramList[0]} OR " + COLUMN + " >= #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void between() {
        // Object value1, Object value2

        // 测试用例：调用 between(Object, Object) 输入 null, null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.between(null, null));

        // 测试用例：调用 between(Object, Object) 输入 "value", null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.between(VALUE, null));

        // 测试用例：调用 between(Object, Object) 输入 null, "value2"
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.between(null, VALUE2));

        // 测试用例：new DefaultWhere 对象，调用 col("name").between("value", "value2").or("name").between("value2", "value")
        // 期望结果：
        //      toString() = "name BETWEEN #{where.paramList[0]} AND #{where.paramList[1]} OR name BETWEEN #{where.paramList[2]} AND #{where.paramList[3]}"
        //      paramList = ["value", "value2", "value2", "value"]
        Where where = new DefaultWhere()
                .col(COLUMN).between(VALUE, VALUE2)
                .or(COLUMN).between(VALUE2, VALUE);
        Assert.assertEquals(where.toString(), COLUMN + " BETWEEN #{where.paramList[0]} AND #{where.paramList[1]} OR " + COLUMN + " BETWEEN #{where.paramList[2]} AND #{where.paramList[3]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
            Assert.assertEquals(paramList.get(2), VALUE2);
            Assert.assertEquals(paramList.get(3), VALUE);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void in() {
        // List<?> list

        // 测试用例：调用 in(List) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.in(null));

        // 测试用例：调用 in(List) 输入 []
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.in(new ArrayList<>()));

        // 测试用例：调用 in(List) 输入 [null]
        // 期望结果：报错 SqlWhereException
        List<?> list = new ArrayList<>();
        list.add(null);
        assertSqlWhereException(e -> e.in(list));

        // 测试用例：调用 in(List) 输入 [null, "value"]
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.in(Arrays.asList(null, VALUE)));

        // 测试用例：调用 in(List) 输入 ["value", null]
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.in(Arrays.asList(VALUE, null)));

        // 测试用例：new DefaultWhere 对象，调用 col("name").in(["value", "value2"]).or("name").in(["value2", "value"])
        // 期望结果：
        //      toString() = "name IN (#{where.paramList[0]}, #{where.paramList[1]}) OR name IN (#{where.paramList[2]}, #{where.paramList[3]})"
        //      paramList = ["value", "value2", "value2", "value"]
        Where where = new DefaultWhere()
                .col(COLUMN).in(Arrays.asList(VALUE, VALUE2))
                .or(COLUMN).in(Arrays.asList(VALUE2, VALUE));
        Assert.assertEquals(where.toString(), COLUMN + " IN (#{where.paramList[0]}, #{where.paramList[1]}) OR " + COLUMN + " IN (#{where.paramList[2]}, #{where.paramList[3]})");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
            Assert.assertEquals(paramList.get(2), VALUE2);
            Assert.assertEquals(paramList.get(3), VALUE);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }

    @Test
    public void like() {
        // String value

        // 测试用例：调用 like(String) 输入 null
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.like(null));

        // 测试用例：调用 like(String) 输入 ""
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.like(""));

        // 测试用例：调用 like(String) 输入 " "
        // 期望结果：报错 SqlWhereException
        assertSqlWhereException(e -> e.like(" "));

        // 测试用例：new DefaultWhere 对象，调用 col("name").like("value").or("name").like("value2")
        // 期望结果：
        //      toString() = "name LIKE #{where.paramList[0]} OR name LIKE #{where.paramList[1]}"
        //      paramList = ["value", "value2"]
        Where where = new DefaultWhere()
                .col(COLUMN).like(VALUE)
                .or(COLUMN).like(VALUE2);
        Assert.assertEquals(where.toString(), COLUMN + " LIKE #{where.paramList[0]} OR " + COLUMN + " LIKE #{where.paramList[1]}");
        try {
            Field field = PowerMockito.field(DefaultWhere.class, "paramList");
            List<Object> paramList = (List<Object>) field.get(where);
            Assert.assertEquals(paramList.get(0), VALUE);
            Assert.assertEquals(paramList.get(1), VALUE2);
        } catch (IllegalAccessException e) {
            Assert.fail("报错了");
        }
    }
}
