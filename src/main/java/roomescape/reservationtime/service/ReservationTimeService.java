package roomescape.reservationtime.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeAlreadyExistsException;
import roomescape.reservationtime.exception.ReservationTimeInUseException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.reservationtime.service.dto.ReservationTimeResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationTimeResult save(final LocalTime startAt) {
        validateDuplicate(startAt);

        ReservationTime reservationTime = ReservationTime.createNew(startAt);

        ReservationTime savedReservationTime =
                reservationTimeRepository.save(reservationTime);

        return ReservationTimeResult.from(savedReservationTime);
    }

    @Transactional
    public void deleteById(final long timeId) {
        validateReservationExists(timeId);
        reservationTimeRepository.deleteById(timeId);
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

    private void validateDuplicate(final LocalTime startAt) {
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new ReservationTimeAlreadyExistsException();
        }
    }

    private void validateReservationExists(final long timeId) {
        if (reservationRepository.existsByTimeId(timeId)) {
            throw new ReservationTimeInUseException();
        }
    }
}
