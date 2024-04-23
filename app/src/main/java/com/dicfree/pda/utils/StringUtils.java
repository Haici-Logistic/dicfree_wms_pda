/**
 * Copyright 2024 Wuhan Haici Technology Co., Ltd 
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dicfree.pda.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/11/4
 */
public class StringUtils {
    /**
     * 判断是否货架号
     * @param str
     * @return
     */
    public static boolean isShelfCode(String str){
        String rule ="[a-zA-Z0-9]{2}-\\d{2}-\\d{2}$";
        return matchRex(str,rule);
    }

    public static boolean matchRex(String src, String rex) {
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(src);
        return m.matches();
    }
}
