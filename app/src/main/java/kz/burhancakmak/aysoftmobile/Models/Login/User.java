package kz.burhancakmak.aysoftmobile.Models.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("CihazId")
    @Expose
    private String PhoneId;

    @SerializedName("Login")
    @Expose
    private String userName;

    @SerializedName("Parola")
    @Expose
    private int userPassword;

    public User() {
    }

    public String getPhoneId() {
        return PhoneId;
    }

    public void setPhoneId(String phoneId) {
        PhoneId = phoneId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(int userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "PhoneId='" + PhoneId + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
