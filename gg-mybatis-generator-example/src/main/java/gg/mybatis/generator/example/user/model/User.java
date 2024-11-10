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

import gg.mybatis.generator.common.domain.BaseEntity;

/**
 * 用户表
 * <p>
 * t_user
 */
public class User extends BaseEntity implements java.io.Serializable {
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否可用，0：否，1：是
     */
    private Byte isEnable;

    private static final long serialVersionUID = 1L;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Byte getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Byte isEnable) {
        this.isEnable = isEnable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(super.getId());
        sb.append(", nickname=").append(nickname);
        sb.append(", email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", isEnable=").append(isEnable);
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
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getIsEnable() == null ? other.getIsEnable() == null : this.getIsEnable().equals(other.getIsEnable()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getIsEnable() == null) ? 0 : getIsEnable().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        // super.setId(null);
        this.nickname = null;
        this.email = null;
        this.password = null;
        this.isEnable = null;
        // super.setCreateTime(null);
        // super.setUpdateTime(null);
        // super.setIsDeleted(null);
    }

    public static boolean isColumn(String column) {
        if (column == null || column.trim().length() == 0) {
            return false;
        }
        column = column.toLowerCase();
        return column.equals("id")
                || column.equals("nickname")
                || column.equals("email")
                || column.equals("password")
                || column.equals("is_enable")
                || column.equals("create_time")
                || column.equals("update_time")
                || column.equals("is_deleted");
    }

    public enum ColumnEnum {
        /**
         * 主键，类型：Long
         */
        id,
        /**
         * 昵称，类型：String
         */
        nickname,
        /**
         * 电子邮箱，类型：String
         */
        email,
        /**
         * 密码，类型：String
         */
        password,
        /**
         * 是否可用，0：否，1：是，类型：Byte
         */
        is_enable,
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
    }
}