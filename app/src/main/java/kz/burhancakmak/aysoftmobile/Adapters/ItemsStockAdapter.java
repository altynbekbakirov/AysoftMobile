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
    private final ItemsStockListener itemsStockListener;
    int digit;

    public interface ItemsStockListener {
        void onItemClick(int position);
    }

    public ItemsStockAdapter(List<ItemsToplamlar> list, int digit, ItemsStockListener itemsStockListener) {
        this.list = list;
        this.digit = digit;
        this.itemsStockListener = itemsStockListener;
    }

    public void setList(List<ItemsToplamlar> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        list.set(position, list.get(position));
        notifyItemChanged(position);
    }

    @NonNull
    @NotNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_stock_recyclerview_layout, parent, false);
        return new StockHolder(view, itemsStockListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StockHolder holder, int position) {
        ItemsToplamlar toplamlar = list.get(position);
        holder.stockAmbarNo.setText(String.valueOf(toplamlar.getDepoNo()));
        holder.stockAmbarAdi.setText(toplamlar.getDepoAdi());
        holder.stockFiiliStok.setText(String.format("%,." + digit + "f", toplamlar.getToplam()));
        holder.stockYeriKodu.setText(toplamlar.getStokYeriKodu());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull StockHolder holder) {
        super.onViewAttachedToWindow(holder);
        /*if ((holder.getLayoutPosition() % 2) == 0) {
            holder.stockLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class StockHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView stockAmbarNo, stockAmbarAdi, stockFiiliStok, stockYeriKodu;
        LinearLayout stockLayout;
        ItemsStockListener itemsStockListener;

        public StockHolder(@NonNull @NotNull View itemView, ItemsStockListener itemsStockListener) {
            super(itemView);
            this.itemsStockListener = itemsStockListener;
            stockAmbarNo = itemView.findViewById(R.id.stockAmbarNo);
            stockAmbarAdi = itemView.findViewById(R.id.stockAmbarAdi);
            stockFiiliStok = itemView.findViewById(R.id.stockFiiliStok);
            stockLayout = itemView.findViewById(R.id.stockLayout);
            stockYeriKodu = itemView.findViewById(R.id.stockYeriKodu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemsStockListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }
}
