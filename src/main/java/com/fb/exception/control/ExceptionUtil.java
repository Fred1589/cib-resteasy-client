package com.fb.exception.control;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fb.exception.entity.CibClientTechnicalException;
import com.fb.exception.entity.ErrorCode;

/**
 * Utility to simplify working with Exceptions.
 */
public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException("Utility class should not be instantiated!");
    }

    /**
     * Checks if causal chain contains a cause that matches the specified class
     *
     * @param ex           as starting point for causal chain
     * @param clazzOfCause type of exception to check for
     * @return true of type was found otherwise false
     */
    public static boolean causalChainContainsClass(final Exception ex, final Class<?> clazzOfCause) {
        return streamCausalChain(ex).anyMatch(causalThrowable -> clazzOfCause.isInstance(causalThrowable));
    }

    /**
     * Returns exception from within causal chain that has the specified class
     *
     * @param ex             as starting point for causal chain
     * @param exceptionClass type of exception to extract
     * @param <T>
     * @return extracted exception or null if nothing matched
     */
    public static <T> T extractException(final Exception ex, final Class<T> exceptionClass) {
        return (T) streamCausalChain(ex).filter(exceptionClass::isInstance).findFirst().orElse(null);
    }

    /**
     * Stream of causal chain of throwable
     *
     * @param throwable from which the causal chain will be constructed
     * @return stream of {@link Throwable} iterating through all causes
     * @throws CibClientTechnicalException if loop in causal chain is detected
     */
    public static Stream<Throwable> streamCausalChain(final Throwable throwable) {
        // Pointer to go over causal chain
        final Throwable[] cause = {throwable};

        // Pointer for loop detection, advances slower over causal chain
        final Throwable[] earlierCause = {throwable};
        final boolean[] advanceEarlierCause = {false};

        final Iterator<Throwable> iterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return cause[0] != null;
            }

            @Override
            public Throwable next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                final Throwable lastCause = cause[0];

                // Advance pointer to next cause
                cause[0] = cause[0].getCause();

                // Abort if iteration has caught up with slow pointer
                if (earlierCause[0] == cause[0]) {
                    throw new CibClientTechnicalException(ErrorCode.T_INTERNAL_SERVER_ERROR,
                            "Loop in causal chain of exception detected!", lastCause);
                }

                // Advance only every second iteration
                if (advanceEarlierCause[0]) {
                    earlierCause[0] = earlierCause[0].getCause();
                }
                advanceEarlierCause[0] = !advanceEarlierCause[0];

                return lastCause;
            }
        };

        final Iterable<Throwable> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
