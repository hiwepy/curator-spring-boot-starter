package org.apache.curator.spring.boot;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CuratorZkProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class CuratorZkPropertiesTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        CuratorZkProperties properties = new CuratorZkProperties();

        assertThat(properties.getNamespace()).isNull();
        assertThat(properties.getMaxCloseWaitMs()).isZero();
        assertThat(properties.getWaitForShutdownTimeoutMs()).isZero();
        assertThat(properties.getSimulatedSessionExpirationPercent()).isEqualTo(-1);
        assertThat(properties.getConnectString()).isNull();
        assertThat(properties.isWithEnsembleTracker()).isTrue();
        assertThat(properties.getSessionTimeoutMs()).isEqualTo(30000);
        assertThat(properties.getConnectionTimeoutMs()).isEqualTo(3000);
        assertThat(properties.isCanBeReadOnly()).isFalse();
        assertThat(properties.getAuthInfo()).isNull();
        assertThat(properties.getBaseSleepTimeMs()).isEqualTo(1000);
        assertThat(properties.getMaxRetries()).isEqualTo(3);
        assertThat(properties.getMaxSleepMs()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CuratorZkProperties properties = new CuratorZkProperties();

        properties.setNamespace("test-ns");
        assertThat(properties.getNamespace()).isEqualTo("test-ns");

        properties.setMaxCloseWaitMs(5000);
        assertThat(properties.getMaxCloseWaitMs()).isEqualTo(5000);

        properties.setWaitForShutdownTimeoutMs(2000);
        assertThat(properties.getWaitForShutdownTimeoutMs()).isEqualTo(2000);

        properties.setSimulatedSessionExpirationPercent(50);
        assertThat(properties.getSimulatedSessionExpirationPercent()).isEqualTo(50);

        properties.setConnectString("localhost:2181");
        assertThat(properties.getConnectString()).isEqualTo("localhost:2181");

        properties.setWithEnsembleTracker(false);
        assertThat(properties.isWithEnsembleTracker()).isFalse();

        properties.setSessionTimeoutMs(60000);
        assertThat(properties.getSessionTimeoutMs()).isEqualTo(60000);

        properties.setConnectionTimeoutMs(5000);
        assertThat(properties.getConnectionTimeoutMs()).isEqualTo(5000);

        properties.setCanBeReadOnly(true);
        assertThat(properties.isCanBeReadOnly()).isTrue();

        properties.setBaseSleepTimeMs(2000);
        assertThat(properties.getBaseSleepTimeMs()).isEqualTo(2000);

        properties.setMaxRetries(5);
        assertThat(properties.getMaxRetries()).isEqualTo(5);

        properties.setMaxSleepMs(100000);
        assertThat(properties.getMaxSleepMs()).isEqualTo(100000);
    }

    @Test
    void authInfo_shouldBeSettableAndGettable() {
        CuratorZkProperties properties = new CuratorZkProperties();
        CuratorZkProperties.CuratorAuthInfo authInfo = new CuratorZkProperties.CuratorAuthInfo();

        authInfo.setScheme("digest");
        authInfo.setAuth("user:password");

        assertThat(authInfo.getScheme()).isEqualTo("digest");
        assertThat(authInfo.getAuth()).isEqualTo("user:password");

        properties.setAuthInfo(List.of(authInfo));
        assertThat(properties.getAuthInfo()).hasSize(1);
        assertThat(properties.getAuthInfo().get(0).getScheme()).isEqualTo("digest");
        assertThat(properties.getAuthInfo().get(0).getAuth()).isEqualTo("user:password");
    }

    @Test
    void prefix_shouldBeCurator() {
        assertThat(CuratorZkProperties.PREFIX).isEqualTo("curator");
    }

}
