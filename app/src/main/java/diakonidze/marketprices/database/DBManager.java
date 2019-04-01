package diakonidze.marketprices.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diakonidze.marketprices.models.Brand;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.MyListItem;
import diakonidze.marketprices.models.Packing;
import diakonidze.marketprices.models.Paramiter;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.models.TableVersion;

public class DBManager {
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;
    private static final String TAG = "DBManager";

    public static void initialaize(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
    }

    public static void openWritable() {
        db = dbHelper.getWritableDatabase();
    }

    public static void openReadable() {
        db = dbHelper.getReadableDatabase();
    }

    public static void close() {
        db.close();
    }

    public static Map<String, TableVersion> getVersions() {
        Log.d(TAG, "select data from " + DBKeys.VERSIONS_TABLE);
        Map<String, TableVersion> versions = new HashMap<>();

        Cursor cursor = db.query(DBKeys.VERSIONS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TableVersion tbv = new TableVersion();
                tbv.setTableName(cursor.getString(cursor.getColumnIndex(DBKeys.TB_NAME)));
                tbv.setVersion(cursor.getInt(cursor.getColumnIndex(DBKeys.VERSION)));
                tbv.setMaxID(cursor.getInt(cursor.getColumnIndex(DBKeys.MAX_ID)));
                versions.put(tbv.getTableName(), tbv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return versions;
    }

    public static List<Product> getProductList() {
        List<Product> products = new ArrayList<>();
        Log.d(TAG, "select data from " + DBKeys.PRODUCTS_TABLE);

        class ParamVal {
            private int id, prodID, paramID;
            private String paramVal;
            private int[] ids;
            private String[] vals;
        }
        List<ParamVal> pvList = new ArrayList<>();

        Cursor pvCursor = db.rawQuery("select * from " + DBKeys.PARAM_VALUE_TABLE + " order by " + DBKeys.PROD_ID, null);
        if (pvCursor.moveToFirst()) {
            do {
                ParamVal paramVal = new ParamVal();
                paramVal.id = pvCursor.getInt(0);
                paramVal.prodID = pvCursor.getInt(1);
                paramVal.paramID = pvCursor.getInt(2);
                paramVal.paramVal = pvCursor.getString(3);

                pvList.add(paramVal);
            } while (pvCursor.moveToNext());
        }
        pvCursor.close();

        List<ParamVal> gropedPV = new ArrayList<>();
        int pvSize = pvList.size();
        int i = 0;

        boolean notEnd = true;
        while (i < pvSize) {
            int curSize = 1;
            int curProdID = pvList.get(i).prodID;
            if (i + curSize < pvSize) {
                while (curProdID == pvList.get(i + curSize).prodID && notEnd) {
                    if (i + curSize + 1 == pvSize) {
                        notEnd = false;
                    } else {
                        curSize++;
                    }
                }
            }
            int[] ids = new int[curSize];
            String[] vals = new String[curSize];

            for (int k = 0; k < curSize; k++) {
                ids[k] = pvList.get(i + k).paramID;
                vals[k] = pvList.get(i + k).paramVal;
            }

            ParamVal pv = new ParamVal();
            pv.id = curProdID;
            pv.ids = ids;
            pv.vals = vals;
            gropedPV.add(pv);

            i += curSize;
        }

        pvSize = gropedPV.size();

        Cursor cursor = db.rawQuery("select * from " + DBKeys.PRODUCTS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(0));
                product.setTypeID(cursor.getInt(1));
                product.setQrCode(cursor.getString(2));
                product.setName(cursor.getString(3));
                product.setPackID(cursor.getInt(4));
                product.setImage(cursor.getString(5));
                product.setBrandID(cursor.getInt(6));

                for (int j = 0; j < pvSize; j++) {
                    if (product.getId() == gropedPV.get(j).id) {
                        product.setParamIDs(gropedPV.get(j).ids);
                        product.setParamValues(gropedPV.get(j).vals);
                        break;
                    }
                }
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public static List<Paramiter> getParamitersList() {
        Log.d(TAG, "select data from " + DBKeys.PARAMITERS_TABLE);
        List<Paramiter> paramiters = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + DBKeys.PARAMITERS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Paramiter paramiter = new Paramiter(
                        cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                );
                paramiters.add(paramiter);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paramiters;
    }

    public static List<Packing> getPacksList() {
        Log.d(TAG, "select data from " + DBKeys.PACKS_TABLE);
        List<Packing> packings = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + DBKeys.PACKS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Packing packing = new Packing(
                        cursor.getInt(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                );
                packings.add(packing);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packings;
    }

    public static List<Brand> getBrandsList() {
        Log.d(TAG, "select data from " + DBKeys.BRANDS_TABLE);
        List<Brand> brands = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + DBKeys.BRANDS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Brand brand = new Brand(
                        cursor.getInt(0)
                        , cursor.getString(1)
                        , ""
                );
                brands.add(brand);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return brands;
    }

    public static List<Market> getMarketsList() {
        Log.d(TAG, "select data from " + DBKeys.MARKETS_TABLE);
        List<Market> markets = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + DBKeys.MARKETS_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                Market market = new Market(
                        cursor.getInt(0)
                        , cursor.getString(1)
                );
                market.setSn(cursor.getString(2));
                market.setLogo(cursor.getString(3));
                market.setImage(cursor.getString(4));
                market.setAddress(cursor.getString(5));
                market.setLocationX(cursor.getDouble(6));
                market.setLocationY(cursor.getDouble(7));
                markets.add(market);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return markets;
    }

    public static void dropTable(String tableName) {
        db.execSQL("DROP TABLE if EXISTS " + tableName);
    }

    public static void createTable(String createSQL) {
        db.execSQL(createSQL);
    }

    public static Long insertNewProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.ID, product.getId());
        values.put(DBKeys.TYPE_ID, product.getTypeID());
        values.put(DBKeys.QR_CODE, product.getQrCode());
        values.put(DBKeys.NAME, product.getName());
        values.put(DBKeys.PACK_ID, product.getPackID());
        values.put(DBKeys.PR_IMAGE, product.getImage());
        values.put(DBKeys.BRAND_ID, product.getBrandID());

        for (int i = 0; i < product.getParamIDs().length; i++) {
            if (product.getParamIDs()[i] != 0) {
                ContentValues paramCV = new ContentValues();
                paramCV.put(DBKeys.ID, 0);
                paramCV.put(DBKeys.PROD_ID, product.getId());
                paramCV.put(DBKeys.PARAM_ID, product.getParamIDs()[i]);
                paramCV.put(DBKeys.PARAM_VALUE, product.getParamValues()[i]);
                db.insert(DBKeys.PARAM_VALUE_TABLE, null, paramCV);
            }
        }
        return db.insert(DBKeys.PRODUCTS_TABLE, null, values);
    }

    public static void updateTableVersion(TableVersion tableVersion) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.VERSION, tableVersion.getVersion());
        values.put(DBKeys.MAX_ID, tableVersion.getMaxID());
        db.update(DBKeys.VERSIONS_TABLE, values, DBKeys.TB_NAME + " = '" + tableVersion.getTableName() + "'", null);
        Log.d(TAG, "updated to -> " + tableVersion.toString());
    }

