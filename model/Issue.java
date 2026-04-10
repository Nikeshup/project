package model;

import java.time.LocalDate;

public class Issue {
    private User user;
    private Book book;
    private LocalDate issueDate;

    public Issue(User user, Book book) {
        this.user = user;
        this.book = book;
        this.issueDate = LocalDate.now();
    }

    public User getUser() { return user; }
    public Book getBook() { return book; }
}