/*
 * Copyright (c) 2019 Baidu, Inc. All Rights Reserved.
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
 
package com.baidu.cloud.starlight.core.rpc.threadpool;

import com.baidu.cloud.starlight.api.common.Constants;
import com.baidu.cloud.starlight.api.common.URI;
import com.baidu.cloud.starlight.api.rpc.RpcService;
import com.baidu.cloud.starlight.api.rpc.config.ServiceConfig;
import com.baidu.cloud.starlight.core.integrate.service.UserService;
import com.baidu.cloud.starlight.core.integrate.service.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by liuruisen on 2020/3/23.
 */
public class RpcThreadPoolFactoryTest {

    private RpcThreadPoolFactory rpcThreadPoolFactory;

    @Before
    public void before() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.MAX_BIZ_WORKER_NUM_KEY, String.valueOf(Constants.DEFAULT_MAX_BIZ_THREAD_POOL_SIZE));
        URI uri = new URI("protocol", "username", "password", "host", 0, "path", parameters);
        rpcThreadPoolFactory = new RpcThreadPoolFactory();
        rpcThreadPoolFactory.initDefaultThreadPool(uri, "test");
    }

    @Test
    public void getThreadPool() {
        RpcService rpcService = new RpcService(UserService.class, new UserServiceImpl());
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInvokeTimeoutMills(1000);
        serviceConfig.setThreadPoolSize(1);
        serviceConfig.setMaxThreadPoolSize(3);
        serviceConfig.setIdleThreadKeepAliveSecond(3);
        serviceConfig.setMaxRunnableQueueSize(100);
        rpcService.setServiceConfig(serviceConfig);
        ThreadPoolExecutor poolExecutor2 = rpcThreadPoolFactory.getThreadPool(rpcService);
        Assert.assertEquals(Constants.DEFAULT_MAX_BIZ_THREAD_POOL_SIZE.intValue(), poolExecutor2.getMaximumPoolSize());
    }

    @Test
    public void testGetThreadPool() {
        ThreadPoolExecutor poolExecutor = rpcThreadPoolFactory.defaultThreadPool();
        Assert.assertEquals(Constants.DEFAULT_BIZ_THREAD_POOL_SIZE.intValue(), poolExecutor.getCorePoolSize());
        Assert.assertEquals(Constants.DEFAULT_MAX_BIZ_THREAD_POOL_SIZE.intValue(), poolExecutor.getMaximumPoolSize());
    }

    @Test
    public void close() {
        RpcService rpcService = new RpcService(UserService.class, new UserServiceImpl());
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setInvokeTimeoutMills(1000);
        serviceConfig.setThreadPoolSize(1);
        serviceConfig.setMaxThreadPoolSize(3);
        serviceConfig.setMaxRunnableQueueSize(100);
        serviceConfig.setIdleThreadKeepAliveSecond(3);
        rpcService.setServiceConfig(serviceConfig);
        ThreadPoolExecutor executor = rpcThreadPoolFactory.getThreadPool(rpcService);
        Assert.assertFalse(executor.isShutdown());
        rpcThreadPoolFactory.close();
        Assert.assertTrue(executor.isShutdown());
    }
}