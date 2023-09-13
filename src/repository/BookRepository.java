package repository;

import domain.Book;
import jdk.jshell.spi.SPIResolutionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class BookRepository {
    private DbConnection dbConnection;

    public BookRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addBook(Book book, int authorId) {
        String query = "INSERT INTO book (title, author_id, isbn, quantity) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            statement.setString(1, book.getTitle());
            statement.setInt(2, getAuthorId(book.getAuthor()));
            statement.setString(3, book.getIsbn());
            statement.setInt(4, book.getQuantity());

            statement.executeUpdate();
            System.out.println("New book added successfully.");
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to add the book: " + e.getMessage());
        }
    };

    public int getAuthorId(String author) throws SQLException {
        String query = "SELECT ID FROM author WHERE name = ?";
        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query);
        stmt.setString(1, author);
        ResultSet resultSet = stmt.executeQuery();

        int authorId = -1;

        if (resultSet.next()) {
            authorId = resultSet.getInt("ID");
        }
        return authorId;
    }

    private String getAuthorName(int author_id) throws SQLException {
        String query = "SELECT name FROM author WHERE id = ?";
        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query);
        stmt.setInt(1, author_id);
        ResultSet resultSet = stmt.executeQuery();

        String authorName = "";

        if (resultSet.next()) {
            authorName = resultSet.getString("name");
        }

        return authorName;
    }

    public int getBookId(String isbn) throws SQLException {
        String query = "SELECT ID FROM book WHERE isbn = ?";
        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query);
        stmt.setString(1, isbn);
        ResultSet resultSet = stmt.executeQuery();

        int bookId = -1;

        if (resultSet.next()) {
            bookId = resultSet.getInt("id");
        }
        return bookId;
    }
    public int numberOfDisponibleCopies(String isbn) throws SQLException {
        String totalOfAvailableCopies = "SELECT COUNT(*) AS availableCopies FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id WHERE isbn = ? AND status = 'available'";

        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(totalOfAvailableCopies);
        stmt.setString(1, isbn);
        ResultSet resultSet = stmt.executeQuery();

        int availableCopies = -1;

        if (resultSet.next()){
            availableCopies = resultSet.getInt("availableCopies");
        }

        return availableCopies;
    }
    public int numberOfTotalCopies(String isbn) throws SQLException {
        String totalOfAllCopies = "SELECT COUNT(*) AS totalCopies FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id WHERE isbn = ?";

        PreparedStatement stmt2 = dbConnection.getConnection().prepareStatement(totalOfAllCopies);
        stmt2.setString(1, isbn);
        ResultSet resultSet2 = stmt2.executeQuery();

        int totalCopies = -1;

        if(resultSet2.next()){
            totalCopies = resultSet2.getInt("totalCopies");
        }

        return totalCopies;
    }

    public boolean isBookExists(String isbn) throws SQLException {
        String query = "SELECT * FROM book WHERE isbn = ?";

        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        statement.setString(1, isbn);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? true : false;
    }


    public void updateBook(Book book, int authorId) throws SQLException{
        String query = "UPDATE book SET title = ? , author_id = ? , quantity = ? WHERE isbn = ?";

        try {
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            statement.setString(1, book.getTitle());
            statement.setInt(2, authorId);
            statement.setString(4, book.getIsbn());
            statement.setInt(3, book.getQuantity());
            statement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Something went wrong when trying to update book : " + e.getMessage());
        }
    }

    public void deleteCopies(int copiesToDelete){
        String query = "DELETE FROM bookcopy WHERE status = 'available' limit ?";
        try {
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            statement.setInt(1, copiesToDelete);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to delete copies: " + e.getMessage());
        }
    }

    public void addCopies(int bookId, int copiesToAdd){
        String query = "INSERT INTO bookcopy (book_id, status) VALUES (?, 'available')";
        try {
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            for(int i = 0; i < copiesToAdd ; i++){
                statement.setInt(1, bookId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to add copies : " + e.getMessage());
        }
    }

    public void addAuthor(String authorName){
        String query = "INSERT INTO author (name) VALUES (?)";
        try {
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            statement.setString(1, authorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to add new author: " + e.getMessage());
        }
    }

    public void deleteCopies(String isbn) throws SQLException {
        String deleteCopies = "delete from bookcopy WHERE book_id = (select id FROM book WHERE isbn = ?);";
        String updateBookStatus = "UPDATE book SET deleted = 1 WHERE isbn = ?";

        try{
            PreparedStatement updateCopiesStatement = dbConnection.getConnection().prepareStatement(deleteCopies);
            updateCopiesStatement.setString(1, isbn);
            updateCopiesStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Something went wrong when trying to update the status of the copies : " + e.getMessage());
        }

        try{
            PreparedStatement deleteStatement = dbConnection.getConnection().prepareStatement(updateBookStatus);
            deleteStatement.setString(1, isbn);
            deleteStatement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Something went wrong when trying to delete book : " + e.getMessage());
        }
    }

    public void insertReservationsIntoArchive(String isbn) throws SQLException {
        String selectQuery = "SELECT * FROM reservation WHERE copy_id in (SELECT bookcopy.id FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id WHERE book.isbn = ?)";

        PreparedStatement selectStatement = dbConnection.getConnection().prepareStatement(selectQuery);
        selectStatement.setString(1, isbn);
        ResultSet resultSet = selectStatement.executeQuery();

        String insertQuery = "INSERT INTO reservation_archive (copy_id, member_id, borrowing_date, return_date) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = dbConnection.getConnection().prepareStatement(insertQuery);

        while(resultSet.next()){
            int bookCopy = resultSet.getInt("copy_id");
            int memberId = resultSet.getInt("member_id");
            Date borrowingDate = resultSet.getDate("borrowing_date");
            Date returnDate = resultSet.getDate("return_date");

            insertStatement.setInt(1, bookCopy );
            insertStatement.setInt(2, memberId);
            insertStatement.setDate(3, borrowingDate);
            insertStatement.setDate(4, returnDate);
            insertStatement.executeUpdate();
        }

        String deleteQuery = "DELETE FROM Reservation WHERE id in (SELECT id FROM reservation WHERE copy_id in (SELECT bookcopy.id FROM bookcopy INNER JOIN book ON book.id = bookcopy.book_id WHERE book.isbn = ?))";
        PreparedStatement deleteStatement = dbConnection.getConnection().prepareStatement(deleteQuery);
        deleteStatement.setString(1, isbn);
        deleteStatement.executeUpdate();

    }

    public List<Book> getAllAvailableBooks() throws SQLException{
        List<Book> availableBooks = new ArrayList<>();

        String query = "SELECT book.id, book.title, book.author_id, book.isbn, book.quantity FROM book WHERE isbn IN (SELECT DISTINCT(book.isbn) FROM book INNER JOIN bookcopy ON book.id = bookcopy.book_id WHERE bookcopy.status = 'available')";
        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            String title = resultSet.getString("title");
            String authorName = getAuthorName(resultSet.getInt("author_id"));
            String isbn = resultSet.getString("isbn");
            int quantity = resultSet.getInt("quantity");

            Book book = new Book(title, "authorId", isbn, quantity);

            availableBooks.add(book);
        }
        return  availableBooks;
    }
    public List<Book> getAllBorrowedBooks() throws SQLException{
        List<Book> availableBooks = new ArrayList<>();

        String query = "SELECT book.id, book.title, book.author_id, book.isbn, book.quantity FROM book WHERE isbn IN (SELECT DISTINCT(book.isbn) FROM book INNER JOIN bookcopy ON book.id = bookcopy.book_id WHERE bookcopy.status = 'not available')";
        PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            String title = resultSet.getString("title");
            String authorName = getAuthorName(resultSet.getInt("author_id"));
            String isbn = resultSet.getString("isbn");
            int quantity = resultSet.getInt("quantity");

            Book book = new Book(title, "authorId", isbn, quantity);

            availableBooks.add(book);
        }
        return  availableBooks;
    }


    public List<Book> searchBook(String titleOrAuthor) throws SQLException {
        List<Book> foundedBooks = new ArrayList<>();

        String query = "SELECT book.title, author.name, book.author_id, book.isbn, book.quantity FROM book INNER JOIN author ON author.id = book.author_id where book.title LIKE ? OR author.name LIKE ?";

        PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query);
        stmt.setString(1, "%" + titleOrAuthor + "%");
        stmt.setString(2, "%" + titleOrAuthor + "%");
        ResultSet resultSet = stmt.executeQuery();

        while (resultSet.next()){
            String title = resultSet.getString("title");
            String authorName = getAuthorName(resultSet.getInt("author_id"));
            String isbn = resultSet.getString("isbn");
            int quantity = resultSet.getInt("quantity");

            Book book = new Book(title, authorName, isbn, quantity);

            foundedBooks.add(book);
        }
        return  foundedBooks;
    }
}