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
package com.dicfree.pda.model.bean;

/**
 * 登录相关信息
 */
public class LoginData {
    /**
     * {
     * 	"accessToken": {
     * 		"tokenValue": "qTB6ND0iYjZC1PGJP8oCBxO6pzo1liUuhYtNYlW7gSBp9u3NgMAyB2R96FiZHaHwETn0AamIFvn9Z_Yitv1cWZulIBnU6gBjYkjswncw9d7AXmaJF5KrevXAfLUkDX4V",
     * 		"expiration": "2023-10-30 01:39:59"
     *        },
     * 	"refreshToken": {
     * 		"tokenValue": "3mbv3Iw_sHV-InwcFrrEKDfJ-ZdRCrpeUqT19V9xV2hq3KvY12XIhUqVP_xqOseSqB6YqwRHl4baRnCu7kHy4ZhtEW-m0pLWZBGeIvSSORH0Vs-UeeAWKqfuwtKAkp08",
     * 		"expiration": "2023-11-28 13:39:59"
     *    }
     * }
     */
    private TokenInfo accessToken;
    private TokenInfo refreshToken;

    public LoginData() {
    }

    public LoginData(TokenInfo accessToken, TokenInfo refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public LoginData(String accessToken, String refreshToken) {
        this.accessToken = new TokenInfo(accessToken);
        this.refreshToken = new TokenInfo(refreshToken);
    }

    public TokenInfo getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(TokenInfo accessToken) {
        this.accessToken = accessToken;
    }

    public TokenInfo getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(TokenInfo refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class TokenInfo{
        /**
         * token
         */
        private String tokenValue;
        /**
         * token过期时间
         */
        private String  expiration;

        public TokenInfo() {
        }

        public TokenInfo(String tokenValue) {
            this.tokenValue = tokenValue;
        }

        public TokenInfo(String tokenValue, String expiration) {
            this.tokenValue = tokenValue;
            this.expiration = expiration;
        }

        public String getTokenValue() {
            return tokenValue;
        }

        public void setTokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
        }

        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }
    }

    public LoginData copyWith(String accessToken, String refreshToken) {
        return new LoginData(
                (null == accessToken)? this.accessToken.getTokenValue() : accessToken,
                (null == refreshToken)? this.refreshToken.getTokenValue() : refreshToken
        );
    }

}
