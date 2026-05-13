package roomescape.theme.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.controller.dto.ThemeCreateRequest;
import roomescape.theme.controller.dto.ThemeResponse;
import roomescape.theme.service.ThemeCommandService;
import roomescape.theme.service.ThemeQueryService;
import roomescape.theme.service.dto.ThemeResult;

@RestController
@RequestMapping("/admin/themes")
@RequiredArgsConstructor
public class ThemeAdminController {

    private final ThemeCommandService themeCommandService;
    private final ThemeQueryService themeQueryService;

    @GetMapping
    public List<ThemeResponse> read() {
        return themeQueryService.getAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse create(@RequestBody ThemeCreateRequest request) {
        ThemeResult themeResult = themeCommandService.save(
                request.name(),
                request.description(),
                request.thumbnailUrl()
        );

        return ThemeResponse.from(themeResult);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        themeCommandService.deleteById(id);
    }

}
