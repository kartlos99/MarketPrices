package diakonidze.marketprices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import diakonidze.marketprices.adapters.SearchListAdapter;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.Keys;
import diakonidze.marketprices.util.NetService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements NetService.taskCompliteListener {

    private static final int REQUEST_CODE_QR_SCAN = 201;
    Context mContext = SearchActivity.this;
    private static final String TAG = "SearchActivity";

    // widgets
    RecyclerView recyclerView;
    SearchView searchView;

    // vars
    SearchListAdapter adapter;
    NetService ns;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
            ns.getSearchedProducts(savedInstanceState.getString("filter_text"), null);
        }else {
            ns.getSearchedProducts("", null);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ns.getSearchedProducts(query, null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, " onChange: " + newText);
                if (newText.length() > 1 || newText.isEmpty())
                    ns.getSearchedProducts(newText, null);
                return false;
            }
        });

        ImageButton btnQrSearch = findViewById(R.id.imgbtn_search_vs_qr);
        btnQrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QrScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
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
    public void onComplite(String key) {
        // damtavrda dzebnis resultatebis chamotvirtva da gamogvaqvs
        Log.d(TAG, " resul size = " + GlobalConstants.SEARCH_RESULT_LIST.size());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SearchListAdapter(mContext, new ArrayList<>(GlobalConstants.SEARCH_RESULT_LIST));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK && data != null) {
            String qrCode = data.getStringExtra(Keys.QR_SCAN_RESULT);
            Log.d(TAG, "- QRcode - " + qrCode);
            TextView tv_info = findViewById(R.id.tv_filters);
            tv_info.setText("QR: " + qrCode);
            ns.getSearchedProducts(null, qrCode);

        }
    }
}
