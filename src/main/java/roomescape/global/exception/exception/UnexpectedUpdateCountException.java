package roomescape.global.exception.exception;

import lombok.Getter;

@Getter
public class UnexpectedUpdateCountException extends RuntimeException {
    public UnexpectedUpdateCountException(int updateRowCount) {
        super("예상치 못한 업데이트 수가 발생했습니다." + " 업데이트된 행 수: " + updateRowCount);
    }
}
