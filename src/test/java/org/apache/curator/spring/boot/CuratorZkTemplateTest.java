package org.apache.curator.spring.boot;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.WatcherRemoveCuratorFramework;
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
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for {@link CuratorZkTemplate}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class CuratorZkTemplateTest {

    @Mock
    private CuratorFramework curatorClient;

    @Mock
    private WatcherRemoveCuratorFramework watcherRemoveClient;

    private RetryPolicy retryPolicy;
    private CuratorZkTemplate template;

    @BeforeEach
    void setUp() {
        retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // Mock the newWatcherRemoveCuratorFramework method that lock constructors call
        lenient().when(curatorClient.newWatcherRemoveCuratorFramework()).thenReturn(watcherRemoveClient);
        template = new CuratorZkTemplate(curatorClient, retryPolicy, 30000);
    }

    @Test
    void constructor_shouldStoreDependencies() {
        assertThat(template.getCuratorClient()).isSameAs(curatorClient);
        assertThat(template.getRetryPolicy()).isSameAs(retryPolicy);
    }

    @Test
    void getDistributedLock_shouldExist() throws Exception {
        // Verify the method exists via reflection
        assertThat(CuratorZkTemplate.class.getMethod("getDistributedLock")).isNotNull();
    }

    @Test
    void getSharedLock_shouldReturnSemaphoreMutex() {
        InterProcessLock lock = template.getSharedLock("/test-lock");
        assertThat(lock).isInstanceOf(InterProcessSemaphoreMutex.class);
    }

    @Test
    void getSharedReentrantLock_shouldReturnMutex() {
        InterProcessLock lock = template.getSharedReentrantLock("/test-lock");
        assertThat(lock).isInstanceOf(InterProcessMutex.class);
    }

    @Test
    void getSharedReentrantReadWriteLock_shouldReturnReadWriteLock() {
        InterProcessReadWriteLock lock = template.getSharedReentrantReadWriteLock("/test-lock");
        assertThat(lock).isNotNull();
    }

    @Test
    void getSharedSemaphore_withKey_shouldReturnSemaphoreV2() {
        InterProcessSemaphoreV2 semaphore = template.getSharedSemaphore("/test-semaphore");
        assertThat(semaphore).isNotNull();
    }

    @Test
    void getSharedSemaphore_withLocks_shouldReturnMultiLock() {
        InterProcessLock lock1 = template.getSharedLock("/lock1");
        InterProcessLock lock2 = template.getSharedLock("/lock2");
        InterProcessMultiLock multiLock = template.getSharedSemaphore(lock1, lock2);
        assertThat(multiLock).isNotNull();
    }

    @Test
    void getBarrier_shouldReturnBarrier() {
        DistributedBarrier barrier = template.getBarrier("/test-barrier");
        assertThat(barrier).isNotNull();
    }

    @Test
    void getDoubleBarrier_shouldReturnDoubleBarrier() {
        DistributedDoubleBarrier barrier = template.getDoubleBarrier("/test-barrier", 3);
        assertThat(barrier).isNotNull();
    }

    @Test
    void getAtomicInteger_shouldReturnAtomicInteger() {
        DistributedAtomicInteger atomicInt = template.getAtomicInteger("/test-atomic-int");
        assertThat(atomicInt).isNotNull();
    }

    @Test
    void getAtomicLong_shouldReturnAtomicLong() {
        DistributedAtomicLong atomicLong = template.getAtomicLong("/test-atomic-long");
        assertThat(atomicLong).isNotNull();
    }

    @Test
    void getAtomicValue_shouldReturnAtomicValue() {
        DistributedAtomicValue atomicValue = template.getAtomicValue("/test-atomic-value");
        assertThat(atomicValue).isNotNull();
    }

    @Test
    void getCuratorClient_shouldReturnInjectedClient() {
        assertThat(template.getCuratorClient()).isSameAs(curatorClient);
    }

    @Test
    void getRetryPolicy_shouldReturnInjectedPolicy() {
        assertThat(template.getRetryPolicy()).isSameAs(retryPolicy);
    }

}
