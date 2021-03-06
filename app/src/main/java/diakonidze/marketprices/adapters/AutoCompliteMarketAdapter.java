package diakonidze.marketprices.adapters;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import diakonidze.marketprices.R;
import diakonidze.marketprices.models.Market;
import diakonidze.marketprices.util.GlobalConstants;

public class AutoCompliteMarketAdapter extends ArrayAdapter<Market> {

    private List<Market> fullMarketList, filteredMarketList;
    ImageView imageView;

    public AutoCompliteMarketAdapter(@NonNull Context context, @NonNull List<Market> objects) {
        super(context, 0, objects);
        fullMarketList = new ArrayList<>(objects);
    }

//    public AutoCompliteMarketAdapter(@NonNull @android.support.annotation.NonNull Context context, @NonNull @android.support.annotation.NonNull List<Market> marketList) {
//        super(context, 0, marketList);
//        fullMarketList = new ArrayList<>(marketList);
//    }

//    @NonNull
//    @android.support.annotation.NonNull
    @Override
    public Filter getFilter() {
        return marketFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.market_ac_row, parent, false);
        }

        TextView textViewMname = convertView.findViewById(R.id.tv_market_name);
        TextView textViewMaddress = convertView.findViewById(R.id.tv_market_address);
        imageView = convertView.findViewById(R.id.img_prod_in_list);

        Market market = getItem(position);
        if (market != null){
            textViewMname.setText(market.getMarketName());
            textViewMaddress.setText(market.getAddress());

            if (market.getLogo().isEmpty()){
                imageView.setImageResource(R.drawable.ic_no_image);
            } else {
                Log.d("IMAGE", market.getLogo());
                Picasso.get()
                        .load(GlobalConstants.HOST_URL + GlobalConstants.MARKET_LOGOS_FOLDER + market.getLogo())
                        .into(imageView);
            }
        }

        return convertView;
    }

    private Filter marketFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence typedtext) {

            if (typedtext != null) {
                Log.d("text", typedtext.toString());
                List<Market> suggestion = new ArrayList<>();

                if (typedtext.length() == 0) {
                    suggestion.addAll(fullMarketList);
                } else {
                    String filterPatern = typedtext.toString().trim();
                    String sArray[] = filterPatern.split(" ");

                    for (Market item : fullMarketList) {
                        String nameAndAddress = item.getMarketName() + " " + item.getAddress();
                        boolean exist = true;
                        for (String fp : sArray) {
                            if (!nameAndAddress.contains(fp)) {
                                exist = false;
                            }
                        }
                        if (exist) {
                            suggestion.add(item);
                        }
                    }
                }
                filteredMarketList = new ArrayList<>(suggestion);
            }
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();

            if (filteredMarketList != null) {
                addAll(filteredMarketList);
            }

            notifyDataSetChanged();
            Log.d("publishRes market:", "changed");
        }
    };

}
