package kz.burhancakmak.aysoftmobile.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaret;
import kz.burhancakmak.aysoftmobile.R;

public class ZiyaretAdapter extends RecyclerView.Adapter<ZiyaretAdapter.ZiyaretHolder> {
    private List<ClientZiyaret> ziyaretList;
    private final OnZiyaretListener onZiyaretListener;

    public interface OnZiyaretListener {
        void onItemClick(int position);
    }

    public ZiyaretAdapter(List<ClientZiyaret> ziyaretList, OnZiyaretListener onZiyaretListener) {
        this.ziyaretList = ziyaretList;
        this.onZiyaretListener = onZiyaretListener;
    }

    public void setZiyaretList(List<ClientZiyaret> ziyaretList) {
        this.ziyaretList = ziyaretList;
        notifyDataSetChanged();
    }


    @NonNull
    @NotNull
    @Override
    public ZiyaretHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ziyaret_recyclerview_layout, parent, false);
        return new ZiyaretHolder(view, onZiyaretListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ZiyaretHolder holder, int position) {
        holder.ziyaretBegdate.setText(ziyaretList.get(position).getBaslangicTarihi());
        holder.ziyaretBegTime.setText(ziyaretList.get(position).getBaslangicSaati());
        holder.ziyaretEnddate.setText(ziyaretList.get(position).getBitisTarihi());
        holder.ziyaretEndTime.setText(ziyaretList.get(position).getBitisSaati());
        holder.ziyaretDefinition.setText(ziyaretList.get(position).getNotlar());
        holder.ziyaretNo.setText(String.valueOf(ziyaretList.get(position).getKayitNo()));

        if (ziyaretList.get(position).getErpGonderildi() < 1) {
            holder.ziyaretImage.setImageResource(R.drawable.ic_check);
        } else {
            holder.ziyaretImage.setImageResource(R.drawable.ic_check_success);
        }

        if (ziyaretList.get(position).getKapatildi() == 0) {
            holder.ziyaretLayout.setBackgroundColor(Color.parseColor("#F5F5DC"));
        } else {
            holder.ziyaretLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ZiyaretHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public int getItemCount() {
        return ziyaretList.size();
    }

    static class ZiyaretHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ziyaretBegdate, ziyaretBegTime, ziyaretEnddate, ziyaretEndTime, ziyaretDefinition, ziyaretNo;
        OnZiyaretListener ziyaretListener;
        ImageView ziyaretImage;
        RelativeLayout ziyaretLayout;

        public ZiyaretHolder(@NonNull @NotNull View itemView, OnZiyaretListener ziyaretListener) {
            super(itemView);
            this.ziyaretListener = ziyaretListener;
            ziyaretBegdate = itemView.findViewById(R.id.ziyaretBegdate);
            ziyaretEnddate = itemView.findViewById(R.id.ziyaretEnddate);
            ziyaretBegTime = itemView.findViewById(R.id.ziyaretBegTime);
            ziyaretEndTime = itemView.findViewById(R.id.ziyaretEndTime);
            ziyaretDefinition = itemView.findViewById(R.id.ziyaretDefinition);
            ziyaretImage = itemView.findViewById(R.id.ziyaretImage);
            ziyaretLayout = itemView.findViewById(R.id.ziyaretLayout);
            ziyaretNo = itemView.findViewById(R.id.ziyaretNo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ziyaretListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

}
