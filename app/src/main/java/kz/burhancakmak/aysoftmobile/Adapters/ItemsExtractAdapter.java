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

import kz.burhancakmak.aysoftmobile.Models.Products.ItemsExtract;
import kz.burhancakmak.aysoftmobile.R;

public class ItemsExtractAdapter extends RecyclerView.Adapter<ItemsExtractAdapter.ItemsHolder> {
    List<ItemsExtract> list;
    int digit1;
    int digit2;

    public ItemsExtractAdapter(List<ItemsExtract> list, int digit1, int digit2) {
        this.list = list;
        this.digit1 = digit1;
        this.digit2 = digit2;
    }

    public void setList(List<ItemsExtract> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ItemsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_extract_recyclerview_layout, parent, false);
        return new ItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemsHolder holder, int position) {
        ItemsExtract product = list.get(position);
        holder.productDate.setText(product.getTarih());
        holder.productOperation.setText(product.getIslemTipi());
        holder.productGiren.setText(String.format("%." + digit1 + "f", product.getGirenMiktar()));
        holder.productCikan.setText(String.format("%." + digit1 + "f", product.getCikanMiktar()));
        holder.productToplam.setText(String.format("%." + digit2 + "f", product.getToplam()));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull ItemsHolder holder) {
        super.onViewAttachedToWindow(holder);
        if ((holder.getLayoutPosition() % 2) != 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemsHolder extends RecyclerView.ViewHolder {
        TextView productDate, productOperation, productGiren, productCikan, productToplam;
        LinearLayout layout;

        public ItemsHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            productDate = itemView.findViewById(R.id.productDate);
            productOperation = itemView.findViewById(R.id.productOperation);
            productGiren = itemView.findViewById(R.id.productGiren);
            productCikan = itemView.findViewById(R.id.productCikan);
            productToplam = itemView.findViewById(R.id.productToplam);
            layout = itemView.findViewById(R.id.productLayout);
        }
    }
}
