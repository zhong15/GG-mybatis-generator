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

import gg.mybatis.generator.common.mapper.BaseMapper;
import gg.mybatis.generator.example.order.model.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表 Mapper
 * <p>
 * t_order
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}