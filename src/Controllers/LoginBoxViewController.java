package Controllers;

import Models.PasswordGenerator;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginBoxViewController implements Initializable {

    @FXML TextField userNameTextField;
    @FXML PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userNameTextField.setText("2");
        passwordField.setText("qqqqqqqqq");
    }

    public void loginButtonPushed(ActionEvent event) throws IOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        int userId = Integer.parseInt(userNameTextField.getText());

        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/COMP1011-AS2", "root", "root");

            String sql = "SELECT * FROM users WHERE userId = ?";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);

            resultSet = ps.executeQuery();

            String dbPassword=null;
            byte[] salt = null;
            User currentUser = null;

            while (resultSet.next())
            {
                dbPassword = resultSet.getString("password");

                Blob blob = resultSet.getBlob("salt");

                int blobLength = (int) blob.length();
                salt = blob.getBytes(1, blobLength);

                currentUser = new User(resultSet.getInt("userId"),
                        resultSet.getBoolean("isManager"),
                        resultSet.getString("userName"),
                        resultSet.getString("contactInfo"));
            }

            String userPW = PasswordGenerator.getSHA512Password(passwordField.getText(), salt);

            SceneChanger sc = new SceneChanger();

            if (userPW.equals(dbPassword))
                SceneChanger.setLoggedInUser(currentUser);

            if (userPW.equals(dbPassword))
                sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
            else
                System.out.println("The userName and password do not match");
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

    }
    }

