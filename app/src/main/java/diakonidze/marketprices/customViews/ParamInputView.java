package diakonidze.marketprices.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import diakonidze.marketprices.R;
import diakonidze.marketprices.models.Paramiter;

public class ParamInputView extends LinearLayout {

    private Context context;
    private LinearLayout parentView;
    private Paramiter paramiter;
    private EditText paramValue;

    public ParamInputView(Context context, Paramiter paramiter) {
        super(context);

        this.paramiter = paramiter;
        this.context = context;
        initView();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = (LinearLayout) inflater.inflate(R.layout.param_enter_row, this, true);

        TextView paramName = parentView.findViewById(R.id.tv_param_name);
        TextView paramUnitName = parentView.findViewById(R.id.tv_param_unit);
        paramValue = parentView.findViewById(R.id.et_param_value);

        paramName.setText(paramiter.getName());
        paramUnitName.setText(paramiter.getMeasureUnit());

    }

    public int getParamID(){
        return paramiter.getId();
    }

    public String getParamVal(){
        if (paramValue.getText().toString().isEmpty()){
            return "0";
        }
        return paramValue.getText().toString();
    }

    public void setParamValhint (String mnishvneloba){
        paramValue.setHint(mnishvneloba);
    }

    public View getView(){
        return this;
    }

}
