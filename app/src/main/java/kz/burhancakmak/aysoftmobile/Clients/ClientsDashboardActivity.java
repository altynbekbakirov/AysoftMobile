package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.AylaraGoreCariSatisTahsilat;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsDashboardQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.GrupBazindaSatis;
import kz.burhancakmak.aysoftmobile.Models.Clients.MarkaBazindaSatisMiktarTutarGore;
import kz.burhancakmak.aysoftmobile.Models.Clients.SatilanMalListesi;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class ClientsDashboardActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    int clientKayitNo;
    String kurusHaneSayisiStok;
    ClCard card;
    List<SatilanMalListesi> satilanMalMiktar = new ArrayList<>();
    List<SatilanMalListesi> satilanMalTutar = new ArrayList<>();
    List<MarkaBazindaSatisMiktarTutarGore> markaBazindaSatisMiktar = new ArrayList<>();
    List<MarkaBazindaSatisMiktarTutarGore> markaBazindaSatisTutar = new ArrayList<>();
    List<GrupBazindaSatis> grupBazindaSatisMiktar = new ArrayList<>();
    List<GrupBazindaSatis> grupBazindaSatisTutar = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_clients_dashboard);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        clientKayitNo = intent.getIntExtra("clientKayitNo", -1);

        databaseHandler = DatabaseHandler.getInstance(this);

        if (clientKayitNo != -1) {
            card = databaseHandler.selectClientById(clientKayitNo);
            kurusHaneSayisiStok = parametreGetir(FIRMA_NO, "KurusHaneSayisiCari", "0");
        }

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(card.getUnvani1());
        toolbar.setSubtitle(R.string.client_popup_analyze);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);*/

        new GetDataFromWeb().execute();
    }

    private void aylaraGoreCariSatisTahsilat(AylaraGoreCariSatisTahsilat list) {
        BarChart barChart = findViewById(R.id.barChart);
        LinearLayout layoutBarChart = findViewById(R.id.layoutBarChart);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);
            List<BarEntry> entriesGroup1 = new ArrayList<>();
            List<BarEntry> entriesGroup2 = new ArrayList<>();

            entriesGroup1.add(new BarEntry(1, list.getOcakSatis()));
            entriesGroup1.add(new BarEntry(2, list.getSubatSatis()));
            entriesGroup1.add(new BarEntry(3, list.getMartSatis()));
            entriesGroup1.add(new BarEntry(4, list.getNisanSatis()));
            entriesGroup1.add(new BarEntry(5, list.getMayisSatis()));
            entriesGroup1.add(new BarEntry(6, list.getHaziranSatis()));
            entriesGroup1.add(new BarEntry(7, list.getTemmuzSatis()));
            entriesGroup1.add(new BarEntry(8, list.getAgustosSatis()));
            entriesGroup1.add(new BarEntry(9, list.getEylulSatis()));
            entriesGroup1.add(new BarEntry(10, list.getEkimSatis()));
            entriesGroup1.add(new BarEntry(11, list.getKasimSatis()));
            entriesGroup1.add(new BarEntry(12, list.getAralikSatis()));


            entriesGroup2.add(new BarEntry(1, list.getOcakTahsilat()));
            entriesGroup2.add(new BarEntry(2, list.getSubatTahsilat()));
            entriesGroup2.add(new BarEntry(3, list.getMartTahsilat()));
            entriesGroup2.add(new BarEntry(4, list.getNisanTahsilat()));
            entriesGroup2.add(new BarEntry(5, list.getMayisTahsilat()));
            entriesGroup2.add(new BarEntry(6, list.getHaziranTahsilat()));
            entriesGroup2.add(new BarEntry(7, list.getTemmuzTahsilat()));
            entriesGroup2.add(new BarEntry(8, list.getAgustosTahsilat()));
            entriesGroup2.add(new BarEntry(9, list.getEylulTahsilat()));
            entriesGroup2.add(new BarEntry(10, list.getEkimTahsilat()));
            entriesGroup2.add(new BarEntry(11, list.getKasimTahsilat()));
            entriesGroup2.add(new BarEntry(12, list.getAralikTahsilat()));


            String[] days = new String[]{"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

            BarDataSet set1 = new BarDataSet(entriesGroup1, "Satis");
            set1.setColor(Color.parseColor("#f39c12"));
            set1.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set1.setBarBorderWidth(2f);
            set1.setBarBorderColor(Color.parseColor("#e67e22"));

            BarDataSet set2 = new BarDataSet(entriesGroup2, "Tahsilat");
            set2.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set2.setColor(Color.parseColor("#27ae60"));
            set2.setBarBorderWidth(2f);
            set2.setBarBorderColor(Color.parseColor("#16a085"));

            float groupSpace = 0.08f;
            float barSpace = 0.08f;
            float barWidth = 0.37f;

            BarData data = new BarData(set1, set2);
            barChart.setData(data);
            data.setBarWidth(barWidth);

            XAxis axis = barChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(days));
            axis.setCenterAxisLabels(true);
            axis.setPosition(XAxis.XAxisPosition.BOTTOM);
            axis.setTextColor(Color.BLACK);
            axis.setTextSize(12f);
            axis.setLabelRotationAngle(270);
            axis.setLabelCount(5);

            barChart.getXAxis().setAxisMinimum(0);
            barChart.getXAxis().setAxisMaximum(0 + barChart.getData().getGroupWidth(groupSpace, barSpace) * 5);
            barChart.getAxisLeft().setAxisMinimum(0);

            barChart.getLegend().setTextSize(14f);
            barChart.getLegend().setTextColor(Color.BLACK);
            barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
            barChart.getLegend().setFormToTextSpace(5f);
            barChart.getLegend().setFormSize(10f);
            barChart.getLegend().setXEntrySpace(20);

            barChart.getDescription().setEnabled(false);
            barChart.groupBars(0, groupSpace, barSpace);
            barChart.setDragEnabled(true);
            barChart.invalidate();
        }

    }

    private void satilanMalMiktar(List<SatilanMalListesi> list) {
        BarChart horizontalBarChart = findViewById(R.id.barChartMiktar);
        LinearLayout layoutBarChartMiktar = findViewById(R.id.layoutBarChartMiktar);

        if (list.size() == 0) {
            layoutBarChartMiktar.setVisibility(View.GONE);
        } else {
            layoutBarChartMiktar.setVisibility(View.VISIBLE);
            List<BarEntry> entriesGroup1 = new ArrayList<>();
            List<String> items = new ArrayList<>();

            if (list.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    entriesGroup1.add(new BarEntry(i, list.get(i).getMiktar()));
                    items.add(list.get(i).getStokAdi());
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    entriesGroup1.add(new BarEntry(i, list.get(i).getMiktar()));
                    items.add(list.get(i).getStokAdi());
                }
            }

            BarDataSet set1 = new BarDataSet(entriesGroup1, "Miktar");
            set1.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(set1);
            horizontalBarChart.setData(data);

            XAxis axis = horizontalBarChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(items));
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
            axis.setTextColor(Color.BLACK);
            axis.setTextSize(10f);
            axis.setLabelCount(items.size());
            axis.setLabelRotationAngle(-90);
            axis.setGranularity(0);
            axis.setGranularityEnabled(true);

            horizontalBarChart.getLegend().setTextSize(14f);
            horizontalBarChart.getLegend().setTextColor(Color.BLACK);
            horizontalBarChart.getLegend().setForm(Legend.LegendForm.SQUARE);
            horizontalBarChart.getLegend().setFormToTextSpace(5f);
            horizontalBarChart.getLegend().setFormSize(10f);
            horizontalBarChart.getLegend().setXEntrySpace(20);

            horizontalBarChart.getDescription().setEnabled(false);
            horizontalBarChart.setDragEnabled(true);
            horizontalBarChart.invalidate();
        }
    }

    private void satilanMalTutar(List<SatilanMalListesi> list) {
        HorizontalBarChart horizontalBarChart = findViewById(R.id.barChartTutar);
        LinearLayout layoutBarChartTutar = findViewById(R.id.layoutBarChartTutar);

        if (list.size() == 0) {
            layoutBarChartTutar.setVisibility(View.GONE);
        } else {
            layoutBarChartTutar.setVisibility(View.VISIBLE);

            List<BarEntry> entriesGroup1 = new ArrayList<>();
            List<String> items = new ArrayList<>();

            if (list.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    entriesGroup1.add(new BarEntry(i, list.get(i).getTutar()));
                    items.add(list.get(i).getStokAdi());
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    entriesGroup1.add(new BarEntry(i, list.get(i).getTutar()));
                    items.add(list.get(i).getStokAdi());
                }
            }

            BarDataSet set1 = new BarDataSet(entriesGroup1, "Tutar");
            set1.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set1.setColors(ColorTemplate.COLORFUL_COLORS);

            BarData data = new BarData(set1);
            horizontalBarChart.setData(data);

            XAxis axis = horizontalBarChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(items));
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
            axis.setTextColor(Color.BLACK);
            axis.setTextSize(10f);
            axis.setLabelCount(items.size());
            axis.setGranularity(0);
            axis.setGranularityEnabled(true);

            horizontalBarChart.getLegend().setTextSize(14f);
            horizontalBarChart.getLegend().setTextColor(Color.BLACK);
            horizontalBarChart.getLegend().setForm(Legend.LegendForm.LINE);
            horizontalBarChart.getLegend().setFormToTextSpace(5f);
            horizontalBarChart.getLegend().setFormSize(10f);
            horizontalBarChart.getLegend().setXEntrySpace(20);

            horizontalBarChart.getDescription().setEnabled(false);
            horizontalBarChart.setDragEnabled(true);
            horizontalBarChart.invalidate();
        }

    }

    private void markaBazindaSatisMiktar(List<MarkaBazindaSatisMiktarTutarGore> list) {
        LineChart lineChart = findViewById(R.id.barChartMarkaSatisMiktar);
        LinearLayout layoutBarChartMarkaSatisMiktar = findViewById(R.id.layoutBarChartMarkaSatisMiktar);

        if (list.size() == 0) {
            layoutBarChartMarkaSatisMiktar.setVisibility(View.GONE);
        } else {
            layoutBarChartMarkaSatisMiktar.setVisibility(View.VISIBLE);

            List<Entry> entries1 = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                entries1.add(new Entry(i, list.get(i).getMiktar()));
                labels.add(list.get(i).getMarka());
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            LineDataSet dataSet1 = new LineDataSet(entries1, "Miktar");
            dataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet1.setValueTextSize(10);
            dataSet1.setValueTextColor(Color.BLACK);
            dataSet1.setLineWidth(8);
            dataSet1.setCircleRadius(6);
            dataSet1.setCircleColor(getResources().getColor(R.color.colorAccent));
            dataSet1.setCircleHoleRadius(4);
            dataSet1.setCircleHoleColor(Color.RED);

            XAxis axis = lineChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(labels));
            axis.setLabelCount(labels.size(), true);
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.TOP);
            axis.setTextColor(getResources().getColor(R.color.black));
            axis.setTextSize(12f);
            axis.setGranularity(0);
            axis.setGranularityEnabled(true);

            dataSets.add(dataSet1);

            LineData lineData = new LineData(dataSets);

            lineChart.setBackgroundColor(getResources().getColor(R.color.alphawhite));
            lineChart.setNoDataText("Gosterilecek veri yok");
            lineChart.setDrawGridBackground(false);
            lineChart.setBorderColor(Color.RED);
            lineChart.setBorderWidth(1f);
            lineChart.getDescription().setEnabled(false);

            lineChart.setData(lineData);
            lineChart.invalidate();
        }

    }

    private void markaBazindaSatisTutar(List<MarkaBazindaSatisMiktarTutarGore> list) {
        LineChart lineChart = findViewById(R.id.barChartMarkaSatisTutar);
        LinearLayout laoutBarChartMarkaSatisTutar = findViewById(R.id.laoutBarChartMarkaSatisTutar);

        if (list.size() == 0) {
            laoutBarChartMarkaSatisTutar.setVisibility(View.GONE);
        } else {
            laoutBarChartMarkaSatisTutar.setVisibility(View.VISIBLE);

            List<String> labels = new ArrayList<>();

            List<Entry> entries1 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                entries1.add(new Entry(i, list.get(i).getTutar()));
                labels.add(list.get(i).getMarka());
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            LineDataSet dataSet1 = new LineDataSet(entries1, "Tutar");
            dataSet1.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet1.setValueTextSize(10);
            dataSet1.setValueTextColor(Color.BLACK);
            dataSet1.setLineWidth(8);
            dataSet1.setCircleRadius(6);
            dataSet1.setCircleColor(getResources().getColor(R.color.red));
            dataSet1.setCircleHoleRadius(3);
            dataSet1.setCircleHoleColor(getResources().getColor(R.color.yellow_apple));
            dataSet1.enableDashedLine(5, 5, 5);

            XAxis axis = lineChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(labels));
            axis.setLabelCount(labels.size(), true);
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
            axis.setTextColor(getResources().getColor(R.color.blue_web));
            axis.setTextSize(12f);
            axis.setGranularity(0);
            axis.setGranularityEnabled(true);

            dataSets.add(dataSet1);

            LineData lineData = new LineData(dataSets);

            Legend legend = lineChart.getLegend();
            legend.setEnabled(true);
            legend.setTextColor(Color.BLACK);
            legend.setTextSize(12f);

            lineChart.setBackgroundColor(getResources().getColor(R.color.alphawhite));
            lineChart.setNoDataText("Gosterilecek veri yok");
            lineChart.setDrawGridBackground(false);
            lineChart.setBorderColor(Color.RED);
            lineChart.setBorderWidth(1f);
            lineChart.getDescription().setEnabled(false);

            lineChart.setData(lineData);
            lineChart.invalidate();
        }
    }

    private void grupBazindaSatisMiktar(List<GrupBazindaSatis> list) {
        PieChart pieChartGrupSatisMiktar = findViewById(R.id.pieChartGrupSatisMiktar);
        LinearLayout layoutPieChartGrupSatisMiktar = findViewById(R.id.layoutPieChartGrupSatisMiktar);

        if (list.size() == 0) {
            layoutPieChartGrupSatisMiktar.setVisibility(View.GONE);
        } else {
            layoutPieChartGrupSatisMiktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Grup bazinda miktar");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartGrupSatisMiktar.setTransparentCircleColor(Color.YELLOW);
            pieChartGrupSatisMiktar.getDescription().setEnabled(false);
            pieChartGrupSatisMiktar.setCenterText("Miktar");
            pieChartGrupSatisMiktar.setCenterTextSize(16f);
            pieChartGrupSatisMiktar.setCenterTextColor(Color.BLACK);
            pieChartGrupSatisMiktar.setData(pieData);
            pieChartGrupSatisMiktar.invalidate();

        }
    }

    private void grupBazindaSatisTutar(List<GrupBazindaSatis> list) {
        PieChart pieChartGrupSatisTutar = findViewById(R.id.pieChartGrupSatisTutar);
        LinearLayout layoutPieChartGrupSatisTutar = findViewById(R.id.layoutPieChartGrupSatisTutar);

        if (list.size() == 0) {
            layoutPieChartGrupSatisTutar.setVisibility(View.GONE);
        } else {
            layoutPieChartGrupSatisTutar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Grup bazinda tutar");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartGrupSatisTutar.getDescription().setEnabled(false);
            pieChartGrupSatisTutar.setCenterText("Tutar");
            pieChartGrupSatisTutar.setCenterTextSize(16f);
            pieChartGrupSatisTutar.setCenterTextColor(Color.BLACK);
            pieChartGrupSatisTutar.setData(pieData);
            pieChartGrupSatisTutar.invalidate();

        }
    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.dashboard_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsDashboardQuery> queryList;
        AylaraGoreCariSatisTahsilat satisTahsilat = new AylaraGoreCariSatisTahsilat();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webSettingsMap.get("web")).create(RetrofitApi.class);
            queryList = retrofitApi.clientDashboard(
                    webSettingsMap.get("uuid"),
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    FIRMA_NO,
                    DONEM_NO,
                    99,
                    card.getKayitNo(),
                    card.getKod()
            );
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ClientsDashboardQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ClientsDashboardQuery query = response.body();
                    if (!query.getHata()) {
                        if (query.getAylaraGoreCariSatisTahsilat().size() > 0) {
                            publishProgress(getString(R.string.data_import_progressbar_clients));
                            for (int i = 2; i < query.getAylaraGoreCariSatisTahsilat().size(); i++) {
                                String[] clients = query.getAylaraGoreCariSatisTahsilat().get(i).split("\\|");
                                satisTahsilat.setCariKayitNo(Integer.parseInt(clients[0]));
                                satisTahsilat.setCariKod(clients[1]);
                                satisTahsilat.setCariUnvan(clients[2]);
                                satisTahsilat.setOcakSatis(Integer.parseInt(clients[3]));
                                satisTahsilat.setOcakTahsilat(Integer.parseInt(clients[4]));
                                satisTahsilat.setSubatSatis(Integer.parseInt(clients[5]));
                                satisTahsilat.setSubatTahsilat(Integer.parseInt(clients[6]));
                                satisTahsilat.setMartSatis(Integer.parseInt(clients[7]));
                                satisTahsilat.setMartTahsilat(Integer.parseInt(clients[8]));
                                satisTahsilat.setNisanSatis(Integer.parseInt(clients[9]));
                                satisTahsilat.setNisanTahsilat(Integer.parseInt(clients[10]));
                                satisTahsilat.setMayisSatis(Integer.parseInt(clients[11]));
                                satisTahsilat.setMayisTahsilat(Integer.parseInt(clients[12]));
                                satisTahsilat.setHaziranSatis(Integer.parseInt(clients[13]));
                                satisTahsilat.setHaziranTahsilat(Integer.parseInt(clients[14]));
                                satisTahsilat.setTemmuzSatis(Integer.parseInt(clients[15]));
                                satisTahsilat.setTemmuzTahsilat(Integer.parseInt(clients[16]));
                                satisTahsilat.setAgustosSatis(Integer.parseInt(clients[17]));
                                satisTahsilat.setAgustosTahsilat(Integer.parseInt(clients[18]));
                                satisTahsilat.setEylulSatis(Integer.parseInt(clients[19]));
                                satisTahsilat.setEylulTahsilat(Integer.parseInt(clients[20]));
                                satisTahsilat.setEkimSatis(Integer.parseInt(clients[21]));
                                satisTahsilat.setEkimTahsilat(Integer.parseInt(clients[22]));
                                satisTahsilat.setKasimSatis(Integer.parseInt(clients[23]));
                                satisTahsilat.setKasimTahsilat(Integer.parseInt(clients[24]));
                                satisTahsilat.setAralikSatis(Integer.parseInt(clients[25]));
                                satisTahsilat.setAralikTahsilat(Integer.parseInt(clients[26]));
                                satisTahsilat.setToplamSatis(Integer.parseInt(clients[27]));
                                satisTahsilat.setToplamTahsilat(Integer.parseInt(clients[28]));
                            }
                        }
                        if (query.getSatilanMalListesiMiktarGore().size() > 0) {
                            for (int i = 2; i < query.getSatilanMalListesiMiktarGore().size(); i++) {
                                String[] clients = query.getSatilanMalListesiMiktarGore().get(i).split("\\|");
                                SatilanMalListesi liste = new SatilanMalListesi();
                                liste.setStokKodu(clients[0]);
                                liste.setStokAdi(clients[1]);
                                liste.setMiktar(Integer.parseInt(clients[2]));
                                satilanMalMiktar.add(liste);
                            }
                        }
                        if (query.getSatilanMalListesiTutarGore().size() > 0) {
                            for (int i = 2; i < query.getSatilanMalListesiMiktarGore().size(); i++) {
                                String[] clients = query.getSatilanMalListesiTutarGore().get(i).split("\\|");
                                SatilanMalListesi liste = new SatilanMalListesi();
                                liste.setStokKodu(clients[0]);
                                liste.setStokAdi(clients[1]);
                                liste.setTutar(Integer.parseInt(clients[2]));
                                satilanMalTutar.add(liste);
                            }
                        }
                        if (query.getMarkaBazindaSatisMiktarGore().size() > 0) {
                            for (int i = 2; i < query.getMarkaBazindaSatisMiktarGore().size(); i++) {
                                String[] marka = query.getMarkaBazindaSatisMiktarGore().get(i).split("\\|");
                                MarkaBazindaSatisMiktarTutarGore list = new MarkaBazindaSatisMiktarTutarGore();
                                list.setMarkaKodu(marka[0]);
                                list.setMarka(marka[1]);
                                list.setMiktar(Integer.parseInt(marka[2]));
                                markaBazindaSatisMiktar.add(list);
                            }
                        }
                        if (query.getMarkaBazindaSatisTutarGore().size() > 0) {
                            for (int i = 2; i < query.getMarkaBazindaSatisTutarGore().size(); i++) {
                                String[] marka = query.getMarkaBazindaSatisTutarGore().get(i).split("\\|");
                                MarkaBazindaSatisMiktarTutarGore list = new MarkaBazindaSatisMiktarTutarGore();
                                list.setMarkaKodu(marka[0]);
                                list.setMarka(marka[1]);
                                list.setTutar(Integer.parseInt(marka[2]));
                                markaBazindaSatisTutar.add(list);
                            }
                        }
                        if (query.getGrupBazindaSatisMiktarGore().size() > 0) {
                            for (int i = 2; i < query.getGrupBazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getGrupBazindaSatisMiktarGore().get(i).split("\\|");
                                GrupBazindaSatis miktar = new GrupBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Integer.parseInt(grup[2]));
                                grupBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getGrupBazindaSatisTutarGore().size() > 0) {
                            for (int i = 2; i < query.getGrupBazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getGrupBazindaSatisTutarGore().get(i).split("\\|");
                                GrupBazindaSatis miktar = new GrupBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Integer.parseInt(grup[2]));
                                grupBazindaSatisTutar.add(miktar);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            aylaraGoreCariSatisTahsilat(satisTahsilat);
            satilanMalMiktar(satilanMalMiktar);
            satilanMalTutar(satilanMalTutar);
            markaBazindaSatisMiktar(markaBazindaSatisMiktar);
            markaBazindaSatisTutar(markaBazindaSatisTutar);
            grupBazindaSatisMiktar(grupBazindaSatisMiktar);
            grupBazindaSatisTutar(grupBazindaSatisTutar);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    public class LabelFormatter extends ValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
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

    private String parametreGetir(String firmaNo, String parametre, String deger) {
        List<CihazlarFirmaParametreler> parametrelerList = databaseHandler.selectParametreGetir(firmaNo, parametre);
        String parametreDeger;
        if (parametrelerList.size() == 1) {
            parametreDeger = parametrelerList.get(0).getParametreDegeri();
        } else {
            parametreDeger = deger;
        }
        return parametreDeger;
    }
}