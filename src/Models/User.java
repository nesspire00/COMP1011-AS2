package Models;

/**
 * A quick model for a user, that gets created from the data from the DB during login
 */
public class User {

    private boolean isAdmin;
    private String userName, contactInfo;
    private int userId;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (!userName.isEmpty())
            this.userName = userName;
        else
            throw new IllegalArgumentException("User Name cannot be empty");
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        if (!contactInfo.isEmpty())
            this.contactInfo = contactInfo;
        else
            throw new IllegalArgumentException("Contact info cannot be empty");
    }

    public User(int userId, boolean isAdmin, String userName, String contactInfo) {

        this.userId = userId;
        this.isAdmin = isAdmin;
        setUserName(userName);
        setContactInfo(contactInfo);
    }

    @Override
    public String toString() {
        return isAdmin ? "admin/" + getUserName() : "employee/" + getUserName();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
