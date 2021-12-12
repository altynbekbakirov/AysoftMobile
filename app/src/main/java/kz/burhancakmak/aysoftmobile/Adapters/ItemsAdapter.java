package kz.burhancakmak.aysoftmobile.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<ItemsWithPrices> itemsList;
    private List<ItemsWithPrices> itemsSearch = new ArrayList<>();
    private final Activity activity;
    public static final int ITEM_LINEAR = 0;
    public static final int ITEM_GRID = 1;
    private int VIEW_TYPE = 0;
    private String kalan1;
    private String kalan2;
    private String fiyat1;
    private String fiyat2;
    int digitCount, digitPrice, ikiFiyatKullanimi, ikiDepoKullanimi;
    ItemsListener listener;

    public interface ItemsListener {
        void onItemListener(int position);
    }

    public ItemsAdapter(List<ItemsWithPrices> itemsList, Activity activity, ItemsListener listener, int digitCount, int digitPrice, int ikiFiyatKullanimi, int ikiDepoKullanimi) {
        this.itemsList = itemsList;
        this.activity = activity;
        this.listener = listener;
        this.digitCount = digitCount;
        this.digitPrice = digitPrice;
        this.ikiFiyatKullanimi = ikiFiyatKullanimi;
        this.ikiDepoKullanimi = ikiDepoKullanimi;
    }

    public void setItemsList(List<ItemsWithPrices> itemsList, int VIEW_TYPE, String kalan1, String kalan2, String fiyat1, String fiyat2) {
        this.itemsList = itemsList;
        this.VIEW_TYPE = VIEW_TYPE;
        this.kalan1 = kalan1;
        this.kalan2 = kalan2;
        this.fiyat1 = fiyat1;
        this.fiyat2 = fiyat2;
        itemsSearch = new ArrayList<>(itemsList);
        notifyDataSetChanged();
    }

    public void setVIEW_TYPE(int VIEW_TYPE) {
        this.VIEW_TYPE = VIEW_TYPE;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (VIEW_TYPE) {
            case ITEM_LINEAR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recyclerview_linear_layout, parent, false);
                return new LinearHolder(view, listener);
            case ITEM_GRID:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recyclerview_grid_layout, parent, false);
                return new GridHolder(view, listener);
        }
        return new LinearHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final int itemType = VIEW_TYPE;
        if (itemType == ITEM_LINEAR) {
            if (holder instanceof LinearHolder) {
                LinearHolder linearHolder = (LinearHolder) holder;
                linearHolder.StokKodu.setText(itemsList.get(position).getStokKodu());
                linearHolder.StokAdi1.setText(itemsList.get(position).getStokAdi1());
                linearHolder.Kalan1.setText(String.format("%." + digitCount + "f", itemsList.get(position).getKalan1()) + " " + itemsList.get(position).getBirim().toLowerCase());
                linearHolder.Kalan2.setText(String.format("%." + digitCount + "f", itemsList.get(position).getKalan2()) + " " + itemsList.get(position).getBirim().toLowerCase());
                linearHolder.Kalan1Label.setText(kalan1);
                linearHolder.Kalan2Label.setText(kalan2);
                linearHolder.StokResim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zoomImageDialog(position);
                    }
                });

                if (!fiyat1.isEmpty()) {
                    linearHolder.Fiyat1Label.setText(fiyat1);
                    linearHolder.Fiyat1.setText(String.format("%." + digitPrice + "f", itemsList.get(position).getFiyat1()) + " " + itemsList.get(position).getDoviz1() + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");
                } else {
                    linearHolder.Fiyat1Label.setVisibility(View.GONE);
                    linearHolder.Fiyat1.setVisibility(View.GONE);
                }

                if (!fiyat2.isEmpty()) {
                    linearHolder.Fiyat2Label.setText(fiyat2);
                    linearHolder.Fiyat2.setText(String.format("%." + digitPrice + "f", itemsList.get(position).getFiyat2()) + " " + itemsList.get(position).getDoviz2() + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");
                } else {
                    linearHolder.Fiyat2Label.setVisibility(View.GONE);
                    linearHolder.Fiyat2.setVisibility(View.GONE);
                }

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
                    String path1 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim1();
                    File file = new File(path1);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path1);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim2().isEmpty()) {
                    String path2 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim2();
                    File file = new File(path2);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path2);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim3().isEmpty()) {
                    String path3 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim3();
                    File file = new File(path3);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path3);
                        linearHolder.StokResim.setImageBitmap(image);
                    } else {
                        linearHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else {
                    linearHolder.StokResim.setImageResource(R.drawable.items_image);
                }
            }
        } else if (itemType == ITEM_GRID) {
            if (holder instanceof GridHolder) {
                GridHolder gridHolder = (GridHolder) holder;
                gridHolder.StokKodu.setText(itemsList.get(position).getStokKodu());
                gridHolder.StokAdi1.setText(itemsList.get(position).getStokAdi1());
                gridHolder.Kalan1.setText(String.format("%." + digitCount + "f", itemsList.get(position).getKalan1()) + " " + itemsList.get(position).getBirim().toLowerCase());
                gridHolder.Kalan2.setText(String.format("%." + digitCount + "f", itemsList.get(position).getKalan2()) + " " + itemsList.get(position).getBirim().toLowerCase());
                gridHolder.Kalan1Label.setText(kalan1);
                gridHolder.Kalan2Label.setText(kalan2);
                gridHolder.StokResim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zoomImageDialog(position);
                    }
                });

                if (!fiyat1.isEmpty()) {
                    gridHolder.Fiyat1Label.setText(fiyat1);
                    gridHolder.Fiyat1.setText(String.format("%." + digitPrice + "f", itemsList.get(position).getFiyat1()) + " " + itemsList.get(position).getDoviz1() + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");
                }

                if (!fiyat2.isEmpty()) {
                    gridHolder.Fiyat2Label.setText(fiyat2);
                    gridHolder.Fiyat2.setText(String.format("%." + digitPrice + "f", itemsList.get(position).getFiyat2()) + " " + itemsList.get(position).getDoviz2() + " (" + itemsList.get(position).getBirim().toLowerCase() + ")");
                }

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
                    String path1 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim1();
                    File file = new File(path1);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path1);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim2().isEmpty()) {
                    String path2 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim2();
                    File file = new File(path2);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path2);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else if (!itemsList.get(position).getStokResim3().isEmpty()) {
                    String path3 = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim3();
                    File file = new File(path3);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path3);
                        gridHolder.StokResim.setImageBitmap(image);
                    } else {
                        gridHolder.StokResim.setImageResource(R.drawable.items_image);
                    }
                } else {
                    gridHolder.StokResim.setImageResource(R.drawable.items_image);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class LinearHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView StokKodu;
        private final TextView StokAdi1;
        private final TextView Kalan1;
        private final TextView Kalan2;
        private final TextView Fiyat1;
        private final TextView Fiyat2;
        private final ImageView StokResim;
        private final TextView Kalan1Label;
        private final TextView Kalan2Label;
        private final TextView Fiyat1Label;
        private final TextView Fiyat2Label;
        ItemsListener listener;

        public LinearHolder(@NonNull View itemView, ItemsListener listener) {
            super(itemView);
            this.listener = listener;
            StokKodu = itemView.findViewById(R.id.item_StokKodu);
            StokAdi1 = itemView.findViewById(R.id.item_StokAdi1);
            Kalan1 = itemView.findViewById(R.id.item_Kalan1);
            Kalan2 = itemView.findViewById(R.id.item_Kalan2);
            Fiyat1 = itemView.findViewById(R.id.item_Fiyat1);
            Fiyat2 = itemView.findViewById(R.id.item_Fiyat2);
            StokResim = itemView.findViewById(R.id.item_StokResim);
            Kalan1Label = itemView.findViewById(R.id.item_Kalan1_label);
            Kalan2Label = itemView.findViewById(R.id.item_Kalan2_label);
            Fiyat1Label = itemView.findViewById(R.id.item_Fiyat1_label);
            Fiyat2Label = itemView.findViewById(R.id.item_Fiyat2_label);
            LinearLayout layout = itemView.findViewById(R.id.item_click_layout);
            layout.setOnClickListener(this);

            if (fiyat1.isEmpty()) {
                Fiyat1Label.setVisibility(View.GONE);
                Fiyat1.setVisibility(View.GONE);
            }

            if (ikiFiyatKullanimi == 0) {
                Fiyat2Label.setVisibility(View.GONE);
                Fiyat2.setVisibility(View.GONE);
            } else {
                if (fiyat2.isEmpty()) {
                    Fiyat2Label.setVisibility(View.GONE);
                    Fiyat2.setVisibility(View.GONE);
                }
            }

            if (ikiDepoKullanimi == 0) {
                Kalan2Label.setVisibility(View.GONE);
                Kalan2.setVisibility(View.GONE);
            } else {
                if (kalan2.isEmpty()) {
                    Kalan2Label.setVisibility(View.GONE);
                    Kalan2.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            listener.onItemListener(getAbsoluteAdapterPosition());
        }
    }

    public class GridHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView StokKodu;
        private final TextView StokAdi1;
        private final TextView Kalan1;
        private final TextView Kalan2;
        private final TextView Fiyat1;
        private final TextView Fiyat2;
        private final ImageView StokResim;
        private final TextView Kalan1Label;
        private final TextView Kalan2Label;
        private final TextView Fiyat1Label;
        private final TextView Fiyat2Label;
        ItemsListener listener;

        public GridHolder(@NonNull View itemView, ItemsListener listener) {
            super(itemView);
            this.listener = listener;
            StokKodu = itemView.findViewById(R.id.item_StokKodu);
            StokAdi1 = itemView.findViewById(R.id.item_StokAdi1);
            Kalan1 = itemView.findViewById(R.id.item_Kalan1);
            Kalan2 = itemView.findViewById(R.id.item_Kalan2);
            Fiyat1 = itemView.findViewById(R.id.item_Fiyat1);
            Fiyat2 = itemView.findViewById(R.id.item_Fiyat2);
            StokResim = itemView.findViewById(R.id.item_StokResim);
            Kalan1Label = itemView.findViewById(R.id.item_Kalan1_label);
            Kalan2Label = itemView.findViewById(R.id.item_Kalan2_label);
            Fiyat1Label = itemView.findViewById(R.id.item_Fiyat1_label);
            Fiyat2Label = itemView.findViewById(R.id.item_Fiyat2_label);
            LinearLayout linearLayout = itemView.findViewById(R.id.linearLayout);
            linearLayout.setOnClickListener(this);

            if (fiyat1.isEmpty()) {
                Fiyat1.setVisibility(View.GONE);
                Fiyat1Label.setVisibility(View.GONE);
            }

            if (ikiFiyatKullanimi == 0) {
                Fiyat2Label.setVisibility(View.GONE);
                Fiyat2.setVisibility(View.GONE);
            } else {
                if (fiyat2.isEmpty()) {
                    Fiyat2.setVisibility(View.GONE);
                    Fiyat2Label.setVisibility(View.GONE);
                }
            }

            if (ikiDepoKullanimi == 0) {
                Kalan2Label.setVisibility(View.GONE);
                Kalan2.setVisibility(View.GONE);
            } else {
                if (kalan2.isEmpty()) {
                    Kalan2Label.setVisibility(View.GONE);
                    Kalan2.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onClick(View v) {
            listener.onItemListener(getAbsoluteAdapterPosition());
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

    private void zoomImageDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.product_image_slider_layout, null);
        builder.setView(view);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        List<SlideModel> modelList = new ArrayList<>();
        if (!itemsList.get(position).getStokResim().isEmpty()) {
            String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim();
            File file = new File(path);
            if (file.exists()) {
                try {
                    modelList.add(new SlideModel(String.valueOf(file.toURL()), ScaleTypes.FIT));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!itemsList.get(position).getStokResim1().isEmpty()) {
            String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim1();
            File file = new File(path);
            if (file.exists()) {
                try {
                    modelList.add(new SlideModel(String.valueOf(file.toURL()), ScaleTypes.FIT));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!itemsList.get(position).getStokResim2().isEmpty()) {
            String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim2();
            File file = new File(path);
            if (file.exists()) {
                try {
                    modelList.add(new SlideModel(String.valueOf(file.toURL()), ScaleTypes.FIT));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!itemsList.get(position).getStokResim3().isEmpty()) {
            String path = activity.getExternalFilesDir("/aysoft") + File.separator + itemsList.get(position).getStokResim3();
            File file = new File(path);
            if (file.exists()) {
                try {
                    modelList.add(new SlideModel(String.valueOf(file.toURL()), ScaleTypes.FIT));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        imageSlider.setImageList(modelList, ScaleTypes.FIT);
        builder.show();
    }

}
