package roomescape.theme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeConstraintException;
import roomescape.theme.exception.ThemeDuplicateException;
import roomescape.theme.repository.ThemeRepository;
import roomescape.theme.service.dto.ThemeResult;

@Service
@Transactional
@RequiredArgsConstructor
public class ThemeCommandService {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeResult save(final String name, final String description, final String thumbnailUrl) {
        validateDuplicate(name);

        Theme theme = Theme.createNew(name, description, thumbnailUrl);

        Theme savedTheme = themeRepository.save(theme);

        return ThemeResult.from(savedTheme);
    }

    public void deleteById(final long themeId) {
        validateReservationExists(themeId);
        themeRepository.deleteById(themeId);
    }

    private void validateDuplicate(final String name) {
        if (themeRepository.existsByName(name)) {
            throw new ThemeDuplicateException();
        }
    }

    private void validateReservationExists(final long themeId) {
        if (reservationRepository.existsByThemeId(themeId)) {
            throw new ThemeConstraintException();
        }
    }
}
