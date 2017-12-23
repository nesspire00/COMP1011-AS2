package Controllers;

import Models.SmartTV;
import Models.Television;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class ShowProductsViewController implements Initializable {

    @FXML private TableView<SmartTV> productsTable;
    @FXML private TableColumn<SmartTV, Double> priceColumn;
    @FXML private TableColumn<SmartTV, Integer> screenSizeColumn;
    @FXML private TableColumn<SmartTV, String> modelColumn;
    @FXML private TableColumn<SmartTV, String> resolutionColumn;
    @FXML private TableColumn<SmartTV, String> brandColumn;
    @FXML private TableColumn<SmartTV, Television.panelType> panelTypeColumn;
    @FXML private TableColumn<SmartTV, String> osColumn;
    @FXML private TableColumn<SmartTV, String> smartFeaturesColumn;
    @FXML private Label currentInventoryPriceLabel;
    @FXML private Label tvsInStock;

    // User-specific buttons
    @FXML private Button employeesButton;
    @FXML private Button reportsButton;
    @FXML private Label greetingLabel;

    /**
     * Sets up the TableView columns and default items on view load.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        priceColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, Double>("storePrice"));
        screenSizeColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, Integer>("screenSize"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("modelNo"));
        resolutionColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("resolution"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("brand"));
        panelTypeColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, Television.panelType>("panelType"));
        osColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("operatingSystem"));
        smartFeaturesColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("smartFeatures"));

        try {
            productsTable.setItems(getTVs());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tvsInStock.setText("TVs in stock: " + Integer.toString(getStockNumber()));
        currentInventoryPriceLabel.setText("Total inventory value: " + NumberFormat.getCurrencyInstance().format(BigDecimal.valueOf(getStockPrice())));


        // Show user-dependent buttons
        if (!SceneChanger.getLoggedInUser().isAdmin()) {
            reportsButton.setVisible(false);
            employeesButton.setText("Edit profile");
        }

        greetingLabel.setText("Welcome, " + SceneChanger.getLoggedInUser().toString());
    }

    /**
     * Returns the count of stock items.
     *
     * @return number of items in stock
     */
    private int getStockNumber() {
        return productsTable.getItems().size();
    }

    /**
     * Changes scene to the add new product scene.
     *
     * @param event
     * @throws IOException
     */
    public void addNewProductButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/AddNewProductView.fxml", "Add a new TV");

    }

    /**
     * Gets the total cost of all stock.
     *
     * @return monetary value of stock.
     */
    private double getStockPrice() {
        double price = 0;
        ObservableList<SmartTV> stock = productsTable.getItems();

        for (Television item : stock) {
            price += item.getStorePrice();
        }
        return price;
    }

    /**
     * Get product info from the DB
     *
     * @return
     */
    public ObservableList<SmartTV> getTVs() throws SQLException {
        ObservableList<SmartTV> tvs = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es/gc200348171?useSSL=false", "gc200348171", "YaTa2qzm");

            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM television");

            while (resultSet.next()) {
                SmartTV tv = new SmartTV(resultSet.getDouble(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        Television.panelType.valueOf(resultSet.getString(7)),
                        resultSet.getString(8),
                        resultSet.getString(9),
                        resultSet.getInt(1));
                tvs.add(tv);
            }
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

        return tvs;
    }

    /**
     * Destroys the logged in user object and goes back to log in scene
     *
     * @param event
     * @throws IOException
     */
    public void logOutButtonPushed(ActionEvent event) throws IOException {
        SceneChanger.setLoggedInUser(null);
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/LoginBoxView.fxml", "Log in");
    }

    /**
     * Shows the all employee scene to an admin user
     *
     * @param event
     * @throws IOException
     */
    public void employeesButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        if (SceneChanger.getLoggedInUser().isAdmin()) {
            sc.changeScene(event, "../Views/ShowUsersView.fxml", "Employees");
        } else {
            RegisterUserViewController controller = new RegisterUserViewController();
            sc.changeScene(event, "../Views/RegisterUserView.fxml", "Edit your profile", SceneChanger.getLoggedInUser(), controller);
        }
    }

    /**
     * Gets the selected item and shows the sales scene
     *
     * @param event
     * @throws IOException
     */
    public void sellItemButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        SaleViewController controller = new SaleViewController();
        try {
            sc.changeScene(event, "../Views/SaleView.fxml", "Sell item", productsTable.getSelectionModel().getSelectedItem(), controller);
        } catch (NullPointerException e) {
            greetingLabel.setText("Pick an item to sell!");
        }
    }

    /**
     * Show the sales report scene to an admin user
     *
     * @param event
     * @throws IOException
     */
    public void reportButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScene(event, "../Views/ReportView.fxml", "Report");
    }
}
