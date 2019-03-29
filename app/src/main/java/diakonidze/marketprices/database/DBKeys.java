package diakonidze.marketprices.database;

public class DBKeys {

    public static final String PRODUCTS_TABLE = "products";
    public static final String BRANDS_TABLE = "brands";
    public static final String MARKETS_TABLE = "markets";
    public static final String PARAMITERS_TABLE = "paramiters";
    public static final String PACKS_TABLE = "packs";
    public static final String PARAM_VALUE_TABLE = "paramvalue";
    public static final String VERSIONS_TABLE = "versions";
    public static final String MYLIST_TABLE = "versions";

    public static final String ID = "id";
    public static final String TYPE_ID = "typeid";
    public static final String QR_CODE = "qr";
    public static final String NAME = "name";
    public static final String PACK_ID = "packID";
    public static final String PR_IMAGE = "p_img";
    public static final String BRAND_ID = "brID";

    public static final String CODE = "code";
    public static final String MEASURE_UNIT = "measureUnit";

    public static final String VALUE_TEXT = "valueText";

    public static final String SN = "sn";
    public static final String LOGO = "logo";
    public static final String IMAGE = "image";
    public static final String ADRESS = "adress";
    public static final String LOCATION_X = "locationX";
    public static final String LOCATION_Y = "locationY";

    public static final String TB_NAME = "table_name";
    public static final String VERSION = "version";
    public static final String MAX_ID = "maxID";

    public static final String REAL_PR_ID = "realPrID";
    public static final String IS_CHECKED = "ischecked";

    public static final String PARAM_VALUE = "value";
    public static final String PROD_ID = "prodID";
    public static final String PARAM_ID = "paramID";

    public static final String CREATE_TABLE_VERSION = String.format("CREATE TABLE if not EXISTS %s ( %s VARCHAR(30) NOT NULL , %s integer DEFAULT 0, %s integer DEFAULT 0 )"
            , VERSIONS_TABLE, TB_NAME, VERSION, MAX_ID);

    public static final String INITIALIZE_VERSIONS_TABLE = String.format(" INSERT INTO %s(%s)  VALUES('%s'),('%s'),('%s'),('%s'),('%s') "
            , VERSIONS_TABLE, TB_NAME, PRODUCTS_TABLE, BRANDS_TABLE, MARKETS_TABLE, PARAMITERS_TABLE, PACKS_TABLE);

    public static final String CREATE_TABLE_MYLIST = String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s integer DEFAULT 0 )"
            , MYLIST_TABLE, REAL_PR_ID, IS_CHECKED);

    public static final String CREATE_TABLE_PRODUCTS =
            String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s integer DEFAULT 0, %s VARCHAR(40) DEFAULT '', %s VARCHAR(80) DEFAULT '', %s integer DEFAULT 0, %s VARCHAR(100) DEFAULT '', %s integer DEFAULT 0 )"
            , PRODUCTS_TABLE
            , ID
            , TYPE_ID
            , QR_CODE
            , NAME
            , PACK_ID
            , PR_IMAGE
            , BRAND_ID);

    public static final String CREATE_TABLE_BRANDS = String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s VARCHAR(80) DEFAULT '' )"
            , BRANDS_TABLE, ID, NAME);

    public static final String CREATE_TABLE_MARKETS =
            String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s VARCHAR(80) DEFAULT '', %s VARCHAR(11) DEFAULT '', %s VARCHAR(100) DEFAULT '', %s VARCHAR(100) DEFAULT '', %s VARCHAR(200) DEFAULT '' , %s double, %s double)"
            , MARKETS_TABLE
            , ID
            , NAME
            , SN
            , LOGO
            , IMAGE
            , ADRESS
            , LOCATION_X
            , LOCATION_Y);

    public static final String CREATE_TABLE_PARAMITERS = String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s VARCHAR(30) DEFAULT '' , %s VARCHAR(30) DEFAULT '', %s VARCHAR(30) DEFAULT '' )"
            , PARAMITERS_TABLE, ID, CODE, NAME, MEASURE_UNIT);

    public static final String CREATE_TABLE_PACKS = String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s VARCHAR(30) DEFAULT '' , %s VARCHAR(30) DEFAULT '')"
            , PACKS_TABLE, ID, CODE, VALUE_TEXT);

    public static final String CREATE_TABLE_PARAMVALUE = String.format("CREATE TABLE if not EXISTS %s ( %s integer , %s integer DEFAULT 0, %s integer DEFAULT 0 , %s VARCHAR(30) DEFAULT '')"
            , PARAM_VALUE_TABLE
            , ID
            , PROD_ID
            , PARAM_ID
            , PARAM_VALUE);

}