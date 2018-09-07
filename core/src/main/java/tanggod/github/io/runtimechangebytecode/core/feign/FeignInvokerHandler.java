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

import feign.InvocationHandlerFactory.MethodHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * HmilyFeignHandler.
 *
 * @author xiaoyu
 */
//实现了InvocationHandler接口  应该是根据 接口类型  来自动注入相应的HmilyFeignHandler
//不同的接口 有不同的HmilyFeignHandler
public class FeignInvokerHandler implements InvocationHandler {

    private Map<Method, MethodHandler> handlers;//项目初始化时候 会赋值  实现了@FeignClient 注解的class  当前class里的接口信息都存在这

    //每个方法都使用 HmilyFeignHandler代理
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        //Object则直接执行方法
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            //不是 事物方法的话  则直接执行
            return this.handlers.get(method).invoke(args);
        }
    }

    /**
     * set handlers.
     *
     * @param handlers handlers
     */
    public void setHandlers(final Map<Method, MethodHandler> handlers) {
        this.handlers = handlers;
    }

}
