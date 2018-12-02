package diakonidze.marketprices.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;

public class Constants {
    //    public static final String HOST_URL = "http://192.168.0.101/market/"; // სამსახურში
    public static final String HOST_URL = "http://192.168.1.6/market/"; // სახში
//    public static final String HOST_URL = "http://app.inf.ge/"; // server

    public static final String IMAGES_FOLDER = "images/"; // სამსახურში

    public static final String GET_PRODUCT_LINK = HOST_URL + "get_products.php";

    public static Boolean COMPLITE_INITIAL_DOWNLOADS = false;
    public static String TAG = "class_Constants";


    public static List<Product> PRODUCT_LIST;
    public static List<Paramiter> PARAMITERS;
    public static List<Packing> PACKS;


    public static void fill_prodList(Context mContext) {

        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonArrayRequest productListRequest = new JsonArrayRequest(Constants.GET_PRODUCT_LINK, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, " GET_PRODUCT_List - come : OK");
                PRODUCT_LIST = new ArrayList<>();
                PARAMITERS = new ArrayList<>();
                PACKS = new ArrayList<>();
                JSONArray jsonProducts = new JSONArray();
                JSONArray jsonParams = new JSONArray();
                JSONArray jsonPacking = new JSONArray();

                try {
                    jsonProducts = response.getJSONArray(0);
                    jsonParams = response.getJSONArray(1);
                    jsonPacking = response.getJSONArray(2);
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
                            for (int j = 0; j < ja_pk.length(); j++){
                                pk[j] = ja_pk.getInt(j);
                            }
                            product.setPacks(pk);

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
