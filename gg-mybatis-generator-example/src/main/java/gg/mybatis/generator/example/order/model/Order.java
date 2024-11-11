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

import gg.mybatis.generator.common.domain.BaseEntity;

/**
 * 订单表
 * <p>
 * t_order
 */
public class Order extends BaseEntity implements java.io.Serializable {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer state;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 地址
     */
    private String address;

    private static final long serialVersionUID = 1L;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(super.getId());
        sb.append(", orderNo=").append(orderNo);
        sb.append(", state=").append(state);
        sb.append(", userId=").append(userId);
        sb.append(", address=").append(address);
        sb.append(", createTime=").append(super.getCreateTime());
        sb.append(", updateTime=").append(super.getUpdateTime());
        sb.append(", isDeleted=").append(super.getIsDeleted());
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Order other = (Order) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getState() == null ? other.getState() == null : this.getState().equals(other.getState()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getState() == null) ? 0 : getState().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        // super.setId(null);
        this.orderNo = null;
        this.state = null;
        this.userId = null;
        this.address = null;
        // super.setCreateTime(null);
        // super.setUpdateTime(null);
        // super.setIsDeleted(null);
    }

    public enum ColumnEnum {
        /**
         * 主键，类型：Long
         */
        id,
        /**
         * 订单编号，类型：String
         */
        order_no,
        /**
         * 订单状态，类型：Integer
         */
        state,
        /**
         * 用户ID，类型：Long
         */
        user_id,
        /**
         * 地址，类型：String
         */
        address,
        /**
         * 创建时间，类型：java.util.Date
         */
        create_time,
        /**
         * 修改时间，类型：java.util.Date
         */
        update_time,
        /**
         * 是否删除，0：否，1：是，类型：Byte
         */
        is_deleted;

        public static boolean isColumn(String column) {
            if (column == null || column.length() == 0) {
                return false;
            }
            column = column.toLowerCase();
            return column.equals("id")
                    || column.equals("order_no")
                    || column.equals("state")
                    || column.equals("user_id")
                    || column.equals("address")
                    || column.equals("create_time")
                    || column.equals("update_time")
                    || column.equals("is_deleted");
        }
    }
}