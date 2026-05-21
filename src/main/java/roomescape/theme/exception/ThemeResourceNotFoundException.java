package roomescape.theme.exception;

import roomescape.global.exception.exception.ResourceNotFoundException;

public class ThemeResourceNotFoundException extends ResourceNotFoundException {
    public ThemeResourceNotFoundException() {
        super("찾는 테마가 없습니다.");
    }
}
