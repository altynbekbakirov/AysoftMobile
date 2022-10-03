package kz.burhancakmak.aysoftmobile.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class PagedListAdapter extends RecyclerView.Adapter<PagedListAdapter.ItemsHolder> implements Filterable {

    private List<ItemsWithPrices> itemsList = new ArrayList<>();
    private List<ItemsWithPrices> itemsSearch = new ArrayList<>();
    private Activity activity;

    public void setItemsList(Activity activity, List<ItemsWithPrices> itemsList) {
        this.activity = activity;
        this.itemsList = itemsList;
        itemsSearch = new ArrayList<>(itemsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recyclerview_linear_layout, parent, false);
        return new ItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsHolder holder, int position) {
        holder.StokKodu.setText(itemsList.get(position).getStokKodu());
        holder.StokAdi1.setText(itemsList.get(position).getStokAdi1());
        holder.Kalan1.setText(String.valueOf(itemsList.get(position).getKalan1()) + " " + itemsList.get(position).getBirim());
        holder.Kalan2.setText(String.valueOf(itemsList.get(position).getKalan2()) + " " + itemsList.get(position).getBirim());
        holder.Fiyat1.setText(String.valueOf(itemsList.get(position).getFiyat1()) + " (" + itemsList.get(position).getBirim() + ")");
        holder.Fiyat2.setText(String.valueOf(itemsList.get(position).getFiyat2()) + " (" + itemsList.get(position).getBirim() + ")");

       /* Glide.with(activity)
                .load("http://89.163.142.197:34444/NewMobil/Picture/" + itemsList.get(position).getStokResim())
                .placeholder(R.drawable.items_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.StokResim);*/
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class ItemsHolder extends RecyclerView.ViewHolder {
        private final TextView StokKodu;
        private final TextView StokAdi1;
        private final TextView Kalan1;
        private final TextView Kalan2;
        private final TextView Fiyat1;
        private final TextView Fiyat2;
        private final ImageView StokResim;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            StokKodu = itemView.findViewById(R.id.item_StokKodu);
            StokAdi1 = itemView.findViewById(R.id.item_StokAdi1);
            Kalan1 = itemView.findViewById(R.id.item_Kalan1);
            Kalan2 = itemView.findViewById(R.id.item_Kalan2);
            Fiyat1 = itemView.findViewById(R.id.item_Fiyat1);
            Fiyat2 = itemView.findViewById(R.id.item_Fiyat2);
            StokResim = itemView.findViewById(R.id.item_StokResim);
        }
    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemsWithPrices> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
               filteredList.addAll(itemsSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ItemsWithPrices item: itemsSearch) {
                    if (item.getStokAdi1().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

}
