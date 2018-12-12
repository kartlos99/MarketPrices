package diakonidze.marketprices.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import diakonidze.marketprices.R;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private Context context;
    private static final String TAG = "Search_List_Adapter";
    private List<RealProduct> productList;

    public SearchListAdapter(Context context, List<RealProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.real_product_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final RealProduct product = productList.get(position);
        Log.d(TAG, "curr_Prod_in_list: "+product.toString());

        holder.tv_Pname.setText(product.getProduct_name());
        String paramFull = "";
        for (int i = 0; i < product.getParamIDs().length; i++) {
            StringBuilder builder = new StringBuilder();
            paramFull += Objects.requireNonNull(GlobalConstants.PARAMIERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getName() + " "
                    + product.getParamValues()[i] + " "
                    + Objects.requireNonNull(GlobalConstants.PARAMIERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getMeasureUnit();
            if (i < product.getParamIDs().length - 1){
                paramFull += "\n";
            }
        }
        holder.tv_Pparams.setText(paramFull);
        holder.tv_Mname.setText(product.getMarketName());
        holder.tv_lastdate.setText(product.getPrAddDate());
        holder.tv_Pprice.setText(String.valueOf(product.getPrice()));

        if (product.getImage().isEmpty()) {
            holder.img_product.setImageResource(R.drawable.ic_no_image);
        }else {
            Picasso.get()
                    .load(GlobalConstants.HOST_URL + GlobalConstants.IMAGES_FOLDER + product.getImage())
                    .into(holder.img_product);
        }

        holder.img_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setInMyList(!product.getInMyList());
                changeMyList(product);
                setInMyListIndicatorIcon(holder, product);
            }
        });

        setInMyListIndicatorIcon(holder, product);
    }

    private void changeMyList(RealProduct realProduct){
        if (realProduct.getInMyList()) {
            GlobalConstants.MY_SHOPING_LIST.add(realProduct);
        }else {
            for (int i=0; i < GlobalConstants.MY_SHOPING_LIST.size(); i++){
                if (realProduct.getId() == GlobalConstants.MY_SHOPING_LIST.get(i).getId()){
                    GlobalConstants.MY_SHOPING_LIST.remove(i);
                }
            }
        }
    }

    private void setInMyListIndicatorIcon(ViewHolder viewHolder, RealProduct realProduct) {
        if (realProduct.getInMyList()) {
            viewHolder.img_addBtn.setImageResource(R.drawable.ic_check_black_24dp);
            viewHolder.img_addBtn.setBackgroundResource(R.drawable.green_circle);
        } else {
            viewHolder.img_addBtn.setImageResource(R.drawable.ic_add_black_24dp);
            viewHolder.img_addBtn.setBackgroundResource(R.drawable.white_circle_green_stroke);
        }
        Log.d(TAG, "shopListSize = " + GlobalConstants.MY_SHOPING_LIST.size());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Pname, tv_Mname, tv_Pparams, tv_Pprice, tv_lastdate;
        ImageView img_product, img_addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Pname = itemView.findViewById(R.id.tv_prod_name);
            tv_Mname = itemView.findViewById(R.id.tv_market_name);
            tv_Pparams = itemView.findViewById(R.id.tv_prod_params);
            tv_Pprice = itemView.findViewById(R.id.tv_prod_price);
            tv_lastdate = itemView.findViewById(R.id.tv_rproduct_date);
            img_product = itemView.findViewById(R.id.img_real_prod);
            img_addBtn = itemView.findViewById(R.id.img_add_btn);

        }
    }
}
