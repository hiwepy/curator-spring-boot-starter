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

/**
 * A ZooKeeper-based distributed lock implementation that creates ephemeral sequential
 * nodes under a root path and grants the lock to the lowest-numbered node, waiting on
 * its predecessor otherwise.
 *
 * @author <a href="https://github.com/loong10k">@Loong Wan</a>
 * @since 1.0.0
 */
public class CuratorZkDistributedLock implements Watcher {

	/** The curator client used to interact with ZooKeeper. */
	private CuratorFramework curatorClient;
	/** Root znode path under which lock nodes are created. */
	private String locksRoot = "/locks";
	/** The predecessor node this lock is currently waiting on. */
	private String waitNode;
	/** The ephemeral sequential node representing this lock holder. */
	private String lockNode;
	/** Latch used to block until the predecessor node is removed. */
	private CountDownLatch latch;
	/** Latch used to block until the client has synchronously connected. */
	private CountDownLatch connectedLatch = new CountDownLatch(1);
	/** Session timeout in milliseconds. */
	private int sessionTimeout = 30000;

	/**
	 * Creates a distributed lock and blocks until the curator client has synchronously
	 * connected to ZooKeeper.
	 * @param curatorClient the curator client
	 * @param sessionTimeout the session timeout in milliseconds
	 */
	public CuratorZkDistributedLock(CuratorFramework curatorClient, int sessionTimeout) {
		try {
			this.curatorClient = curatorClient;
			this.sessionTimeout = sessionTimeout;
			connectedLatch.await();
		} catch (InterruptedException e) {
			throw new CuratorLockException(e);
		}
	}

	/**
	 * Handles ZooKeeper watch events, releasing the connect latch on sync-connected and
	 * the wait latch on subsequent notifications.
	 * @param event the watched event
	 */
	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			connectedLatch.countDown();
			return;
		}

		if (this.latch != null) {
			this.latch.countDown();
		}
	}

	/**
	 * Acquires the distributed lock for the given key, blocking until it is obtained.
	 * @param lockKey the lock key (typically a business identifier)
	 */
	public void acquireLock(String lockKey) {
		try {
			if (this.tryLock(lockKey)) {
				return;
			} else {
				waitForLock(waitNode, sessionTimeout);
			}
		} catch (KeeperException e) {
			throw new CuratorLockException(e);
		} catch (InterruptedException e) {
			throw new CuratorLockException(e);
		} catch (Exception e) {
			throw new CuratorLockException(e);
		}
	}

	/**
	 * Attempts to acquire the distributed lock for the given key without blocking;
	 * when this holder is not the lowest-numbered node the predecessor is recorded for
	 * later waiting.
	 * @param lockKey the lock key
	 * @return {@code true} if the lock was acquired
	 * @throws Exception if ZooKeeper interaction fails
	 */
	public boolean tryLock(String lockKey) throws Exception {
		try {
			// 传入进去的locksRoot + “/” + lockKey
			// 假设lockKey代表了一个商品id，比如说1
			// locksRoot = locks
			// /locks/10000000000，/locks/10000000001，/locks/10000000002
			// 创建临时有序节点
			lockNode = curatorClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(locksRoot + "/" + lockKey,
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
			throw new CuratorLockException(e);
		} catch (InterruptedException e) {
			throw new CuratorLockException(e);
		}
		return false;
	}

	/**
	 * Blocks until the predecessor node no longer exists or the wait time elapses.
	 * @param waitNode the predecessor node to wait on
	 * @param waitTime the maximum time to wait in milliseconds
	 * @return {@code true} once the wait completes
	 * @throws Exception if ZooKeeper interaction fails
	 */
	private boolean waitForLock(String waitNode, long waitTime) throws Exception {

		Stat stat = curatorClient.checkExists().forPath(locksRoot + "/" + waitNode);
		if (stat != null) {
			this.latch = new CountDownLatch(1);
			this.latch.await(waitTime, TimeUnit.MILLISECONDS);
			this.latch = null;
		}
		return true;
	}

	/**
	 * Releases the distributed lock by deleting this holder's ephemeral node.
	 * @return {@code true} if the lock node was successfully removed
	 */
	public boolean unlock() {
		try {
			// 删除/locks/10000000000节点
			curatorClient.delete().forPath(lockNode);
			lockNode = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}