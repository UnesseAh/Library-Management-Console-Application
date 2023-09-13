package domain;

import repository.*;
import service.CopyService;
import service.MemberService;
import service.BookService;
import service.ReservationService;
import java.sql.SQLException;
import java.util.*;

public class Main {
    private static DbConnection dbConnection;

    static {
        try {
            dbConnection = new DbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static BookRepository bookRepository = new BookRepository(dbConnection);
    private static BookService bookService = new BookService(bookRepository);

    private static MemberRepository memberRepository = new MemberRepository(dbConnection);
    private static MemberService memberService = new MemberService(memberRepository);

    private static ReservationRepository reservationRepository = new ReservationRepository(dbConnection);
    private static ReservationService reservationService = new ReservationService(reservationRepository);

    private static CopyRepository copyRepository = new CopyRepository(dbConnection);
    private static CopyService copyService = new CopyService(copyRepository);

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {

        ShowMenu();

        try {
            int choiceNumber = scanner.nextInt();
            scanner.nextLine();

            switch (choiceNumber) {
                case 1:
                    addNewBook();
                    break;
                case 2:
                    updateBook();
                    break;
                case 3:
                    deleteBook();
                    break;
                case 4:
                    displayAvailableBooks();
                    break;
                case 5:
                    displayBorrowedBooks();
                    break;
                case 6:
                    searchBook();
                    break;
                case 7:
                    borrowBook();
                    break;
                case 8:
                    returnBook();
                    break;
                case 9:
                    showStatistics();
                    break;
                case 0:
                    System.out.println("You exited the program successfully.");
                default:
                    System.out.println("Option (" + choiceNumber + ") doesn't exist.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private static void ShowMenu(){
        System.out.println("╔═════════════════════════════════════════╗");
        System.out.println("║           Library Management            ║");
        System.out.println("║═════════════════════════════════════════║");
        System.out.println("║ 1. ║ Add a new book                     ║");
        System.out.println("║ 2. ║ Update a book                      ║");
        System.out.println("║ 3. ║ Delete a book                      ║");
        System.out.println("║ 4. ║ Display all 'available' books      ║");
        System.out.println("║ 5. ║ Display all 'borrowed' books       ║");
        System.out.println("║ 6. ║ Search for a book                  ║");
        System.out.println("║ 7. ║ Borrow a book                      ║");
        System.out.println("║ 8. ║ Return a book                      ║");
        System.out.println("║ 9. ║ Statistics                         ║");
        System.out.println("║ 0. ║ Exit the program                   ║");
        System.out.println("╚═════════════════════════════════════════╝");
        System.out.println("Enter your choice: ");
    };

    private static void addNewBook() throws SQLException{

        System.out.println("Enter the title of the book:");
        String title = scanner.nextLine();

        System.out.println("Enter the ISBN of the book:");
        String ISBN = scanner.nextLine();

        System.out.println("Enter the name of the author:");
        String author = scanner.nextLine();

        System.out.println("Enter the quantity of the book:");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        Book newBook = new Book(title, author, ISBN, quantity);

        bookService.addBook(newBook);
    }

    private static void updateBook() throws SQLException{
        System.out.println("Enter the ISBN of the book to update :");
        String isbn = scanner.nextLine();

        if(bookService.isBookExists(isbn)){
            System.out.println("Enter the new title of the book:");
            String title = scanner.nextLine();

            System.out.println("Enter the name of the author:");
            String author = scanner.nextLine();

            System.out.println("Enter the new quantity of the book:");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            Book book = new Book(title, author, isbn, quantity);

            bookService.updateBook(book);

            System.out.println("Book has been updated successfully!");
        }else {
            System.out.println("No book has the ISBN you entered.");
        }
    }
    private static void deleteBook() throws SQLException{
        System.out.println("Enter the ISBN of the book");
        String isbn = scanner.nextLine();

        if(bookService.isBookExists(isbn)) {
            bookService.deleteBook(isbn);
            System.out.println("Book deleted successfully!");
        }else {
            System.out.println("No book has the ISBN you entered.");
        }
    }
    private static void displayAvailableBooks() throws SQLException {
        List<Book> availableBooks = bookService.getAllAvailableBooks();

        if(availableBooks.isEmpty()){
            System.out.println("No available books found.");
        }else {
            bookListLoop(availableBooks);
        }
    }

    private static void bookListLoop(List<Book> availableBooks) {
        for(Book book : availableBooks){
            System.out.println("Title : " + book.getTitle());
            System.out.println("Author id : " + book.getAuthor());
            System.out.println("ISBN : " + book.getIsbn());
            System.out.println("Quantity : " + book.getQuantity());
            System.out.println("+++++++++++++++++++++++++++++");
        }
    }

    private static void displayBorrowedBooks() throws SQLException {
        List<Book> borrowedBooks = bookService.getAllBorrowedBooks();

        if(borrowedBooks.isEmpty()){
            System.out.println("No borrowed books are available");
        }else {
            bookListLoop(borrowedBooks);
        }
    }

    private static void searchBook() throws SQLException{
        System.out.println("Search for books by title or author : ");
        String titleOrAuthor = scanner.nextLine();

        List<Book> foundedBooks = bookService.searchBook(titleOrAuthor);


        if(foundedBooks.isEmpty()){
            System.out.println("No books found.");
        }else {
            for(Book book : foundedBooks) {
                System.out.println("Title :" + book.getTitle());
                System.out.println("Author ID :" + book.getAuthor());
                System.out.println("ISBN :" + book.getIsbn());
                System.out.println("Quantity :" + book.getQuantity());
                System.out.println("+++++++++++++++++++++++++++++");
            }
        }

    }
    private static void borrowBook() throws SQLException{
        System.out.println("Enter the ISBN of the book you want to borrow :");
        String isbn = scanner.nextLine();

        if(bookService.isBookExists(isbn)){
            System.out.println("Enter your member number :");
            int memberNumber = scanner.nextInt();
            scanner.nextLine();
            if(memberService.isMemberExists(memberNumber)){
                int memberId = memberService.getMemberId(memberNumber);
                int copyId = copyService.getAvailableCopyId(isbn);
                java.sql.Date borrowingDate = null;
                java.sql.Date returnDate = null;

                System.out.println("memberid :" + memberId);
                System.out.println("copyid :" + copyId);
                System.out.println("borrowingdate :" + borrowingDate);
                System.out.println("return date :" + returnDate);

                Reservation reservation = new Reservation(memberId, copyId, borrowingDate, returnDate);

                System.out.println(reservationService.makeReservation(reservation).getCopyId());

            }else {
                System.out.println("member doesn't exist");
            }
        }else {
            System.out.println("Book doesn't exists");
        }
    }
    private static void returnBook() throws SQLException {
        System.out.println("Enter the ISBN of the book :");
        String isbn = scanner.nextLine();
        if(bookService.isBookExists(isbn)){
            System.out.println("Enter the member Code");
            int memberNumber = scanner.nextInt();
            scanner.nextLine();
            scanner.close();
            if(memberService.isMemberExists(memberNumber)) {
                int memberId = memberService.getMemberId(memberNumber);
                int test = reservationService.returnBook(isbn, memberId);
                System.out.println("Book returned successfully!");
            }else {
                System.out.println("There is no member with the provided member number");
            }
        }else {
            System.out.println("There is no book with the provided ISBN");
        }
    }
    private static void showStatistics() throws SQLException {
        copyRepository.displaystatistics();
        System.out.println("Statistics");
    }
}