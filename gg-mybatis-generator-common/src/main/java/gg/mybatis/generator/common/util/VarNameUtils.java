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

/**
 * @author Zhong
 * @since 0.0.2
 */
public class VarNameUtils {
    private VarNameUtils() {
    }

    /**
     * 驼峰命名转下划线命名
     *
     * @param name 变量名
     * @return null 如果变量名空
     */
    public static String camelCaseToUnderScoreCase(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if (name.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0) {
                sb.append(toLowerCase(c));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append('_').append(toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static char toLowerCase(char c) {
        if (c < 'A' || c > 'Z') {
            return c;
        }

        final int i = 'a' - 'A';
        return (char) (c + i);
    }
}
