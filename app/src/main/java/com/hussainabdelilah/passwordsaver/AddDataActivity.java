package com.hussainabdelilah.passwordsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hussainabdelilah.passwordsaver.database.Database.*;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AddDataActivity extends AppCompatActivity {

    int userId;
    int categoryId;
    EditText account;
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        //////////////////////////////////////////////
        getSupportActionBar().setTitle(R.string.add_new_account);
        userId = getIntent().getExtras().getInt("userId");
        categoryId = getIntent().getExtras().getInt("categoryId");
        account = (EditText)findViewById(R.id.addDataEditTextAccount);
        username = (EditText)findViewById(R.id.addDataEditTextUsername);
        password = (EditText)findViewById(R.id.addDataEditTextPassword);
    }

    public void onClickSave(View view)
    {
        setData();
    }

    public void onClickCancel(View view)
    {
        finish();
    }

    private void setData()
    {
        String accountText = account.getText().toString();
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        if(accountText.equals("") || usernameText.equals("") || passwordText.equals(""))
        {
            // You can't leave any input boxes empty.
            Toast.makeText(this, R.string.add_data_empty_input_box, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataTable.Columns.ACCOUNT, accountText);
            contentValues.put(DataTable.Columns.USERNAME, usernameText);
            contentValues.put(DataTable.Columns.PASSWORD, passwordText);
            contentValues.put(DataTable.Columns.ID_USERNAME, userId);
            contentValues.put(DataTable.Columns.ID_CATEGORY, categoryId);
            db.insert(DataTable.TABLE_NAME, null, contentValues);
            finish();
        }catch (Exception e)
        {
            //Database unavailable.
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
        }
    }
}
