package com.student.digimart.Prevalent;

import com.student.digimart.Models.Users;

public class Prevalent {
    private static Users currentOnlineUser;
    public static final String UserEmailKey = "UserEmail";
    public static final String UserPasswordKey = "UserPassword";
    
    public static Users getCurrentOnlineUser() {
        return currentOnlineUser;
    }

    public static void setCurrentOnlineUser(Users user) {
        currentOnlineUser = user;
    }
}
