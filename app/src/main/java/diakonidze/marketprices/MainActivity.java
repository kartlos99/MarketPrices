package diakonidze.marketprices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import diakonidze.marketprices.database.DBManager;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.MyListItem;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.NetService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // layout elements - widgets
    private AutoCompleteTextView inputProduct;
    private AutoCompleteTextView inputBrand;
    private AutoCompleteTextView inputMarket;
    private ChipGroup chipGroup;
    private LinearLayout paramConteiner;
    private FloatingActionButton btnDone;
    private TextInputEditText etPrice;
    private TextInputEditText etMessage;
    private AppCompatImageButton btnChooseImage, btnTakeImage;
    private ImageView imgPrRealImage;

    private LinearLayout uncheckedConteiner, checkedConteiner;
    // widgets
    RecyclerView recyclerView;
    SearchView searchView;
    public static ProgressBar progressBar;

    // vars


    // vars

    //    private int selectedPackID = 0;
    //    private int selectedBrandID = 0;
    private Boolean validateProduct = false;
    private Boolean validateMarket = false;
    private Boolean validateBrand = false;
    private Bitmap bitmap;
    private Uri imagePath;
    private String pictureImagePath = "";
    private String mCurrentPhotoPath = "";
    private int lastPhotoImportOper = 0;

    CameraSource cameraSource;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    TextView textViewQR;

    Context mContext = MainActivity.this;
    private static String TAG = "mainActivity";
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private int CAMERA_PERMISION_CODE = 24;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress1);

        if (GlobalConstants.PRODUCT_LIST == null) {
            MainActivity.progressBar.setVisibility(View.VISIBLE);
            progressBar = findViewById(R.id.progress1);
            progressBar.setVisibility(View.VISIBLE);

            DBManager.initialaize(this);
            DBManager.openReadable();
            GlobalConstants.PRODUCT_LIST = DBManager.getProductList();
            GlobalConstants.PARAMITERS = DBManager.getParamitersList();
            GlobalConstants.PACKS = DBManager.getPacksList();
            GlobalConstants.BRANDS = DBManager.getBrandsList();
            GlobalConstants.MARKETS = DBManager.getMarketsList();
            GlobalConstants.MY_ITEMS_LIST = DBManager.getMyList();
            DBManager.close();
            Log.d(TAG, "list size " + GlobalConstants.PRODUCT_LIST.size());
            Log.d(TAG, "list size " + GlobalConstants.PARAMITERS.size());
            Log.d(TAG, "list size " + GlobalConstants.PACKS.size());
            Log.d(TAG, "list size " + GlobalConstants.BRANDS.size());
            Log.d(TAG, "list size " + GlobalConstants.MARKETS.size());

            GlobalConstants.PARAMITERS_HASH = new HashMap<>();
            GlobalConstants.PACKS_HASH = new HashMap<>();
            GlobalConstants.MARKETS_HASH = new HashMap<>();
            for (Paramiter item : GlobalConstants.PARAMITERS) {
                GlobalConstants.PARAMITERS_HASH.put(String.valueOf(item.getId()), item);
            }
            for (Packing item : GlobalConstants.PACKS) {
                GlobalConstants.PACKS_HASH.put(String.valueOf(item.getId()), item);
            }
            for (Market item : GlobalConstants.MARKETS) {
                GlobalConstants.MARKETS_HASH.put(String.valueOf(item.getId()), item);
            }
            MainActivity.progressBar.setVisibility(View.INVISIBLE);
            NetService ns = new NetService(mContext);
            ns.checkVersionState();
            if (GlobalConstants.MY_ITEMS_LIST.size() > 0) {
                String ids = "";
                for (int i = 0; i < GlobalConstants.MY_ITEMS_LIST.size(); i++) {
                    ids += GlobalConstants.MY_ITEMS_LIST.get(i).getRealProdID();
                    if (i < GlobalConstants.MY_ITEMS_LIST.size() - 1) {
                        ids += ",";
                    }
                }
                ns.getSearchedProducts(null, null, ids);
            }
        }


        Button button = findViewById(R.id.bb);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QrScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
            }
        });

        textViewQR = findViewById(R.id.textv);

        Chip chip = findViewById(R.id.chip8);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (GlobalConstants.PRODUCT_LIST != null) {
                    switch (menuItem.getItemId()) {
                        case R.id.bnm_promotion:

                            break;
                        case R.id.bnm_search:
                            Intent intent2 = new Intent(mContext, SearchActivity.class);
                            startActivity(intent2);
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
                } else {
                    GlobalConstants.showtext(mContext, "weit for inialization");
                }

                //Sibling transitions
                return false;
            }
        });

        Menu bottomMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = bottomMenu.getItem(0);
        menuItem.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            textViewQR.setText(data.getStringExtra("scan_result"));
        }

    }
}
