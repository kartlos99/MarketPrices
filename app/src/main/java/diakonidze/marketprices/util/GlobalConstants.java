package diakonidze.marketprices.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diakonidze.marketprices.AddActivity;
import diakonidze.marketprices.models.Brand;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;

public class GlobalConstants {
    public static final String HOST_URL = "http://192.168.0.101/market/"; // სამსახურში
//    public static final String HOST_URL = "http://192.168.1.6/market/"; // სახში
//    public static final String HOST_URL = "http://app.inf.ge/"; // server

    public static final String IMAGES_FOLDER = "images/";
    public static final String MARKET_LOGOS_FOLDER = "images/market_logos/";


    public static final String GET_PRODUCT_LINK = HOST_URL + "get_products.php";
    public static final String INS_REAL_PROD = HOST_URL + "ins_real_prod.php";
    public static final String GET_SEARCH_RESULT = HOST_URL + "get_search_result.php";

    public static Boolean COMPLITE_INITIAL_DOWNLOADS = false;


    public static List<Product> PRODUCT_LIST;
    public static List<Paramiter> PARAMITERS;
    public static List<Packing> PACKS;
    public static List<Brand> BRANDS;
    public static List<Market> MARKETS;
    public static List<RealProduct> SEARCH_RESULT_LIST;

    public static List<RealProduct> MY_SHOPING_LIST = new ArrayList<>();

    public static HashMap<String, Paramiter> PARAMIERS_HASH;
    public static HashMap<String, Market> MARKETS_HASH;

    public static void showtext(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
