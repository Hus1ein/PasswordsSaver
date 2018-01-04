package com.hussainabdelilah.passwordsaver;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hussainabdelilah.passwordsaver.database.Database.*;
import com.hussainabdelilah.passwordsaver.database.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PasswordsFragment extends Fragment {

    private ListView listView;
    private int userId;
    private int categoryId;
    private List<Integer> accountId;
    private List<String> accountOf;
    private List<String> username;
    private List<String> password;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_passwords, container, false);
        accountId = new ArrayList<>();
        accountOf = new ArrayList<>();
        username = new ArrayList<>();
        password = new ArrayList<>();
        getData(inflater.getContext());
        //////////////////////////////
        PasswordsListAdapter adapter = new PasswordsListAdapter(inflater.getContext() , accountOf, username, inflater);
        listView = (ListView)layout.findViewById(R.id.passwordsFragmentListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(inflater.getContext(), AccountDataActivity.class);
                i.putExtra("accountId", accountId.get(position));
                i.putExtra("accountOf", accountOf.get(position));
                i.putExtra("username", username.get(position));
                i.putExtra("password", password.get(position));
                startActivity(i);

            }
        });
        return layout;
    }
    public void setId(int pUserId, int pCategoryId)
    {
        this.userId = pUserId;
        this.categoryId = pCategoryId;
    }
    private void getData(Context context)
    {
        try {
            DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(context);
            SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
            Cursor cursor = db.query(DataTable.TABLE_NAME,
                    new String[]{DataTable.Columns.ACCOUNT, DataTable.Columns.USERNAME, DataTable.Columns.PASSWORD,
                                 DataTable.Columns.ID},
                   DataTable.Columns.ID_USERNAME + " = ? AND " + DataTable.Columns.ID_CATEGORY + " = ? ",
                    new String[]{Integer.toString(userId), Integer.toString(categoryId)},
                    null, null, null);
            cursor.moveToLast();
            while(!cursor.isBeforeFirst())
            {
                accountId.add(cursor.getInt(cursor.getColumnIndex(DataTable.Columns.ID)));
                accountOf.add(cursor.getString(cursor.getColumnIndex(DataTable.Columns.ACCOUNT)));
                username.add(cursor.getString(cursor.getColumnIndex(DataTable.Columns.USERNAME)));
                password.add(cursor.getString(cursor.getColumnIndex(DataTable.Columns.PASSWORD)));
                cursor.moveToPrevious();
            }
            cursor.close();
        }catch (Exception e)
        {
            //Database unavailable.
            Toast.makeText(context, R.string.database_unavailable, Toast.LENGTH_LONG).show();
        }
    }

}
