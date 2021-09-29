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
import kz.burhancakmak.aysoftmobile.R;

public class DataImportAdapter extends RecyclerView.Adapter<DataImportAdapter.DataHolder> {
    List<DataImportCount> dataList = new ArrayList<>();

    public DataImportAdapter(List<DataImportCount> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @NotNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_import_recyclerview_layout, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataHolder holder, int position) {
        holder.dataName.setText(dataList.get(position).getDataName());
        holder.dataCount.setText(String.valueOf(dataList.get(position).getDataCount()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class DataHolder extends RecyclerView.ViewHolder {
        TextView dataName, dataCount;

        public DataHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dataName = itemView.findViewById(R.id.dataName);
            dataCount = itemView.findViewById(R.id.dataCount);
        }
    }
}
