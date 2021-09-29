package kz.burhancakmak.aysoftmobile.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientExtract;
import kz.burhancakmak.aysoftmobile.R;

public class ClientsExtractAdapter extends RecyclerView.Adapter<ClientsExtractAdapter.ClientHolder> {
    List<ClientExtract> list;
    int digit;

    public ClientsExtractAdapter(List<ClientExtract> list, int digit) {
        this.list = list;
        this.digit = digit;
    }

    public void setList(List<ClientExtract> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ClientHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clients_extract_recyclerview_layout, parent, false);
        return new ClientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClientHolder holder, int position) {
        ClientExtract client = list.get(position);
        holder.clientDate.setText(client.getTarih());
        holder.clientOperation.setText(client.getIslemTipi());
        holder.clientDebit.setText(String.format("%." + digit + "f", client.getDAlacak()));
        holder.clientCredit.setText(String.format("%." + digit + "f", client.getBorc()));
        holder.clientBalance.setText(String.format("%." + digit + "f", client.getYBakiye()));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull ClientHolder holder) {
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

    class ClientHolder extends RecyclerView.ViewHolder {
        TextView clientDate, clientOperation, clientDebit, clientCredit, clientBalance;
        LinearLayout layout;

        public ClientHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            clientDate = itemView.findViewById(R.id.clientDate);
            clientOperation = itemView.findViewById(R.id.clientOperation);
            clientDebit = itemView.findViewById(R.id.clientDebit);
            clientCredit = itemView.findViewById(R.id.clientCredit);
            clientBalance = itemView.findViewById(R.id.clientBalance);
            layout = itemView.findViewById(R.id.clientLayout);
        }
    }
}
