package diakonidze.marketprices.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import diakonidze.marketprices.R;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.util.Constants;

public class AutoCompliteProductAdapter extends ArrayAdapter<Product> {

    private List<Product> productsFullList, filteredProductList;

    public AutoCompliteProductAdapter(@NonNull Context context, @NonNull List<Product> productList) {
        super(context, 0, productList);
        productsFullList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return productsFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_ac_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.tv_prod_name);
        ImageView imageView = convertView.findViewById(R.id.img_prod_in_list);

        Product product = getItem(position);
        if (product != null){
            textViewName.setText(product.getName());

            if (product.getImage().isEmpty()){
                imageView.setImageResource(R.drawable.ic_no_image);
            } else {
                Log.d("IMAGE", product.getImage());
                Picasso.get()
                        .load(Constants.HOST_URL + Constants.IMAGES_FOLDER + product.getImage())
                        .into(imageView);
            }
        }

        return convertView;
    }

    private Filter productsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence typedtext) {

            Log.d("text", typedtext.toString());
            FilterResults results = new FilterResults();
            List<Product> suggestion = new ArrayList<>();

            if ( typedtext.length() == 0){
                suggestion.addAll(productsFullList);
            } else {
                String filterPatern = typedtext.toString().trim();
                for (Product item : productsFullList){
                    if (item.getName().contains(filterPatern)){
                        suggestion.add(item);
                    }
                }
            }

            filteredProductList = new ArrayList<>(suggestion);
//            results.values = suggestion;
//            results.count = suggestion.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();

            if (filteredProductList != null) {
                addAll(filteredProductList);
            }

            notifyDataSetChanged();
            Log.d("publishRes", "changed");
        }
    };


}
