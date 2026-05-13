package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.repository.ThemeRepository;
import roomescape.theme.service.dto.ThemeResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ThemeQueryService {

    private final ThemeRepository themeRepository;

    public List<ThemeResult> getAll() {
        return themeRepository.findAll().stream()
                .map(ThemeResult::from)
                .toList();
    }

    public List<ThemeResult> getPopularThemes(final int period, final int limit) {
        return themeRepository.findPopularThemes(
                        period,
                        limit,
                        LocalDate.now()
                ).stream()
                .map(ThemeResult::from)
                .toList();
    }
}
