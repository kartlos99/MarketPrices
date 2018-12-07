package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

//import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;

import diakonidze.marketprices.adapters.AutoCompliteMarketAdapter;
import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.customViews.ParamInputView;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.NetService;

public class AddActivity extends AppCompatActivity implements NetService.taskCompliteListener {

    private static final String TAG = "AddActivity";

    // layout elements - widgets
    private AutoCompleteTextView inputProduct;
    private AutoCompleteTextView inputBrand;
    private AutoCompleteTextView inputMarket;
    private ChipGroup chipGroup;
    private LinearLayout paramConteiner;
    private FloatingActionButton btnDone;
    private TextInputEditText etPrice;
    private TextInputEditText etMessage;


    // vars
    private Context mContext = AddActivity.this;
    private int selectedPackID = 0;
    private int selectedBrandID = 0;
    private RealProduct newRealProduct;

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalConstants.PRODUCT_LIST != null) {
            AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, new ArrayList<>(GlobalConstants.PRODUCT_LIST));
            inputProduct.setAdapter(productAdapter);
        }
        if (GlobalConstants.MARKETS != null){
            AutoCompliteMarketAdapter marketAdapter = new AutoCompliteMarketAdapter(mContext, new ArrayList<Market>(GlobalConstants.MARKETS));
            inputMarket.setAdapter(marketAdapter);
        }
        hideKeyboard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("realPR", newRealProduct);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init_components();
        if (savedInstanceState != null) {
            // recreate moxda
            newRealProduct = (RealProduct) savedInstanceState.getSerializable("realPR");
        } else {
            newRealProduct = new RealProduct();
        }

        Log.d(TAG, " RealPR: " + newRealProduct.toString());

        inputProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, adapterView.getSelectedItem().toString());
                Product product = (Product) inputProduct.getAdapter().getItem(i);
                Log.d(TAG, " i: " + i);
                Log.d(TAG, " prID: " + product.getId());
                newRealProduct.setProductID(product.getId());
                newRealProduct.setParamIDs(product.getParams());

                ImageView imageView = findViewById(R.id.img_product);
                TextView tViewSelectedName = findViewById(R.id.tv_selected_product);

                tViewSelectedName.setText(product.getName());

                if (product.getImage().isEmpty()) {
                    imageView.setImageResource(R.drawable.ic_no_image);
                } else {
                    Log.d("IMAGE", product.getImage());
                    Picasso.get()
                            .load(GlobalConstants.HOST_URL + GlobalConstants.IMAGES_FOLDER + product.getImage())
                            .into(imageView);
                }

                int[] productPacks = product.getPacks();
                chipGroup.removeAllViews();

                for (int j = 0; j < product.getPacks().length; j++) {
                    Chip chip = new Chip(mContext);

                    for (int k = 0; k < GlobalConstants.PACKS.size(); k++) {
                        if (GlobalConstants.PACKS.get(k).getId() == productPacks[j]) {
                            chip.setText(GlobalConstants.PACKS.get(k).getValue());
                            chip.setCheckable(true);
                            chip.setElevation(3.2f);
                            chip.setTag(GlobalConstants.PACKS.get(k).getId());
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Chip chipx = (Chip) v;
                                    if (chipx.isChecked()) {
                                        selectedPackID = (int) chipx.getTag();
                                    } else {
                                        selectedPackID = 0;
                                    }
                                    Boolean b = chipx.isChecked();
                                    Log.d(TAG, "Chip_TEXT: " + chipx.getText() + " state: " + b + " : TAG: " + chipx.getTag());
                                    hideKeyboard();
                                }
                            });
                            Log.d(TAG, "ChipID: " + chip.getId());
                            chipGroup.addView(chip);
                        }
                    }
                }

                int[] params = product.getParams();
                paramConteiner.removeAllViews();

                for (int j = 0; j < params.length; j++) {

                    for (int k = 0; k < GlobalConstants.PARAMITERS.size(); k++) {
                        if (params[j] == GlobalConstants.PARAMITERS.get(k).getId()) {
                            ParamInputView paramInputView = new ParamInputView(mContext, GlobalConstants.PARAMITERS.get(k));
                            paramInputView.setTag(GlobalConstants.PARAMITERS.get(k).getCode());

                            paramConteiner.addView(paramInputView);
                        }
                    }
                }

                hideKeyboard();
            }
        });

        // magazia avirchiet
        inputMarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Market market = (Market) inputMarket.getAdapter().getItem(position);
                Log.d(TAG, " pos: " + position);
                Log.d(TAG, " marketID: " + market.getId());
                newRealProduct.setMarketID(market.getId());

                ImageView imageView = findViewById(R.id.img_market_logo);

                if (market.getLogo().isEmpty()) {
                    imageView.setImageResource(R.drawable.ic_no_image);
                } else {
                    Log.d("IMAGE", market.getLogo());
                    Picasso.get()
                            .load(GlobalConstants.HOST_URL + GlobalConstants.MARKET_LOGOS_FOLDER + market.getLogo())
                            .into(imageView);

                }
            }
        });

        String[] br = new String[GlobalConstants.BRANDS.size()];
        for (int i = 0; i < GlobalConstants.BRANDS.size(); i++) {
            br[i] = GlobalConstants.BRANDS.get(i).getBrandName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.select_dialog_item, br);
        inputBrand.setAdapter(arrayAdapter);

        inputBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
                for (int i = 0; i < GlobalConstants.BRANDS.size(); i++) {
                    if (GlobalConstants.BRANDS.get(i).getBrandName().equals(inputBrand.getAdapter().getItem(position).toString())) {
                        selectedBrandID = GlobalConstants.BRANDS.get(i).getId();
                    }
                }

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRealProduct.setPackingID(selectedPackID);
                newRealProduct.setBrandID(selectedBrandID);
                if (etPrice.getText().toString().isEmpty()){
                    etPrice.setText("0");
                }
                String price = etPrice.getText().toString();
                newRealProduct.setPrice(Float.valueOf(price));
                newRealProduct.setComment(etMessage.getText().toString());

                if (newRealProduct.getParamIDs() != null) {
                    String[] paramVal = new String[newRealProduct.getParamIDs().length];

                    for (int i = 0; i < paramConteiner.getChildCount(); i++) {
                        ParamInputView paramInputView = (ParamInputView) paramConteiner.getChildAt(i);
                        if (paramInputView != null) {
                            paramVal[i] = paramInputView.getParamVal();
                            newRealProduct.getParamIDs()[i] = paramInputView.getParamID();
                        } else {
                            paramVal[i] = "0";
                            newRealProduct.getParamIDs()[i] = 0;
                        }
                    }
                    newRealProduct.setParamValues(paramVal);
                }

                Log.d(TAG, " RealPR: " + newRealProduct.toString());
                NetService ns = new NetService(mContext);
                ns.setCompliteListener(AddActivity.this);
                ns.insertNewRealProduct(newRealProduct);
            }
        });

        hideKeyboard();
    }

    public TextView getbtn(){
        TextView tViewSelectedName = findViewById(R.id.tv_selected_product);
        return tViewSelectedName;
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

        etPrice = findViewById(R.id.et_price);
        etMessage = findViewById(R.id.et_message);

        inputProduct = findViewById(R.id.atv_product_name);
        inputBrand = findViewById(R.id.atv_brand_name);
        inputMarket = findViewById(R.id.atv_market_name);
        paramConteiner = findViewById(R.id.param_conteiner);
        chipGroup = findViewById(R.id.gr_packs);
        btnDone = findViewById(R.id.btn_add_real_product);

    }

    private void hideKeyboard() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(inputProduct.getWindowToken(), 0);
    }

    @Override
    public void onComplite() {
        btnDone.setBackgroundColor(Color.RED);
        btnDone.setBackgroundResource(R.drawable.ic_no_image);
        etPrice.setText("0");
        etMessage.setText("");
        GlobalConstants.showtext(mContext, "ჩაწერილია!");
        Log.d(TAG, "OnComplite shemovida");
    }
}
