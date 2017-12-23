import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Launch the login view
     *
     * DEFAULT ADMIN USER:
     * userId:      1
     * password:    password123
     * (You can create your own non-admin users later)
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("Views/LoginBoxView.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Inventory");
            primaryStage.setScene(scene);
            scene.getStylesheets().add(Main.class.getResource("bootstrap3.css").toExternalForm());
            primaryStage.show();
    }


}
