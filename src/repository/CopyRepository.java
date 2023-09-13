package repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CopyRepository {
    private DbConnection dbConnection;

    public CopyRepository(DbConnection dbConnection){
        this.dbConnection = dbConnection;
    }

    public int getAvailableCopyId(String isbn) throws SQLException {
        String query = "SELECT bookcopy.id FROM `book` INNER JOIN bookcopy ON book.id = bookcopy.book_id WHERE book.isbn = ? AND bookcopy.status = 'Available' LIMIT 1";

        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        statement.setString(1, isbn);
        ResultSet resultSet = statement.executeQuery();

        int copyId = -1;

        if(resultSet.next()){
            copyId = resultSet.getInt("id");
            updateCopy(copyId);
        }
        return copyId;
    }

    public void updateCopy(int id) throws SQLException {
        String updateQuery = "UPDATE bookcopy SET status = 'not available' WHERE id = ?";
        PreparedStatement updateStatement = dbConnection.getConnection().prepareStatement(updateQuery);
        updateStatement.setInt(1, id);
        updateStatement.executeUpdate();
    }

    public void displaystatistics() throws SQLException{
        String statsQuery = "SELECT (SELECT COUNT(*) FROM `bookcopy` WHERE status = 'available') as available, (SELECT COUNT(*) FROM `bookcopy` WHERE status = 'not available') as not_available, (SELECT COUNT(*) FROM `bookcopy` WHERE status = 'lost') as lost;";
        PreparedStatement statement = dbConnection.getConnection().prepareStatement(statsQuery);
        ResultSet resultSet  = statement.executeQuery();

        if(resultSet.next()){
            int available = resultSet.getInt("available");
            int notAvailable = resultSet.getInt("not_available");
            int lost = resultSet.getInt("lost");

            System.out.println("Statistics");
            System.out.println("--------------------");
            System.out.format("%-15s %s%n", "Status", "Count");
            System.out.println("--------------------");
            System.out.format("%-15s %d%n", "Available", available);
            System.out.format("%-15s %d%n", "Not Available", notAvailable);
            System.out.format("%-15s %d%n", "Lost", lost);
            System.out.println("--------------------");

        }
    }
}
























