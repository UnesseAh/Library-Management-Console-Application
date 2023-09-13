package service;

import domain.Book;
import repository.BookRepository;

import java.sql.SQLException;
import java.util.List;

public class BookService {
    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) throws SQLException {
        if (bookRepository.getAuthorId(book.getAuthor()) < 1) {
            bookRepository.addAuthor(book.getAuthor());
        }
        int authorId = bookRepository.getAuthorId(book.getAuthor());

        if (validateBook(book)) {
            bookRepository.addBook(book, authorId);
        }else {
            System.out.println("The data you entered is not valid :\n Make sure the title is not empty, the quantity is greater than 0, the author exists and the ISBN starts with a number followed by a dash.");
        }
    }

    public void updateBook(Book book) throws SQLException {

        if (bookRepository.getAuthorId(book.getAuthor()) < 1) {
            bookRepository.addAuthor(book.getAuthor());
        }
        int authorId = bookRepository.getAuthorId(book.getAuthor());

        if (validateBook(book)) {
            int totalCopies = bookRepository.numberOfTotalCopies(book.getIsbn());
            int copiesDisponible = bookRepository.numberOfDisponibleCopies(book.getIsbn());

            int userQuantity = book.getQuantity();
            int borrowedBooks = totalCopies - copiesDisponible;

            if(userQuantity > totalCopies) {
                int copiesToAdd = userQuantity - totalCopies;
                bookRepository.updateBook(book, authorId);
                int bookId = bookRepository.getBookId(book.getIsbn());
                bookRepository.addCopies(bookId, copiesToAdd);
            }else {
                if(userQuantity < borrowedBooks){
                    System.out.println("Please enter a quantity number greater than : " + borrowedBooks);
                }else {
                    int copiesToDelete = totalCopies - userQuantity;
                    bookRepository.updateBook(book, authorId);
                    bookRepository.deleteCopies(copiesToDelete);
                }
            }
        } else {
            System.out.println("The data you entered is not valid :\n Make sure the title is not empty, the quantity is greater than 0, the author exists and the ISBN starts with a number followed by a dash.");
        }
    }

    public boolean validateBook(Book book) {
        boolean isTitleValid = !book.getTitle().isEmpty() && book.getTitle().length() <= 100;
        boolean isISBNValid = book.getIsbn().matches("\\d-\\w{14}");
        boolean isQuantityValid = book.getQuantity() > 0;
        return isTitleValid && isISBNValid && isQuantityValid;
    }

    public boolean isBookExists(String isbn) throws SQLException {
        boolean bookExists = bookRepository.isBookExists(isbn);
        return bookExists;
    }

    public void deleteBook(String isbn) throws SQLException{
        boolean isISBNValid = isbn.matches("\\d-\\w{14}");

        if(!isISBNValid){
            bookRepository.deleteCopies(isbn);
            bookRepository.insertReservationsIntoArchive(isbn);
        }else {
            System.out.println("ISBN must start with a character followed by a dash.");
        }
    }

    public List<Book> getAllAvailableBooks() throws SQLException {
        return bookRepository.getAllAvailableBooks();
    }

    public List<Book> getAllBorrowedBooks() throws SQLException {
        return bookRepository.getAllBorrowedBooks();
    }

    public List<Book> searchBook(String title) throws SQLException{
        return bookRepository.searchBook(title);
    }

}
