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

public class ShowProductsViewController implements Initializable, ControllerInterface {

    @FXML private Button sellProductButton;
    @FXML private TableView<SmartTV> productsTable;
    @FXML private TableColumn<SmartTV, Double> priceColumn;
    @FXML private TableColumn<SmartTV, Integer> screenSizeColumn;
    @FXML private TableColumn<SmartTV, String> modelColumn;
    @FXML private TableColumn<SmartTV, String> resolutionColumn;
    @FXML private TableColumn<SmartTV, String> brandColumn;
    @FXML private TableColumn<SmartTV, String> featuresColumn;
    @FXML private TableColumn<SmartTV, Television.panelType> panelTypeColumn;
    @FXML private TableColumn<SmartTV, String> osColumn;
    @FXML private TableColumn<SmartTV, String> smartFeaturesColumn;
    @FXML private Label currentInventoryPriceLabel;
    @FXML private Label tvsInStock;

    /**
     * Sets up the TableView columns and default items on view load.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sellProductButton.setDisable(true);

        priceColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, Double>("storePrice"));
        screenSizeColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, Integer>("screenSize"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("modelNo"));
        resolutionColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("resolution"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("brand"));
        featuresColumn.setCellValueFactory(new PropertyValueFactory<SmartTV, String>("features"));
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

    }

    /**
     * Returns the count of stock items.
     * @return number of items in stock
     */
    private int getStockNumber(){
        return productsTable.getItems().size();
    }

    /**
     * Changes scene to the add new product scene and passes current list of products for further editing.
     * @param event
     * @throws IOException
     */
    public void addNewProductButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        ObservableList<SmartTV> currentProductsList = productsTable.getItems();

        AddNewProductViewController controller = new AddNewProductViewController();

        sc.changeScene(event, "../Views/AddNewProductView.fxml", "Add new product", currentProductsList, controller);
    }

    /**
     * Gets the total cost of all stock.
     * @return monetary value of stock.
     */
    private double getStockPrice(){
        double price = 0;
        ObservableList<SmartTV> stock = productsTable.getItems();

        for(Television item : stock){
            price += item.getStorePrice();
        }
        return price;
    }

    /**
     * Sets up default products for demonstration.
     * @return
     */
//    public ObservableList<SmartTV> getTVs(){
//        ObservableList<SmartTV> televisions = FXCollections.observableArrayList();
//        televisions.add(new SmartTV(1099.99, 65, "UN65MU6290FXZC",
//                                    "4K UHD", "Samsung", "HDR", Television.panelType.AMOLED,
//                                    "Tizen", "YouTube, Netflix"));
//
//        televisions.add(new SmartTV(1999.99, 55, "OLED55C7P",
//                                    "4K UHD", "LG", "HDR", Television.panelType.OLED,
//                                    "webOS", "YouTube, Netflix"));
//
//        return televisions;
//    }
    public ObservableList<SmartTV> getTVs() throws SQLException {
        ObservableList<SmartTV> tvs = FXCollections.observableArrayList();

        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/COMP1011-AS2?useSSL=false", "root", "root");

            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM television");

            while (resultSet.next()){
                SmartTV tv = new SmartTV(resultSet.getDouble(2),
                                        resultSet.getDouble(3),
                                        resultSet.getInt(4),
                                        resultSet.getString(5),
                                        resultSet.getString(6),
                                        resultSet.getString(7),
                                        resultSet.getString(8),
                                        Television.panelType.valueOf(resultSet.getString(9)),
                                        resultSet.getString(10),
                                        resultSet.getString(11));
                tvs.add(tv);
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

        return tvs;
    }

    /**
     * Mandatory method. Handles passed in data from other views.
     * @param itemList
     */
    @Override
    public void preloadData(ObservableList<SmartTV> itemList) {
        //productsTable.setItems(itemList);
        tvsInStock.setText("TVs in stock: " + Integer.toString(getStockNumber()));
        currentInventoryPriceLabel.setText("Total inventory value: " + NumberFormat.getCurrencyInstance().format(BigDecimal.valueOf(getStockPrice())));
    }
}
