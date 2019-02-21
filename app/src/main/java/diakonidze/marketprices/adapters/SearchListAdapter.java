package diakonidze.marketprices.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import diakonidze.marketprices.R;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.models.RealProduct;
import diakonidze.marketprices.util.GlobalConstants;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private Context context;
    private static final String TAG = "Search_List_Adapter";
    private List<RealProduct> productList;
    private ColorStateList colorList1;
    private ColorStateList colorList2;

    public SearchListAdapter(Context context, List<RealProduct> productList) {
        this.context = context;
        this.productList = productList;
        colorList1 = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        Color.RED,
                        Color.GREEN
                }
        );
        colorList2 = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        Color.RED,
                        Color.GRAY
                }
        );
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
        Log.d(TAG, "curr_Prod_in_list: " + product.toString());

        holder.tv_Pname.setText(product.getProduct_name());
        String paramFull = "";
        for (int i = 0; i < product.getParamIDs().length; i++) {
            StringBuilder builder = new StringBuilder();
            paramFull += Objects.requireNonNull(GlobalConstants.PARAMITERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getName() + " "
                    + product.getParamValues()[i] + " "
                    + Objects.requireNonNull(GlobalConstants.PARAMITERS_HASH.get(String.valueOf(product.getParamIDs()[i]))).getMeasureUnit();
            if (i < product.getParamIDs().length - 1) {
                paramFull += "\n";
            }
        }
        holder.tv_Pparams.setText(paramFull);
        holder.tv_Mname.setText(product.getMarketName());
        holder.tv_lastdate.setText(product.getPrAddDate());
        holder.tv_Pprice.setText(String.valueOf(product.getPrice()));

        if (product.getImage().isEmpty()) {
            holder.img_product.setImageResource(R.drawable.ic_no_image);
        } else {
            Picasso.get()
                    .load(GlobalConstants.HOST_URL + GlobalConstants.IMAGES_FOLDER + product.getImage())
                    .resize(200, 200)
                    .centerCrop()
                    .error(R.drawable.ic_no_image)
                    .into(holder.img_product);
        }

        holder.img_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setInMyList(!product.getInMyList());
                changeMyList(product);
                setInMyListIndicator(holder.img_addBtn, product.getInMyList());
            }
        });

        final String paramsText = paramFull;

        holder.img_openBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View bsView = LayoutInflater.from(context).inflate(R.layout.rp_modal_bs, null);
                BottomSheetDialog bsDialog = new BottomSheetDialog(context);
                bsDialog.setContentView(bsView);
                bsDialog.show();

                final TextView tv_prName = bsView.findViewById(R.id.bs_name);
                TextView tv_param = bsView.findViewById(R.id.tv_bs_param);
                TextView tv_price = bsView.findViewById(R.id.tv_bs_price);
                TextView tv_date = bsView.findViewById(R.id.tv_bs_date);
                TextView tv_market = bsView.findViewById(R.id.tv_bs_market);
                TextView tv_brand = bsView.findViewById(R.id.tv_bs_brand);
                TextView tv_user = bsView.findViewById(R.id.tv_bs_user);
                TextView tv_packing = bsView.findViewById(R.id.tv_bs_packing);
                final ImageView img_product = bsView.findViewById(R.id.img_bs_pr);
                ImageView img_market = bsView.findViewById(R.id.img_bs_market);
                final ImageView img_basket = bsView.findViewById(R.id.img_bs_basket);

                Market market = GlobalConstants.MARKETS_HASH.get(String.valueOf(product.getMarketID()));

                tv_prName.setText(product.getProduct_name());
                tv_param.setText(paramsText);
                tv_price.setText(String.valueOf(product.getPrice()));
                tv_date.setText(product.getPrAddDate());
                tv_market.setText(market.toString());
                tv_brand.setText(product.getBrandName());
                tv_packing.setText(product.getPacking());
                img_product.setImageDrawable(holder.img_product.getDrawable());
                final CircularRevealCardView cardView = bsView.findViewById(R.id.bs_carcview);

                Log.d(TAG,"market: " + market.fulltoString());
                if (!market.getLogo().isEmpty()){
                    Picasso.get()
                            .load(GlobalConstants.HOST_URL + GlobalConstants.MARKET_LOGOS_FOLDER + market.getLogo())
                            .error(R.drawable.ic_no_image)
                            .into(img_market);
                }

                setInMyListIndicator(img_basket, product.getInMyList());
                img_basket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        product.setInMyList(!product.getInMyList());
                        changeMyList(product);
                        setInMyListIndicator(holder.img_addBtn, product.getInMyList());
                        setInMyListIndicator(img_basket, product.getInMyList());

                        if (product.getInMyList()){
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_jump_right);
                            cardView.startAnimation(animation);
                        }
                    }
                });

            }
        });

        setInMyListIndicator(holder.img_addBtn, product.getInMyList());
    }

    private void changeMyList(RealProduct realProduct) {
        if (realProduct.getInMyList()) {
            GlobalConstants.MY_SHOPING_LIST.add(realProduct);
        } else {
            for (int i = 0; i < GlobalConstants.MY_SHOPING_LIST.size(); i++) {
                if (realProduct.getId() == GlobalConstants.MY_SHOPING_LIST.get(i).getId()) {
                    GlobalConstants.MY_SHOPING_LIST.remove(i);
                }
            }
        }
    }

    private void setInMyListIndicator(ImageView imageView, Boolean isInBasket){
        if (isInBasket){
            imageView.setImageResource(R.drawable.ic_remove_shopping_24dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(colorList2);
            }
        }else {
            imageView.setImageResource(R.drawable.ic_add_shopping_24dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(colorList1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Pname, tv_Mname, tv_Pparams, tv_Pprice, tv_lastdate;
        ImageView img_product, img_addBtn, img_openBS;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Pname = itemView.findViewById(R.id.tv_prod_name);
            tv_Mname = itemView.findViewById(R.id.tv_market_name);
            tv_Pparams = itemView.findViewById(R.id.tv_prod_params);
            tv_Pprice = itemView.findViewById(R.id.tv_prod_price);
            tv_lastdate = itemView.findViewById(R.id.tv_rproduct_date);
            img_product = itemView.findViewById(R.id.img_real_prod);
            img_addBtn = itemView.findViewById(R.id.img_add_btn);
            img_openBS = itemView.findViewById(R.id.img_open_bs);
        }
    }
}
