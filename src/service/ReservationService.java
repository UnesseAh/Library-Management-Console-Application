package service;

import domain.Reservation;
import repository.ReservationRepository;
import repository.MemberRepository;
import repository.CopyRepository;
import repository.BookRepository;

import java.sql.SQLException;

public class ReservationService {
    private ReservationRepository reservationRepository;
    private MemberRepository memberRepository;
    private CopyRepository copyRepository;
    private BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository, CopyRepository copyRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    public Reservation makeReservation(String isbn, int memberNumber) throws SQLException {
        if (bookRepository.isBookExists(isbn)) {
            if (memberRepository.isMemberExists(memberNumber)) {
                int memberId = memberRepository.getMemberId(memberNumber);
                int copyId = copyRepository.getAvailableCopyId(isbn);
                java.sql.Date borrowingDate = new java.sql.Date(System.currentTimeMillis());
                java.sql.Date returnDate = new java.sql.Date(borrowingDate.getTime() + 14);

                Reservation reservation = new Reservation(memberId, copyId, borrowingDate, returnDate);
                return reservationRepository.makeReservation(reservation);
            } else {
                System.out.println("Member doesn't exist");
                return null;
            }
        } else {
            System.out.println("Book with the provided ISBN doesn't exist");
            return null;
        }
    }

    public int returnBook (String isbn, int memberId) throws SQLException {
        return reservationRepository.returnBook(isbn, memberId);
    }
}
