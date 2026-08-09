package org.apache.curator.spring.boot;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooKeeper.States;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Curator ZooKeeper operations.
 * <p>Requires a running ZooKeeper instance. Disabled by default for unit testing.</p>
 *
 * @author <a href="https://github.com/loong10k">@Loong Wan</a>
 * @since 1.0.0
 */
@Disabled("Requires a running ZooKeeper instance")
class CuratorTest {

    private static final int SESSION_TIMEOUT = 30 * 1000;
    private static final int CONNECTION_TIMEOUT = 3 * 1000;
    private static final String CONNECT_ADDR = "192.168.1.1:2100,192.168.1.1:2101,192.168.1.:2102";

    @Test
    void testTransaction() {
        // Integration test - disabled
    }

    @Test
    void testWatcher() {
        // Integration test - disabled
    }

    @Test
    void retryPolicyCreation_shouldWork() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        assertThat(retryPolicy).isNotNull();
    }

}
