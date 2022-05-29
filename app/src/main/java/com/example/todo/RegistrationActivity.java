package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    EditText editTextUserName, editTextPassword;
    TextView textViewHaveAccount;
    Button buttonLogin;
    SQLiteDatabaseHelper sqLiteDatabaseHelper;
    LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        loginData = new LoginData(this);
        initViews();
    }

    private void initViews() {
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewHaveAccount = findViewById(R.id.textViewHaveAccount);
        textViewHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        } else if (password.length() < 5) {
            Toast.makeText(this, "Passwort muss mindestens 5 Zeichen lang sein", Toast.LENGTH_SHORT).show();
        } else {
            UserModel userModel = new UserModel();
            userModel.setUserPassword(password);
            userModel.setUserName(userName);
            if (sqLiteDatabaseHelper.addNewUser(userModel)) {
                Toast.makeText(this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                loginData.setLogin(userName, password);
                cleanStackAndStartNewActivity();
            } else {
                Toast.makeText(this, "User mit Username  " + userName + " existiert bereits", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cleanStackAndStartNewActivity() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);// start intent
    }

}
