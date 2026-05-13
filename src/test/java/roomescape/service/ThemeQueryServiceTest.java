package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.service.stub.FakeThemeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.theme.service.ThemeQueryService;
import roomescape.theme.service.dto.ThemeResult;

class ThemeQueryServiceTest {

    private ThemeQueryService themeQueryService;
    private ThemeRepository themeRepository;

    @BeforeEach
    void setUp() {
        themeRepository = new FakeThemeRepository();
        themeQueryService = new ThemeQueryService(themeRepository);
    }

    @Test
    @DisplayName("테마 전체 조회")
    void getAll_success() {
        themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));

        assertThat(themeQueryService.getAll())
                .extracting(ThemeResult::name)
                .containsExactly("미술관의 밤");
    }

    @Test
    @DisplayName("인기 테마 조회")
    void getPopularThemes_success() {
        themeRepository.save(Theme.createNew("미술관의 밤", "설명", "thumb"));

        assertThat(themeQueryService.getPopularThemes(7, 10))
                .extracting(ThemeResult::name)
                .containsExactly("미술관의 밤");
    }
}
