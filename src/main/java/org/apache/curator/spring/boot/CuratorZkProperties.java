/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.curator.spring.boot;

import java.util.List;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * https://www.ishumei.com/
 * 
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@ConfigurationProperties(CuratorZkProperties.PREFIX)
@Data
public class CuratorZkProperties {

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;
	public static final String PREFIX = "curator";

	/**
	 * As ZooKeeper is a shared space, users of a given cluster should stay within
         * a pre-defined namespace. If a namespace is set here, all paths will get pre-pended
         * with the namespace
	 */
	private String namespace;
	
	/**
	 * time to wait during close to join background threads
	 */
	private int maxCloseWaitMs;
	
	/**
	 * Set a timeout for {@link CuratorZookeeperClient#close(int)}  }.
         * The default is 0, which means that this feature is disabled.
	 */
	private int waitForShutdownTimeoutMs;
	
	/**
	 * new simulated session expiration percentage
	 */
	private int simulatedSessionExpirationPercent = -1;
	/**
	 * list of servers to connect to;
	 * 192.168.1.1:2100,192.168.1.1:2101,192.168.1.:2102
	 */
	private String connectString;
	
	/**
	 * Allows to configure if the ensemble configuration changes will be watched.
         * The default value is {@code true}.
	 */
	private boolean withEnsembleTracker = true;
	
	/**
	 * 会话超时时间（单位：毫秒），默认 30000
	 */
	private int sessionTimeoutMs = 30000;
	/**
	 * 连接超时时间（单位：毫秒），默认 3000
	 */
	private int connectionTimeoutMs = 3000;
	
	/**
	 * if true, allow ZooKeeper client to enter
     *                      read only mode in case of a network partition. See
     *                      {@link ZooKeeper#ZooKeeper(String, int, Watcher, long, byte[], boolean)}
     *                      for details
	 */
	private boolean canBeReadOnly;

	private List<CuratorAuthInfo> authInfo;
	
	/**
	 * initial amount of time to wait between retries
	 */
	private int baseSleepTimeMs = 1000;
	/**
	 * max number of times to retry
	 */
	private int maxRetries = DEFAULT_MAX_RETRIES; 
	/**
	 * max time in ms to sleep on each retry
	 */
	private int maxSleepMs = DEFAULT_MAX_SLEEP_MS;
	
	@Data
	public class CuratorAuthInfo {
		
		private String    scheme;
		private String    auth;
		
	}

}
