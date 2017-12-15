package Models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;

public abstract class Television {
    private double storePrice;
    private int screenSize, tvId;
    private String modelNo, resolution, brand;
    private panelType panelType;
    private File tvImageFile;

    public enum panelType{
        CRT, OLED, PLASMA, LCD, LED, AMOLED;
    }

    public int getTvId() {
        return tvId;
    }

    public void setTvId(int tvId) {
        this.tvId = tvId;
    }

    /**
     * Default constructor for Television object without an image.
     * @param storePrice
     * @param screenSize
     * @param modelNo
     * @param resolution
     * @param brand
     * @param panelType
     */
    public Television(double storePrice, int screenSize, String modelNo, String resolution, String brand, panelType panelType, int tvId) {
        setStorePrice(storePrice);
        setScreenSize(screenSize);
        setModelNo(modelNo);
        setResolution(resolution);
        setBrand(brand);
        setPanelType(panelType);
        setTvId(tvId);
    }

    public Television(double storePrice, int screenSize, String modelNo, String resolution, String brand, panelType panelType) {
        setStorePrice(storePrice);
        setScreenSize(screenSize);
        setModelNo(modelNo);
        setResolution(resolution);
        setBrand(brand);
        setPanelType(panelType);
    }

    /**
     * Alternative constructor for Television object that can store an image.
     * @param storePrice
     * @param screenSize
     * @param modelNo
     * @param resolution
     * @param brand
     * @param panelType
     * @param tvImageFile
     */
    public Television(double storePrice, int screenSize, String modelNo, String resolution, String brand, panelType panelType, File tvImageFile) throws IOException {
        setStorePrice(storePrice);
        setScreenSize(screenSize);
        setModelNo(modelNo);
        setResolution(resolution);
        setBrand(brand);
        setPanelType(panelType);
        this.tvImageFile = tvImageFile;
        copyImageFile();
    }

    public double getStorePrice() {
        return storePrice;
    }

    /**
     * Checks that the price is not negative or 0
     * @param storePrice
     */
    public void setStorePrice(double storePrice) {
        if(storePrice > 0){
            this.storePrice = storePrice;
        }
        else{
            throw new IllegalArgumentException("The store price cannot be negative or 0");
        }

    }

    public int getScreenSize() {
        return screenSize;
    }

    /**
     * Checks if entered screen size is in range of 15-80"
     * @param screenSize
     */
    public void setScreenSize(int screenSize) {
        if(screenSize >= 15 && screenSize <= 80){
            this.screenSize = screenSize;
        }
        else{
            throw new IllegalArgumentException("The screen size has to be in rage of 15 - 80");
        }
    }

    public String getModelNo() {
        return modelNo;
    }

    /**
     * Checks that the modelNumber is not empty
     * @param modelNo
     */
    public void setModelNo(String modelNo) {
        if(!modelNo.equals("")){
            this.modelNo = modelNo;
        }
        else{
            throw new IllegalArgumentException("The model number cannot be empty");
        }
    }

    public String getResolution() {
        return resolution;
    }

    /**
     * Checks that the resolution is not empty.
     * @param resolution
     */
    public void setResolution(String resolution) {
        if(!resolution.equals("")){
            this.resolution = resolution;
        }
        else{
            throw new IllegalArgumentException("The resolution cannot be empty");
        }
    }

    public String getBrand() {
        return brand;
    }

    /**
     * Checks that the brand is not empty.
     * @param brand
     */
    public void setBrand(String brand) {
        if(!brand.equals("")){
            this.brand = brand;
        }
        else{
            throw new IllegalArgumentException("The brand cannot be empty");
        }
    }

    public Television.panelType getPanelType() {
        return panelType;
    }

    public void setPanelType(Television.panelType panelType) {
        this.panelType = panelType;
    }

    public File getTvImageFile() {
        return tvImageFile;
    }

    public void setTvImageFile(File tvImageFile) {
        this.tvImageFile = tvImageFile;
    }

    /**
     * This method will copy the file specified to the images directory on this server and give it
     * a unique name
     */
    public void copyImageFile() throws IOException
    {
        //create a new Path to copy the image into a local directory
        Path sourcePath = tvImageFile.toPath();

        String uniqueFileName = getUniqueFileName(tvImageFile.getName());

        Path targetPath = Paths.get("./src/img/"+uniqueFileName);

        //copy the file to the new directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        //update the imageFile to point to the new File
        tvImageFile = new File(targetPath.toString());
    }


    /**
     * This method will receive a String that represents a file name and return a
     * String with a random, unique set of letters prefixed to it
     */
    private String getUniqueFileName(String oldFileName)
    {
        String newName;

        //create a Random Number Generator
        SecureRandom rng = new SecureRandom();

        //loop until we have a unique file name
        do
        {
            newName = "";

            //generate 32 random characters
            for (int count=1; count <=32; count++)
            {
                int nextChar;

                do
                {
                    nextChar = rng.nextInt(123);
                } while(!validCharacterValue(nextChar));

                newName = String.format("%s%c", newName, nextChar);
            }
            newName += oldFileName;

        } while (!uniqueFileInDirectory(newName));

        return newName;
    }


    /**
     * This method will search the images directory and ensure that the file name
     * is unique
     */
    public boolean uniqueFileInDirectory(String fileName)
    {
        File directory = new File("./src/img/");

        File[] dir_contents = directory.listFiles();

        for (File file : dir_contents)
        {
            if (file.getName().equals(fileName))
                return false;
        }
        return true;
    }

    /**
     * This method will validate if the integer given corresponds to a valid
     * ASCII character that could be used in a file name
     */
    public boolean validCharacterValue(int asciiValue)
    {

        //0-9 = ASCII range 48 to 57
        if (asciiValue >= 48 && asciiValue <= 57)
            return true;

        //A-Z = ASCII range 65 to 90
        if (asciiValue >= 65 && asciiValue <= 90)
            return true;

        //a-z = ASCII range 97 to 122
        if (asciiValue >= 97 && asciiValue <= 122)
            return true;

        return false;
    }
}
