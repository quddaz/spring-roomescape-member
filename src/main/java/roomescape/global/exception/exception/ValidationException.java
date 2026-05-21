package roomescape.global.exception.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("잘못된 입력입니다.");
        this.errors = errors;
    }

}
