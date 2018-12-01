package diakonidze.marketprices.util;

import android.content.Context;
import android.widget.Toast;

public class Constants {
    public static final String HOST_URL = "http://192.168.0.101/market/";
//    public static final String HOST_URL = "http://app.inf.ge/show_res.php";


    public static final String GET_PRODUCT_LINK = HOST_URL + "get_products.php";


    public static void showtext(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
