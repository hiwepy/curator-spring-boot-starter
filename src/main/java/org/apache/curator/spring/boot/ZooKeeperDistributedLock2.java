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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperDistributedLock2 implements Watcher {
	
	private CuratorFramework curatorClient;
	private String locksRoot = "/locks";
	private String waitNode;
	private String lockNode;
	private CountDownLatch latch;
	private CountDownLatch connectedLatch = new CountDownLatch(1);
	private int sessionTimeout = 30000;

	public ZooKeeperDistributedLock2(CuratorFramework curatorClient) {
		try {
			this.curatorClient = curatorClient;
			connectedLatch.await();
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
	}

	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			connectedLatch.countDown();
			return;
		}

		if (this.latch != null) {
			this.latch.countDown();
		}
	}

	public void acquireDistributedLock(String productId) {
		try {
			if (this.tryLock(productId)) {
				return;
			} else {
				waitForLock(waitNode, sessionTimeout);
			}
		} catch (KeeperException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		} catch (Exception e) {
			throw new LockException(e);
		}
	}

	public boolean tryLock(String productId) throws Exception {
		try {
			// 传入进去的locksRoot + “/” + productId
			// 假设productId代表了一个商品id，比如说1
			// locksRoot = locks
			// /locks/10000000000，/locks/10000000001，/locks/10000000002
			// 创建临时有序节点
			lockNode = curatorClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(locksRoot + "/" + productId,
					new byte[0]);

			// 看看刚创建的节点是不是最小的节点
			// locks：10000000000，10000000001，10000000002
			List<String> locks = curatorClient.getChildren().forPath(locksRoot);
			Collections.sort(locks);

			if (lockNode.equals(locksRoot + "/" + locks.get(0))) {
				// 如果是最小的节点,则表示取得锁
				return true;
			}
			
			// 如果不是最小的节点，找到比自己小1的节点
			int previousLockIndex = -1;
			for (int i = 0; i < locks.size(); i++) {
				if (lockNode.equals(locksRoot + "/" + locks.get(i))) {
					previousLockIndex = i - 1;
					break;
				}
			}
			
			this.waitNode = locks.get(previousLockIndex);
		} catch (KeeperException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
		return false;
	}

	private boolean waitForLock(String waitNode, long waitTime) throws Exception {
		
		Stat stat = curatorClient.checkExists().forPath(locksRoot + "/" + waitNode);
		if (stat != null) {
			this.latch = new CountDownLatch(1);
			this.latch.await(waitTime, TimeUnit.MILLISECONDS);
			this.latch = null;
		}
		return true;
	}
	
	public void unlock() {
		try {
			// 删除/locks/10000000000节点
			// 删除/locks/10000000001节点
			System.out.println("unlock " + lockNode);
			curatorClient.delete().forPath(lockNode);
			lockNode = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}