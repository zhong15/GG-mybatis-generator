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

package gg.mybatis.generator.common.util;

import gg.mybatis.generator.common.exception.SqlWhereException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zhong
 * @since 0.0.1
 */
public class SqlUtils {
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    private SqlUtils() {
    }

    public static long offset(int page, int size) {
        return (page - 1) * size;
    }

    public static boolean existsPage(long count, int page, int size) {
        return (page - 1) * size < count;
    }

    public static String orderBy(String... ss) {
        if (ss == null || ss.length == 0) {
            throw new SqlWhereException("order by ss error");
        }
        StringBuilder sb = new StringBuilder();
        for (String e : ss) {
            if (StringUtils.isBlank(e)) {
                throw new SqlWhereException("order by ss error");
            }
            e = e.trim();
            if (e.equalsIgnoreCase(ASC) || e.equalsIgnoreCase(DESC)) {
                sb.append(" ").append(e.toUpperCase());
            } else {
                sb.append(", ").append(e);
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        if (sb.charAt(0) == ',') {
            return sb.substring(2);
        } else {
            throw new SqlWhereException("order by ss error: " + sb);
        }
    }
}
