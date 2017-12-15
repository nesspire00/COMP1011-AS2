package Controllers;

import Models.SmartTV;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChanger {

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        SceneChanger.loggedInUser = loggedInUser;
    }

    /**
     * Changes scene to the requested FXML file.
     * @param event
     * @param viewName name of the view
     * @param title title of the new window
     * @throws IOException
     */
    public void changeScene(ActionEvent event, String viewName, String title) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        scene.getStylesheets().add(SceneChanger.class.getResource("../bootstrap3.css").toExternalForm());
        stage.show();
    }

    public void changeScene(ActionEvent event, String viewName, String title, User employee, ControllerInterface controllerInterface) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerInterface = loader.getController();
        controllerInterface.preloadData(employee);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        scene.getStylesheets().add(SceneChanger.class.getResource("../bootstrap3.css").toExternalForm());
        stage.show();
    }
    public void changeScene(ActionEvent event, String viewName, String title, SmartTV smartTV, ControllerInterface controllerInterface) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        controllerInterface = loader.getController();
        controllerInterface.preloadData(smartTV);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        scene.getStylesheets().add(SceneChanger.class.getResource("../bootstrap3.css").toExternalForm());
        stage.show();
    }
}
