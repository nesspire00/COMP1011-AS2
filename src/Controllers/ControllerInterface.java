package Controllers;

import Models.SmartTV;
import Models.User;
import javafx.collections.ObservableList;

public interface ControllerInterface {

    /**
     * Forces all implementing classes to load the data, passed on from the previous view.
     */

    void preloadData(User employee);

    void preloadData(SmartTV smartTV);

}
