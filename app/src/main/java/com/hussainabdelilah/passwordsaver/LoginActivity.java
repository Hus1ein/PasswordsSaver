package com.hussainabdelilah.passwordsaver;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hussainabdelilah.passwordsaver.database.Database.*;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //////////////////////////////////////////////////////
        username = (EditText)findViewById(R.id.loginEditTextUsername);
        password = (EditText)findViewById(R.id.loginEditTextPassword);
        /////////////////////////////////////////////////////
        getSupportActionBar().setTitle(R.string.log_in);  // set title (Log in).
    }
    @Override
    public void onStop()
    {
        // Deleting username and password in the EditTexts.
        super.onStop();
        username.setText("");
        password.setText("");
    }

    public void onClickLoginButton(View view) {
        //Getting text from username and password EditText.
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        // Trying connection to database and then searching for username and password in database.
        try {
            DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
            Cursor cursor = db.query(UsernamesTable.TABLE_NAME,
                    new String[]{UsernamesTable.Columns.ID, UsernamesTable.Columns.USERNAME, UsernamesTable.Columns.PASSWORD},
                    UsernamesTable.Columns.USERNAME + " = ?",
                    new String[]{usernameText},
                    null, null, null);
            if(cursor.moveToFirst()) //If username is in database, then ...
            {
                String correctPassword = cursor.getString(cursor.getColumnIndex(UsernamesTable.Columns.PASSWORD));
                if(correctPassword.equals(passwordText))
                {
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra("usernameId", cursor.getInt(cursor.getColumnIndex(UsernamesTable.Columns.ID)));
                    startActivity(i);
                }
                else  // Incorrect password.
                    Toast.makeText(this, R.string.login_incorrect_password, Toast.LENGTH_LONG).show();
            }
            else  // Incorrect username.
                Toast.makeText(this, R.string.login_incorrect_username, Toast.LENGTH_LONG).show();
            cursor.close();
        }catch (SQLiteException e)
        {
            //Database unavailable
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSignUpButton(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }
}
