package com.hussainabdelilah.passwordsaver;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;
import com.hussainabdelilah.passwordsaver.database.Database.*;

public class SignupActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ///////////////////////////////////////////////////////////
        //Display back button on action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Set title (Sign up).
        getSupportActionBar().setTitle(R.string.sign_up);
        ///////////////////////////////////////////////////////////
        username = (EditText)findViewById(R.id.signupEditTextUsername);
        password = (EditText)findViewById(R.id.signupEditTextPassword);
        confirmPassword = (EditText)findViewById(R.id.signupEditTextConfirmPassword);
    }

    public void onClickSignUpButton(View view) {
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();
        if(correctText(usernameText) && correctText(passwordText) && correctText(confirmPasswordText)) {
            if (passwordText.equals(confirmPasswordText)) {
                try {
                    DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
                    SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
                    Cursor cursor = db.query(UsernamesTable.TABLE_NAME,
                            new String[]{UsernamesTable.Columns.USERNAME},
                            UsernamesTable.Columns.USERNAME + " = ?",
                            new String[]{usernameText},
                            null, null, null);
                    if (cursor.moveToFirst()) {
                        // Someone already has that username.
                        Toast.makeText(this, R.string.sign_up_someone_use_that_username, Toast.LENGTH_LONG).show();
                    } else  // Everything is ok :)
                    {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(UsernamesTable.Columns.USERNAME, usernameText);
                        contentValues.put(UsernamesTable.Columns.PASSWORD, passwordText);
                        db.insert(UsernamesTable.TABLE_NAME, null, contentValues);
                        ///////////////////////////////////////////////////////////////////
                        // Adding favorites list to CategoryTable, using username id.
                        cursor = db.query(UsernamesTable.TABLE_NAME,
                                new String[]{UsernamesTable.Columns.ID},
                                UsernamesTable.Columns.USERNAME + " = ?",
                                new String[]{usernameText},
                                null, null, null);
                        cursor.moveToFirst();
                        int userId = cursor.getInt(cursor.getColumnIndex(UsernamesTable.Columns.ID));
                        contentValues = new ContentValues();
                        contentValues.put(CategoryTable.Columns.ID_USERNAME, userId);
                        contentValues.put(CategoryTable.Columns.CATEGORY, "Favorites");
                        db.insert(CategoryTable.TABLE_NAME, null, contentValues);
                        /////////////////////////////////////////////////////////////////////
                        finish();
                    }
                    cursor.close();
                } catch (SQLiteException e) {
                    // Database unavailable.
                    Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
                }
            }
            else {
                //These passwords don't match. Try again.
                Toast.makeText(this, R.string.sign_up_passwords_dont_match, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            //You can use small letters, numbers and full stop, more over, you have to use more than 7 character.
            Toast.makeText(this, getString(R.string.sign_up_correct_text), Toast.LENGTH_LONG).show();
        }
    }
    //Ensure availability of conditions in the text :
    // a) using small letters, numbers and full stop.
    // b) using more than 7 character.
    private boolean correctText(String text)
    {
        if(text.equals(""))
            return false;
        if(text.length() < 8)
            return false;
        ////////////////////////////////////////////////////////////////////////
        char allowedToUse[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                               'k','l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                               'v', 'u', 'w', 'x', 'y', 'z', '.', '1', '2', '3',
                               '4', '5', '6', '7', '8', '9', '0'};
        int count = 0;
        for(int i=0; i<text.length(); i++)
        {
            for(int j=0; j<allowedToUse.length; j++)
            {
                if(text.charAt(i) == allowedToUse[j])
                {
                    count++;
                    break;
                }
            }
        }
        if(count == text.length())
            return true;
        else
            return false;
        /////////////////////////////////////////////////////////////////////////////////
    }
}
