package com.hussainabdelilah.passwordsaver;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.hussainabdelilah.passwordsaver.database.Database.*;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;

public class AccountDataActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private EditText accountOf;
    private EditText username;
    private EditText password;
    private int accountId;
    String accountOfText;
    String usernameText;
    String passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);
        ////////////////////////////////////////////////////
        getSupportActionBar().setTitle(R.string.account_details); //Account details.
        /////////////////////////////////////////////////////
        Intent i = getIntent();
        accountOfText = i.getStringExtra("accountOf");
        usernameText = i.getStringExtra("username");
        passwordText =  i.getStringExtra("password");
        accountId = i.getExtras().getInt("accountId");
        ///////////////////////////////////////////////////
        linearLayout = (LinearLayout)findViewById(R.id.accountDataLinearLayout);
        linearLayout.setVisibility(LinearLayout.GONE); //Hiding linear layout
        accountOf = (EditText)findViewById(R.id.accountDataEditTextAccountOf);
        username = (EditText)findViewById(R.id.accountDataEditTextUsername);
        password = (EditText)findViewById(R.id.accountDataEditTextPassword);
        ////////////////////////////////////////////////////
        accountOf.setText("Account of : \n" + accountOfText);
        username.setText("Username is : \n" + usernameText);
        password.setText("Password is : \n" + passwordText);
        accountOf.setEnabled(false);
        username.setEnabled(false);
        password.setEnabled(false);
        /////////////////////////////////////////////////////
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if(menuItem.getItemId() == R.id.action_edit_account)
        {
            accountOf.setEnabled(true);
            username.setEnabled(true);
            password.setEnabled(true);
            accountOf.setText(accountOfText);
            username.setText(usernameText);
            password.setText(passwordText);
            linearLayout.setVisibility(LinearLayout.VISIBLE);
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_delete_account)
        {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.account_data_delete_account)  // Delete account.
                    .setMessage(R.string.account_data_are_you_sure)  //Are you sure you want to delete this account?
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(AccountDataActivity.this);
                                SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
                                db.delete(DataTable.TABLE_NAME,
                                        DataTable.Columns.ID + " = ?", new String[]{Integer.toString(accountId)});
                            }catch (Exception e)
                            {
                                //Database unavailable.
                                Toast.makeText(AccountDataActivity.this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    public void onClickSave(View view)
    {
        String tempAccountOfText = accountOf.getText().toString();
        String tempUsernameText = username.getText().toString();
        String tempPasswordText = password.getText().toString();
        if(tempAccountOfText.equals("") || tempUsernameText.equals("") ||
                tempPasswordText.equals(""))
        {
            // You can't leave any input boxes empty.
            Toast.makeText(this, R.string.add_data_empty_input_box, Toast.LENGTH_LONG).show();
            return;
        }
        accountOfText = tempAccountOfText;
        usernameText = tempUsernameText;
        passwordText = tempPasswordText;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataTable.Columns.ACCOUNT, accountOfText);
        contentValues.put(DataTable.Columns.USERNAME, usernameText);
        contentValues.put(DataTable.Columns.PASSWORD, passwordText);
        try{
            DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
            SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
            db.update(DataTable.TABLE_NAME, contentValues,
                     DataTable.Columns.ID + " = ?", new String[]{Integer.toString(accountId)});
            // Changes saved.
            Toast.makeText(this, R.string.changes_saved, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            // Database unavailable.
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
        }
        finish();
    }
    public void onClickCancel(View view)
    {
        accountOf.setText("Account of : \n" + accountOfText);
        username.setText("Username is : \n" + usernameText);
        password.setText("Password is : \n" + passwordText);
        accountOf.setEnabled(false);
        username.setEnabled(false);
        password.setEnabled(false);
        linearLayout.setVisibility(LinearLayout.GONE);
    }
}
