package org.apache.curator.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CuratorLockException}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class CuratorLockExceptionTest {

    @Test
    void constructorWithMessage_shouldStoreMessage() {
        CuratorLockException ex = new CuratorLockException("lock failed");
        assertThat(ex).hasMessage("lock failed");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructorWithException_shouldWrapCause() {
        Exception cause = new RuntimeException("root cause");
        CuratorLockException ex = new CuratorLockException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex).hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void constructorWithInterruptedException_shouldWrapCorrectly() {
        InterruptedException cause = new InterruptedException("interrupted");
        CuratorLockException ex = new CuratorLockException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    void exception_shouldBeRuntimeException() {
        CuratorLockException ex = new CuratorLockException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void serialVersionUID_shouldBeSet() throws Exception {
        java.lang.reflect.Field field = CuratorLockException.class.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        long uid = field.getLong(null);
        assertThat(uid).isEqualTo(1L);
    }

}
