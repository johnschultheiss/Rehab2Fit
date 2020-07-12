package com.r2f.rehab2fit.data;

import java.io.IOException;

//
//  UserInfo
//  Container for user information
//
public class UserInfo {

    enum Status{
        statusUnknown,
        statusLoggedIn,
        statusNoSuchUser,
        statusInvalidPassword
    }

    public Status userStatus = Status.statusUnknown;
    // Constructor
    public UserInfo(){clear();}

    // Accessor methods, set and get
    public String getUserId() {
        return userId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getPhone(){ return phone;}
    public String getAddress(){ return address;}
    public String getPassword(){ return password;}

    public void setUserId(String uid) { userId = uid; }
    public void setDisplayName(String dn) { displayName = dn; }
    public void setPhone(String ph){ phone = ph;}
    public void setAddress(String addr){ address = addr;}
    public void setPassword(String pw ){ password = pw;}

    public boolean isLoggedIn() {
        return userStatus == Status.statusLoggedIn;
    }

    // Public methods
    // Initialize a new or existing UserInfo instance
    public void clear(){
        userId      = "";         // email
        displayName = "";         // full name
        phone       = "";         // Phone #
        address     = "";         // Street address
        password    = "";         // Password, plain text
        userStatus  = Status.statusUnknown;
    }

    // Set all fields and flag the user as logged in
    private void setLoggedInUser(UserInfo user) {
        userId      = user.getUserId();
        displayName = user.getDisplayName();     // full name
        phone       = user.getPhone();           // Phone #
        address     = user.getAddress();         // Street address
        password    = user.getPassword();        // Password, plain text
        userStatus  = Status.statusLoggedIn;
    }

    public void logout() {
        loggedIn = false;
    }

    // private data
    private String userId;          // email
    private String displayName;     // full name
    private String phone;           // Phone #
    private String address;         // Street address
    private String password;        // Password, plain text
    private boolean loggedIn;

}