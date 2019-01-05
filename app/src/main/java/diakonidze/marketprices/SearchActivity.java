package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import diakonidze.marketprices.adapters.SearchListAdapter;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.NetService;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements NetService.taskCompliteListener {

    Context mContext = SearchActivity.this;
    private static final String TAG = "SearchActivity";

    // widgets
    RecyclerView recyclerView;
    SearchView searchView;

    // vars
    SearchListAdapter adapter;
    NetService ns;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("filter_text", searchView.getQuery().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init_components();

        ns = new NetService(mContext);
        ns.setCompliteListener(this);

        if (savedInstanceState != null){
            ns.getSearchedProducts(savedInstanceState.getString("filter_text"));
        }else {
            ns.getSearchedProducts("");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ns.getSearchedProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, " onChange: " + newText);
                if (newText.length() > 1 || newText.isEmpty())
                    ns.getSearchedProducts(newText);
                return false;
            }
        });

    }

    private void init_components() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bnm_promotion:
                        Intent intent1 = new Intent(mContext, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.bnm_search:

                        break;
                    case R.id.bnm_add:
                        Intent intent3 = new Intent(mContext, AddActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.bnm_mylist:
                        Intent intent4 = new Intent(mContext, MyListActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.bnm_user:
                        Intent intent5 = new Intent(mContext, PersonalActivity.class);
                        startActivity(intent5);
                        break;
                }
                overridePendingTransition(0, 0);
                return false;
            }
        });

        Menu bottomMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = bottomMenu.getItem(1);
        menuItem.setChecked(true);

        recyclerView = findViewById(R.id.list_search_results);
        searchView = findViewById(R.id.search_view);
    }

    @Override
    public void onComplite() {
        // damtavrda dzebnis resultatebis chamotvirtva da gamogvaqvs
        Log.d(TAG, " resul size = " + GlobalConstants.SEARCH_RESULT_LIST.size());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SearchListAdapter(mContext, new ArrayList<>(GlobalConstants.SEARCH_RESULT_LIST));
        recyclerView.setAdapter(adapter);
    }
}
