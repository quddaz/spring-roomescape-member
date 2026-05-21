package roomescape.theme.exception;

import roomescape.global.exception.exception.ResourceInUseException;

public class ThemeInUseException extends ResourceInUseException {
    public ThemeInUseException() {
        super("예약이 존재하는 테마는 삭제할 수 없습니다.");
    }
}
