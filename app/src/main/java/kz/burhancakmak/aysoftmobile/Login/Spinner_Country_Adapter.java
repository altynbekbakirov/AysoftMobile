package kz.burhancakmak.aysoftmobile.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import kz.burhancakmak.aysoftmobile.R;


public class Spinner_Country_Adapter extends ArrayAdapter<Spinner_Country> {
    public Spinner_Country_Adapter(Context context, ArrayList<Spinner_Country> countryList) {
        super(context, 0, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return SpinnerInitView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return SpinnerInitView(position, convertView, parent);
    }

    private View SpinnerInitView (int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_countries, parent, false);
        }

        ImageView spinnerImage = convertView.findViewById(R.id.spinner_image);
        TextView spinnerText = convertView.findViewById(R.id.spinner_text);

        Spinner_Country currentItem = getItem(position);

        if (currentItem != null) {
            spinnerImage.setImageResource(currentItem.getCountryImage());
            spinnerText.setText(currentItem.getCountryName());
        }

        return convertView;
    }
}
