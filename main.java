import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class CrudApplication extends Application {

    private Connection connection;
    private ObservableList<Product> productList;
    private TableView<Product> tableView;
    private TextField nameField, descriptionField, priceField, idField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CRUD Application");

        // Create GUI components
        idField = new TextField();
        nameField = new TextField();
        descriptionField = new TextField();
        priceField = new TextField();
        tableView = new TableView<>();

        // Set up TableView
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn);

        // Buttons
        Button loadButton = new Button("Load Data");
        loadButton.setOnAction(e -> loadData());

        Button insertButton = new Button("Insert Data");
        insertButton.setOnAction(e -> insertData());

        Button updateButton = new Button("Update Data");
        updateButton.setOnAction(e -> updateData());

        Button deleteButton = new Button("Delete Data");
        deleteButton.setOnAction(e -> deleteData());

        // Layout
        GridPane grid = new GridPane();
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(loadButton, 0, 4);
        grid.add(insertButton, 1, 4);
        grid.add(updateButton, 0, 5);
        grid.add(deleteButton, 1, 5);
        grid.add(tableView, 0, 6, 2, 1);

        // Scene and Stage
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize database connection
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // Update with your own database URL, username, and password
            String url = "jdbc:mysql://localhost:3306/your_database_name";
            String user = "your_username";
            String password = "your_password";
            connection = DriverManager.getConnection(url, user, password);
            productList = FXCollections.observableArrayList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        productList.clear();
        String query = "SELECT * FROM Product"; // Replace with your actual table name
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                productList.add(new Product(id, name, description, price));
            }
            tableView.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        double price = Double.parseDouble(priceField.getText());
        String query = "INSERT INTO Product (name, description, price) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setDouble(3, price);
            statement.executeUpdate();
            loadData(); // Refresh the TableView
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        String description = descriptionField.getText();
        double price = Double.parseDouble(priceField.getText());
        String query = "UPDATE Product SET name=?, description=?, price=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setDouble(3, price);
            statement.setInt(4, id);
            statement.executeUpdate();
            loadData(); // Refresh the TableView
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteData() {
        int id = Integer.parseInt(idField.getText());
        String query = "DELETE FROM Product WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            loadData(); // Refresh the TableView
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
