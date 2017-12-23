package Controllers;

import Models.PasswordGenerator;
import Models.SmartTV;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterUserViewController implements Initializable, ControllerInterface {

    @FXML private TextField userNameTextField;
    @FXML private TextField contactInfoTextField;
    @FXML private CheckBox isManagerCheckBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label registerHeadingLabel;
    @FXML private Button registerUserButton;
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SceneChanger.getLoggedInUser().isAdmin()) ;
        isManagerCheckBox.setVisible(false);
    }

    /**
     * Try to validate inputs and save the new user into the DB with a hashed password and the salt
     *
     * @param event
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public void registerUserButtonClicked(ActionEvent event) throws SQLException, NoSuchAlgorithmException, IOException {

        Connection conn = null;
        PreparedStatement statement = null;


        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es/gc200348171?useSSL=false", "gc200348171", "YaTa2qzm");

            byte[] salt = PasswordGenerator.getSalt();

            if (user == null) {
                if (passwordField.getText().length() > 8) {
                    if (passwordField.getText().equals(confirmPasswordField.getText())) {
                        statement = conn.prepareStatement("INSERT INTO users (userName, contactInfo, isManager, password, salt) VALUES (?,?,?,?,?)");
                        statement.setString(1, userNameTextField.getText());
                        statement.setString(2, contactInfoTextField.getText());
                        statement.setBoolean(3, isManagerCheckBox.isSelected());
                        statement.setString(4, Models.PasswordGenerator.getSHA512Password(passwordField.getText(), salt));
                        statement.setBlob(5, new javax.sql.rowset.serial.SerialBlob(salt));
                    } else {
                        System.out.println("Passwords do not match");
                    }
                } else {
                    System.out.println("Password is too short");
                }
            } else if (user != null && !passwordField.getText().isEmpty() && !confirmPasswordField.getText().isEmpty()) {
                if (passwordField.getText().length() > 8) {
                    if (passwordField.getText().equals(confirmPasswordField.getText())) {
                        statement = conn.prepareStatement("UPDATE users SET userName = ?, contactInfo = ?, isManager = ?, password = ?, salt = ? WHERE userId = " + user.getUserId());
                        statement.setString(1, userNameTextField.getText());
                        statement.setString(2, contactInfoTextField.getText());
                        statement.setBoolean(3, isManagerCheckBox.isSelected());
                        statement.setString(4, Models.PasswordGenerator.getSHA512Password(passwordField.getText(), salt));
                        statement.setBlob(5, new javax.sql.rowset.serial.SerialBlob(salt));
                    } else {
                        System.out.println("Passwords do not match");
                    }
                } else {
                    System.out.println("Password is too short");
                }
            } else if (user != null) {
                statement = conn.prepareStatement("UPDATE users SET userName = ?, contactInfo = ?, isManager = ? WHERE userId = " + user.getUserId());
                statement.setString(1, userNameTextField.getText());
                statement.setString(2, contactInfoTextField.getText());
                statement.setBoolean(3, isManagerCheckBox.isSelected());
            }

            statement.execute();
            System.out.println("User Saved!");
            SceneChanger sc = new SceneChanger();
            sc.changeScene(event, "../Views/ShowUsersView.fxml", "Employees");
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
        }
    }

    /**
     * Returns user back to appropriate page, whether they are a manager or not
     *
     * @param event
     * @throws IOException
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException {
        if (user != null && !SceneChanger.getLoggedInUser().isAdmin()) {
            SceneChanger sc = new SceneChanger();
            sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
        } else {
            SceneChanger sc = new SceneChanger();
            sc.changeScene(event, "../Views/ShowUsersView.fxml", "Employees");
        }
    }

    /**
     * If an employee object gets passed in, the user is updated, so this method sets up labels and buttons accordingly
     *
     * @param employee
     */
    @Override
    public void preloadData(User employee) {
        user = employee;
        userNameTextField.setText(employee.getUserName());
        contactInfoTextField.setText(employee.getContactInfo());
        isManagerCheckBox.setSelected(employee.isAdmin());

        registerHeadingLabel.setText("Editing user: " + user.toString());
        registerUserButton.setText("Update user");
    }

    @Override
    public void preloadData(SmartTV smartTV) {

    }
}
