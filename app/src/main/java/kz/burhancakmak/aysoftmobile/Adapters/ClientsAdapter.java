package kz.burhancakmak.aysoftmobile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Clients.ClientsExtractActivity;
import kz.burhancakmak.aysoftmobile.Clients.ClientsMapActivity;
import kz.burhancakmak.aysoftmobile.Clients.ClientsTasksActivity;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.R;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientsHolder> implements Filterable {
    private List<ClCard> cardList;
    private List<ClCard> clientSearch = new ArrayList<>();
    Context context;
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private final ClientClickListener listener;
    int digit;

    public interface ClientClickListener {
        void onItemClick(int position);
    }

    public void setCardList(List<ClCard> cardList) {
        this.cardList = cardList;
        clientSearch = new ArrayList<>(cardList);
        notifyDataSetChanged();
    }

    public ClientsAdapter(Context context, List<ClCard> cardList, ClientClickListener listener, int digit) {
        this.context = context;
        this.cardList = cardList;
        this.listener = listener;
        this.digit = digit;
    }

    @NonNull
    @NotNull
    @Override
    public ClientsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clients_recyclerview_layout, parent, false);
        return new ClientsHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClientsHolder holder, int position) {
        /*viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(cardList.get(position).getKayitNo()));
        viewBinderHelper.closeLayout(String.valueOf(cardList.get(position).getKayitNo()));*/
        ClCard card = cardList.get(position);
        holder.client_name.setText(card.getUnvani1());
        holder.client_code.setText(card.getKod());
        holder.client_tel.setText(card.getTelefon1());
        holder.client_address.setText(card.getAdres1());
        holder.client_balance.setText(String.format("%." + digit + "f", card.getBakiye()));

        if (card.getKordinatLongitude() != 0.0 && card.getKordinatLatitute() != 0.0) {
            holder.locationLayout.setVisibility(View.INVISIBLE);
        } else {
            holder.locationLayout.setVisibility(View.VISIBLE);
        }

        if ((card.getSiparisBeklemede() - card.getSiparisGonderildi()) == 0) {
            holder.client_order_state.setVisibility(View.INVISIBLE);
        } else {
            if ((card.getSiparisBeklemede() - card.getSiparisGonderildi()) > 0) {
                holder.client_order_state.setVisibility(View.VISIBLE);
                holder.client_order_state.setBackground(context.getDrawable(R.drawable.rounded_textview_red));
                holder.client_order_state.setText(String.valueOf(card.getSiparisBeklemede() - card.getSiparisGonderildi()));
            }

            if ((card.getSiparisBeklemede() - card.getSiparisGonderildi()) < 0) {
                holder.client_order_state.setVisibility(View.VISIBLE);
                holder.client_order_state.setBackground(context.getDrawable(R.drawable.rounded_textview_green));
                holder.client_order_state.setText(String.valueOf(-(card.getSiparisBeklemede() - card.getSiparisGonderildi())));
            }
        }

        /*holder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClientsMapActivity.class);
                intent.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                ((Activity) context).startActivityForResult(intent, 105);
            }
        });
        holder.btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClientsExtractActivity.class);
                intent.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                ((Activity) context).startActivityForResult(intent, 205);
            }
        });
        holder.btnStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClientsTasksActivity.class);
                intent.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                intent.putExtra("position", position);
                ((Activity) context).startActivityForResult(intent, 305);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull ClientsHolder holder) {
        super.onViewAttachedToWindow(holder);
        /*if ((holder.getLayoutPosition() % 2) == 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        } */
    }

    static class ClientsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView client_name, client_code, client_tel;
        TextView client_address, client_balance, client_order_state;
        LinearLayout layout;
        RelativeLayout locationLayout;
        SwipeRevealLayout swipeRevealLayout;
        Button btnLocation, btnExtract, btnStatement;
        ClientClickListener listener;

        public ClientsHolder(@NonNull @NotNull View itemView, ClientClickListener listener) {
            super(itemView);
            this.listener = listener;
            client_name = itemView.findViewById(R.id.client_name);
            client_code = itemView.findViewById(R.id.client_code);
            client_tel = itemView.findViewById(R.id.client_tel);
            client_address = itemView.findViewById(R.id.client_address);
            client_balance = itemView.findViewById(R.id.client_balance);
            client_order_state = itemView.findViewById(R.id.client_order_state);
            layout = itemView.findViewById(R.id.layout);
            locationLayout = itemView.findViewById(R.id.locationLayout);
            /*swipeRevealLayout = itemView.findViewById(R.id.swipeLayout);
            btnLocation = itemView.findViewById(R.id.btnLocation);
            btnExtract = itemView.findViewById(R.id.btnExtract);
            btnStatement = itemView.findViewById(R.id.btnStatement);*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ClCard> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(clientSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ClCard item : clientSearch) {
                    if (item.getUnvani1().toLowerCase().contains(filterPattern)
                            || item.getKod().toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cardList.clear();
            cardList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
