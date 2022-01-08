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

import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportKasaTask;
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportSiparisTask;
import kz.burhancakmak.aysoftmobile.R;

public class DataExportKasaAdapter extends RecyclerView.Adapter<DataExportKasaAdapter.DataHolder>{
    List<DataExportKasaTask> dataList;
    boolean isSelectedAll = false;
    DataExportKasaListener listener;

    public interface DataExportKasaListener {
        void onItemClick(int position);
    }

    public DataExportKasaAdapter(List<DataExportKasaTask> dataList, DataExportKasaListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    public void setDataList(List<DataExportKasaTask> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_export_kasa_recyclerview_layout, parent, false);
        return new DataHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.dateValue.setText(dataList.get(position).getTarih());
        holder.sumValue.setText(String.valueOf(dataList.get(position).getTutar()));
        holder.clientName.setText(dataList.get(position).getKod() + ", " + dataList.get(position).getUnvani());
        holder.aciklamaValue.setText(dataList.get(position).getAciklama());
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
        } else {
            holder.imageValue.setImageResource(R.drawable.ic_check_success);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateValue, sumValue, clientName, aciklamaValue;
        ImageView imageValue;
        CheckBox cbSelect;
        RelativeLayout dataExportLayout;
        DataExportKasaListener listener;

        public DataHolder(@NonNull @NotNull View itemView, DataExportKasaListener listener) {
            super(itemView);
            this.listener = listener;
            dateValue = itemView.findViewById(R.id.dateValue);
            sumValue = itemView.findViewById(R.id.sumValue);
            clientName = itemView.findViewById(R.id.clientName);
            aciklamaValue = itemView.findViewById(R.id.aciklamaValue);
            imageValue = itemView.findViewById(R.id.imageValue);
            cbSelect = itemView.findViewById(R.id.cbSelect);
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
