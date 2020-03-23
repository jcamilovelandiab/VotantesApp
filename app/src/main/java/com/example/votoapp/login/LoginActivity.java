package com.example.votoapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.votoapp.ConsultorMainActivity;
import com.example.votoapp.R;
import com.example.votoapp.MainActivity;
import com.example.votoapp.model.entities.Role;
import com.example.votoapp.model.repositories.UserRepository;

public class LoginActivity extends AppCompatActivity {

    Button btn_login, btn_exit;
    EditText et_user, et_password;
    UserRepository userRepository = UserRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.login_btn_signin);
        btn_exit = findViewById(R.id.login_btn_exit);
        et_user = findViewById(R.id.login_et_user);
        et_password = findViewById(R.id.login_et_password);

        configureBtnLogin();
        configureBtnExit();

    }

    private void configureBtnExit() {
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configureBtnLogin() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user, password;
                user= et_user.getText().toString();
                password = et_password.getText().toString();
                if(user.trim().equals("")) {
                    Toast.makeText(v.getContext(),"Enter your user", Toast.LENGTH_SHORT).show();
                }else if(password.trim().equals("")) {
                    Toast.makeText(v.getContext(),"Enter your password", Toast.LENGTH_SHORT).show();
                }else{
                    LoginResult loginResult = userRepository.login(user,password);
                    if(loginResult.getError()!=null){
                        Toast.makeText(v.getContext(), loginResult.getError(), Toast.LENGTH_SHORT).show();
                    }else if(loginResult.getSuccess()!=null){
                        Role role=loginResult.getSuccess().getRole();
                        Intent intent;
                        if(role.equals(Role.administrador)){
                            intent = new Intent(v.getContext(), MainActivity.class);
                        }else{
                            intent = new Intent(v.getContext(), ConsultorMainActivity.class);
                        }
                        intent.putExtra("user", user);
                        intent.putExtra("role", role.toString());

                        startActivity(intent);
                        et_password.setText("");
                        //finish();
                    }
                }
            }
        });
    }
}
