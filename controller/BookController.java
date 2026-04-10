package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Book;
import service.LibraryService;

public class BookController {

    @FXML
    private TextField title, author, quantity;

    @FXML
    public void addBook() {
        Book book = new Book(
            LibraryService.books.size() + 1,
            title.getText(),
            author.getText(),
            Integer.parseInt(quantity.getText())
        );

        LibraryService.addBook(book);
    }
}