package controller;

import model.Book;
import model.User;
import service.LibraryService;

public class IssueController {

    public void issueBook(User user, Book book) {
        boolean success = LibraryService.issueBook(user, book);

        if (success) {
            System.out.println("Book Issued");
        } else {
            System.out.println("Out of Stock");
        }
    }
}