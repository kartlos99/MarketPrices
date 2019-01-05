package diakonidze.marketprices;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;

//import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import diakonidze.marketprices.adapters.AutoCompliteMarketAdapter;
import diakonidze.marketprices.adapters.AutoCompliteProductAdapter;
import diakonidze.marketprices.customViews.ParamInputView;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.NetService;

import static androidx.core.content.FileProvider.getUriForFile;

public class AddActivity extends AppCompatActivity implements NetService.taskCompliteListener {

    private static final String TAG = "AddActivity";
    private static final int CHOOSE_IMG_REQUEST = 902;
    private static final int TAKE_IMG_REQUEST = 903;
    private static final int MAX_IMAGE_SIZE = 800;

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

    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalConstants.PRODUCT_LIST != null) {
            AutoCompliteProductAdapter productAdapter = new AutoCompliteProductAdapter(mContext, new ArrayList<>(GlobalConstants.PRODUCT_LIST));
            inputProduct.setAdapter(productAdapter);
        }
        if (GlobalConstants.MARKETS != null) {
            AutoCompliteMarketAdapter marketAdapter = new AutoCompliteMarketAdapter(mContext, new ArrayList<Market>(GlobalConstants.MARKETS));
            inputMarket.setAdapter(marketAdapter);
        }
        hideKeyboard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            if (GlobalConstants.PRODUCT_LIST != null) {
                Product product;
                for (int i = 0; i < GlobalConstants.PRODUCT_LIST.size(); i++) {
                    if (newRealProduct.getProductID() == GlobalConstants.PRODUCT_LIST.get(i).getId()) {
                        product = GlobalConstants.PRODUCT_LIST.get(i);
                        afterProductSelected(product);
                        break;
                    }
                }
            }
            Chip chip = chipGroup.findViewWithTag(newRealProduct.getPackingID());
            if (chip != null)
                chip.setChecked(true);

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
                newRealProduct.setParamIDs(product.getParams());

                afterProductSelected(product);
                hideKeyboard();
            }


        });

        // magazia avirchiet
        inputMarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Market market = (Market) inputMarket.getAdapter().getItem(position);
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
                        newRealProduct.setBrandID(GlobalConstants.BRANDS.get(i).getId());
                    }
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProduct = checkProductInput(inputProduct.getText().toString());
                validateMarket = checkMarketInput(inputMarket.getText().toString());
                validateBrand = checkBrandInput(inputBrand.getText().toString());
                checkAll();
            }
        });

        hideKeyboard();
    }

    private void afterProductSelected(Product product) {
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
    }

    private void checkAll() {
        if (!validateProduct) {
            // aseti produqti ar gvaqvs bazashi da ra vqnat?
            askforProductValidation();
        } else if (!validateMarket) {
            // aseti magazia ar gvaqvs bazashi da ra vqnat?
            askforMarketValidation();
        } else if (!validateBrand) {
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
        builder.setMessage("დაემატოს \'" + inputBrand.getText().toString() + "\' როგორც ახალი ბრენდი?")
                .setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateBrand = true;
                        newRealProduct.setBrandID(0);
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
        builder.setMessage("დაემატოს \'" + inputProduct.getText().toString() + "\' როგორც ახალი პროდუქტი?")
                .setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateProduct = true;
                        paramConteiner.removeAllViews();
                        chipGroup.removeAllViews();
                        newRealProduct.setProductID(0);
                        newRealProduct.setProduct_name(inputProduct.getText().toString());
                        newRealProduct.setPackingID(0);
                        newRealProduct.setParamIDs(null);
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
        if (validateProduct && validateMarket && validateBrand) {

            if (newRealProduct.getProductID() == 0) {
                newRealProduct.setPackingID(0);
            }

            if (etPrice.getText().toString().isEmpty()) {
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
            ns.insertNewRealProduct(newRealProduct, imageToString(bitmap));
        }
        validateProduct = false;
        validateMarket = false;
        validateBrand = false;
    }

    private boolean checkProductInput(String inputText) {
        for (int i = 0; i < GlobalConstants.PRODUCT_LIST.size(); i++) {
            if (GlobalConstants.PRODUCT_LIST.get(i).getName().equals(inputText)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMarketInput(String inputText) {
        for (int i = 0; i < GlobalConstants.MARKETS.size(); i++) {
            if (GlobalConstants.MARKETS.get(i).toString().equals(inputText)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkBrandInput(String inputText) {
        for (int i = 0; i < GlobalConstants.BRANDS.size(); i++) {
            if (GlobalConstants.BRANDS.get(i).getBrandName().equals(inputText)) {
                return true;
            }
        }
        return false;
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
        @SuppressLint("WrongConstant")
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
    public void onComplite() {
        GlobalConstants.showtext(mContext, "ჩაწერილია!");
        Log.d(TAG, "OnComplite shemovida");
        formReset();
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
    }
}
