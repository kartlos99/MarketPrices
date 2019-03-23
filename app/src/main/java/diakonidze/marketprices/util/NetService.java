package diakonidze.marketprices.util;

import android.content.Context;
import android.util.Log;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import diakonidze.marketprices.models.Brand;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.ProductType;
import diakonidze.marketprices.models.RealProduct;

public class NetService {
    private Context netContext;
    private RequestQueue queue;
    private static String TAG = "class_NetService";

    public NetService(Context netContext) {
        this.netContext = netContext;
        queue = Volley.newRequestQueue(netContext);
    }

    public void getSearchedProducts(@Nullable String query, @Nullable final String qrcode) {
        String url;
        if (qrcode != null) {
            url = GlobalConstants.GET_SEARCH_RESULT + "?qrcode=" + qrcode;
        } else {
            url = GlobalConstants.GET_SEARCH_RESULT + "?filter_text=" + query;
        }
        Log.d(TAG, "search_URL: " + url);

        JsonArrayRequest prodSearchRequest = new JsonArrayRequest(url
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                if (qrcode == null)
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
//                        realProduct.setBrandID(jsonProduct.getInt("brandID"));
//                        realProduct.setComment(jsonProduct.getString("comment"));
                        realProduct.setPrAddDate(jsonProduct.getString("createDate").split(" ")[0]);
//                        realProduct.setProduct_name(jsonProduct.getString("product_name"));
                        realProduct.setMarketName(jsonProduct.getString("marketName"));
//                        realProduct.setBrandName(jsonProduct.getString("brandName"));
//                        realProduct.setPacking(jsonProduct.getString("packing"));

//                        JSONArray ja_paramIDs = jsonProduct.getJSONArray("paramIDs");
//                        JSONArray ja_paramVal = jsonProduct.getJSONArray("pVal");
//                        JSONArray ja_paramName = jsonProduct.getJSONArray("pName");
//                        int[] param = new int[ja_paramIDs.length()];
//                        String[] paramVal = new String[ja_paramIDs.length()];
//                        String[] paramName = new String[ja_paramIDs.length()];
//
//                        for (int j = 0; j < ja_paramIDs.length(); j++) {
//                            param[j] = ja_paramIDs.getInt(j);
//                            paramVal[j] = ja_paramVal.getString(j);
//                            paramName[j] = ja_paramName.getString(j);
//                        }
//                        realProduct.setParamIDs(param);
//                        realProduct.setParamValues(paramVal);
//                        realProduct.setParamNames(paramName);

//                        Log.d(TAG, "*************************************************************111111");
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

                    for (int ii = 0; ii < GlobalConstants.MY_SHOPING_LIST.size(); ii++) {
                        if (realProduct.getId() == GlobalConstants.MY_SHOPING_LIST.get(ii).getId()) {
                            realProduct.setInMyList(true);
                            break;
                        }
                    }

                    realProduct.setProduct(findProductByID(realProduct.getProductID()));

                    Log.d(TAG, realProduct.toString());
//                    if (qrcode == null) {
                    GlobalConstants.SEARCH_RESULT_LIST.add(realProduct);
//                    } else {
//                        GlobalConstants.LAST_SCANED_RPROD = realProduct;
//                    }
                }

                Log.d(TAG, "SRS = " + GlobalConstants.SEARCH_RESULT_LIST.size());
                if (qrcode != null && GlobalConstants.SEARCH_RESULT_LIST.size() > 0) {
                    GlobalConstants.LAST_SCANED_RPROD = GlobalConstants.SEARCH_RESULT_LIST.get(0);
                    compliteListener.onComplite(Keys.PROD_SEARCH_QR);
                } else {
                    compliteListener.onComplite(Keys.PROD_SEARCH);
                }

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

    private Product findProductByID(int productID) {
        for (int i = 0 ; i < GlobalConstants.PRODUCT_LIST.size(); i++){
            if (productID == GlobalConstants.PRODUCT_LIST.get(i).getId()){
                return GlobalConstants.PRODUCT_LIST.get(i);
            }
        }
        Log.d(TAG, "prod am ID -it ver moiZebna - " + productID);
        return null;
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
                GlobalConstants.PRODUCT_TYPES = new ArrayList<>();

                JSONArray jsonProducts = new JSONArray();
                JSONArray jsonParams = new JSONArray();
                JSONArray jsonPacking = new JSONArray();
                JSONArray jsonBrands = new JSONArray();
                JSONArray jsonMarkets = new JSONArray();
                JSONArray jsonPrTypes = new JSONArray();

                try {
                    jsonProducts = response.getJSONArray(0);
                    jsonParams = response.getJSONArray(1);
                    jsonPacking = response.getJSONArray(2);
                    jsonBrands = response.getJSONArray(3);
                    jsonMarkets = response.getJSONArray(4);
                    jsonPrTypes = response.getJSONArray(5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < jsonProducts.length(); i++) {
                    try {

                        JSONObject item = jsonProducts.getJSONObject(i);
                        if (item != null) {
                            Product product = new Product(item.getInt("id"), item.getString("name"));

                            product.setPackID(item.getInt("packID"));
                            product.setQrCode(item.getString("qr"));
                            product.setTypeID(item.getInt("typeID"));
                            product.setBrandID(item.getInt("brID"));

                            JSONArray jaParamIDs = item.getJSONArray("pID");
                            JSONArray jaParamValues = item.getJSONArray("pVal");
                            int[] paramIDs = new int[jaParamIDs.length()];
                            String[] paramValues = new String[jaParamIDs.length()];
                            for (int j = 0; j < jaParamIDs.length(); j++) {
                                paramIDs[j] = jaParamIDs.getInt(j);
                                paramValues[j] = jaParamValues.getString(j);
                            }
                            product.setParamIDs(paramIDs);
                            product.setParamValues(paramValues);

                            if (!item.isNull("p_img")) {
                                product.setImage(item.getString("p_img"));
                            } else {
                                product.setImage("");
                            }

                            GlobalConstants.PRODUCT_LIST.add(product);
//                            Log.d(TAG, "item: " + item.toString());
//                            Log.d(TAG, "prod: " + product.allToString());
                        }


                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                GlobalConstants.PARAMITERS_HASH = new HashMap<>();
                for (int i = 0; i < jsonParams.length(); i++) {
                    try {
                        JSONObject item = jsonParams.getJSONObject(i);
                        Paramiter paramiter = new Paramiter(item.getInt("id")
                                , item.getString("code")
                                , item.getString("name")
                                , item.getString("measureUnit")
                        );
                        GlobalConstants.PARAMITERS.add(paramiter);
                        GlobalConstants.PARAMITERS_HASH.put(item.getString("id"), paramiter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                GlobalConstants.PACKS_HASH = new HashMap<>();
                for (int i = 0; i < jsonPacking.length(); i++) {
                    try {
                        JSONObject item = jsonPacking.getJSONObject(i);
                        Packing packing = new Packing(
                                item.getInt("id"),
                                item.getString("code"),
                                item.getString("valueText")
                        );
                        GlobalConstants.PACKS.add(packing);
                        GlobalConstants.PACKS_HASH.put(item.getString("id"), packing);
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

                GlobalConstants.MARKETS_HASH = new HashMap<>();
                for (int i = 0; i < jsonMarkets.length(); i++) {
                    try {
                        JSONObject item = jsonMarkets.getJSONObject(i);
                        Market market = new Market(
                                item.getInt("id"),
                                item.getString("marketName")
                        );
                        if (!item.isNull("logo")) {
                            market.setLogo(item.getString("logo"));
                        }
                        market.setSn(item.getString("sn"));
                        market.setAddress(item.getString("address"));
                        if (!item.isNull("comment")) {
                            market.setComment(item.getString("comment"));
                        }

                        GlobalConstants.MARKETS.add(market);
                        GlobalConstants.MARKETS_HASH.put(item.getString("id"), market);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < jsonPrTypes.length(); i++) {
                    try {
                        JSONObject item = jsonPrTypes.getJSONObject(i);
                        ProductType productType = new ProductType(item.getInt("id"));
                        productType.setCode(item.getString("code"));
                        productType.setName(item.getString("name"));
                        productType.setImage(item.getString("image"));

                        JSONArray jaPacks = item.getJSONArray("all_pack");
                        int[] packs = new int[jaPacks.length()];
                        for (int j = 0; j < jaPacks.length(); j++) {
                            packs[j] = jaPacks.getInt(j);
                        }
                        JSONArray jaParam = item.getJSONArray("all_param");
                        int[] params = new int[jaParam.length()];
                        for (int j = 0; j < jaParam.length(); j++) {
                            params[j] = jaParam.getInt(j);
                        }
                        productType.setAll_pack(packs);
                        productType.setAll_param(params);

                        GlobalConstants.PRODUCT_TYPES.add(productType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, " MARKETs SIZE : " + GlobalConstants.MARKETS_HASH.size());

                GlobalConstants.COMPLITE_INITIAL_DOWNLOADS = true;
                long time = System.currentTimeMillis();
                Log.d(TAG, " Time value in millisecinds " + time);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, " GET_PRODUCT_List - come : error");
                Log.d(TAG, Arrays.toString(error.getStackTrace()));
            }
        });

        queue.add(productListRequest);
    }

    // ***********************  axali realuri produqtis chawera  ********************************
    public void insertNewRealProduct(RealProduct realProduct, String imageStr) {
        Log.d(TAG, " ins_new_rProd!!!");
        final HashMap<String, String> params = new HashMap<>();

        params.put("prod_id", String.valueOf(realProduct.getProductID()));
        if (realProduct.getProductID() == 0) {
            // e.i. ar vicit ra produqtia, axali emateba
            params.put("prod_name", realProduct.getProduct().getName());
            params.put("packing_id", String.valueOf(realProduct.getProduct().getPackID()));

            for (int i = 0; i < realProduct.getProduct().getParamIDs().length; i++) {
                params.put("paramIDs[" + i + "]", String.valueOf(realProduct.getProduct().getParamIDs()[i]));
                params.put("paramValues[" + i + "]", realProduct.getProduct().getParamValues()[i]);
            }

            params.put("brand_id", String.valueOf(realProduct.getProduct().getBrandID()));
            if (realProduct.getProduct().getBrandID() == 0) {
                params.put("brand_name", realProduct.getBrandName());
            }
            if (realProduct.getProduct().getQrCode() != null) {
                params.put("qr", realProduct.getProduct().getQrCode());
            }else {
                params.put("qr", "123");
            }
        } else {
            // e.i. vicit ra produqticaa
        }
        params.put("market_id", String.valueOf(realProduct.getMarketID()));
        if (realProduct.getMarketID() == 0) {
            params.put("market_name", realProduct.getMarketName());
        }

        params.put("price", String.valueOf(realProduct.getPrice()));
        params.put("comment", realProduct.getComment());
        if (imageStr != null && !imageStr.isEmpty()) {
            params.put("image", imageStr);
        }
        Log.d("URL:", GlobalConstants.INS_REAL_PROD);
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
                        String error1 = jsonObject.getString("error1");
                        GlobalConstants.showtext(netContext, "ჩაწერა ვერ მოხერხდა, server Error:\n" + error + " + " + error1);
                    } else {
                        // aq unda gavaketot rame roca namdvilad vicit rom chaiwera
                        compliteListener.onComplite(Keys.INS_REAL_PROD);
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
            protected Map<String, String> getParams() {
                return params;
            }
        };

        Log.d("INS Real Prod Params", params.toString());
        queue.add(request);
    }

    // am inerfaiss viyenebt imistvis rom Activitis gavagebinot
// ragac procesis dasruleba, da iq ragac operaciebi gavaketot
    public interface taskCompliteListener {
        void onComplite(String servCode);

    }

    private taskCompliteListener compliteListener = null;

    public void setCompliteListener(taskCompliteListener mListener) {
        compliteListener = mListener;
    }
}
