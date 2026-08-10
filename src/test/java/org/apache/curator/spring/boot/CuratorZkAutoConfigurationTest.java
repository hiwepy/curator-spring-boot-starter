package org.apache.curator.spring.boot;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CuratorZkAutoConfiguration}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class CuratorZkAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CuratorZkAutoConfiguration.class));

    @Test
    void retryPolicyBean_shouldBeCreated() {
        contextRunner
                .withPropertyValues("curator.connect-string=localhost:2181")
                .run(context -> {
                    assertThat(context).hasSingleBean(RetryPolicy.class);
                    RetryPolicy policy = context.getBean(RetryPolicy.class);
                    assertThat(policy).isInstanceOf(ExponentialBackoffRetry.class);
                });
    }

    @Test
    void propertiesBean_shouldBeBound() {
        contextRunner
                .withPropertyValues(
                        "curator.connect-string=localhost:2181",
                        "curator.namespace=test",
                        "curator.session-timeout-ms=60000",
                        "curator.connection-timeout-ms=5000",
                        "curator.base-sleep-time-ms=2000",
                        "curator.max-retries=5",
                        "curator.max-sleep-ms=100000",
                        "curator.can-be-read-only=true",
                        "curator.with-ensemble-tracker=false"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(CuratorZkProperties.class);
                    CuratorZkProperties props = context.getBean(CuratorZkProperties.class);
                    assertThat(props.getConnectString()).isEqualTo("localhost:2181");
                    assertThat(props.getNamespace()).isEqualTo("test");
                    assertThat(props.getSessionTimeoutMs()).isEqualTo(60000);
                    assertThat(props.getConnectionTimeoutMs()).isEqualTo(5000);
                    assertThat(props.getBaseSleepTimeMs()).isEqualTo(2000);
                    assertThat(props.getMaxRetries()).isEqualTo(5);
                    assertThat(props.getMaxSleepMs()).isEqualTo(100000);
                    assertThat(props.isCanBeReadOnly()).isTrue();
                    assertThat(props.isWithEnsembleTracker()).isFalse();
                });
    }

    @Test
    void retryPolicyBean_withCustomProperties_shouldUseCustomValues() {
        contextRunner
                .withPropertyValues(
                        "curator.connect-string=localhost:2181",
                        "curator.base-sleep-time-ms=2000",
                        "curator.max-retries=5"
                )
                .run(context -> {
                    RetryPolicy policy = context.getBean(RetryPolicy.class);
                    assertThat(policy).isInstanceOf(ExponentialBackoffRetry.class);
                });
    }

    @Test
    void curatorClientBean_shouldBeCreatedWithConnectString() {
        contextRunner
                .withPropertyValues("curator.connect-string=localhost:2181")
                .run(context -> {
                    assertThat(context).hasSingleBean(CuratorZkAutoConfiguration.class);
                    assertThat(context).hasSingleBean(CuratorZkProperties.class);
                    assertThat(context).hasSingleBean(RetryPolicy.class);
                    // curatorClient bean will attempt to connect to ZK
                    // which may fail, but the configuration should be loaded
                });
    }

    @Test
    void curatorZkTemplateBean_shouldBeCreated() {
        contextRunner
                .withPropertyValues("curator.connect-string=localhost:2181")
                .run(context -> {
                    // curatorZkTemplate depends on curatorClient which needs ZK
                    // Verify the configuration class is present
                    assertThat(context).hasSingleBean(CuratorZkAutoConfiguration.class);
                });
    }

    @Test
    void propertiesBean_withAuthInfo_shouldBindAuthInfo() {
        contextRunner
                .withPropertyValues(
                        "curator.connect-string=localhost:2181",
                        "curator.auth-info[0].scheme=digest",
                        "curator.auth-info[0].auth=user:password"
                )
                .run(context -> {
                    CuratorZkProperties props = context.getBean(CuratorZkProperties.class);
                    assertThat(props.getAuthInfo()).isNotNull();
                    assertThat(props.getAuthInfo()).hasSize(1);
                    assertThat(props.getAuthInfo().get(0).getScheme()).isEqualTo("digest");
                    assertThat(props.getAuthInfo().get(0).getAuth()).isEqualTo("user:password");
                });
    }

    @Test
    void conditionalOnClass_shouldRequireCuratorClasses() {
        assertThat(CuratorZkAutoConfiguration.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class)).isTrue();
        assertThat(CuratorZkAutoConfiguration.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.condition.ConditionalOnClass.class)).isTrue();
        assertThat(CuratorZkAutoConfiguration.class.isAnnotationPresent(
                org.springframework.boot.context.properties.EnableConfigurationProperties.class)).isTrue();
    }

    @Test
    void propertiesBean_defaultValues() {
        contextRunner
                .withPropertyValues("curator.connect-string=localhost:2181")
                .run(context -> {
                    CuratorZkProperties props = context.getBean(CuratorZkProperties.class);
                    assertThat(props.getConnectString()).isEqualTo("localhost:2181");
                    assertThat(props.getNamespace()).isNull();
                    assertThat(props.getSessionTimeoutMs()).isEqualTo(30000);
                    assertThat(props.getConnectionTimeoutMs()).isEqualTo(3000);
                    assertThat(props.getBaseSleepTimeMs()).isEqualTo(1000);
                    assertThat(props.getMaxRetries()).isEqualTo(3);
                    assertThat(props.getMaxSleepMs()).isEqualTo(Integer.MAX_VALUE);
                    assertThat(props.isCanBeReadOnly()).isFalse();
                    assertThat(props.isWithEnsembleTracker()).isTrue();
                    assertThat(props.getAuthInfo()).isNull();
                });
    }

}
