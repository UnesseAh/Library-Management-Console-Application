package service;

import domain.Reservation;
import repository.ReservationRepository;

import java.sql.SQLException;

public class ReservationService {
    private ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation makeReservation(Reservation reservation) throws SQLException {
        return reservationRepository.makeReservation(reservation);
    }

    public int returnBook (String isbn, int memberId) throws SQLException {
        return reservationRepository.returnBook(isbn, memberId);
    }
}
