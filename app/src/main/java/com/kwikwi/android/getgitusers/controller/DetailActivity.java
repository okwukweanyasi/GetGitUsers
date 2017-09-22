package com.kwikwi.android.getgitusers.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kwikwi.android.getgitusers.R;
import com.kwikwi.android.getgitusers.api.Client;
import com.kwikwi.android.getgitusers.api.Service;
import com.kwikwi.android.getgitusers.model.UserDisplayResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KWIKWI on 9/16/2017.
 */

public class DetailActivity extends AppCompatActivity {
    TextView Link, UserName, fullName, Bio;
    Toolbar mActionBarToolBar;
    ImageView imageView;
    String displayName ;
    String userBio ;
    //int newInt;
    ProgressDialog pd;

    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView)findViewById(R.id.user_image_header);
        UserName = (TextView)findViewById(R.id.username);
        fullName = (TextView) findViewById(R.id.full_name);
        Bio = (TextView)findViewById(R.id.githubabout);


        Link = (TextView)findViewById(R.id.link);

        String userName = getIntent().getExtras().getString("login");
        String avatarurl = getIntent().getExtras().getString("avatar");
        String link = getIntent().getExtras().getString("html_url");
        //String dispName = getIntent().getExtras().getString("full_name");
        //String bio = getIntent().getExtras().getString("bio");

        moreUserDetails(userName);

        Link.setText(link);
        Link.setTextColor(getResources().getColor(R.color.linkColor));
        Linkify.addLinks(Link, Linkify.WEB_URLS);

        UserName.setText(userName);
        Glide.with(this)
                .load(avatarurl)
                .placeholder(R.drawable.loading)
                .into(imageView);
       
        getSupportActionBar().setTitle("Profile Details");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(getDefaultShareIntent());
        return true;
    }

    private void moreUserDetails(String uName){
        String _usr;
        _usr = uName;
        if ((_usr.charAt(_usr.length() - 1))==  's' ||(_usr.charAt(_usr.length() - 1))==  'S'){
            _usr = _usr + "\'";
        }else{
            _usr = _usr + "\'s";
        }
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.retr_github_user_data, _usr));
        pd.setCancelable(false);
        pd.show();

        try{
            Client newClient = new Client();
            Service newApiService = newClient.getClient().create(Service.class);
            Call<UserDisplayResponse> newCall = newApiService.getUser("/users/" + uName);


            newCall.enqueue(new Callback<UserDisplayResponse>() {
                @Override
                public void onResponse(Call<UserDisplayResponse> call, Response<UserDisplayResponse> response) {

                    displayName = response.body().getName();
                    fullName.setText(displayName);
                    userBio = response.body().getBio();
                    Bio.setText(userBio);
                    pd.hide();

                }

                @Override
                public void onFailure(Call<UserDisplayResponse> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error retrieving more user details", Toast.LENGTH_SHORT).show();

                }
            });

        }catch(Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            pd.hide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pd.dismiss();
    }

        /** Returns a share intent */
    private Intent getDefaultShareIntent(){
        String userName = getIntent().getExtras().getString("login");
        String link = getIntent().getExtras().getString("html_url");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Github Developer");
        intent.putExtra(Intent.EXTRA_TEXT,"Check out this awesome developer @" + userName + ", " + link +" .");
        intent.putExtra(Intent.EXTRA_ORIGINATING_URI, link);
        return intent.createChooser(intent, "Open With");
    }
}
