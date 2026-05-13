package roomescape.service.stub;

import java.util.List;
import roomescape.reservation.repository.ReservationQueryRepository;
import roomescape.reservation.service.dto.ReservationResult;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;

public class FakeReservationQueryRepository implements ReservationQueryRepository {

    private final FakeReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public FakeReservationQueryRepository(
            final FakeReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Override
    public List<ReservationResult> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservation -> ReservationResult.of(
                        reservation,
                        reservationTimeRepository.findById(reservation.getTimeId())
                                .orElseThrow(ReservationTimeNotFoundException::new)
                ))
                .toList();
    }

    @Override
    public List<ReservationResult> findAllByName(final String name) {
        return findAll().stream()
                .filter(reservation -> reservation.name().equals(name))
                .toList();
    }
}
