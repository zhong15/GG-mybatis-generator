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

package gg.mybatis.generator.example.user.service.impl;

import gg.mybatis.generator.common.sql.DefaultWhere;
import gg.mybatis.generator.common.util.SqlUtils;
import gg.mybatis.generator.example.user.mapper.UserMapper;
import gg.mybatis.generator.example.user.model.User;
import gg.mybatis.generator.example.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static gg.mybatis.generator.common.util.SqlUtils.DESC;

/**
 * @author Zhong
 * @since 0.0.1
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void test() {
        final String searchKey = "foo";

        List<String> columnList = Arrays.asList(User.ID_long, User.NICKNAME_str);

        final int pageNum = 1;
        final int pageSize = 10;

        // SELECT a.id , a.nickname
        // ...
        // WHERE nickname LIKE ? OR nickname LIKE ?
        // ORDER BY nickname DESC, id DESC
        // LIMIT ?, ?
        // %foo%(String), %foo%(String), 0(Long), 10(Integer)
        List<User> list = userMapper.selectByWherePageIdIn(columnList,
                new DefaultWhere()
                        .col(User.NICKNAME_str).like("%" + searchKey + "%")
                        .or(User.NICKNAME_str).like("%" + searchKey + "%"),
                SqlUtils.orderBy(User.NICKNAME_str, DESC, User.ID_long, DESC),
                SqlUtils.offset(pageNum, pageSize),
                pageSize);

        log.info("user number: {}", list.size());

        // SELECT a.id , a.nickname
        // ...
        // WHERE id >= ? AND (nickname LIKE ? OR nickname LIKE ?)
        // ORDER BY nickname DESC, id DESC
        // LIMIT ?, ?
        // 1(Long), %foo%(String), %foo%(String), 0(Long), 10(Integer)
        list = userMapper.selectByWherePageIdIn(columnList,
                new DefaultWhere()
                        .col(User.ID_long).gte(1L)
                        .and()
                        .open()
                        .col(User.NICKNAME_str).like("%" + searchKey + "%")
                        .or(User.NICKNAME_str).like("%" + searchKey + "%")
                        .close(),
                SqlUtils.orderBy(User.NICKNAME_str, DESC, User.ID_long, DESC),
                SqlUtils.offset(pageNum, pageSize),
                pageSize);

        log.info("user number: {}", list.size());
    }
}
