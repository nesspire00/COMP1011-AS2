package Controllers;

import Models.SmartTV;
import Models.Television;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddNewProductViewController implements Initializable, ControllerInterface {

    private ObservableList<SmartTV> currentTVList;
    private FileChooser fileChooser;
    private File imageFile;

    @FXML private TextField priceField;
    @FXML private TextField screenSizeField;
    @FXML private TextField modelField;
    @FXML private TextField resolutionField;
    @FXML private TextField brandField;
    @FXML private TextField featuresField;
    @FXML private ComboBox<Television.panelType> panelTypeComboBox;
    @FXML private TextField osField;
    @FXML private TextField smartFeaturesField;
    @FXML private Label errorLabel;
    @FXML private ImageView imageView;

    /**
     * Sets up the combobox and ImageView on view load.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panelTypeComboBox.getItems().setAll(Television.panelType.values());
        panelTypeComboBox.getSelectionModel().selectFirst();
        imageView.setImage(new Image("tv.png"));
        errorLabel.setText(" ");
    }

    /**
     * Handles the "Create" button push.
     * Attempts to create a new SmartTV object. If successful, add it to the list of products,
     * sends it back and changes scene.
     * @param event
     * @throws IOException
     */
    public void createButtonPushed(ActionEvent event) throws SQLException, IOException {
        double storePrice = Double.parseDouble(priceField.getText());
        double manufacturerPrice = Double.parseDouble(priceField.getText());
        int screenSize = Integer.parseInt(screenSizeField.getText());
        String model = modelField.getText();
        String resolution = resolutionField.getText();
        String brand = brandField.getText();
        String features = featuresField.getText();
        Television.panelType panel = panelTypeComboBox.getSelectionModel().getSelectedItem();
        String os = osField.getText();
        String smartFeatures = smartFeaturesField.getText();
        Image image = imageView.getImage();

        try {
            SmartTV testTv = new SmartTV(storePrice, manufacturerPrice, screenSize, model, resolution, brand, features, panel, os, smartFeatures);

            Connection conn = null;
            PreparedStatement statement = null;

            try{
                conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/COMP1011-AS2?useSSL=false", "root", "root");

                statement = conn.prepareStatement("INSERT INTO television (storePrice, manufacturerPrice, screenSize, modelNo, resolution, brand, features, panelType, operatingSystem, smartFeatures) VALUES (?,?,?,?,?,?,?,?,?,?)");
                statement.setDouble(1, storePrice);
                statement.setDouble(2, manufacturerPrice);
                statement.setInt(3, screenSize);
                statement.setString(4, model);
                statement.setString(5, resolution);
                statement.setString(6, brand);
                statement.setString(7, features);
                statement.setString(8, panel.toString());
                statement.setString(9, os);
                statement.setString(10, smartFeatures);

                statement.execute();
            }
            catch (SQLException e){
                System.err.println(e);
            }
            finally {
                if (conn != null)
                    conn.close();
                if (statement != null)
                    statement.close();
            }

            SceneChanger sc = new SceneChanger();
            sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory");
        }
        catch(IllegalArgumentException e) {
            if (!priceField.getText().matches("[0-9]")) {
                errorLabel.setText("Price has to be a positive number");
            }
            if (!screenSizeField.getText().matches("[0-9]")) {
                errorLabel.setText("Screen size has to be a positive number");
            } else {
                errorLabel.setText(e.getMessage());
            }
        }
    }

    /**
     * Handles the cancel button clicked.
     * Passes the unchanged list of products back, and changes scene.
     * @param event
     * @throws IOException
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        ShowProductsViewController controller = new ShowProductsViewController();

        sc.changeScene(event, "../Views/ShowProductsView.fxml", "Inventory", currentTVList, controller);
    }

    /**
     * Mandatory method. Loads data passed from the previous view.
     * @param itemList
     */
    @Override
    public void preloadData(ObservableList<SmartTV> itemList) {
        currentTVList = itemList;
    }

    /**
     * Handles the "Browse" button click.
     * Opens the system file chooser and lets the user pick the image for their product.
     * @param event
     */
    public void chooseImageButtonPushed(ActionEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("*.JPG File", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("*.PNG File", "*.png");
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);

        String userDirectoryString = System.getProperty("user.home")+ "//Pictures";
        File userDirectory = new File(userDirectoryString);

        if(!userDirectory.canRead()){
            userDirectory = new File(System.getProperty("user.home"));
        }

        fileChooser.setInitialDirectory(userDirectory);

        imageFile = fileChooser.showOpenDialog(stage);

        if(imageFile.isFile()){
            try{
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.imageView.setImage(image);
            } catch (IOException e){
                System.err.println(e);
            }
        }
    }
}
