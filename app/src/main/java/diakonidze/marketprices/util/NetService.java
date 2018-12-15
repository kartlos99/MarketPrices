package diakonidze.marketprices.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import diakonidze.marketprices.models.Brand;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;

public class NetService {
    private Context netContext;
    private RequestQueue queue;
    private static String TAG = "class_NetService";

    public NetService(Context netContext) {
        this.netContext = netContext;
        queue = Volley.newRequestQueue(netContext);
    }

    public void getSearchedProducts(String query) {
        JsonArrayRequest prodSearchRequest = new JsonArrayRequest(GlobalConstants.GET_SEARCH_RESULT + "?filter_text=" + query
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GlobalConstants.SEARCH_RESULT_LIST = new ArrayList<>();
//                Log.d(TAG, "resp_size = " + response.length());
                for (int i = 0; i < response.length(); i++) {
                    RealProduct realProduct = new RealProduct();
                    JSONObject jsonProduct = null;
                    try {
                        jsonProduct = response.getJSONObject(i);

                        realProduct.setId(jsonProduct.getInt("id"));
                        realProduct.setProductID(jsonProduct.getInt("productID"));
                        realProduct.setMarketID(jsonProduct.getInt("marketID"));
                        realProduct.setPrice(Float.valueOf(jsonProduct.getString("price")));
                        realProduct.setBrandID(jsonProduct.getInt("brandID"));
                        realProduct.setPackingID(jsonProduct.getInt("packingID"));
                        realProduct.setComment(jsonProduct.getString("comment"));
                        realProduct.setPrAddDate(jsonProduct.getString("createDate").split(" ")[0]);
                        realProduct.setProduct_name(jsonProduct.getString("product_name"));
                        realProduct.setMarketName(jsonProduct.getString("marketName"));
                        realProduct.setBrandName(jsonProduct.getString("brandName"));
                        realProduct.setPacking(jsonProduct.getString("packing"));

                        JSONArray ja_paramIDs = jsonProduct.getJSONArray("paramIDs");
                        JSONArray ja_paramVal = jsonProduct.getJSONArray("pVal");
                        JSONArray ja_paramName = jsonProduct.getJSONArray("pName");
                        int[] param = new int[ja_paramIDs.length()];
                        String[] paramVal = new String[ja_paramIDs.length()];
                        String[] paramName = new String[ja_paramIDs.length()];

                        for (int j = 0; j < ja_paramIDs.length(); j++) {
                            param[j] = ja_paramIDs.getInt(j);
                            paramVal[j] = ja_paramVal.getString(j);
                            paramName[j] = ja_paramName.getString(j);
                        }
                        realProduct.setParamIDs(param);
                        realProduct.setParamValues(paramVal);
                        realProduct.setParamNames(paramName);

                        if (!jsonProduct.isNull("image")) {
                            realProduct.setImage(jsonProduct.getString("image"));
                        } else if (!jsonProduct.isNull("p_image")) {
                            realProduct.setImage(jsonProduct.getString("p_image"));
                        } else {
                            realProduct.setImage(realProduct.getId() + ".jpg");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int ii = 0; ii < GlobalConstants.MY_SHOPING_LIST.size(); ii++){
                        if (realProduct.getId() == GlobalConstants.MY_SHOPING_LIST.get(ii).getId()){
                            realProduct.setInMyList(true);
                            break;
                        }
                    }

                    Log.d(TAG, realProduct.toString());
                    GlobalConstants.SEARCH_RESULT_LIST.add(realProduct);
                }

                Log.d(TAG, "SRS = " + GlobalConstants.SEARCH_RESULT_LIST.size());
                compliteListener.onComplite();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " GET_Searched_List - come : error");
                Log.d(TAG, error.getStackTrace().toString());
            }
        });

        queue.add(prodSearchRequest);

    }

    // *****************************  tavdapirveli monacemebis wamogeba  **************************
    public void fill_prodList() {

        JsonArrayRequest productListRequest = new JsonArrayRequest(GlobalConstants.GET_PRODUCT_LINK, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, " GET_PRODUCT_List - come : OK");

                GlobalConstants.PRODUCT_LIST = new ArrayList<>();
                GlobalConstants.PARAMITERS = new ArrayList<>();
                GlobalConstants.PACKS = new ArrayList<>();
                GlobalConstants.BRANDS = new ArrayList<>();
                GlobalConstants.MARKETS = new ArrayList<>();

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

                            GlobalConstants.PRODUCT_LIST.add(product);
                            Log.d(TAG, "item: " + item.toString());
                            Log.d(TAG, "prod: " + product.allToString());
                        }


                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                GlobalConstants.PARAMIERS_HASH = new HashMap<>();
                for (int i = 0; i < jsonParams.length(); i++) {
                    try {
                        JSONObject item = jsonParams.getJSONObject(i);
                        Paramiter paramiter = new Paramiter(item.getInt("id")
                                , item.getString("code")
                                , item.getString("name")
                                , item.getString("measureUnit")
                        );
                        GlobalConstants.PARAMITERS.add(paramiter);
                        GlobalConstants.PARAMIERS_HASH.put(item.getString("id"), paramiter);
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
                        GlobalConstants.PACKS.add(packing);
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
                        GlobalConstants.BRANDS.add(brand);
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

                        GlobalConstants.MARKETS.add(market);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, " MARKETs SIZE : " + GlobalConstants.MARKETS.size());

                GlobalConstants.COMPLITE_INITIAL_DOWNLOADS = true;
                long time = System.currentTimeMillis();
                Log.d(TAG, " Time value in millisecinds " + time);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " GET_PRODUCT_List - come : error");
                Log.d(TAG, error.getStackTrace().toString());
            }
        });

        queue.add(productListRequest);
    }

    // ***********************  axali realuri produqtis chawera  ********************************
    public void insertNewRealProduct(RealProduct realProduct, String imageStr) {

        final HashMap<String, String> params = new HashMap<String, String>();

        params.put("prod_id", String.valueOf(realProduct.getProductID()));
        if (realProduct.getProductID() == 0){
            params.put("prod_name", realProduct.getProduct_name());
        }else {
            params.put("packing_id", String.valueOf(realProduct.getPackingID()));
            for (int i = 0; i < realProduct.getParamIDs().length; i++) {
                params.put("paramIDs[" + i + "]", String.valueOf(realProduct.getParamIDs()[i]));
                params.put("paramValues[" + i + "]", realProduct.getParamValues()[i]);
            }
        }
        params.put("market_id", String.valueOf(realProduct.getMarketID()));
        if (realProduct.getMarketID() == 0){
            params.put("market_name", realProduct.getMarketName());
        }
        params.put("brand_id", String.valueOf(realProduct.getBrandID()));
        if (realProduct.getBrandID() == 0){
            params.put("brand_name", realProduct.getBrandName());
        }
        params.put("price", String.valueOf(realProduct.getPrice()));
        params.put("comment", realProduct.getComment());
        if (imageStr != null && !imageStr.isEmpty()){
            params.put("image", imageStr);
        }

        StringRequest request = new StringRequest(Request.Method.POST, GlobalConstants.INS_REAL_PROD
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp_INS_PR:", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("id");
                    if (id == 0) {
                        String error = jsonObject.getString("error");
                        GlobalConstants.showtext(netContext, "ჩაწერა ვერ მოხერხდა, server Error:\n" + error);
                    } else {
                        // aq unda gavaketot rame roca namdvilad vicit rom chaiwera
                        compliteListener.onComplite();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERR:", error.getMessage());
                GlobalConstants.showtext(netContext, "ჩაწერა ვერ მოხერხდა, connection Error");
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

    // am inerfaiss viyenebt imistvis rom Activitis gavagebinot
    // ragac procesis dasruleba, da iq ragac operaciebi gavaketot
    public interface taskCompliteListener {
        void onComplite();
    }

    private taskCompliteListener compliteListener = null;

    public void setCompliteListener(taskCompliteListener mListener) {
        compliteListener = mListener;
    }
}
