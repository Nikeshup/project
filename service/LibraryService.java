package service;

import model.Book;
import model.User;
import model.Issue;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    public static List<Book> books = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    public static List<Issue> issues = new ArrayList<>();

    // Static sample data
    static {
        users.add(new User("admin", "123", "admin"));
        users.add(new User("student", "123", "student"));

        books.add(new Book(1, "Java Basics", "James Gosling", 5));
        books.add(new Book(2, "Data Structures", "Mark Allen", 3));
    }

    // LOGIN
    public static User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // ADD BOOK
    public static void addBook(Book book) {
        books.add(book);
    }

    // ISSUE BOOK
    public static boolean issueBook(User user, Book book) {
        if (book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1);
            issues.add(new Issue(user, book));
            return true;
        }
        return false;
    }

    // RETURN BOOK
    public static void returnBook(Book book) {
        book.setQuantity(book.getQuantity() + 1);
    }
}