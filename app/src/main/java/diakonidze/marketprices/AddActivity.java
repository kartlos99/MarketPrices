package diakonidze.marketprices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import diakonidze.marketprices.adapters.AutoCompliteMarketAdapter;
import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.customViews.ParamInputView;
import diakonidze.marketprices.interfaces.ProductFilterListener;
import diakonidze.marketprices.models.Brand;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.Keys;
import diakonidze.marketprices.util.NetService;

public class AddActivity extends AppCompatActivity implements NetService.taskCompliteListener, ProductFilterListener {

    private static final String TAG = "AddActivity";
    private static final int CHOOSE_IMG_REQUEST = 902;
    private static final int TAKE_IMG_REQUEST = 903;
    private static final int MAX_IMAGE_SIZE = 800;
    private static final int REQUEST_CODE_QR_SCAN = 801;

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
    private TextView tvQrcode;
    private AppCompatCheckBox chkNewProduct;
    private LinearLayout productInputArea;

    // vars
    private Context mContext = AddActivity.this;
    //    private int selectedPackID = 0;
    //    private int selectedBrandID = 0;
    private RealProduct newRealProduct;
    private Boolean validateProduct = false;
    private Boolean validateMarket = false;
    private Boolean validateBrand = false;
    private Bitmap bitmap;
    private Uri imagePath;
    private String pictureImagePath = "";
    private String mCurrentPhotoPath = "";
    private int lastPhotoImportOper = 0;
    private String qrCode;

