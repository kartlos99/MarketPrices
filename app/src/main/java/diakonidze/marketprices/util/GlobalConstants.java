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

    public static Boolean COMPLITE_INITIAL_DOWNLOADS = false;
    public static String TAG = "class_Constants";


    public static List<Product> PRODUCT_LIST;
    public static List<Paramiter> PARAMITERS;
    public static List<Packing> PACKS;
    public static List<Brand> BRANDS;
    public static List<Market> MARKETS;

    public static void insertNewRealProduct(final Context mContext, RealProduct realProduct, final AddActivity activity) {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("prod_id", String.valueOf(realProduct.getProductID()));
        params.put("market_id", String.valueOf(realProduct.getMarketID()));
        params.put("price", String.valueOf(realProduct.getPrice()));
        params.put("brand_id", String.valueOf(realProduct.getBrandID()));
        params.put("packing_id", String.valueOf(realProduct.getPackingID()));
        params.put("comment", realProduct.getComment());
        for (int i= 0; i<realProduct.getParamIDs().length; i++){
            params.put("paramIDs["+i+"]", String.valueOf(realProduct.getParamIDs()[i]));
            params.put("paramValues["+i+"]", realProduct.getParamValues()[i]);
        }
//        params.put("paramIDs", Arrays.toString(realProduct.getParamIDs()));
//        params.put("paramValues", Arrays.toString(realProduct.getParamValues()));


        StringRequest request = new StringRequest(Request.Method.POST, GlobalConstants.INS_REAL_PROD + "?ert=asdf", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp_INS_PR:", response);
                if (response.equals("0")){
                    showtext(mContext, "ჩაწერა ვერ მოხერხდა, server Error");
                    activity.getbtn().setText("Caiwera!!!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERR:", error.getMessage());
                showtext(mContext, "ჩაწერა ვერ მოხერხდა, connection Error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }

        };

        Log.d("INS Real Prod Params", params.toString());
        queue.add(request);

    }

    public static void fill_prodList(Context mContext) {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonArrayRequest productListRequest = new JsonArrayRequest(GlobalConstants.GET_PRODUCT_LINK, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, " GET_PRODUCT_List - come : OK");

                PRODUCT_LIST = new ArrayList<>();
                PARAMITERS = new ArrayList<>();
                PACKS = new ArrayList<>();
                BRANDS = new ArrayList<>();
                MARKETS = new ArrayList<>();

                JSONArray jsonProducts = new JSONArray();
                JSONArray jsonParams = new JSONArray();
                JSONArray jsonPacking = new JSONArray();
                JSONArray jsonBrands = new JSONArray();
                JSONArray jsonMarkets = new JSONArray();

                try {
                    jsonProducts = response.getJSONArray(0);
                    jsonParams = response.getJSONArray(1);
                    jsonPacking = response.getJSONArray(2);
                    jsonBrands = response.getJSONArray(3);
                    jsonMarkets = response.getJSONArray(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < jsonProducts.length(); i++) {
                    try {

                        JSONObject item = jsonProducts.getJSONObject(i);
                        if (item != null) {
                            Product product = new Product(item.getInt("id"), item.getString("name"));

                            JSONArray ja_pk = item.getJSONArray("packs");
                            int[] pk = new int[ja_pk.length()];
                            for (int j = 0; j < ja_pk.length(); j++) {
                                pk[j] = ja_pk.getInt(j);
                            }
                            product.setPacks(pk);

                            JSONArray ja_param = item.getJSONArray("param");
                            int[] param = new int[ja_param.length()];
                            for (int j = 0; j < ja_param.length(); j++) {
                                param[j] = ja_param.getInt(j);
                            }
                            product.setParams(param);

                            if (!item.isNull("p_img")) {
                                product.setImage(item.getString("p_img"));
                            } else if (!item.isNull("pt_img")) {
                                product.setImage(item.getString("pt_img"));
                            } else if (!item.isNull("pg_img")) {
                                product.setImage(item.getString("pg_img"));
                            } else {
                                product.setImage("");
                            }

                            PRODUCT_LIST.add(product);
                            Log.d(TAG, "item: " + item.toString());
                            Log.d(TAG, "prod: " + product.allToString());
                        }


                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                for (int i = 0; i < jsonParams.length(); i++) {
                    try {
                        JSONObject item = jsonParams.getJSONObject(i);
                        Paramiter paramiter = new Paramiter(item.getInt("id")
                                , item.getString("code")
                                , item.getString("name")
                                , item.getString("measureUnit")
                        );
                        PARAMITERS.add(paramiter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < jsonPacking.length(); i++) {
                    try {
                        JSONObject item = jsonPacking.getJSONObject(i);
                        Packing packing = new Packing(
                                item.getInt("id"),
                                item.getString("code"),
                                item.getString("valueText")
                        );
                        PACKS.add(packing);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < jsonBrands.length(); i++) {
                    try {
                        JSONObject item = jsonBrands.getJSONObject(i);
                        Brand brand = new Brand(
                                item.getInt("id"),
                                item.getString("brandName"),
                                item.getString("brandNameEng")
                        );
                        BRANDS.add(brand);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < jsonMarkets.length(); i++) {
                    try {
                        JSONObject item = jsonMarkets.getJSONObject(i);
                        Market market = new Market(
                                item.getInt("id"),
                                item.getString("marketName")
                        );
                        market.setLogo(item.getString("logo"));
                        market.setSn(item.getString("sn"));
                        market.setAddress(item.getString("address"));
                        market.setComment(item.getString("comment"));

                        MARKETS.add(market);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, " MARKETs SIZE : " + MARKETS.size());

                COMPLITE_INITIAL_DOWNLOADS = true;
                long time = System.currentTimeMillis();
                Log.d(TAG, " Time value in millisecinds " + time);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " GET_PRODUCT_List - come : error");
                Log.d(TAG, error.getLocalizedMessage());

            }
        });

        queue.add(productListRequest);

    }


    public static void showtext(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
