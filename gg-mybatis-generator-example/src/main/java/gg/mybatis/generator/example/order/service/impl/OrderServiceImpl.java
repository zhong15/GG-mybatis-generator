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

package gg.mybatis.generator.example.order.service.impl;

import gg.mybatis.generator.common.sql.DefaultWhere;
import gg.mybatis.generator.common.util.SqlUtils;
import gg.mybatis.generator.example.order.mapper.OrderMapper;
import gg.mybatis.generator.example.order.model.Order;
import gg.mybatis.generator.example.order.service.OrderService;
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
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void test() {
        final String searchKey = "20";

        List<String> columnList = Arrays.asList(Order.ID_long, Order.ORDER_NO_str);

        final int pageNum = 1;
        final int pageSize = 10;

        // SELECT a.id , a.order_no
        // ...
        // WHERE ( order_no LIKE ? OR address LIKE ? ) AND is_deleted = 0
        // ORDER BY create_time DESC, id DESC
        // LIMIT ?, ?
        // %20%(String), %20%(String), 0(Long), 10(Integer)
        List<Order> list = orderMapper.selectByWherePageIdIn(columnList,
                new DefaultWhere()
                        .col(Order.ORDER_NO_str).like("%" + searchKey + "%")
                        .or(Order.ADDRESS_str).like("%" + searchKey + "%"),
                SqlUtils.orderBy(Order.CREATE_TIME_date, DESC, Order.ID_long, DESC),
                SqlUtils.offset(pageNum, pageSize),
                pageSize);

        log.info("order number: {}", list.size());

        // SELECT a.id , a.order_no
        // ...
        // WHERE id >= ? AND (order_no LIKE ? OR address LIKE ?) AND is_deleted = 0
        // ORDER BY create_time DESC, id DESC
        // LIMIT ?, ?
        // 1(Long), %20%(String), %20%(String), 0(Long), 10(Integer)
        list = orderMapper.selectByWherePageIdIn(columnList,
                new DefaultWhere()
                        .col(Order.ID_long).ge(1L)
                        .and()
                        .open()
                        .col(Order.ORDER_NO_str).like("%" + searchKey + "%")
                        .or(Order.ADDRESS_str).like("%" + searchKey + "%")
                        .close(),
                SqlUtils.orderBy(Order.CREATE_TIME_date, DESC, Order.ID_long, DESC),
                SqlUtils.offset(pageNum, pageSize),
                pageSize);

        log.info("order number: {}", list.size());
    }
}