    public static void insertNewParamiter(Paramiter paramiter) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.ID, paramiter.getId());
        values.put(DBKeys.CODE, paramiter.getCode());
        values.put(DBKeys.NAME, paramiter.getName());
        values.put(DBKeys.MEASURE_UNIT, paramiter.getMeasureUnit());
        db.insert(DBKeys.PARAMITERS_TABLE, null, values);
    }

    public static void insertNewBrand(Brand brand) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.ID, brand.getId());
        values.put(DBKeys.NAME, brand.getBrandName());
        db.insert(DBKeys.BRANDS_TABLE, null, values);
    }

    public static void insertNewMarket(Market market) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.ID, market.getId());
        values.put(DBKeys.NAME, market.getMarketName());
        values.put(DBKeys.SN, market.getSn());
        values.put(DBKeys.LOGO, market.getLogo());
        values.put(DBKeys.IMAGE, market.getImage());
        values.put(DBKeys.ADRESS, market.getAddress());
        values.put(DBKeys.LOCATION_X, market.getLocationX());
        values.put(DBKeys.LOCATION_Y, market.getLocationY());
        db.insert(DBKeys.MARKETS_TABLE, null, values);
    }

    public static void insertNewPacking(Packing packing) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.ID, packing.getId());
        values.put(DBKeys.CODE, packing.getCode());
        values.put(DBKeys.VALUE_TEXT, packing.getValue());
        db.insert(DBKeys.PACKS_TABLE, null, values);
    }

    public static void insertShopingItem(int id, int isChecked) {
        ContentValues values = new ContentValues();
        values.put(DBKeys.REAL_PR_ID, id);
        values.put(DBKeys.IS_CHECKED, isChecked);
        db.insert(DBKeys.MYLIST_TABLE, null, values);
    }
//
//    public static void deleteShopingItem(int id) {
//        db.delete(DBKeys.MYLIST_TABLE, DBKeys.REAL_PR_ID + " = " + id, null);
//    }

    public static List<MyListItem> getMyList() {
        Log.d(TAG, "select data from " + DBKeys.BRANDS_TABLE);
        List<MyListItem> myListItems = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + DBKeys.MYLIST_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                MyListItem item = new MyListItem();
                item.setRealProdID(cursor.getInt(0));
                item.setChecked(cursor.getInt(1) == 1);
                myListItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myListItems;
    }

    public static void saveMyList(List<RealProduct> myItemList) {

        db.delete(DBKeys.MYLIST_TABLE, null, null);

        if (myItemList.size() > 0) {
            for (int i = 0; i < myItemList.size(); i++) {
                insertShopingItem(myItemList.get(i).getId(), myItemList.get(i).getChecked() ? 1 : 0);
            }
        }
    }
}
