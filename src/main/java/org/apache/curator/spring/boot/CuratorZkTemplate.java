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
 * https://www.cnblogs.com/qlqwjy/p/10518900.html
 */
public class CuratorZkTemplate {

	private CuratorFramework curatorClient;
	private RetryPolicy retryPolicy;
	private int sessionTimeout = 30000;
	
	public CuratorZkTemplate(CuratorFramework curatorClient, RetryPolicy retryPolicy, int sessionTimeout) {
		this.curatorClient = curatorClient;
		this.retryPolicy = retryPolicy;
		this.sessionTimeout = sessionTimeout;
	}

	public CuratorZkDistributedLock getDistributedLock() {
		return new CuratorZkDistributedLock(curatorClient, sessionTimeout);
	}
	
	/**
	 * 共享锁，不可重入--- InterProcessSemaphoreMutex
	 * 
	 * @param lockKey
	 * @return
	 */
	public InterProcessLock getSharedLock(String lockKey) {
		return new InterProcessSemaphoreMutex(curatorClient, lockKey);
	}

	/**
	 * 共享可重入锁--- InterProcessMutex
	 * 
	 * @param lockKey
	 * @return
	 */
	public InterProcessLock getSharedReentrantLock(String lockKey) {
		return new InterProcessMutex(curatorClient, lockKey);
	}

	/**
	 * 共享可重入读写锁--- InterProcessMutex
	 * 
	 * @param lockKey
	 * @return
	 */
	public InterProcessReadWriteLock getSharedReentrantReadWriteLock(String lockKey) {
		return new InterProcessReadWriteLock(curatorClient, lockKey);
	}

	/**
	 * 共享信号量--- InterProcessSemaphoreV2
	 * 
	 * @param lockKey
	 * @return
	 */
	public InterProcessSemaphoreV2 getSharedSemaphore(String lockKey) {
		// 创建一个信号量, Curator 以公平锁的方式进行实现
		return new InterProcessSemaphoreV2(curatorClient, lockKey, 1);
	}

	/**
	 * 多重共享锁--- InterProcessMultiLock
	 * 
	 * @param lockKey
	 * @return
	 */
	public InterProcessMultiLock getSharedSemaphore(InterProcessLock... locks) {
		// 创建多重锁对象
		return new InterProcessMultiLock(Arrays.asList(locks));
	}

	public DistributedBarrier getBarrier(String barrierPath) {
		return new DistributedBarrier(curatorClient, barrierPath);
	}

	public DistributedDoubleBarrier getDoubleBarrier(String barrierPath, int memberQty) {
		return new DistributedDoubleBarrier(curatorClient, barrierPath, memberQty);
	}
	
	public DistributedAtomicInteger getAtomicInteger(String lockKey) {
		return new DistributedAtomicInteger(curatorClient, lockKey, retryPolicy);
	}
	
	public DistributedAtomicLong getAtomicLong(String lockKey) {
		return new DistributedAtomicLong(curatorClient, lockKey, retryPolicy);
	}

	public DistributedAtomicValue getAtomicValue(String lockKey) {
		return new DistributedAtomicValue(curatorClient, lockKey, retryPolicy);
	}

	public CuratorFramework getCuratorClient() {
		return curatorClient;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

}
