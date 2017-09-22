package com.kwikwi.android.getgitusers.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kwikwi.android.getgitusers.ItemAdapter;
import com.kwikwi.android.getgitusers.MinMaxRangeInputFilter;
import com.kwikwi.android.getgitusers.R;
import com.kwikwi.android.getgitusers.api.Client;
import com.kwikwi.android.getgitusers.api.Service;
import com.kwikwi.android.getgitusers.model.Item;
import com.kwikwi.android.getgitusers.model.ItemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    TextView Disconnected, Total, ThisPage, PageResult;
    Button First, Next, Prev, Last;
    LinearLayout NoConnection, TopBar;
    ScrollView Recyclercontainer;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    int pageNumber=1;
    int currentPage,allPages;
    long allTotal;
    int lastPage=0;
    int _1st, _2nd,_diff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Total = (TextView)findViewById(R.id.total_users);
        PageResult = (TextView)findViewById(R.id.page_results) ;
        ThisPage = (EditText)findViewById(R.id.this_page);

        First = (Button)findViewById(R.id.first);
        Next = (Button)findViewById(R.id.next);
        First = (Button)findViewById(R.id.first);
        Prev = (Button)findViewById(R.id.prev);
        Last = (Button)findViewById(R.id.last);

        initViews(pageNumber);

        swipeContainer = (SwipeRefreshLayout ) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        ThisPage = (EditText)findViewById(R.id.this_page);



        ThisPage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {
                    Log.d("Logging Listener: ", ThisPage.getText()+"");
                    pageNumber = Integer.parseInt(ThisPage.getText().toString());
                }
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                loadJSON(pageNumber);
                Toast.makeText(MainActivity.this, getText(R.string.git_users_refreshd), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews(int pNo){
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.retr_github_data));
        pd.setCancelable(false);
        pd.show();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.smoothScrollToPosition(0);
        First.setEnabled(false);
        Prev.setEnabled(false);
        loadJSON(pNo);
    }

    private void loadJSON(int pgNum){
        Disconnected = (TextView)findViewById(R.id.disconnected);
        NoConnection = (LinearLayout) findViewById((R.id.no_connection));
        TopBar = (LinearLayout)findViewById(R.id.topbar);
        Recyclercontainer = (ScrollView)findViewById(R.id.recycler_scroll);

        try{
            Client Client = new Client();
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<ItemResponse> call = apiService.getItems(pgNum);

            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                    List<Item> items = response.body().getItems();
                    allTotal = response.body().getTotal_count();
                    currentPage = response.body().getItems().size();
                    Total.setText(response.body().getTotal_count()+"");
                    if ((allTotal%currentPage)>0){//check if we have a spill over dividing total result by paginated item
                        lastPage = 1;
                    }
                    allPages = (int)(allTotal/currentPage) + lastPage; //integer division result of total page and paginated items, adding spill over if any
                    ThisPage.setFilters(new InputFilter[]{new MinMaxRangeInputFilter(MainActivity.this, 1 , allPages)});//set max filter to total number of app pages

                    _diff = currentPage -1;
                    _1st = (currentPage * pageNumber)-_diff;
                    _2nd = currentPage * pageNumber;
                    PageResult.setText(getString(R.string.result_pane, _1st, _2nd, allTotal));
                    recyclerView.setAdapter(new ItemAdapter(getApplicationContext(), items));
                    TopBar.setVisibility(View.VISIBLE);
                    recyclerView.smoothScrollToPosition(0);
                    swipeContainer.setRefreshing(false);
                    pd.hide();
                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Log.d("Error", "failure msg: " + t.getMessage());
                    Toast.makeText(MainActivity.this, getString(R.string.err_fetch_data), Toast.LENGTH_SHORT).show();
                    NoConnection.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    TopBar.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    pd.dismiss();

                }
            });
        }catch (Exception e){
           Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        onPause();
    }

    public void refreshMain(View view){
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);//Restart Main Activity, attempting network reconnection
    }

    public void navigatePages(int nPageNo){
        pageNumber = nPageNo;
        loadJSON(pageNumber);
        _diff = currentPage -1;
        _1st = (currentPage * pageNumber)-_diff;
        _2nd = currentPage * pageNumber;

        PageResult.setText(getString(R.string.result_pane, _1st, _2nd, allTotal));

        setCurrentPage(pageNumber);
        if(pageNumber == 1){
            disableBack();
            enableForward();
        }
        if(pageNumber > 1){
            enableForward();
        }
        if (pageNumber == allPages){
            disableForward();
            enableBack();
        }
        if (pageNumber < allPages){
            enableBack();
        }
    }
    public void disableBack(){
        First.setEnabled(false);
        Prev.setEnabled(false);
    }
    public void enableBack(){
        First.setEnabled(true);
        Prev.setEnabled(true);
    }

    public void enableForward(){
        Last.setEnabled(true);
        Next.setEnabled(true);
    }
    public void disableForward(){
        Last.setEnabled(false);
        Next.setEnabled(false);
    }


    public void moveFirst(View view){
        if(pageNumber == 1){
            disableBack();
            enableForward();
        }
        if(pageNumber > 1){
            enableForward();
            navigatePages(1);
        }
    }

    public void moveLast(View view){
        if (pageNumber == allPages){
            disableForward();
            enableBack();
        }
        if (pageNumber < allPages){
            enableBack();
            navigatePages(allPages);
        }
    }
    public void movePrev(View view){
        if(pageNumber == 1){
            disableBack();
            enableForward();
        }
        if(pageNumber > 1){
            enableForward();
            navigatePages(pageNumber-1);
        }
    }
    public void moveNext(View view){
        if (pageNumber == allPages){
            disableForward();
            enableBack();
        }
        if (pageNumber < allPages){
            enableBack();
            navigatePages(pageNumber+1);
        }

    }

    public void setCurrentPage(int pos){
        ThisPage.setText(pos+"");
    }


}
