package kz.burhancakmak.aysoftmobile.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportSiparisTask;
import kz.burhancakmak.aysoftmobile.R;

public class DataExportSiparisAdapter extends RecyclerView.Adapter<DataExportSiparisAdapter.DataHolder> {
    List<DataExportSiparisTask> dataList;
    boolean isSelectedAll = false;
    DataExportSiparisListener listener;
    int digit;

    public interface DataExportSiparisListener {
        void onItemClick(int position);
    }

    public DataExportSiparisAdapter(List<DataExportSiparisTask> dataList, DataExportSiparisListener listener, int digit) {
        this.dataList = dataList;
        this.listener = listener;
        this.digit = digit;
    }

    public void setDataList(List<DataExportSiparisTask> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_export_siparis_recyclerview_layout, parent, false);
        return new DataHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.dateValue.setText(dataList.get(position).getTarih());
        holder.sumValue.setText(String.format("%,." + digit + "f", dataList.get(position).getTutar()));
        holder.indirimvalue.setText(String.format("%,." + digit + "f", dataList.get(position).getGenelIndirimTutari()));
        holder.netTutarValue.setText(String.format("%,." + digit + "f", dataList.get(position).getNetTutar()));
        holder.clientName.setText(dataList.get(position).getKod() + ", " + dataList.get(position).getUnvani());
        holder.cbSelect.setChecked(dataList.get(position).getCbChecked());

        holder.cbSelect.setEnabled(dataList.get(position).getErpGonderildi() <= 0);

        if (!isSelectedAll) {
            holder.cbSelect.setChecked(false);
            dataList.get(position).setCbChecked(false);
        } else {
            holder.cbSelect.setChecked(true);
            dataList.get(position).setCbChecked(true);
        }

        if (!holder.cbSelect.isChecked()) {
            holder.dataExportLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.dataExportLayout.setBackgroundColor(Color.parseColor("#ecf0f1"));
        }

        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.cbSelect.isChecked()) {
                    holder.dataExportLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    dataList.get(position).setCbChecked(false);
                } else {
                    holder.dataExportLayout.setBackgroundColor(Color.parseColor("#ecf0f1"));
                    dataList.get(position).setCbChecked(true);
                }
            }
        });

        if (dataList.get(position).getErpGonderildi() < 1) {
            holder.imageValue.setImageResource(R.drawable.ic_check);
            holder.durumValue.setText(R.string.client_order_status_pending);
        } else {
            holder.imageValue.setImageResource(R.drawable.ic_check_success);
            holder.durumValue.setText(R.string.client_order_status_sent);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateValue, sumValue, clientName, durumValue, indirimvalue, netTutarValue;
        ImageView imageValue;
        CheckBox cbSelect;
        RelativeLayout dataExportLayout;
        DataExportSiparisListener listener;

        public DataHolder(@NonNull @NotNull View itemView, DataExportSiparisListener listener) {
            super(itemView);
            this.listener = listener;
            dateValue = itemView.findViewById(R.id.dateValue);
            sumValue = itemView.findViewById(R.id.sumValue);
            clientName = itemView.findViewById(R.id.clientName);
            durumValue = itemView.findViewById(R.id.durumValue);
            imageValue = itemView.findViewById(R.id.imageValue);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            indirimvalue = itemView.findViewById(R.id.indirimvalue);
            netTutarValue = itemView.findViewById(R.id.netTutarValue);
            dataExportLayout = itemView.findViewById(R.id.dataExportLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    public void selectAll() {
        isSelectedAll = true;
        notifyDataSetChanged();
    }

    public void deSelectAll() {
        isSelectedAll = false;
        notifyDataSetChanged();
    }
}
