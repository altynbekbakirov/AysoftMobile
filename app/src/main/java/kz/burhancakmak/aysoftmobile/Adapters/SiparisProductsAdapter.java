package kz.burhancakmak.aysoftmobile.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class SiparisProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<ItemsWithPrices> itemsList;
    private List<ItemsWithPrices> itemsSearch = new ArrayList<>();
    private final OnOrderClickListener onOrderClickListener;
    private final Activity activity;
    private int VIEW_TYPE = 0;
    public static final int ITEM_LINEAR = 0;
    public static final int ITEM_GRID = 1;
    private String kalan1;
    private String kalan2;
    private String fiyat1;
    private String fiyat2;
    int digitCount, digitPrice, ikiDepoKullanimi;

    public interface OnOrderClickListener {
        void onItemClick(int position);
    }

    public SiparisProductsAdapter(Activity activity, List<ItemsWithPrices> itemsList, OnOrderClickListener onOrderClickListener, int digitCount, int digitPrice, int ikiDepoKullanimi) {
        this.activity = activity;
        this.itemsList = itemsList;
        this.onOrderClickListener = onOrderClickListener;
        this.digitCount = digitCount;
        this.digitPrice = digitPrice;
        this.ikiDepoKullanimi = ikiDepoKullanimi;
    }

    public void setItemsList(List<ItemsWithPrices> itemsList, int VIEW_TYPE, String kalan1, String kalan2, String fiyat1, String fiyat2) {
        this.itemsList = itemsList;
        itemsSearch = new ArrayList<>(itemsList);
        this.VIEW_TYPE = VIEW_TYPE;
        this.kalan1 = kalan1;
        this.kalan2 = kalan2;
        this.fiyat1 = fiyat1;
        this.fiyat2 = fiyat2;
        notifyDataSetChanged();
    }

    public void setVIEW_TYPE(int VIEW_TYPE) {
        this.VIEW_TYPE = VIEW_TYPE;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = null;
        if (VIEW_TYPE == ITEM_LINEAR) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_products_recyclerview_linear_layout, parent, false);
            return new LinearHolder(view, onOrderClickListener);
        } else if (VIEW_TYPE == ITEM_GRID) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_products_recyclerview_grid_layout, parent, false);
            return new GridHolder(view, onOrderClickListener);
        }
        return new LinearHolder(view, onOrderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final int itemType = VIEW_TYPE;
        if (itemType == ITEM_LINEAR) {
            if (holder instanceof LinearHolder) {
                LinearHolder linearHolder = (LinearHolder) holder;
                linearHolder.StokAdi1.setText(itemsList.get(position).getStokAdi1());
                linearHolder.StokKodu.setText(itemsList.get(position).getStokKodu());
                linearHolder.Kalan1Label.setText(kalan1);
                linearHolder.Kalan2Label.setText(kalan2);
                linearHolder.fiyat1Label.setText(fiyat1);
                linearHolder.Kalan1.setText(String.format("%,." + digitCount + "f", itemsList.get(position).getKalan1()) + " " + itemsList.get(position).getBirim().toLowerCase());
                linearHolder.Kalan2.setText(String.format("%,." + digitCount + "f", itemsList.get(position).getKalan2()) + " " + itemsList.get(position).getBirim().toLowerCase());
                linearHolder.Fiyat1.setText(String.format("%,." + digitPrice + "f", itemsList.get(position).getFiyat1()) + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");

                if (!itemsList.get(position).getStokResim().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim1().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim1();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim2().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim2();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim3().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim3();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else {
                    linearHolder.StokResim.setImageResource(R.drawable.items_image);
                }

                if (itemsList.get(position).getMiktar() == null || itemsList.get(position).getMiktar() == 0) {
                    linearHolder.txtCounter.setVisibility(View.INVISIBLE);
                } else {
                    linearHolder.txtCounter.setVisibility(View.VISIBLE);
                    linearHolder.txtCounter.setText(String.valueOf(itemsList.get(position).getMiktar()));
                }
            }
        } else if (itemType == ITEM_GRID) {
            if (holder instanceof GridHolder) {
                GridHolder gridHolder = (GridHolder) holder;
                gridHolder.StokAdi1.setText(itemsList.get(position).getStokAdi1());
                gridHolder.StokKodu.setText(itemsList.get(position).getStokKodu());
                gridHolder.Kalan1Label.setText(kalan1);
                gridHolder.Kalan2Label.setText(kalan2);
                gridHolder.fiyat1Label.setText(fiyat1);
                gridHolder.Kalan1.setText(String.format("%,." + digitCount + "f", itemsList.get(position).getKalan1()) + " " + itemsList.get(position).getBirim().toLowerCase());
                gridHolder.Kalan2.setText(String.format("%,." + digitCount + "f", itemsList.get(position).getKalan2()) + " " + itemsList.get(position).getBirim().toLowerCase());
                gridHolder.Fiyat1.setText(String.format("%,." + digitPrice + "f", itemsList.get(position).getFiyat1()) + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");

                if (!itemsList.get(position).getStokResim().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim1().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim1();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim2().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim2();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim3().isEmpty()) {
                    String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim3();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else {
                    gridHolder.StokResim.setImageResource(R.drawable.items_image);
                }

                if (itemsList.get(position).getMiktar() == null || itemsList.get(position).getMiktar() == 0) {
                    gridHolder.txtCounter.setVisibility(View.INVISIBLE);
                } else {
                    gridHolder.txtCounter.setVisibility(View.VISIBLE);
                    gridHolder.txtCounter.setText(String.valueOf(itemsList.get(position).getMiktar()));
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class LinearHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView StokAdi1, StokKodu, Kalan1, Kalan2, Fiyat1, txtCounter, Kalan1Label, Kalan2Label, fiyat1Label;
        ImageView StokResim;
        OnOrderClickListener onOrderClickListener;

        public LinearHolder(@NonNull @NotNull View itemView, OnOrderClickListener onOrderClickListener) {
            super(itemView);
            this.onOrderClickListener = onOrderClickListener;
            StokResim = itemView.findViewById(R.id.StokResim);
            StokAdi1 = itemView.findViewById(R.id.StokAdi1);
            StokKodu = itemView.findViewById(R.id.StokKodu);
            Kalan1 = itemView.findViewById(R.id.Kalan1);
            Kalan1Label = itemView.findViewById(R.id.kalan1_label);
            Kalan2Label = itemView.findViewById(R.id.kalan2_label);
            Kalan2 = itemView.findViewById(R.id.Kalan2);
            Fiyat1 = itemView.findViewById(R.id.Fiyat1);
            fiyat1Label = itemView.findViewById(R.id.fiyat1_label);
            txtCounter = itemView.findViewById(R.id.txtCounter);
            itemView.setOnClickListener(this);

            if (ikiDepoKullanimi == 0) {
                Kalan2Label.setVisibility(View.GONE);
                Kalan2.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            onOrderClickListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    class GridHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView StokAdi1, StokKodu, Kalan1, Kalan2, Fiyat1, txtCounter, Kalan2Label, Kalan1Label, fiyat1Label;
        ImageView StokResim;
        OnOrderClickListener onOrderClickListener;

        public GridHolder(@NonNull @NotNull View itemView, OnOrderClickListener onOrderClickListener) {
            super(itemView);
            this.onOrderClickListener = onOrderClickListener;
            StokResim = itemView.findViewById(R.id.StokResim);
            StokAdi1 = itemView.findViewById(R.id.StokAdi1);
            StokKodu = itemView.findViewById(R.id.StokKodu);
            Kalan1 = itemView.findViewById(R.id.Kalan1);
            Kalan1Label = itemView.findViewById(R.id.kalan1_label);
            Kalan2Label = itemView.findViewById(R.id.kalan2_label);
            Kalan2 = itemView.findViewById(R.id.Kalan2);
            Fiyat1 = itemView.findViewById(R.id.Fiyat1);
            fiyat1Label = itemView.findViewById(R.id.fiyat1_label);
            txtCounter = itemView.findViewById(R.id.txtCounter);
            itemView.setOnClickListener(this);

            if (ikiDepoKullanimi == 0) {
                Kalan2Label.setVisibility(View.GONE);
                Kalan2.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            onOrderClickListener.onItemClick(getAbsoluteAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemsWithPrices> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemsSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemsWithPrices item : itemsSearch) {
                    if (item.getStokAdi1().toLowerCase().contains(filterPattern)
                            || item.getStokKodu().toLowerCase().contains(filterPattern)
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
            itemsList.clear();
            itemsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
