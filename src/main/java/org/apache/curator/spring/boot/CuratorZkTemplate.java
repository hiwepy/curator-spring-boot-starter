package org.apache.curator.spring.boot;

import java.util.Arrays;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicValue;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;

/**
 * Template exposing Curator's distributed primitives (locks, barriers and atomic
 * values) backed by a {@link CuratorFramework} client.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CuratorZkTemplate {

	private CuratorFramework curatorClient;
	private RetryPolicy retryPolicy;
	private int sessionTimeout = 30000;

	/**
	 * Creates a curator template with the given client, retry policy and session timeout.
	 * @param curatorClient the curator client
	 * @param retryPolicy the retry policy used by distributed atomic values
	 * @param sessionTimeout the session timeout in milliseconds
	 */
	public CuratorZkTemplate(CuratorFramework curatorClient, RetryPolicy retryPolicy, int sessionTimeout) {
		this.curatorClient = curatorClient;
		this.retryPolicy = retryPolicy;
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * Returns a custom ZooKeeper-based distributed lock that manages its own ephemeral
	 * sequential nodes.
	 * @return a new distributed lock instance
	 */
	public CuratorZkDistributedLock getDistributedLock() {
		return new CuratorZkDistributedLock(curatorClient, sessionTimeout);
	}

	/**
	 * Returns a non-reentrant shared lock ({@code InterProcessSemaphoreMutex}).
	 * @param lockKey the lock path key
	 * @return the shared lock
	 */
	public InterProcessLock getSharedLock(String lockKey) {
		return new InterProcessSemaphoreMutex(curatorClient, lockKey);
	}

	/**
	 * Returns a reentrant shared lock ({@code InterProcessMutex}).
	 * @param lockKey the lock path key
	 * @return the reentrant shared lock
	 */
	public InterProcessLock getSharedReentrantLock(String lockKey) {
		return new InterProcessMutex(curatorClient, lockKey);
	}

	/**
	 * Returns a reentrant shared read/write lock ({@code InterProcessReadWriteLock}).
	 * @param lockKey the lock path key
	 * @return the reentrant read/write lock
	 */
	public InterProcessReadWriteLock getSharedReentrantReadWriteLock(String lockKey) {
		return new InterProcessReadWriteLock(curatorClient, lockKey);
	}

	/**
	 * Returns a shared semaphore with a single permit ({@code InterProcessSemaphoreV2}).
	 * @param lockKey the semaphore path key
	 * @return the shared semaphore
	 */
	public InterProcessSemaphoreV2 getSharedSemaphore(String lockKey) {
		// 创建一个信号量, Curator 以公平锁的方式进行实现
		return new InterProcessSemaphoreV2(curatorClient, lockKey, 1);
	}

	/**
	 * Returns a multi-lock that manages several locks as a single logical lock
	 * ({@code InterProcessMultiLock}).
	 * @param locks the locks to compose
	 * @return the multi-lock
	 */
	public InterProcessMultiLock getSharedSemaphore(InterProcessLock... locks) {
		// 创建多重锁对象
		return new InterProcessMultiLock(Arrays.asList(locks));
	}

	/**
	 * Returns a distributed barrier at the given path.
	 * @param barrierPath the barrier path
	 * @return the distributed barrier
	 */
	public DistributedBarrier getBarrier(String barrierPath) {
		return new DistributedBarrier(curatorClient, barrierPath);
	}

	/**
	 * Returns a distributed double barrier that releases all members simultaneously once
	 * the required member count has entered.
	 * @param barrierPath the barrier path
	 * @param memberQty the number of members required
	 * @return the distributed double barrier
	 */
	public DistributedDoubleBarrier getDoubleBarrier(String barrierPath, int memberQty) {
		return new DistributedDoubleBarrier(curatorClient, barrierPath, memberQty);
	}

	/**
	 * Returns a distributed atomic integer at the given path.
	 * @param lockKey the counter path key
	 * @return the distributed atomic integer
	 */
	public DistributedAtomicInteger getAtomicInteger(String lockKey) {
		return new DistributedAtomicInteger(curatorClient, lockKey, retryPolicy);
	}

	/**
	 * Returns a distributed atomic long at the given path.
	 * @param lockKey the counter path key
	 * @return the distributed atomic long
	 */
	public DistributedAtomicLong getAtomicLong(String lockKey) {
		return new DistributedAtomicLong(curatorClient, lockKey, retryPolicy);
	}

	/**
	 * Returns a distributed atomic byte-buffer value at the given path.
	 * @param lockKey the counter path key
	 * @return the distributed atomic value
	 */
	public DistributedAtomicValue getAtomicValue(String lockKey) {
		return new DistributedAtomicValue(curatorClient, lockKey, retryPolicy);
	}

	/**
	 * Returns the underlying curator client.
	 * @return the curator client
	 */
	public CuratorFramework getCuratorClient() {
		return curatorClient;
	}

	/**
	 * Returns the retry policy used for distributed atomic operations.
	 * @return the retry policy
	 */
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

}
