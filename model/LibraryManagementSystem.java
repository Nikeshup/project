package model;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LibraryManagementSystem extends Application {

    // --- DATA STORAGE (In-Memory ArrayLists/ObservableLists) ---
    private static ObservableList<Book> bookList = FXCollections.observableArrayList();
    private static ObservableList<User> userList = FXCollections.observableArrayList();
    private static ObservableList<IssuedBook> issuedList = FXCollections.observableArrayList();

    private Stage window;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.window = stage;
        window.setTitle("Library Pro - Integrated System");

        // --- SEED DATA ---
        bookList.addAll(
            new Book("B001", "Java: The Complete Reference", "Herbert Schildt", 5),
            new Book("B002", "Clean Code", "Robert C. Martin", 3),
            new Book("B003", "Design Patterns", "Erich Gamma", 2)
        );
        userList.add(new User("nikesh", "Nikesh", "nikesh@admin.com", "nks123", "Teacher"));
        userList.add(new User("student1", "Sushant", "sushant@edu.com", "pass123", "Student"));

        showLogin();
    }

    // --- 1. LOGIN & REGISTRATION ---
    private void showLogin() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("LIBRARY SYSTEM");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 35));
        title.setFill(Color.WHITE);

        VBox form = new VBox(12);
        form.setMaxWidth(320);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 15;");

        TextField userField = new TextField(); userField.setPromptText("Username (ID)");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password");
        
        Button loginBtn = new Button("LOG IN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Button signupBtn = new Button("Create New Account");
        signupBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-underline: true;");

        loginBtn.setOnAction(e -> {
            String uid = userField.getText();
            String pwd = passField.getText();
            currentUser = userList.stream()
                    .filter(u -> u.getId().equals(uid) && u.getPassword().equals(pwd))
                    .findFirst().orElse(null);

            if (currentUser != null) {
                initMainShell();
            } else {
                new Alert(Alert.AlertType.ERROR, "Access Denied: Invalid Credentials").show();
            }
        });

        signupBtn.setOnAction(e -> showRegistration());

        form.getChildren().addAll(userField, passField, loginBtn, signupBtn);
        layout.getChildren().addAll(title, form);
        window.setScene(new Scene(layout, 1000, 700));
        window.show();
    }

    private void showRegistration() {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label head = new Label("User Registration");
        head.setFont(Font.font("System", FontWeight.BOLD, 22));

        TextField idF = new TextField(); idF.setPromptText("User ID (Username)");
        TextField nameF = new TextField(); nameF.setPromptText("Full Name");
        TextField emailF = new TextField(); emailF.setPromptText("Email");
        PasswordField passF = new PasswordField(); passF.setPromptText("Password");
        ComboBox<String> roleCB = new ComboBox<>(FXCollections.observableArrayList("Student", "Teacher"));
        roleCB.setPromptText("Select Role");
        roleCB.setMaxWidth(Double.MAX_VALUE);

        Button regBtn = new Button("Register Now");
        regBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        regBtn.setOnAction(e -> {
            if(idF.getText().isEmpty() || roleCB.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
                return;
            }
            userList.add(new User(idF.getText(), nameF.getText(), emailF.getText(), passF.getText(), roleCB.getValue()));
            showLogin();
        });

        layout.getChildren().addAll(head, idF, nameF, emailF, passF, roleCB, regBtn, new Button("Back") {{
            setOnAction(a -> showLogin());
        }});
        window.setScene(new Scene(layout, 400, 600));
    }

    // --- 2. DASHBOARD SHELL ---
    private void initMainShell() {
        mainLayout = new BorderPane();
        contentArea = new StackPane();

        // Top Navigation
        HBox topNav = new HBox(20);
        topNav.setPadding(new Insets(15, 25, 15, 25));
        topNav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        
        Label brand = new Label("LIBRARY MANAGEMENT SYSTEM");
        brand.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label userInfo = new Label(currentUser.getName() + " [" + currentUser.getRole() + "]");
        topNav.getChildren().addAll(brand, spacer, userInfo);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 10, 10, 10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #34495e;");

        // Role-Based Buttons
        Button btnBooks = createNavBtn("📚 Browse Books");
        btnBooks.setOnAction(e -> contentArea.getChildren().setAll(createBookManagementView()));

        sidebar.getChildren().add(btnBooks);

        if (currentUser.getRole().equals("Teacher")) {
            Button btnStudents = createNavBtn("👤 Students");
            Button btnIssue = createNavBtn("🔄 Issue Center");
            btnStudents.setOnAction(e -> contentArea.getChildren().setAll(createStudentManagementView()));
            btnIssue.setOnAction(e -> contentArea.getChildren().setAll(createIssueCenterView()));
            sidebar.getChildren().addAll(btnStudents, btnIssue);
        } else {
            Button btnMyBooks = createNavBtn("📖 My Borrowed Books");
            btnMyBooks.setOnAction(e -> contentArea.getChildren().setAll(createStudentLoansView()));
            sidebar.getChildren().add(btnMyBooks);
        }

        Button btnLogout = createNavBtn("🚪 Logout");
        btnLogout.setOnAction(e -> showLogin());
        sidebar.getChildren().addAll(new Separator(), btnLogout);

        mainLayout.setTop(topNav);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        // Default View
        contentArea.getChildren().setAll(createBookManagementView());
        window.setScene(new Scene(mainLayout, 1100, 750));
    }

    // --- 3. BOOK MANAGEMENT (Teacher CRUD + Student View) ---
    private VBox createBookManagementView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));

        Label head = new Label("Library Repository");
        head.setFont(Font.font("System", FontWeight.BOLD, 24));

        TableView<Book> table = new TableView<>(bookList);
        setupColumn(table, "Book ID", "id", 100);
        setupColumn(table, "Title", "title", 250);
        setupColumn(table, "Author", "author", 200);
        setupColumn(table, "Available Qty", "quantity", 120);

        view.getChildren().addAll(head, table);

        if (currentUser.getRole().equals("Teacher")) {
            HBox form = new HBox(10);
            TextField idF = new TextField(); idF.setPromptText("ID");
            TextField tiF = new TextField(); tiF.setPromptText("Title");
            TextField auF = new TextField(); auF.setPromptText("Author");
            TextField qtF = new TextField(); qtF.setPromptText("Qty");
            Button addBtn = new Button("Save Book");
            addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

            addBtn.setOnAction(e -> {
                if(idF.getText().isEmpty()) return;
                bookList.add(new Book(idF.getText(), tiF.getText(), auF.getText(), Integer.parseInt(qtF.getText())));
                idF.clear(); tiF.clear(); auF.clear(); qtF.clear();
            });

            Button delBtn = new Button("Remove Selected");
            delBtn.setOnAction(e -> bookList.remove(table.getSelectionModel().getSelectedItem()));

            form.getChildren().addAll(idF, tiF, auF, qtF, addBtn);
            view.getChildren().addAll(form, delBtn);
        } else {
            Button borrowBtn = new Button("Quick Borrow Selected");
            borrowBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            borrowBtn.setOnAction(e -> {
                Book b = table.getSelectionModel().getSelectedItem();
                if(b != null && b.getQuantity() > 0) {
                    b.setQuantity(b.getQuantity() - 1);
                    issuedList.add(new IssuedBook(b.getId(), currentUser.getId()));
                    table.refresh();
                    new Alert(Alert.AlertType.INFORMATION, "Borrowed Successfully!").show();
                }
            });
            view.getChildren().add(borrowBtn);
        }

        return view;
    }

    // --- 4. STUDENT MANAGEMENT (Teacher Only) ---
    private VBox createStudentManagementView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));
        
        TableView<User> table = new TableView<>(userList);
        setupColumn(table, "User ID", "id", 150);
        setupColumn(table, "Full Name", "name", 200);
        setupColumn(table, "Role", "role", 150);
        
        view.getChildren().addAll(new Label("User Management"), table);
        return view;
    }

    // --- 5. ISSUE CENTER (Teacher Only) ---
    private VBox createIssueCenterView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));

        HBox issueForm = new HBox(15);
        TextField bId = new TextField(); bId.setPromptText("Book ID");
        TextField sId = new TextField(); sId.setPromptText("Student ID");
        Button issueBtn = new Button("Confirm Issue");
        issueBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");

        TableView<IssuedBook> table = new TableView<>(issuedList);
        setupColumn(table, "Book ID", "bookId", 150);
        setupColumn(table, "Student ID", "studentId", 150);
        setupColumn(table, "Issue Date", "date", 250);

        issueBtn.setOnAction(e -> {
            Book b = bookList.stream().filter(book -> book.getId().equals(bId.getText())).findFirst().orElse(null);
            if (b != null && b.getQuantity() > 0) {
                b.setQuantity(b.getQuantity() - 1);
                issuedList.add(new IssuedBook(bId.getText(), sId.getText()));
                bId.clear(); sId.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Inventory Error!").show();
            }
        });

        issueForm.getChildren().addAll(bId, sId, issueBtn);
        view.getChildren().addAll(new Label("Issued Records"), issueForm, table);
        return view;
    }

    // --- 6. STUDENT LOANS (Student View) ---
    private VBox createStudentLoansView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(30));
        
        ObservableList<IssuedBook> myLoans = issuedList.filtered(l -> l.getStudentId().equals(currentUser.getId()));
        TableView<IssuedBook> table = new TableView<>(myLoans);
        setupColumn(table, "Book ID", "bookId", 200);
        setupColumn(table, "Date Borrowed", "date", 300);

        Button returnBtn = new Button("Return Selected Book");
        returnBtn.setOnAction(e -> {
            IssuedBook loan = table.getSelectionModel().getSelectedItem();
            if(loan != null) {
                Book b = bookList.stream().filter(book -> book.getId().equals(loan.getBookId())).findFirst().get();
                b.setQuantity(b.getQuantity() + 1);
                issuedList.remove(loan);
            }
        });

        view.getChildren().addAll(new Label("My Borrowed Books"), table, returnBtn);
        return view;
    }

    // --- HELPERS ---
    private Button createNavBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setPadding(new Insets(12));
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1;"));
        return b;
    }

    private <T> void setupColumn(TableView<T> table, String title, String prop, double width) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        table.getColumns().add(col);
    }

    // --- MODELS ---
    public static class Book {
        private String id, title, author;
        private IntegerProperty quantity;
        public Book(String id, String title, String author, int qty) { 
            this.id = id; this.title = title; this.author = author; 
            this.quantity = new SimpleIntegerProperty(qty); 
        }
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getQuantity() { return quantity.get(); }
        public void setQuantity(int q) { this.quantity.set(q); }
        public IntegerProperty quantityProperty() { return quantity; }
    }

    public static class User {
        private String id, name, email, password, role;
        public User(String id, String name, String email, String password, String role) {
            this.id = id; this.name = name; this.email = email; this.password = password; this.role = role;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getPassword() { return password; }
    }

    public static class IssuedBook {
        private String bookId, studentId, date;
        public IssuedBook(String bId, String sId) {
            this.bookId = bId; this.studentId = sId;
            this.date = LocalDate.now().toString();
        }
        public String getBookId() { return bookId; }
        public String getStudentId() { return studentId; }
        public String getDate() { return date; }
    }
}