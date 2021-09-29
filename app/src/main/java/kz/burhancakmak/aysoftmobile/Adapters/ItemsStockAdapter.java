package kz.burhancakmak.aysoftmobile.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.R;

public class ItemsStockAdapter extends RecyclerView.Adapter<ItemsStockAdapter.StockHolder> {
    List<ItemsToplamlar> list;
    int digit;

    public ItemsStockAdapter(List<ItemsToplamlar> list, int digit) {
        this.list = list;
        this.digit = digit;
    }

    public void setList(List<ItemsToplamlar> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_stock_recyclerview_layout, parent, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StockHolder holder, int position) {
        ItemsToplamlar toplamlar = list.get(position);
        holder.stockAmbarNo.setText(String.valueOf(toplamlar.getDepoNo()));
        holder.stockAmbarAdi.setText(toplamlar.getDepoAdi());
        holder.stockFiiliStok.setText(String.format("%." + digit + "f", toplamlar.getToplam()));
        holder.stockGercekStok.setText(String.format("%." + digit + "f", toplamlar.getToplam()));
        holder.stockYeriKodu.setText(toplamlar.getStokYeriKodu());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull StockHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((holder.getLayoutPosition() % 2) == 0) {
            holder.stockLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StockHolder extends RecyclerView.ViewHolder {
        TextView stockAmbarNo, stockAmbarAdi, stockFiiliStok, stockRezerver, stockGercekStok, stockBekleyenUretim, stockYeriKodu;
        LinearLayout stockLayout;

        public StockHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            stockAmbarNo = itemView.findViewById(R.id.stockAmbarNo);
            stockAmbarAdi = itemView.findViewById(R.id.stockAmbarAdi);
            stockFiiliStok = itemView.findViewById(R.id.stockFiiliStok);
            stockRezerver = itemView.findViewById(R.id.stockRezerver);
            stockGercekStok = itemView.findViewById(R.id.stockGercekStok);
            stockBekleyenUretim = itemView.findViewById(R.id.stockBekleyenUretim);
            stockLayout = itemView.findViewById(R.id.stockLayout);
            stockYeriKodu = itemView.findViewById(R.id.stockYeriKodu);
        }
    }
}
