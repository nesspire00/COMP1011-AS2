package Controllers;

import Models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ShowUsersViewController implements Initializable{

    @FXML private Label greetingLabel;
    @FXML private TableView<User> employeeTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> contactInfoColumn;
    @FXML private Label numberOfEmployeesLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<User, String>("contactInfo"));

        try {
            employeeTable.setItems(getEmployees());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        numberOfEmployeesLabel.setText("Total employees: " + Integer.toString(employeeTable.getItems().size()));

        // Show user-dependent buttons

        greetingLabel.setText("Welcome, " + SceneChanger.getLoggedInUser().toString());
    }

    /**
     * Sets up default products for demonstration.
     * @return
     */
    public ObservableList<User> getEmployees() throws SQLException {
        ObservableList<User> employees = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/COMP1011-AS2?useSSL=false", "root", "root");

            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT userId, userName, contactInfo, isManager FROM users");

            while (resultSet.next()){
                User employee = new User(
                        resultSet.getInt(1),
                        resultSet.getBoolean(4),
                        resultSet.getString(2),
                        resultSet.getString(3));
                employees.add(employee);
            }
        }
        catch (SQLException e){
            System.err.println(e);
        }
        finally {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }

        return employees;
    }

    public void goToRegisterUserScene(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event,"../Views/RegisterUserView.fxml", "RegisterUser");
    }

    public void editUserButtonPushed(ActionEvent event) throws IOException {
        RegisterUserViewController controller = new RegisterUserViewController();
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/RegisterUserView.fxml", "Edit user", employeeTable.getSelectionModel().getSelectedItem(), controller);
    }

    public void returnButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
    }
}
