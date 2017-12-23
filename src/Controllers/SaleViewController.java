package Controllers;

import Models.SmartTV;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SaleViewController implements Initializable, ControllerInterface {

    @FXML private Label itemNameLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private DatePicker transactionDatePicker;
    @FXML private TextField priceTextField;
    @FXML private Label messageLabel;
    private int tvId;
    private double storePrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> quantityValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
        quantitySpinner.setValueFactory(quantityValueFactory);
        quantitySpinner.setEditable(false);
        transactionDatePicker.setValue(LocalDate.now());
        messageLabel.setText("");
    }

    public void backButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
    }

    @Override
    public void preloadData(User employee) {

    }

    /**
     * Load the item name, store price and id from the main scene
     *
     * @param tv
     */
    @Override
    public void preloadData(SmartTV tv) {
        itemNameLabel.setText(tv.getBrand() + " " + tv.getModelNo() + " " + tv.getResolution() + " (ID:" + tv.getTvId() + ")");
        tvId = tv.getTvId();
        storePrice = tv.getStorePrice();
        priceTextField.setPromptText("Original price: " + NumberFormat.getCurrencyInstance().format(BigDecimal.valueOf(storePrice)));
    }

    /**
     * Validate and record the sale in db
     *
     * @throws SQLException
     */
    public void saleButtonPushed() throws SQLException {
        try {
            double salePrice = Double.parseDouble(priceTextField.getText());
            if (salePrice > 0 && salePrice < 99999.99) {
                Connection conn = null;
                PreparedStatement statement = null;
                ResultSet resultSet = null;

                try {
                    conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es/gc200348171?useSSL=false", "gc200348171", "YaTa2qzm");
                    conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es/gc200348171?useSSL=false", "gc200348171", "YaTa2qzm");

                    statement = conn.prepareStatement("INSERT INTO sales (itemId, storePrice, salePrice, saleDate) VALUES (?,?,?,?)");
                    statement.setInt(1, tvId);
                    statement.setDouble(2, quantitySpinner.getValue() * storePrice);
                    statement.setDouble(3, quantitySpinner.getValue() * salePrice);
                    statement.setDate(4, Date.valueOf(transactionDatePicker.getValue()));

                    statement.execute();

                    messageLabel.setText("Sale successful, you can now return to the inventory or sell again.");
                } catch (SQLException e) {
                    System.err.println(e);
                } finally {
                    if (conn != null)
                        conn.close();
                    if (statement != null)
                        statement.close();
                    if (resultSet != null)
                        resultSet.close();
                }
            } else {
                messageLabel.setText("Price cannot be negative or 0 and no more than $99999.99");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid price");
        }
    }
}
