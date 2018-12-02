package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.util.Constants;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    // layout elements
    private AutoCompleteTextView inputProduct;
    private ChipGroup chipGroup;

    // vars
    private Context mContext = AddActivity.this;
//    private String[] products = {"puri", "yveli", "khacho 6%", "khacho 0%", "yveli sulguni", "mdnari yveli", "shavi puri"};
//    private List<Product> PRODUCT_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init_components();

        if (Constants.PRODUCT_LIST != null) {
            AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, Constants.PRODUCT_LIST);
            inputProduct.setAdapter(productAdapter);
        }


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

                if (product.getImage().isEmpty()) {
                    imageView.setImageResource(R.drawable.ic_no_image);
                } else {
                    Log.d("IMAGE", product.getImage());
                    Picasso.get()
                            .load(Constants.HOST_URL + Constants.IMAGES_FOLDER + product.getImage())
                            .into(imageView);
                }

                int[] productPacks = product.getPacks();
                chipGroup.removeAllViews();

                for (int j = 0; j < product.getPacks().length; j++) {
                    Chip chip = new Chip(mContext);

                    for (int k = 0; k < Constants.PACKS.size(); k++){
                        if (Constants.PACKS.get(k).getId() == productPacks[j]){
                            chip.setText(Constants.PACKS.get(k).getValue());
                            chip.setCheckable(true);
                            Log.d(TAG,"ChipID: " + chip.getId());

                            chipGroup.addView(chip);
                        }
                    }


                }

            }
        });

        chipGroup = findViewById(R.id.gr_packs);
        Chip chip = new Chip(mContext);
        chip.setText("added");
        chip.setCheckable(true);
        chipGroup.addView(chip);

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                int id = group.getCheckedChipId();
                if (id != -1) {
                    Chip chip1 = (Chip) group.getChildAt(id);
                    Log.d(TAG, "ChipID: " + chip1.getId() + " " + chip1.getText().toString());
                }

                Constants.showtext(mContext, "chekID: " + group.getCheckedChipId());
                switch (checkedId) {
                    case 1:
                        Constants.showtext(mContext, "111");
                        break;
                    case 2:
                        Constants.showtext(mContext, "2");
                        break;
                    case 3:
                        Constants.showtext(mContext, "3");

                        break;
                    case 4:
                        Constants.showtext(mContext, "44");
                        break;
                }
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


}
