package com.hussainabdelilah.passwordsaver;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hussainabdelilah.passwordsaver.database.Database.*;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase db;
    private boolean doubleBackToLogOutPressedOnce = false;
    private int userId;
    private List<Integer> categoriesId;
    private List<String> mainMenu;
    private ListView listView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int currentPosition;  // Current Category.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ///////////////////////////////////////////////////////////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///////////////////////////////////////////////////////////
        currentPosition = 0;   // Current category is "Favorites".
        userId = getIntent().getExtras().getInt("usernameId");           // Getting user id from LoginActivity.
        categoriesId = new ArrayList<>();
        mainMenu = new ArrayList<>();
        //////////////////////////////////////////////////////////
        try {
            databaseOpenHelper = new DatabaseOpenHelper(this);
            db = databaseOpenHelper.getReadableDatabase();
        }catch (SQLiteException e)
        {
            // Close MainActivity if database unavailable.
            Toast.makeText(this, R.string.database_unavailable, Toast.LENGTH_LONG).show();
            finish();
        }
        //////////////////////////////////////////////////////////
        setMainMenuAndCategoriesId();   //setting categories from database (Using userId)
        listView = (ListView)findViewById(R.id.list_view);             //ListView
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);  //DrawerLayout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, mainMenu);  //ArrayAdapter
        listView.setAdapter(adapter);                                  //Set 'adapter' for the ListView (set categories)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //Set onItemClickListener for the ListView
                selectedItem(position);   //Select Category from the list view
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////
        // Setting Listener to DrawerLayout using ActionBarDrawerToggle.
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer){
          public void onDrawerClosed(View drawerView)
          {
              super.onDrawerClosed(drawerView);
              invalidateOptionsMenu();
          }
          public void onDrawerOpened(View drawerView)
          {
              super.onDrawerOpened(drawerView);
              invalidateOptionsMenu();
          }

        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        ///////////////////////////////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //////////////////////////////////////////////////////////////////////
        if(savedInstanceState != null)
        {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
        selectedItem(currentPosition); //Refresh data
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToLogOutPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToLogOutPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to Log out", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToLogOutPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    // This function will be call when we call onDrawerClosed or onDrawerOpen.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = drawerLayout.isDrawerOpen(listView);
        menu.findItem(R.id.action_create_category).setVisible(drawerOpen);
        menu.findItem(R.id.action_delete_category).setVisible(drawerOpen);
        menu.findItem(R.id.action_create_account).setVisible(!drawerOpen);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onCreateOptionsMenu(menu);
    }
    // Set icons on ActionBar, using menu_main.xml file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_create_category).setVisible(false);
        menu.findItem(R.id.action_delete_category).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(menuItem))
            return true;
        if(menuItem.getItemId() == R.id.action_logout)
        {
            finish();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_create_account)
        {
            Intent i = new Intent(this, AddDataActivity.class);
            i.putExtra("userId", userId);
            i.putExtra("categoryId", categoriesId.get(currentPosition));
            startActivity(i);
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_create_category)
        {
            createNewCategory();
            return true;
        }
        else if(menuItem.getItemId() == R.id.action_delete_category)
        {
            deleteThisCategory();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", currentPosition);
    }
    // Setting mainMenu and categoriesId.
    private void setMainMenuAndCategoriesId()
    {
        Cursor cursor = db.query(CategoryTable.TABLE_NAME,
                    new String[]{CategoryTable.Columns.ID, CategoryTable.Columns.CATEGORY},
                    CategoryTable.Columns.ID_USERNAME + " = ?",
                    new String[]{Integer.toString(userId)},
                    null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            mainMenu.add(cursor.getString(cursor.getColumnIndex(CategoryTable.Columns.CATEGORY)));
            categoriesId.add(cursor.getInt(cursor.getColumnIndex(CategoryTable.Columns.ID)));
            cursor.moveToNext();
        }
        cursor.close();
    }
    // Open selected category from ListView (Categories list).
    private void selectedItem(int position)
    {
        currentPosition = position;
        PasswordsFragment fragment = new PasswordsFragment();
        fragment.setId(userId, categoriesId.get(position));  //Sending userId and categoryId to fragment.
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(listView);
        setActionBarTitle(position);
    }
    // Set ActionBar title when open a category.
    private void setActionBarTitle(int position)
    {
        String title = mainMenu.get(position);
        getSupportActionBar().setTitle(title);
    }
    private void createNewCategory()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Create new category");
        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)}); // Setting max length for edit text
        input.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
        builder.setView(input);
        // Set up the buttons:
        // 1) save:
        builder.setPositiveButton(R.string.main_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategory = input.getText().toString();
                int j=0;
                for(int i=0; i<newCategory.length(); i++)
                {
                    if(newCategory.charAt(i) == ' ')
                        j++;
                }
                if(j == newCategory.length() || newCategory.equals(""))
                {
                    //You can't leave this input box empty.
                    Toast.makeText(MainActivity.this, R.string.main_empty_input_box, Toast.LENGTH_LONG).show();
                    return;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(CategoryTable.Columns.ID_USERNAME, userId);
                contentValues.put(CategoryTable.Columns.CATEGORY, newCategory);
                db.insert(CategoryTable.TABLE_NAME, null,contentValues);
                mainMenu = new ArrayList<>();
                categoriesId = new ArrayList<>();
                setMainMenuAndCategoriesId();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, mainMenu);  //ArrayAdapter
                listView.setAdapter(adapter);
            }
        });
        // 2) cancel:
        builder.setNegativeButton(R.string.main_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteThisCategory()
    {
        if(currentPosition == 0)
        {
            // You can't delete favorite category
            Toast.makeText(MainActivity.this, R.string.main_deleting_favorite, Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            // Title (Delete this category)
            builder.setTitle(R.string.main_delete_category)
                    //Message (Are you sure you want to delete this category?)
                    .setMessage(R.string.main_confirm_deleting)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(CategoryTable.TABLE_NAME, CategoryTable.Columns.ID + " = ? ",
                                        new String[]{Integer.toString(categoriesId.get(currentPosition))});
                            db.delete(DataTable.TABLE_NAME, DataTable.Columns.ID_CATEGORY + " = ? AND " +
                                                DataTable.Columns.ID_USERNAME + " = ?",
                                        new String[]{Integer.toString(categoriesId.get(currentPosition)),
                                                Integer.toString(userId)});
                            mainMenu = new ArrayList<>();
                            categoriesId = new ArrayList<>();
                            setMainMenuAndCategoriesId();
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, mainMenu);  //ArrayAdapter
                            listView.setAdapter(adapter);
                            selectedItem(0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
