package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.models.Product;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    // layout elements
    private AutoCompleteTextView inputProduct;

    // vars
    private Context mContext = AddActivity.this;
    private String[] products = {"puri", "yveli", "khacho 6%", "khacho 0%", "yveli sulguni", "mdnari yveli", "shavi puri"};
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init_components();

        fill_prodList();

        AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, productList);

        inputProduct.setAdapter(productAdapter);

        inputProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, adapterView.getSelectedItem().toString());
                Product product = (Product) inputProduct.getAdapter().getItem(i);
                Log.d(TAG, " i: " + i);
                Log.d(TAG, " prID: " + product.getId());
            }
        });

        inputProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, adapterView.getSelectedItem().toString());
//                Log.d(TAG, " i: " + i);
//                Log.d(TAG, " L: " + l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void init_components() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.bnm_promotion:
                        Intent intent1 = new Intent(mContext, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.bnm_search:
                        Intent intent2 = new Intent(mContext, SearchActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.bnm_add:

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
        MenuItem menuItem = bottomMenu.getItem(2);
        menuItem.setChecked(true);

        inputProduct = findViewById(R.id.atv_product_name);
    }

    private void fill_prodList(){
        productList = new ArrayList<>();
        productList.add(new Product(23, "puri"));
        productList.add(new Product(24, "xacho 2%"));
        productList.add(new Product(25, "puri shavi"));
        productList.add(new Product(26, "xacho 13%"));
        productList.add(new Product(27, "puri bageti"));
        productList.add(new Product(28, "yveli shavi"));
    }
}
