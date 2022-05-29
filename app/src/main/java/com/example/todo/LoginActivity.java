package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUserName, editTextPassword;
    TextView textViewNotRegistered;
    Button buttonLogin;
    SQLiteDatabaseHelper sqLiteDatabaseHelper;
    LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        loginData = new LoginData(this);
        initViews();
    }

    private void initViews() {
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewNotRegistered = findViewById(R.id.textViewNotRegistered);
        textViewNotRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

    }

    private void doLogin() {
        String userName = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();
        if (userName.equals("")) {
            Toast.makeText(this, "Username erforderlich", Toast.LENGTH_SHORT).show();
        } else if (userName.contains(" ")) {
            Toast.makeText(this, "Keine Leerzeichen im Username erlaubt", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(this, "Passwort erforderlich", Toast.LENGTH_SHORT).show();
        } else {
            UserModel userModel = sqLiteDatabaseHelper.getUser(userName.trim());
            if (userModel == null) {
                Toast.makeText(this, "Account " + userName + " existiert nicht", Toast.LENGTH_LONG).show();
            } else if (userModel.getUserPassword().equals(password)) {
                loginData.setLogin(userName, password);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Passwort falsch eingegeben", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
