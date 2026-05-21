package roomescape.theme.exception;

import roomescape.global.exception.exception.AlreadyExistsException;

public class ThemeAlreadyExistsException extends AlreadyExistsException {
    public ThemeAlreadyExistsException() {
        super("테마는 중복 생성이 불가능합니다.");
    }
}
