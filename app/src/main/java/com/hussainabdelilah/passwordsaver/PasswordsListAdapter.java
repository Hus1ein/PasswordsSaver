package com.hussainabdelilah.passwordsaver;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PasswordsListAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final List<String> accountOfList;
	private final List<String> usernameList;
	private final LayoutInflater inflater;

	public PasswordsListAdapter(Context context, List<String> pAccountOfList, List<String> pUsernameList, LayoutInflater pInflater) {
		super(context, R.layout.list_row, pAccountOfList);
		this.context=context;
		this.accountOfList = pAccountOfList;
		this.usernameList = pUsernameList;
		this.inflater = pInflater;
	}
	@Override
	public View getView(int position,View view,ViewGroup parent) {
		//LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.list_row, null,true);
		
		TextView accountOf = (TextView) rowView.findViewById(R.id.passwordsFragmentAccountOf);
		TextView username = (TextView) rowView.findViewById(R.id.passwordsFragmentUsername);
		String accountOfText = accountOfList.get(position);
        String usernameText = "Username : " + usernameList.get(position);
        if(accountOfText.length() > 15 && usernameText.length() <= 20)
        {
            String text = "";
            for(int i=0; i<15; i++)
                text += accountOfText.charAt(i);
            text += "...";
            accountOf.setText(text);
            username.setText(usernameText);
        }
        else if(usernameText.length() > 20 && accountOfText.length() <= 15)
        {
            String text = "";
            for(int i=0; i<20; i++)
                text += usernameText.charAt(i);
            text += "...";
            username.setText(text);
            accountOf.setText(accountOfText);
        }
        else if(accountOfText.length() > 15 && usernameText.length() > 20)
        {
            String text = "";
            for(int i=0; i<15; i++)
                text += accountOfText.charAt(i);
            text += "...";
            accountOf.setText(text);
            text = "";
            for(int i=0; i<20; i++)
                text += usernameText.charAt(i);
            text += "...";
            username.setText(text);
        }
        if(accountOfText.length() <= 15 && usernameText.length() <= 20)
        {
            accountOf.setText(accountOfText);
            username.setText(usernameText);
        }
            return rowView;
		
	}
}