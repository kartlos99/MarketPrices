package diakonidze.marketprices.customViews;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import diakonidze.marketprices.R;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;

public class MyListItemView extends ConstraintLayout {

    private Context context;
    private LinearLayout uncheckedConteiner, checkedConteiner;
    private RealProduct product;
    private Boolean isChecked;

    private static final String TAG = "My_List_ItemView";

    private ConstraintLayout parentView;
    private CheckBox checkBox;
    private Button btn_itemRemove;
    private TextView tv_dasaxeleba, tv_magazia, tv_price;

    public MyListItemView(final Context context, final RealProduct product, Boolean isChecked, final LinearLayout uncheckedConteiner, final LinearLayout checkedConteiner) {
        super(context);
        this.context = context;
        this.uncheckedConteiner = uncheckedConteiner;
        this.checkedConteiner = checkedConteiner;
        this.product = product;
//        this.isChecked = isChecked;
        Log.d(TAG, "MyListItemView Constructor");
        initView();

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkBox.isChecked();
                product.setChecked(isChecked);

                if (isChecked) {
                    Log.d(TAG, "oncheck_click : 91");
                    tv_dasaxeleba.setPaintFlags(tv_dasaxeleba.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    uncheckedConteiner.removeView(getView());
                    MyListItemView copy = new MyListItemView(context, product, true, uncheckedConteiner, checkedConteiner);
                    checkedConteiner.addView(copy);
                } else {
                    Log.d(TAG, "oncheck_click: 92");
                    tv_dasaxeleba.setPaintFlags(tv_dasaxeleba.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    checkedConteiner.removeView(getView());
                    MyListItemView copy = new MyListItemView(context, product, false, uncheckedConteiner, checkedConteiner);
                    uncheckedConteiner.addView(copy);
                }
            }
        });

        btn_itemRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "track : 10");
                if (checkBox.isChecked()) {
                    checkedConteiner.removeView(getView());
                } else {
                    uncheckedConteiner.removeView(getView());
                }
                for (int i = 0; i < GlobalConstants.MY_SHOPING_LIST.size(); i++) {
                    if (GlobalConstants.MY_SHOPING_LIST.get(i).getId() == product.getId()) {
                        GlobalConstants.MY_SHOPING_LIST.remove(i);
                    }
                }
            }
        });
    }

    private void initView() {
        Log.d(TAG, " initView - myListItem");
        parentView = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.my_list_row, this, true);

        checkBox = parentView.findViewById(R.id.checkBox);
        btn_itemRemove = parentView.findViewById(R.id.btn_item_remove);
        tv_dasaxeleba = parentView.findViewById(R.id.tv_dasaxeleba);
        tv_magazia = parentView.findViewById(R.id.tv_magazia);
        tv_price = parentView.findViewById(R.id.tv_prod_price);

        tv_dasaxeleba.setText(product.getProduct_name());
        tv_magazia.setText(product.getMarketName());
        tv_price.setText(String.valueOf(product.getPrice()));

        checkBox.setChecked(product.getChecked());
        setState(product.getChecked());
    }

    public View getView() {
        return this;
    }

    public void setState(boolean ischecked) {
        Log.d(TAG, " tavdapirveli check-is daeneba");
//        checkBox.setChecked(isChecked);
        if (ischecked) {
            tv_dasaxeleba.setPaintFlags(tv_dasaxeleba.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv_dasaxeleba.setPaintFlags(tv_dasaxeleba.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }


}
