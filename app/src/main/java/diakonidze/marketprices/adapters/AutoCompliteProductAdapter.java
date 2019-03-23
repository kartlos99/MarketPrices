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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import diakonidze.marketprices.R;
import diakonidze.marketprices.interfaces.ProductFilterListener;
import diakonidze.marketprices.models.Product;
import diakonidze.marketprices.util.GlobalConstants;

public class AutoCompliteProductAdapter extends ArrayAdapter<Product> {

    private List<Product> productsFullList, filteredProductList;
    private ProductFilterListener filterListener;

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

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_ac_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.tv_prod_name);
        ImageView imageView = convertView.findViewById(R.id.img_prod_in_list);

        Product product = getItem(position);
        if (product != null) {
            textViewName.setText(product.toString());

            if (product.getImage().isEmpty()) {
                imageView.setImageResource(R.drawable.ic_no_image);
            } else {
                Log.d("IMAGE", product.getImage());
                Picasso.get()
                        .load(GlobalConstants.HOST_URL + GlobalConstants.IMAGES_FOLDER + product.getImage())
                        .into(imageView);
            }
        }

        return convertView;
    }

    private Filter productsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence typedtext) {

            if (typedtext != null) {
                String inputText = typedtext.toString().trim();
                Pattern patToReplace = Pattern.compile("\\s+");
                Matcher matcher = patToReplace.matcher(inputText);

                String myPattern = ".*" + matcher.replaceAll(".*") + ".*";
                Log.d("PaTTerN:", myPattern);

                Pattern filterPattern = Pattern.compile(myPattern);
                Matcher filterMacher;

                Log.d("text", typedtext.toString());
                List<Product> suggestion = new ArrayList<>();

                if (inputText.length() == 0) {
                    suggestion.addAll(productsFullList);
                } else {
                    for (Product item : productsFullList) {
                        filterMacher = filterPattern.matcher(item.toString());
                        if (filterMacher.matches()) {
                            suggestion.add(item);
                        }
                    }
                }
                filteredProductList = new ArrayList<>(suggestion);
            }
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();

            if (filteredProductList != null) {
                addAll(filteredProductList);
                filterListener.filteringFinished(filteredProductList.size());
            }

            notifyDataSetChanged();
            Log.d("publishRes", "changed");
        }
    };

    public void setFilterListener(ProductFilterListener filterFinishedListener) {
        filterListener = filterFinishedListener;
    }
}