    private NetService ns;

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalConstants.PRODUCT_LIST != null) {
            AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, new ArrayList<>(GlobalConstants.PRODUCT_LIST));
            productAdapter.setFilterListener(this);
            inputProduct.setAdapter(productAdapter);
        }
        if (GlobalConstants.MARKETS != null) {
            AutoCompliteMarketAdapter marketAdapter = new AutoCompliteMarketAdapter(mContext, new ArrayList<>(GlobalConstants.MARKETS));
            inputMarket.setAdapter(marketAdapter);
        }
        hideKeyboard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, " RealPR_________________ SAVE : " + newRealProduct.toString());
        outState.putSerializable("realPR", newRealProduct);
        outState.putParcelable("image_path_uri", imagePath);
        outState.putString("image_path_str", mCurrentPhotoPath);
        outState.putInt("img_op", lastPhotoImportOper);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init_components();
        if (savedInstanceState != null) {
            // recreate moxda
            newRealProduct = (RealProduct) savedInstanceState.getSerializable("realPR");
            Log.d(TAG, " RealPR_________________ OPEN : " + newRealProduct.toString());
            if (newRealProduct != null) {
                showRProduct(newRealProduct);
                etPrice.setText(String.valueOf(newRealProduct.getPrice()));
                etMessage.setText(newRealProduct.getComment());
            }

            lastPhotoImportOper = savedInstanceState.getInt("img_op");
            if (lastPhotoImportOper == CHOOSE_IMG_REQUEST) {
                imagePath = (Uri) savedInstanceState.get("image_path_uri");
                if (imagePath != null)
                    loadPrImageFromPath(imagePath);
            }
            if (lastPhotoImportOper == TAKE_IMG_REQUEST) {
                mCurrentPhotoPath = savedInstanceState.getString("image_path_str");
                if (mCurrentPhotoPath != null)
                    setPic();
            }
        } else {
            newRealProduct = new RealProduct();
        }

        Log.d(TAG, " RealPR: " + newRealProduct.toString());

        // productis archeva
        inputProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, adapterView.getSelectedItem().toString());
                Product product = (Product) inputProduct.getAdapter().getItem(i);
                Log.d(TAG, " i: " + i);
                Log.d(TAG, " prID: " + product.getId());
                newRealProduct.setProductID(product.getId());
                newRealProduct.setProduct(product);

                afterProductSelected(product);
                hideKeyboard();
            }
        });

        // magazia avirchiet
        inputMarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Market market = (Market) inputMarket.getAdapter().getItem(position);
                setMarket(market);
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
                String brName = inputBrand.getAdapter().getItem(position).toString();
                Brand brand = checkBrandInput(brName);
                if (brand != null) {
                    newRealProduct.getProduct().setBrandID(brand.getId());
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProduct = checkProductInput(inputProduct.getText().toString()) != null;
                validateMarket = checkMarketInput(inputMarket.getText().toString()) != null;
                if (chkNewProduct.isChecked())
                    validateBrand = checkBrandInput(inputBrand.getText().toString()) != null;
                checkAll();
            }
        });

        ImageButton imgBtnStartQR = findViewById(R.id.img_btnQrScan);
        imgBtnStartQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QrScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
            }
        });

        ImageButton btnFromReset = findViewById(R.id.imgbtn_reset_from);
        btnFromReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCode = "";
                newRealProduct = new RealProduct();
                formReset();
            }
        });

        chkNewProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productInputArea.setVisibility(View.VISIBLE);
                    formInputArea();
                } else {
                    productInputArea.setVisibility(View.GONE);
                }
            }
        });

        ns = new NetService(mContext);
        ns.setCompliteListener(this);

        hideKeyboard();
    }

    private void formInputArea() {

        paramConteiner.removeAllViews();

        for (int k = 0; k < GlobalConstants.PARAMITERS.size(); k++) {
            ParamInputView paramInputView = new ParamInputView(mContext, GlobalConstants.PARAMITERS.get(k));
            paramInputView.setTag(GlobalConstants.PARAMITERS.get(k).getCode());
            if (GlobalConstants.PARAMITERS.get(k).getCode().equals("piece")) {
                paramInputView.setParamValhint("1");
            } else {
                paramInputView.setParamValhint("0");
            }
            paramConteiner.addView(paramInputView);
        }

        chipGroup.removeAllViews();

        for (int k = 0; k < GlobalConstants.PACKS.size(); k++) {
            Chip chip = new Chip(mContext);
            chip.setText(GlobalConstants.PACKS.get(k).getValue());
            chip.setCheckable(true);
            chip.setTag(GlobalConstants.PACKS.get(k).getId());
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Chip chipx = (Chip) v;
                    if (chipx.isChecked()) {
                        newRealProduct.getProduct().setPackID((int) chipx.getTag());
                    } else {
                        newRealProduct.getProduct().setPackID(0);
                    }
                    hideKeyboard();
                }
            });
            Log.d(TAG, "ChipID: " + chip.getId());
            chipGroup.addView(chip);
        }

        inputBrand.setText("");
    }

    private void setMarket(@NonNull Market market) {
        Log.d(TAG, " marketID: ******************************" + market.getId());
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

    private void showRProduct(RealProduct rProdToShow) {
        Log.d(TAG, " RealPR_inshow: " + rProdToShow.toString());
//        if (GlobalConstants.PRODUCT_LIST != null) {
//            Product product;
//            for (int i = 0; i < GlobalConstants.PRODUCT_LIST.size(); i++) {
//                if (rProdToShow.getProductID() == GlobalConstants.PRODUCT_LIST.get(i).getId()) {
//                    product = GlobalConstants.PRODUCT_LIST.get(i);
//                    afterProductSelected(product);
//                    break;
//                }
//            }
//        }
        afterProductSelected(rProdToShow.getProduct());

//        etPrice.setText(String.valueOf(rProdToShow.getPrice()));
//        etMessage.setText(rProdToShow.getComment());
/*
        String[] rpValues = rProdToShow.getParamValues();
        int[] rpParamIDs = rProdToShow.getParamIDs();

        for (int i = 0 ; i< rpParamIDs.length; i++){
            Paramiter parameter = GlobalConstants.PARAMITERS_HASH.get(String.valueOf(rpParamIDs[i]));
            if (parameter != null){
                ParamInputView paramInputView = paramConteiner.findViewWithTag(parameter.getCode());
                EditText etParamVal = paramInputView.findViewById(R.id.et_param_value);
                etParamVal.setText(rpValues[i]);
            }
        }*/

        /*Chip chip = chipGroup.findViewWithTag(rProdToShow.getPackingID());
        if (chip != null)
            chip.setChecked(true);*/
    }

    private void afterProductSelected(Product product) {
        ImageView imageView = findViewById(R.id.img_product);
        TextView tViewSelectedName = findViewById(R.id.tv_selected_product);
        inputProduct.setText( product.getId() == 0 ? "" : product.toString());
        tvQrcode.setText(product.getQrCode());

        String productInfo = product.getName() + "\n";
        Brand brand = GlobalConstants.findBrandByID(product.getBrandID());
        if (brand != null) {
            productInfo += "მწარმოებელი: " + brand.getBrandName() + "\n";
        }

        String paramFull = "";
        for (int i = 0; i < product.getParamIDs().length; i++) {
            paramFull += Objects.requireNonNull(GlobalConstants.PARAMITERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getName() + " "
                    + product.getParamValues()[i] + " "
                    + Objects.requireNonNull(GlobalConstants.PARAMITERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getMeasureUnit();
            if (i < product.getParamIDs().length - 1) {
                paramFull += "\n";
            }
        }
        productInfo += paramFull;

        Packing packing = GlobalConstants.PACKS_HASH.get(String.valueOf(product.getPackID()));
        if (packing != null) {
            productInfo += "\nშეფუთვა: " + packing.getValue();
        }

        tViewSelectedName.setText(productInfo);
        if (product.getImage().isEmpty()) {
            imageView.setImageResource(R.drawable.ic_no_image);
        } else {
            Log.d("IMAGE", product.getImage());
            Picasso.get()
                    .load(GlobalConstants.HOST_URL + GlobalConstants.IMAGES_FOLDER + product.getImage())
                    .into(imageView);
        }

        /*int[] productPacks = product.getPacks();
        chipGroup.removeAllViews();

        for (int j = 0; j < product.getPacks().length; j++) {
            Chip chip = new Chip(mContext);

            for (int k = 0; k < GlobalConstants.PACKS.size(); k++) {
                if (GlobalConstants.PACKS.get(k).getId() == productPacks[j]) {
                    chip.setText(GlobalConstants.PACKS.get(k).getValue());
                    chip.setCheckable(true);

                    chip.setTag(GlobalConstants.PACKS.get(k).getId());
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Chip chipx = (Chip) v;
                            if (chipx.isChecked()) {
                                newRealProduct.setPackingID((int) chipx.getTag());
                            } else {
                                newRealProduct.setPackingID(0);
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
        }*/

        /*int[] params = product.getParamIDs();
        paramConteiner.removeAllViews();

        for (int j = 0; j < params.length; j++) {

            for (int k = 0; k < GlobalConstants.PARAMITERS.size(); k++) {
                if (params[j] == GlobalConstants.PARAMITERS.get(k).getId()) {
                    ParamInputView paramInputView = new ParamInputView(mContext, GlobalConstants.PARAMITERS.get(k));
                    paramInputView.setTag(GlobalConstants.PARAMITERS.get(k).getCode());

                    paramConteiner.addView(paramInputView);
                }
            }
        }*/
    }

    private void checkAll() {
        if (!validateProduct) {
            // aseti produqti ar gvaqvs bazashi da ra vqnat?
            askforProductValidation();
        } else if (!validateMarket) {
            // aseti magazia ar gvaqvs bazashi da ra vqnat?
            askforMarketValidation();
        } else if (!validateBrand && chkNewProduct.isChecked()) {
            // aseti brandi ar gvaqvs bazashi da ra vqnat?
            askforBrandValidation();
        } else {
            postNewProductData();
        }
    }

    private void askforBrandValidation() {
        Log.d(TAG, " ახალი ბრანდი");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("ახალი ბრანდი/მწარმოებელი");
        builder.setMessage("დაემატოს \' " + inputBrand.getText().toString() + " \' როგორც ახალი ბრენდი?")
                .setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateBrand = true;
                        newRealProduct.getProduct().setBrandID(0);
                        newRealProduct.setBrandName(inputBrand.getText().toString());
                        checkAll();
                    }
                })
                .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputBrand.requestFocus();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askforMarketValidation() {
        Log.d(TAG, " ახალი მაღაზია");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("ახალი მაღაზია");
        builder.setMessage("დაემატოს \'" + inputMarket.getText().toString() + "\' როგორც ახალი მაღაზია?")
                .setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateMarket = true;
                        newRealProduct.setMarketID(0);
                        newRealProduct.setMarketName(inputMarket.getText().toString());
                        checkAll();
                    }
                })
                .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputMarket.requestFocus();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void askforProductValidation() {
        Log.d(TAG, " ახალი პროდუქტი");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("ახალი პროდუქტი");
        builder.setMessage("დაემატოს \' " + inputProduct.getText().toString() + " \' როგორც ახალი პროდუქტი?")
                .setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateProduct = true;
//                        paramConteiner.removeAllViews();
//                        chipGroup.removeAllViews();
                        newRealProduct.setProductID(0);
                        newRealProduct.getProduct().setName(inputProduct.getText().toString());
//                        newRealProduct.getProduct().setPackID(0);
//                        newRealProduct.getProduct().setParamIDs(null);
                        ImageView imageView = findViewById(R.id.img_product);
                        TextView tViewSelectedName = findViewById(R.id.tv_selected_product);
                        tViewSelectedName.setText("");
                        imageView.setImageResource(R.drawable.ic_no_image);
                        checkAll();
                    }
                })
                .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputProduct.requestFocus();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void postNewProductData() {
        Log.d(TAG, " postNewProductData: შევიდა");
        if (validateProduct && validateMarket && (validateBrand || !chkNewProduct.isChecked())) {

            if (etPrice.getText().toString().isEmpty()) {
                etPrice.setText("0");
            }
            String price = etPrice.getText().toString();
            newRealProduct.setPrice(Float.valueOf(price));
            newRealProduct.setComment(etMessage.getText().toString());

            if (newRealProduct.getProductID() == 0) {
                String[] paramVal = new String[GlobalConstants.PARAMITERS.size()];
                int[] paramIDs = new int[GlobalConstants.PARAMITERS.size()];

                for (int i = 0; i < paramConteiner.getChildCount(); i++) {
                    ParamInputView paramInputView = (ParamInputView) paramConteiner.getChildAt(i);
                    if (paramInputView != null) {
                        paramVal[i] = paramInputView.getParamVal();
                        paramIDs[i] = paramInputView.getParamID();
                    } else {
                        paramVal[i] = "0";
                        paramIDs[i] = 0;
                    }
                }
                newRealProduct.getProduct().setParamIDs(paramIDs);
                newRealProduct.getProduct().setParamValues(paramVal);

                if (qrCode != null)
                    newRealProduct.getProduct().setQrCode(qrCode);
            }

            Log.d(TAG, " RealPR: " + newRealProduct.toString());
            NetService ns = new NetService(mContext);
            ns.setCompliteListener(AddActivity.this);
            ns.insertNewRealProduct(newRealProduct, imageToString(bitmap));
        }
        validateProduct = false;
        validateMarket = false;
        validateBrand = false;
    }

    private Product checkProductInput(String inputText) {
        for (int i = 0; i < GlobalConstants.PRODUCT_LIST.size(); i++) {
            if (GlobalConstants.PRODUCT_LIST.get(i).toString().equals(inputText)) {
                return GlobalConstants.PRODUCT_LIST.get(i);
            }
        }
        return null;
    }

    private Market checkMarketInput(String inputText) {
        for (int i = 0; i < GlobalConstants.MARKETS.size(); i++) {
            if (GlobalConstants.MARKETS.get(i).toString().equals(inputText)) {
                return GlobalConstants.MARKETS.get(i);
            }
        }
        return null;
    }

    private Brand checkBrandInput(String inputText) {
        for (int i = 0; i < GlobalConstants.BRANDS.size(); i++) {
            if (GlobalConstants.BRANDS.get(i).getBrandName().equals(inputText)) {
                return GlobalConstants.BRANDS.get(i);
            }
        }
        return null;
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
        btnChooseImage = findViewById(R.id.btn_choose_image);
        btnTakeImage = findViewById(R.id.btn_take_image);
        imgPrRealImage = findViewById(R.id.img_prod_real_image);
        tvQrcode = findViewById(R.id.tv_qr_code);
        productInputArea = findViewById(R.id.input_product_area);
        chkNewProduct = findViewById(R.id.chk_new_prod);

        btnChooseImage.setOnClickListener(chooseImageListener);
        btnTakeImage.setOnClickListener(takeImageListener);
    }

    private View.OnClickListener chooseImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "choose image cliked");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, CHOOSE_IMG_REQUEST);
        }
    };

    private View.OnClickListener takeImageListener = new View.OnClickListener() {
        //        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            if (hasCamera()) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    deleteTempImageFile(mCurrentPhotoPath);

                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d(TAG, "ar sheiqmna faili: " + ex.getMessage());
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(mContext,
                                "diakonidze.marketprices",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, TAKE_IMG_REQUEST);
                    }
                }

            } else {
                btnTakeImage.setEnabled(false);
            }
        }
    };

    private void deleteTempImageFile(String filePath) {
        File oldFile = new File(filePath);
        if (oldFile.exists()) {
            oldFile.delete();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lastPhotoImportOper = requestCode;

        if (requestCode == CHOOSE_IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            deleteTempImageFile(mCurrentPhotoPath);
            imagePath = data.getData();
            loadPrImageFromPath(imagePath);
        }

        if (requestCode == TAKE_IMG_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "- Take Image Result -");
            setPic();
        }

        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK && data != null) {
//            GlobalConstants.LAST_SCANED_RPROD = null;
            qrCode = data.getStringExtra(Keys.QR_SCAN_RESULT);

//            tvQrcode.setText(qrCode);
//            ns.getSearchedProducts(null, qrCode);
            Log.d(TAG, "- QRcode - " + qrCode);
            findProductByQr(qrCode);
        }
    }

    private void findProductByQr(String qrCode) {
        Boolean inList = false;
        for (Product product : GlobalConstants.PRODUCT_LIST){
            if (product.getQrCode().equals(qrCode)){
                Log.d(TAG, "- Prod Found - " + qrCode);
                newRealProduct.setProduct(product);
                newRealProduct.setProductID(product.getId());
                inList = true;
                break;
            }
        }
        if (!inList){
            Log.d(TAG, "- Prod NOT Found - " + qrCode);
            newRealProduct.setProduct(new Product(0, ""));
            newRealProduct.getProduct().setQrCode(qrCode);
            newRealProduct.setProductID(0);
            inputProduct.setText("");
        }
        afterProductSelected(newRealProduct.getProduct());
    }


    private void loadPrImageFromPath(Uri path) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
            imgPrRealImage.setImageBitmap(bitmap);
            imgPrRealImage.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imgPrRealImage.getWidth();
        int targetH = imgPrRealImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 4;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgPrRealImage.setImageBitmap(bitmap);
    }

    private Boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private String imageToString(Bitmap bitmapImg) {
        if (bitmapImg == null)
            return "";
        if (bitmapImg.getWidth() > MAX_IMAGE_SIZE) {
            float ratio = Math.min(
                    (float) MAX_IMAGE_SIZE / bitmapImg.getWidth(),
                    (float) MAX_IMAGE_SIZE / bitmapImg.getHeight());
            int width = Math.round(ratio * bitmapImg.getWidth());
            int height = Math.round(ratio * bitmapImg.getHeight());
            bitmapImg = Bitmap.createScaledBitmap(bitmapImg, width, height, true);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImg.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(inputProduct.getWindowToken(), 0);
    }

    @Override
    public void onComplite(String key) {
        if (key.equals(Keys.INS_REAL_PROD)) {
            GlobalConstants.showtext(mContext, "ჩაწერილია!");
            Log.d(TAG, "OnComplite shemovida");
            formReset();
        }
        if (key.equals(Keys.PROD_SEARCH_QR) && GlobalConstants.LAST_SCANED_RPROD != null) {
            newRealProduct = GlobalConstants.LAST_SCANED_RPROD;
/*
პროდუქტის შტრიხ კოდზე არ დევს ინფორმაცია მაღაზიის შესახებ.
უბრალოდ ამ მაღაზიაში იპოვა, ჩვენს ბაზაში
            Market market = GlobalConstants.MARKETS_HASH.get(String.valueOf(newRealProduct.getMarketID()));
            if (market != null) {
                setMarket(market);
                inputMarket.setText(market.toString());
            }
*/
//            inputBrand.setText(newRealProduct.getBrandName());
            showRProduct(newRealProduct);
        }

    }

    private void formReset() {
        etPrice.setText("0");
        etMessage.setText("");
        paramConteiner.removeAllViews();
        chipGroup.removeAllViews();
        inputProduct.setText("");
        inputBrand.setText("");
        inputMarket.setText("");
        ImageView imageView = findViewById(R.id.img_market_logo);
        imageView.setImageResource(R.drawable.market);
        imgPrRealImage.setImageResource(R.drawable.ic_no_image);
        deleteTempImageFile(mCurrentPhotoPath);
        ImageView imagePr = findViewById(R.id.img_product);
        imagePr.setImageResource(R.drawable.ic_no_image);
        TextView tViewSelectedName = findViewById(R.id.tv_selected_product);
        tViewSelectedName.setText("");
        tvQrcode.setText("");
        chkNewProduct.setChecked(false);
        chkNewProduct.setVisibility(View.GONE);
        productInputArea.setVisibility(View.GONE);
    }

    @Override
    public void filteringFinished(int itemCount) {
        Log.d(TAG, "- filterSize: " + itemCount);
        if (itemCount == 0 && inputProduct.getText().toString().length() >= 3) {
            chkNewProduct.setVisibility(View.VISIBLE);
        } else {
            chkNewProduct.setVisibility(View.GONE);
            chkNewProduct.setChecked(false);
        }
    }
}