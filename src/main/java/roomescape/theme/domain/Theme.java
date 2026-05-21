package roomescape.theme.domain;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import roomescape.global.domain.DomainPreconditions;
import roomescape.theme.exception.ThemeValidationException;

@Getter
@EqualsAndHashCode(of = "id")
public class Theme {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnailUrl;

    private Theme(final Long id, final String name, final String description, final String thumbnailUrl) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static Theme createNew(final String name, final String description, final String thumbnailUrl) {
        return new Theme(null, name, description, thumbnailUrl);
    }

    public static Theme of(final long id, final String name, final String description, final String thumbnailUrl) {
        return new Theme(id, name, description, thumbnailUrl);
    }

    private void validateName(final String name) {
        DomainPreconditions.requireNonNull(name, new ThemeValidationException(List.of("테마의 이름은 비어있을 수 없습니다.")));
        DomainPreconditions.require(!name.isBlank(), new ThemeValidationException(List.of("테마의 이름은 비어있을 수 없습니다.")));
    }

    public Theme withId(final long id) {
        return new Theme(id, name, description, thumbnailUrl);
    }

}
