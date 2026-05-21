package roomescape.global.domain;

import java.util.List;

public final class DomainPreconditions {

    private DomainPreconditions() {
    }

    public static <T> T requireNonNull(
            final T value,
            final RuntimeException exception
    ) {
        if (value == null) {
            throw exception;
        }
        return value;
    }

    public static void require(
            final boolean condition,
            final RuntimeException exception
    ) {
        if (!condition) {
            throw exception;
        }
    }

    public static void collectIfNull(
            final Object value,
            final List<String> errors,
            final String message
    ) {
        if (value == null) {
            errors.add(message);
        }
    }

    public static void collectIfFalse(
            final boolean condition,
            final List<String> errors,
            final String message
    ) {
        if (!condition) {
            errors.add(message);
        }
    }

}
