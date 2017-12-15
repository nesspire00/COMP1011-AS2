package Models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SmartTV extends Television {
    private String operatingSystem, smartFeatures;

    /**
     * A constructor for SmartTV object, with inherited properties from super class and 2 of its own
     * @param storePrice
     * @param screenSize
     * @param modelNo
     * @param resolution
     * @param brand
     * @param panelType
     * @param operatingSystem
     * @param smartFeatures
     */
    public SmartTV(double storePrice, int screenSize, String modelNo, String resolution, String brand, Television.panelType panelType, String operatingSystem, String smartFeatures) {
        super(storePrice, screenSize, modelNo, resolution, brand, panelType);
        this.operatingSystem = operatingSystem;
        this.smartFeatures = smartFeatures;
    }

    public SmartTV(double storePrice, int screenSize, String modelNo, String resolution, String brand, Television.panelType panelType, String operatingSystem, String smartFeatures, int tvId) {
        super(storePrice, screenSize, modelNo, resolution, brand, panelType, tvId);
        this.operatingSystem = operatingSystem;
        this.smartFeatures = smartFeatures;
    }

    /**
     * Constructor for SmartTV object with an image.
     * @param storePrice
     * @param screenSize
     * @param modelNo
     * @param resolution
     * @param brand
     * @param panelType
     * @param operatingSystem
     * @param smartFeatures
     * @param image
     */
    public SmartTV(double storePrice, int screenSize, String modelNo, String resolution, String brand, Television.panelType panelType, String operatingSystem, String smartFeatures, File image) throws IOException {
        super(storePrice, screenSize, modelNo, resolution, brand, panelType, image);
        this.operatingSystem = operatingSystem;
        this.smartFeatures = smartFeatures;
        copyImageFile();
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * Checks that the OS is not empty.s
     * @param operatingSystem
     */
    public void setOperatingSystem(String operatingSystem) {
        if(!operatingSystem.equals("")){
            this.operatingSystem = operatingSystem;
        }
        else{
            throw new IllegalArgumentException("Operating system cannot be empty");
        }
    }

    public String getSmartFeatures() {
        return smartFeatures;
    }

    public void setSmartFeatures(String smartFeatures) {
        this.smartFeatures = smartFeatures;
    }

    public void insertIntoDb() throws SQLException {

            Connection conn = null;
            PreparedStatement statement = null;

            try{
                conn = DriverManager.getConnection("jdbc:mysql://localhost:8889/COMP1011-AS2?useSSL=false", "root", "root");

                statement = conn.prepareStatement("INSERT INTO television (storePrice, screenSize, modelNo, resolution, brand, panelType, operatingSystem, smartFeatures, image) VALUES (?,?,?,?,?,?,?,?,?)");
                statement.setDouble(1, getStorePrice());
                statement.setInt(2, getScreenSize());
                statement.setString(3, getModelNo());
                statement.setString(4, getResolution());
                statement.setString(5, getBrand());
                statement.setString(6, getPanelType().toString());
                statement.setString(7, operatingSystem);
                statement.setString(8, smartFeatures);
                statement.setString(9, getTvImageFile().getName());

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
        }

    }
