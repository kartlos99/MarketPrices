package diakonidze.marketprices.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import diakonidze.marketprices.R;
import diakonidze.marketprices.models.Product;

public class AutoCompliteProductAdapter extends ArrayAdapter<Product> {

    private List<Product> productsFullList;

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

        Product product = getItem(position);
        if (product != null){
            textViewName.setText(product.getName());
        }

        return convertView;
    }

    private Filter productsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence typedtext) {

            FilterResults results = new FilterResults();
            List<Product> suggestion = new ArrayList<>();

            if (typedtext == null || typedtext.length() == 0){
                suggestion.addAll(productsFullList);
            } else {
                String filterPatern = typedtext.toString().trim();
                for (Product item : productsFullList){
                    if (item.getName().contains(filterPatern)){
                        suggestion.add(item);
                    }
                }
            }

            results.values = suggestion;
            results.count = suggestion.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


}
