package com.example.votoapp.login;

import com.example.votoapp.model.entities.User;

public class LoginResult {

    public String error;
    public User success;

    public LoginResult(String error, User success){
        this.error = error;
        this.success =success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public User getSuccess() {
        return success;
    }

    public void setSuccess(User success) {
        this.success = success;
    }
}