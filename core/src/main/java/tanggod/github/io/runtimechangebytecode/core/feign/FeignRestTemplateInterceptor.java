/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tanggod.github.io.runtimechangebytecode.core.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import tanggod.github.io.common.utils.GsonUtils;
import tanggod.github.io.runtimechangebytecode.core.jwt.JWT;

import java.util.Collection;
import java.util.Objects;

/**
 * HmilyRestTemplateInterceptor.
 *
 * @author xiaoyu
 */
@Configuration
//每次接口调用都会先执行这个请求拦截器
public class FeignRestTemplateInterceptor implements RequestInterceptor {

    @Autowired(required = false)
    private JWT jwt;

    @Override
    public void apply(final RequestTemplate requestTemplate) {
        if (Objects.isNull(jwt))
            return;
        //当前事物上下文
        Object token = jwt.getToken();
        //把token 放进请求的head中
        requestTemplate.header(jwt.getHeaderTokenKey(), GsonUtils.getInstance().toJson(token));
    }

}
