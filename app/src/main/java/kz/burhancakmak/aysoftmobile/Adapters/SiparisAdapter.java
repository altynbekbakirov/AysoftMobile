package kz.burhancakmak.aysoftmobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSiparis;
import kz.burhancakmak.aysoftmobile.R;
public class SiparisAdapter extends RecyclerView.Adapter<SiparisAdapter.OrderHolder> {
    private List<ClientSiparis> orderList;
    OrderClickListener orderClickListener;
    Context context;
    int digit;

    public interface OrderClickListener {
        void orderClick(int position);
    }

    public SiparisAdapter(Context context, List<ClientSiparis> orderList, OrderClickListener orderClickListener, int digit) {
        this.context = context;
        this.orderClickListener = orderClickListener;
        this.orderList = orderList;
        this.digit = digit;
    }

    public void setOrderList(List<ClientSiparis> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_siparis_recyclerview_layout, parent, false);
        return new OrderHolder(view, orderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderHolder holder, int position) {
        holder.siparisNo.setText(String.valueOf(orderList.get(position).getKayitNo()));
        holder.siparisDate.setText(orderList.get(position).getTarih());
        holder.siparisTutar.setText(String.format("%." + digit + "f", orderList.get(position).getTutar()));
        if (orderList.get(position).getErpGonderildi() < 1) {
            holder.siparisDurum.setText(context.getString(R.string.client_order_status_pending));
            holder.siparisImage.setImageResource(R.drawable.ic_check);
        } else {
            holder.siparisDurum.setText(context.getString(R.string.client_order_status_sent));
            holder.siparisImage.setImageResource(R.drawable.ic_check_success);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView siparisNo, siparisDurum, siparisDate, siparisTutar;
        ImageView siparisImage;
        OrderClickListener orderClickListener;

        public OrderHolder(@NonNull @NotNull View itemView, OrderClickListener orderClickListener) {
            super(itemView);
            this.orderClickListener = orderClickListener;
            siparisNo = itemView.findViewById(R.id.siparisNo);
            siparisDurum = itemView.findViewById(R.id.siparisDurum);
            siparisDate = itemView.findViewById(R.id.siparisDate);
            siparisTutar = itemView.findViewById(R.id.siparisTutar);
            siparisImage = itemView.findViewById(R.id.siparisImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            orderClickListener.orderClick(getAbsoluteAdapterPosition());
        }
    }

}
