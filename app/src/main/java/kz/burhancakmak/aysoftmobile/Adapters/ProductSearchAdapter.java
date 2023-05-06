package kz.burhancakmak.aysoftmobile.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Dataimport.DataImportCount;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsSearchWarehouses;
import kz.burhancakmak.aysoftmobile.R;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.DataHolder> {
    List<ItemsSearchWarehouses> dataList;

    public ProductSearchAdapter(List<ItemsSearchWarehouses> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @NotNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_search_recyclerview_layout, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataHolder holder, int position) {
        holder.itemsAmbarNo.setText(dataList.get(position).getDepono());
        holder.itemsAmbarAdi.setText(String.valueOf(dataList.get(position).getDepoadi()));
        holder.itemsAmbarMiktar.setText(String.valueOf(dataList.get(position).getMiktar()));
        holder.itemsAmbarBirim.setText(String.valueOf(dataList.get(position).getSatilacakbirim()));
        holder.itemsAmbarBirimCarpan.setText(String.valueOf(dataList.get(position).getSatilacakbirimcarpan()));
        holder.itemsAmbarSatilacakMiktar.setText(String.valueOf(dataList.get(position).getSatilacakmiktar()));
        holder.itemsAmbarLocation.setText(String.valueOf(dataList.get(position).getStoklokasyon()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder {
        TextView itemsAmbarNo, itemsAmbarAdi, itemsAmbarMiktar, itemsAmbarBirim, itemsAmbarBirimCarpan, itemsAmbarSatilacakMiktar, itemsAmbarLocation;

        public DataHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemsAmbarNo = itemView.findViewById(R.id.itemsAmbarNo);
            itemsAmbarAdi = itemView.findViewById(R.id.itemsAmbarAdi);
            itemsAmbarMiktar = itemView.findViewById(R.id.itemsAmbarMiktar);
            itemsAmbarBirim = itemView.findViewById(R.id.itemsAmbarBirim);
            itemsAmbarBirimCarpan = itemView.findViewById(R.id.itemsAmbarBirimCarpan);
            itemsAmbarSatilacakMiktar = itemView.findViewById(R.id.itemsAmbarSatilacakMiktar);
            itemsAmbarLocation = itemView.findViewById(R.id.itemsAmbarLocation);
        }
    }
}
