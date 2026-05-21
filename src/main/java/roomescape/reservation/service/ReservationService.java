package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.time.TimeManager;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservation.exception.ReservationResourceNotFoundException;
import roomescape.reservation.exception.ReservationUnexpectedUpdateCountException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeResourceNotFoundException;
import roomescape.theme.repository.ThemeRepository;
import roomescape.reservationtime.exception.ReservationTimeResourceNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final TimeManager timeManager;
    private final ThemeRepository themeRepository;

    public List<ReservationResult> getAll() {
        return reservationRepository.findAll();
    }

    public List<ReservationResult> getAllByName(final String name) {
        return reservationRepository.findAllByName(name);
    }

    @Transactional
    public ReservationResult save(final String name, final LocalDate date, final Long timeId, final Long themeId) {
        ReservationTime reservationTime = findReservationTime(timeId);

        Theme theme = findTheme(themeId);
        validateDuplicate(date, timeId, themeId);

        Reservation reservation = Reservation.createNew(name, date, reservationTime.getId(), theme.getId());
        reservation.validateNotPast(reservationTime.getStartAt(), timeManager.nowDateTime());

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResult.from(savedReservation, theme, reservationTime.getStartAt());
    }

    @Transactional
    public void deleteById(final long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void deleteById(final long id, final String name) {
        Reservation reservation = findReservation(id);
        reservation.validateOwner(name);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void update(final long id, final String name, final LocalDate date, final Long timeId, final Long themeId) {
        Reservation reservation = findReservation(id);
        reservation.validateOwner(name);

        ReservationTime reservationTime = findReservationTime(timeId);
        Theme theme = findTheme(themeId);
        validateDuplicate(date, timeId, themeId);

        reservation = reservation.modify(date, reservationTime.getId(), theme.getId());
        reservation.validateNotPast(reservationTime.getStartAt(), timeManager.nowDateTime());

        int updateRowCount = reservationRepository.update(reservation);
        validateSingleRowUpdate(updateRowCount);
    }

    private Reservation findReservation(final long id) {
        return reservationRepository.findById(id)
                .orElseThrow(ReservationResourceNotFoundException::new);
    }

    private ReservationTime findReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(ReservationTimeResourceNotFoundException::new);
    }

    private Theme findTheme(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(ThemeResourceNotFoundException::new);
    }

    private void validateDuplicate(final LocalDate date, final Long timeId, final Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new ReservationAlreadyExistsException();
        }
    }

    private void validateSingleRowUpdate(final int updateRowCount) {
        if (updateRowCount != 1) {
            throw new ReservationUnexpectedUpdateCountException(updateRowCount);
        }
    }

}
