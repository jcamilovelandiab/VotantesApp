package com.example.votoapp.model.repositories;

import com.example.votoapp.login.LoginResult;
import com.example.votoapp.model.entities.Role;
import com.example.votoapp.model.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private static volatile UserRepository instance;

    private Map<String, User> users;

    // private constructor : singleton access
    private UserRepository() {
        users = new HashMap<String,User>();
        User admin = new User("admin","admin", Role.administrador);
        User administrador = new User("DESMovil","titulo2020A", Role.administrador);
        User consultor = new User("desmovil", "comple2020A", Role.consultor);
        User cons = new User("consultor", "consultor", Role.consultor);

        users.put(administrador.getUser(), administrador);
        users.put(consultor.getUser(), consultor);
        users.put(admin.getUser(), admin);
        users.put(cons.getUser(), cons);
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LoginResult login(final String user, final String password) {
        LoginResult loginResult;
        if(users.containsKey(user) && users.get(user).getPassword().equals(password)){
            User u = users.get(user);
            u.setPassword(null);
            loginResult = new LoginResult(null, u);
        }else{
            loginResult = new LoginResult("Invalid login", null);
        }
        return loginResult;
    }

}
