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
package com.dicfree.pda.base.bean;

/**
 * Desc: 网关、环境相关的配置放到这里
 **/
public class GatewayFamily {
    BuildEnv env;
    GatewayConfig projectGateway;    ///网关配置

    public GatewayFamily() {
        projectGateway = new GatewayConfig();
    }

    public GatewayConfig getProjectGateway() {
        return projectGateway;
    }

    public GatewayFamily setProjectGateway(GatewayConfig projectGateway) {
        this.projectGateway = projectGateway;
        return this;
    }

    public BuildEnv getEnv() {
        return env;
    }

    public GatewayFamily setEnv(BuildEnv env) {
        this.env = env;
        return this;
    }
}
