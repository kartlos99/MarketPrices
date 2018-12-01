package diakonidze.marketprices;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.util.Constants;

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


        inputProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, adapterView.getSelectedItem().toString());
                Product product = (Product) inputProduct.getAdapter().getItem(i);
                Log.d(TAG, " i: " + i);
                Log.d(TAG, " prID: " + product.getId());

                ImageView imageView = findViewById(R.id.img_product);
                TextView tViewSelectedName = findViewById(R.id.tv_selected_product);

                tViewSelectedName.setText(product.getName());

                if (product.getImage().isEmpty()){
                    imageView.setImageResource(R.drawable.ic_no_image);
                } else {
                    Log.d("IMAGE", product.getImage());
                    Picasso.get()
                            .load(Constants.HOST_URL + product.getImage())
                            .into(imageView);
                }
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

                switch (menuItem.getItemId()) {
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

    private void fill_prodList() {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonArrayRequest productListRequest = new JsonArrayRequest(Constants.GET_PRODUCT_LINK, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, " GET_PRODUCT_List - come : OK");
                productList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject item = response.getJSONObject(i);
                        if (item != null) {
                            Product product = new Product(item.getInt("id"), item.getString("name"));

                            if (!item.isNull("p_img")) {
                                product.setImage(item.getString("p_img"));
                            } else if (!item.isNull("pt_img")) {
                                product.setImage(item.getString("pt_img"));
                            } else if (!item.isNull("pg_img")) {
                                product.setImage(item.getString("pg_img"));
                            } else {
                                product.setImage("");
                            }

                            productList.add(product);
                            Log.d(TAG, "item: " + item.toString());
                            Log.d(TAG, "prod: " + product.allToString());
                        }


                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, productList);
                inputProduct.setAdapter(productAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " GET_PRODUCT_List - come : error");
                Log.d(TAG, error.getMessage());

            }
        });


        queue.add(productListRequest);

    }
}
