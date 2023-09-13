package repository;

import domain.Reservation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRepository {
    private static DbConnection dbConnection;
    public ReservationRepository(DbConnection dbConnection){this.dbConnection = dbConnection;}

    public static Reservation makeReservation(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (copy_id, member_id, borrowing_date, return_date) VALUES (?, ?, SYSDATE(), SYSDATE())";
        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        statement.setInt(1, reservation.getCopyId());
        statement.setInt(2, reservation.getMemberId());
        statement.executeUpdate();
        return reservation;
    }

    public int returnBook (String isbn, int memberId) throws SQLException {
        String query = "SELECT reservation.id FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id INNER JOIN reservation ON reservation.copy_id = bookcopy.id WHERE reservation.member_id = ? AND reservation.copy_id IN (SELECT bookcopy.id FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id WHERE book.isbn = ?)";
        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        statement.setInt(1, memberId);
        statement.setString(2, isbn);
        ResultSet resultSet = statement.executeQuery();

        int reservationId = 0;
        if(resultSet.next()){
            reservationId = resultSet.getInt("id");

            String updateStatus = "UPDATE bookcopy SET status = 'available' WHERE id = (SELECT copy_id FROM reservation WHERE id = ?)";
            PreparedStatement updateStatement = dbConnection.getConnection().prepareStatement(updateStatus);
            updateStatement.setInt(1, reservationId);
            updateStatement.executeUpdate();

            String deleteReservation = "DELETE FROM reservation WHERE id = ?";
            PreparedStatement deleteStatement = dbConnection.getConnection().prepareStatement(deleteReservation);
            deleteStatement.setInt(1, reservationId);
            deleteStatement.executeUpdate();


        }
        return reservationId;
    }
}
