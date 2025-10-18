package com.snhu.weight_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    // initialize variables for user credentials
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize text entry fields for user credentials
        EditText txtFieldUsername = findViewById(R.id.editTextUsername);
        EditText txtFieldPassword = findViewById(R.id.editTextPassword);

        Button btnLogin = findViewById(R.id.buttonLogin);
        Button btnRegister = findViewById(R.id.buttonRegister);

        btnLogin.setOnClickListener(v -> {
            // get username and password from fields
             username = txtFieldUsername.getText().toString();
             password = txtFieldPassword.getText().toString();
            System.out.println(username);
            if (!username.isEmpty() && !password.isEmpty()) {
                signIn(v);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please fill every field.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        btnRegister.setOnClickListener(v -> {
            // get username and password from fields
            username = txtFieldUsername.getText().toString();
            password = txtFieldPassword.getText().toString();
            System.out.println("attempting to register " + username);
            if (!username.isEmpty() && !password.isEmpty()) {
                registerUser(v);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please fill every field.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Login with user-provided credentials
     * Provide feedback on success or failure
     *
     * @param view  the activity view
     */
    public void signIn(View view){
        String username_in = username;
        String password_in = password;
        DBHandler db = new DBHandler(this);

        AuthenticatedUser user = db.getUser(username_in,password_in);
        if(user != null) {
            System.out.println("User: " + user.getUsername());

            // db.loadInitialData();

            System.out.println("LOGGING IN USER ID: " + user.get_id());

            // upon login, pass user to next activity screen along with current user info
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("currentUser", (Serializable) user);
            intent.putExtra("origin", "Login");
            startActivity(intent);
        } else {
            // inform user on unsuccessful login
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid login.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Create a new user entry in users DB
     *
     * @param view   the current activity view
     */
    public void registerUser(View view){
        String username_in = username;
        String password_in = password;
        DBHandler db = new DBHandler(this);
        AuthenticatedUser user;

        // call addUser method to create new user entry
        if(db.addUser(username, password)) {
            Toast toast = Toast.makeText(getApplicationContext(), "New user created!", Toast.LENGTH_LONG);
            toast.show();
            user = new AuthenticatedUser(username_in,password_in);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Error creating user account.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

}
