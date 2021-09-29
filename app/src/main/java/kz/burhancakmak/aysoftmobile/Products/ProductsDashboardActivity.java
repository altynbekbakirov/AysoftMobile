package kz.burhancakmak.aysoftmobile.Products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class ProductsDashboardActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    LineChart lineChart;
    BarChart barChart;
    PieChart pieChart;
    ItemsWithPrices items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_products_dashboard);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        databaseHandler = DatabaseHandler.getInstance(this);
        Intent intent = getIntent();
        int kayitNo = intent.getIntExtra("kayitNo", -1);

        if (kayitNo != -1) {
            items = databaseHandler.selectProductInfo(kayitNo, "NP-PAZAR");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_nav_dashboard);
        toolbar.setSubtitle(items.getStokAdi1());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        barChart = findViewById(R.id.bar_chart);
        pieChart = findViewById(R.id.pie_chart);

        BarDataSet barDataSet = new BarDataSet(barValues1(), "Net satis");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);

        PieDataSet pieDataSet = new PieDataSet(pieValues1(), "Title");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Net satis");
        pieChart.animate();
    }

    private List<BarEntry> barValues1() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(2017, 560, R.drawable.icon_location));
        entries.add(new BarEntry(2018, 815, R.drawable.ic_arrow_back));
        entries.add(new BarEntry(2019, 720, R.drawable.ic_arrow_circle_down));
        entries.add(new BarEntry(2020, 785, R.drawable.ic_arrow_circle_up));
        entries.add(new BarEntry(2021, 635, R.drawable.ic_arrow_drop_down));
        return entries;
    }

    private List<PieEntry> pieValues1() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(100, "Sasha"));
        entries.add(new PieEntry(150, "Vasya"));
        entries.add(new PieEntry(200, "Kolya"));
        entries.add(new PieEntry(130, "Masha"));
        return entries;
    }

    private List<Entry> getDataList1() {
        List<Entry> list = new ArrayList<>();
        list.add(new Entry(1, 20));
        list.add(new Entry(2, 14));
        list.add(new Entry(3, 55));
        list.add(new Entry(4, 88));
        list.add(new Entry(5, 5));
        return list;
    }

    private List<Entry> getDataList2() {
        List<Entry> list = new ArrayList<>();
        list.add(new Entry(1, 55));
        list.add(new Entry(2, 114));
        list.add(new Entry(3, 15));
        list.add(new Entry(4, 150));
        list.add(new Entry(5, 180));
        return list;
    }

    private void setPhoneDefaultLanguage(String code) {
        String countryCode;
        switch (code) {
            case "Türkçe":
                countryCode = "tr";
                break;
            case "Русский":
                countryCode = "ru";
                break;
            case "English":
                countryCode = "en";
                break;
            default:
                countryCode = "en";
        }
        setLocale(this, countryCode);
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private class MyValueFormatter extends ValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return value + " $";
        }
    }

}