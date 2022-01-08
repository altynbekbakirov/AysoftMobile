package kz.burhancakmak.aysoftmobile.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Clients.ClientKasa;
import kz.burhancakmak.aysoftmobile.R;

public class KasaAdapter extends RecyclerView.Adapter<KasaAdapter.KasaHolder> {
    private List<ClientKasa> kasaList;
    private final OnKasaListener onKasaListener;
    int digit;

    public interface OnKasaListener {
        void onItemClick(int position);
    }

    public KasaAdapter(List<ClientKasa> kasaList, OnKasaListener onKasaListener, int digit) {
        this.kasaList = kasaList;
        this.onKasaListener = onKasaListener;
        this.digit = digit;
    }

    public void setKasaList(List<ClientKasa> kasaList) {
        this.kasaList = kasaList;
        notifyDataSetChanged();
    }


    @NonNull
    @NotNull
    @Override
    public KasaHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_kasa_recyclerview_layout, parent, false);
        return new KasaHolder(view, onKasaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull KasaHolder holder, int position) {
        holder.txtDate.setText(kasaList.get(position).getTarih());
        holder.txtSum.setText(String.format("%,." + digit + "f", kasaList.get(position).getTutar()));
        if (kasaList.get(position).getTutar() < 0) {
            holder.txtSum.setTextColor(Color.RED);
        }
        holder.txtFisNo.setText(kasaList.get(position).getMakbuzNo());
        holder.txtAciklama.setText(kasaList.get(position).getAciklama());
        if (kasaList.get(position).getErpGonderildi() < 1) {
            holder.kasaErpImage.setImageResource(R.drawable.ic_check);
        } else {
            holder.kasaErpImage.setImageResource(R.drawable.ic_check_success);
        }
    }

    @Override
    public int getItemCount() {
        return kasaList.size();
    }

    static class KasaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtDate, txtSum, txtFisNo, txtAciklama;
        OnKasaListener onKasaListener;
        ImageView kasaErpImage;

        public KasaHolder(@NonNull @NotNull View itemView, OnKasaListener onKasaListener) {
            super(itemView);
            this.onKasaListener = onKasaListener;
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSum = itemView.findViewById(R.id.txtSum);
            txtFisNo = itemView.findViewById(R.id.txtFisNo);
            txtAciklama = itemView.findViewById(R.id.txtAciklama);
            kasaErpImage = itemView.findViewById(R.id.kasaErpImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onKasaListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

}
