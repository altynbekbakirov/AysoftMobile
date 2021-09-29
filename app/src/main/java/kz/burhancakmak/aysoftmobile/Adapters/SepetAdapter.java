package kz.burhancakmak.aysoftmobile.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.R;

public class SepetAdapter extends RecyclerView.Adapter<SepetAdapter.CardHolder> {
    List<ClientSepet> orderList;
    private final OnOrderListener orderListener;
    Context context;
    int digitCount, digitTotal;

    public interface OnOrderListener {
        void onOrderClick(int position);
    }

    public SepetAdapter(Context context, List<ClientSepet> orderList, OnOrderListener onOrderListener, int digitCount, int digitTotal) {
        this.context = context;
        this.orderList = orderList;
        this.orderListener = onOrderListener;
        this.digitCount = digitCount;
        this.digitTotal = digitTotal;
    }

    public void setOrderList(List<ClientSepet> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sepet_recyclerview_layout, parent, false);
        return new CardHolder(view, orderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CardHolder holder, int position) {
        String sum = holder.itemView.getContext().getString(R.string.items_tutar_label);
        holder.orderDefinition.setText(orderList.get(position).getStokAdi());
        holder.orderCode.setText(orderList.get(position).getStokKodu());
        holder.orderInfo.setText("(" + orderList.get(position).getStokMiktar() + " "
                + orderList.get(position).getStokBirim() + " X "
                + String.format("%." + digitTotal + "f", orderList.get(position).getStokFiyat()) + ") = "
                + String.format("%." + digitTotal + "f", orderList.get(position).getStokMiktar() * orderList.get(position).getStokFiyat())
        );
        holder.orderTotal.setText(sum + ": " + String.format("%." + digitTotal + "f", orderList.get(position).getStokMiktar() * orderList.get(position).getStokFiyat()));

        if (!orderList.get(position).getStokResim1().isEmpty()) {
            String path = context.getExternalFilesDir("/aysoft") + File.separator + orderList.get(position).getStokResim1();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                holder.orderImage.setImageBitmap(image);
            } else {
                holder.orderImage.setImageResource(R.drawable.items_image);
            }
        } else if (!orderList.get(position).getStokResim2().isEmpty()) {
            String path = context.getExternalFilesDir("/aysoft") + File.separator + orderList.get(position).getStokResim2();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                holder.orderImage.setImageBitmap(image);
            } else {
                holder.orderImage.setImageResource(R.drawable.items_image);
            }
        } else if (!orderList.get(position).getStokResim3().isEmpty()) {
            String path = context.getExternalFilesDir("/aysoft") + File.separator + orderList.get(position).getStokResim3();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                holder.orderImage.setImageBitmap(image);
            } else {
                holder.orderImage.setImageResource(R.drawable.items_image);
            }
        } else if (!orderList.get(position).getStokResim4().isEmpty()) {
            String path = context.getExternalFilesDir("/aysoft") + File.separator + orderList.get(position).getStokResim4();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                holder.orderImage.setImageBitmap(image);
            } else {
                holder.orderImage.setImageResource(R.drawable.items_image);
            }
        } else {
            holder.orderImage.setImageResource(R.drawable.items_image);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView orderDefinition, orderCode, orderInfo, orderTotal;
        ImageView orderImage;
        OnOrderListener onOrderListener;

        public CardHolder(@NonNull @NotNull View itemView, OnOrderListener onOrderListener) {
            super(itemView);
            this.onOrderListener = onOrderListener;
            orderDefinition = itemView.findViewById(R.id.orderDefinition);
            orderCode = itemView.findViewById(R.id.orderCode);
            orderInfo = itemView.findViewById(R.id.orderInfo);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderImage = itemView.findViewById(R.id.orderImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOrderListener.onOrderClick(getAbsoluteAdapterPosition());
        }
    }
}
