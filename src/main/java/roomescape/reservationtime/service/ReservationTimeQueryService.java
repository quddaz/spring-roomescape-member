package roomescape.reservationtime.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.reservationtime.service.dto.ReservationTimeResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationTimeQueryService {

    private final ReservationTimeRepository reservationTimeRepository;

    public List<ReservationTimeResult> findAllByThemeId(final long themeId) {
        return reservationTimeRepository.findAllByThemeId(themeId).stream()
                .map(ReservationTimeResult::from)
                .toList();
    }

    public List<ReservationTimeResult> findAll() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResult::from)
                .toList();
    }

    public List<ReservationTimeResult> findAvailableTimes(final LocalDate date, final long themeId) {
        return reservationTimeRepository.findAvailableTimes(date, themeId).stream()
                .map(ReservationTimeResult::from)
                .toList();
    }
}
