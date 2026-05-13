package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.repository.ReservationQueryRepository;
import roomescape.reservation.service.dto.ReservationResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationQueryRepository reservationQueryRepository;

    public List<ReservationResult> getAll() {
        return reservationQueryRepository.findAll();
    }

    public List<ReservationResult> getAllByName(final String name) {
        return reservationQueryRepository.findAllByName(name);
    }
}
