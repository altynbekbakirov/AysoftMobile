package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonSyntaxException;

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
import kz.burhancakmak.aysoftmobile.Models.Clients.OzelKodBazindaSatis;
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
    String kurusHaneSayisiStok, CariDashBoardAylikSatisTahsilatGoster;
    ClCard card;
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    List<SatilanMalListesi> satilanMalMiktar = new ArrayList<>();
    List<SatilanMalListesi> satilanMalTutar = new ArrayList<>();
    List<MarkaBazindaSatisMiktarTutarGore> markaBazindaSatisMiktar = new ArrayList<>();
    List<MarkaBazindaSatisMiktarTutarGore> markaBazindaSatisTutar = new ArrayList<>();
    List<GrupBazindaSatis> grupBazindaSatisMiktar = new ArrayList<>();
    List<GrupBazindaSatis> grupBazindaSatisTutar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel1KodBazindaSatisMiktar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel1KodBazindaSatisTutar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel2KodBazindaSatisMiktar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel2KodBazindaSatisTutar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel3KodBazindaSatisMiktar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel3KodBazindaSatisTutar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel4KodBazindaSatisMiktar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel4KodBazindaSatisTutar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel5KodBazindaSatisMiktar = new ArrayList<>();
    List<OzelKodBazindaSatis> ozel5KodBazindaSatisTutar = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clients_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_extract_refresh) {
            new GetDataFromWeb().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        Intent intent = getIntent();
        clientKayitNo = intent.getIntExtra("clientKayitNo", -1);

        databaseHandler = DatabaseHandler.getInstance(this);
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);

        if (clientKayitNo != -1) {
            card = databaseHandler.selectClientById(clientKayitNo);
            kurusHaneSayisiStok = parametreGetir("KurusHaneSayisiCari", "0");
            CariDashBoardAylikSatisTahsilatGoster = parametreGetir("CariDashBoardAylikSatisTahsilatGoster", "0");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(card.getUnvani1());
        toolbar.setSubtitle(R.string.client_popup_analyze);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);

        new GetDataFromWeb().execute();
    }

    private void aylaraGoreCariSatisTahsilat(AylaraGoreCariSatisTahsilat list) {
        BarChart barChart = findViewById(R.id.barChart);
        LinearLayout layoutBarChart = findViewById(R.id.layoutBarChart);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
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

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.products_extract_sale_count));
                set1.setColor(Color.parseColor("#f39c12"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#e67e22"));

                BarDataSet set2 = new BarDataSet(entriesGroup2, getString(R.string.client_dashboard_month_collections));
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
                axis.setLabelCount(12);

                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(0 + barChart.getData().getGroupWidth(groupSpace, barSpace) * 12);
                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

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
            } catch (Exception e) {
            }
        }
    }

    /*private void satilanMalMiktar(List<SatilanMalListesi> list) {
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

            BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_order_bottom_panel_count));
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
    }*/

    private void satilanMalMiktar(List<SatilanMalListesi> list) {
        HorizontalBarChart horizontalBarChart = findViewById(R.id.barChartMiktar);
        LinearLayout layoutBarChartTutar = findViewById(R.id.layoutBarChartMiktar);

        if (list.size() == 0) {
            layoutBarChartTutar.setVisibility(View.GONE);
        } else {
            layoutBarChartTutar.setVisibility(View.VISIBLE);

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

            BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.items_miktar_label));
            set1.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set1.setColors(ColorTemplate.COLORFUL_COLORS);

            BarData data = new BarData(set1);
            horizontalBarChart.setData(data);

            XAxis axis = horizontalBarChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(items));
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
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

            BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.items_tutar_label));
            set1.setValueTextSize(8f);
            set1.setValueTextColor(Color.BLACK);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(set1);
            horizontalBarChart.setData(data);

            XAxis axis = horizontalBarChart.getXAxis();
            axis.setValueFormatter(new IndexAxisValueFormatter(items));
            axis.setEnabled(true);
            axis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
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
        PieChart pieChart = findViewById(R.id.pieChartMarkaSatisMiktar);
        LinearLayout layoutPieChartMarkaSatisMiktar = findViewById(R.id.layoutPieChartMarkaSatisMiktar);

        if (list.size() == 0) {
            layoutPieChartMarkaSatisMiktar.setVisibility(View.GONE);
        } else {
            layoutPieChartMarkaSatisMiktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet dataSet1 = new PieDataSet(pieEntries, getString(R.string.client_order_bottom_panel_count));
            dataSet1.setColors(ColorTemplate.JOYFUL_COLORS);
            dataSet1.setValueTextSize(14f);

            PieData pieData = new PieData(dataSet1);

            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChart.setCenterTextSize(14f);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setData(pieData);
            pieChart.invalidate();
        }

    }

    private void markaBazindaSatisTutar(List<MarkaBazindaSatisMiktarTutarGore> list) {
        PieChart pieChart = findViewById(R.id.pieChartMarkaSatisTutar);
        LinearLayout layoutPieChartMarkaSatisTutar = findViewById(R.id.layoutPieChartMarkaSatisTutar);

        if (list.size() == 0) {
            layoutPieChartMarkaSatisTutar.setVisibility(View.GONE);
        } else {
            layoutPieChartMarkaSatisTutar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet dataSet1 = new PieDataSet(pieEntries, getString(R.string.items_tutar_label));
            dataSet1.setColors(ColorTemplate.PASTEL_COLORS);
            dataSet1.setValueTextSize(14f);

            PieData pieData = new PieData(dataSet1);

            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText(getString(R.string.items_tutar_label));
            pieChart.setCenterTextSize(14f);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setData(pieData);
            pieChart.invalidate();
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

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_group_count));
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartGrupSatisMiktar.setTransparentCircleColor(Color.YELLOW);
            pieChartGrupSatisMiktar.getDescription().setEnabled(false);
            pieChartGrupSatisMiktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
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

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_group_sum));
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartGrupSatisTutar.getDescription().setEnabled(false);
            pieChartGrupSatisTutar.setCenterText(getString(R.string.items_tutar_label));
            pieChartGrupSatisTutar.setCenterTextSize(16f);
            pieChartGrupSatisTutar.setCenterTextColor(Color.BLACK);
            pieChartGrupSatisTutar.setData(pieData);
            pieChartGrupSatisTutar.invalidate();
        }
    }

    private void ozelKod1BazindaSatisMiktar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel1Miktar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel1Miktar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_count));
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod1BazindaSatisTutar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel1Tutar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel1Tutar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_sum));
            pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.items_tutar_label));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod2BazindaSatisMiktar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel2Miktar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel2Miktar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_count));
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();

        }
    }

    private void ozelKod2BazindaSatisTutar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel2Tutar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel2Tutar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_sum));
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.items_tutar_label));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod3BazindaSatisMiktar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel3Miktar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel3Miktar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_count));
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod3BazindaSatisTutar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel3Tutar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel3Tutar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_sum));
            pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.items_tutar_label));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod4BazindaSatisMiktar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel4Miktar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel4Miktar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_count));
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod4BazindaSatisTutar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel4Tutar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel4Tutar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_sum));
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.items_tutar_label));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod5BazindaSatisMiktar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel5Miktar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel5Miktar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getMiktar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_count));
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.client_order_bottom_panel_count));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private void ozelKod5BazindaSatisTutar(List<OzelKodBazindaSatis> list) {
        PieChart pieChartOzel1Miktar = findViewById(R.id.pieChartOzel5Tutar);
        LinearLayout layoutPieChartOzel1Miktar = findViewById(R.id.layoutPieChartOzel5Tutar);

        if (list.size() == 0) {
            layoutPieChartOzel1Miktar.setVisibility(View.GONE);
        } else {
            layoutPieChartOzel1Miktar.setVisibility(View.VISIBLE);

            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                pieEntries.add(new PieEntry(list.get(i).getTutar(), list.get(i).getMarka()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, getString(R.string.client_dashboard_special_sum));
            pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            pieDataSet.setValueTextSize(14f);

            PieData pieData = new PieData(pieDataSet);

            pieChartOzel1Miktar.getDescription().setEnabled(false);
            pieChartOzel1Miktar.setCenterText(getString(R.string.items_tutar_label));
            pieChartOzel1Miktar.setCenterTextSize(16f);
            pieChartOzel1Miktar.setCenterTextColor(Color.BLACK);
            pieChartOzel1Miktar.setData(pieData);
            pieChartOzel1Miktar.invalidate();
        }
    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.dashboard_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsDashboardQuery> queryList;
        AylaraGoreCariSatisTahsilat satisTahsilat = new AylaraGoreCariSatisTahsilat();
        String hata = "";

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
                        if (query.getAylaraGoreCariSatisTahsilat() != null &&
                                query.getAylaraGoreCariSatisTahsilat().size() > 2) {
                            publishProgress(getString(R.string.data_import_progressbar_clients));
                            for (int i = 2; i < query.getAylaraGoreCariSatisTahsilat().size(); i++) {
                                String[] clients = query.getAylaraGoreCariSatisTahsilat().get(i).split("\\|");
                                satisTahsilat.setCariKayitNo(Integer.parseInt(clients[0]));
                                satisTahsilat.setCariKod(clients[1]);
                                satisTahsilat.setCariUnvan(clients[2]);
                                satisTahsilat.setOcakSatis(Float.parseFloat(clients[3]));
                                satisTahsilat.setOcakTahsilat(Float.parseFloat(clients[4]));
                                satisTahsilat.setSubatSatis(Float.parseFloat(clients[5]));
                                satisTahsilat.setSubatTahsilat(Float.parseFloat(clients[6]));
                                satisTahsilat.setMartSatis(Float.parseFloat(clients[7]));
                                satisTahsilat.setMartTahsilat(Float.parseFloat(clients[8]));
                                satisTahsilat.setNisanSatis(Float.parseFloat(clients[9]));
                                satisTahsilat.setNisanTahsilat(Float.parseFloat(clients[10]));
                                satisTahsilat.setMayisSatis(Float.parseFloat(clients[11]));
                                satisTahsilat.setMayisTahsilat(Float.parseFloat(clients[12]));
                                satisTahsilat.setHaziranSatis(Float.parseFloat(clients[13]));
                                satisTahsilat.setHaziranTahsilat(Float.parseFloat(clients[14]));
                                satisTahsilat.setTemmuzSatis(Float.parseFloat(clients[15]));
                                satisTahsilat.setTemmuzTahsilat(Float.parseFloat(clients[16]));
                                satisTahsilat.setAgustosSatis(Float.parseFloat(clients[17]));
                                satisTahsilat.setAgustosTahsilat(Float.parseFloat(clients[18]));
                                satisTahsilat.setEylulSatis(Float.parseFloat(clients[19]));
                                satisTahsilat.setEylulTahsilat(Float.parseFloat(clients[20]));
                                satisTahsilat.setEkimSatis(Float.parseFloat(clients[21]));
                                satisTahsilat.setEkimTahsilat(Float.parseFloat(clients[22]));
                                satisTahsilat.setKasimSatis(Float.parseFloat(clients[23]));
                                satisTahsilat.setKasimTahsilat(Float.parseFloat(clients[24]));
                                satisTahsilat.setAralikSatis(Float.parseFloat(clients[25]));
                                satisTahsilat.setAralikTahsilat(Float.parseFloat(clients[26]));
                                satisTahsilat.setToplamSatis(Float.parseFloat(clients[27]));
                                satisTahsilat.setToplamTahsilat(Float.parseFloat(clients[28]));
                            }
                        }
                        if (query.getSatilanMalListesiMiktarGore() != null &&
                                query.getSatilanMalListesiMiktarGore().size() > 2) {
                            satilanMalMiktar.clear();
                            for (int i = 2; i < query.getSatilanMalListesiMiktarGore().size(); i++) {
                                String[] clients = query.getSatilanMalListesiMiktarGore().get(i).split("\\|");
                                SatilanMalListesi liste = new SatilanMalListesi();
                                liste.setStokKodu(clients[0]);
                                liste.setStokAdi(clients[1]);
                                liste.setMiktar(Float.parseFloat(clients[2]));
                                satilanMalMiktar.add(liste);
                            }
                        }
                        if (query.getSatilanMalListesiTutarGore() != null
                                && query.getSatilanMalListesiTutarGore().size() > 2) {
                            satilanMalTutar.clear();
                            for (int i = 2; i < query.getSatilanMalListesiMiktarGore().size(); i++) {
                                String[] clients = query.getSatilanMalListesiTutarGore().get(i).split("\\|");
                                SatilanMalListesi liste = new SatilanMalListesi();
                                liste.setStokKodu(clients[0]);
                                liste.setStokAdi(clients[1]);
                                liste.setTutar(Float.parseFloat(clients[2]));
                                satilanMalTutar.add(liste);
                            }
                        }
                        if (query.getMarkaBazindaSatisMiktarGore() != null
                                && query.getMarkaBazindaSatisMiktarGore().size() > 2) {
                            markaBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getMarkaBazindaSatisMiktarGore().size(); i++) {
                                String[] marka = query.getMarkaBazindaSatisMiktarGore().get(i).split("\\|");
                                MarkaBazindaSatisMiktarTutarGore list = new MarkaBazindaSatisMiktarTutarGore();
                                list.setMarkaKodu(marka[0]);
                                list.setMarka(marka[1]);
                                list.setMiktar(Float.parseFloat(marka[2]));
                                markaBazindaSatisMiktar.add(list);
                            }
                        }
                        if (query.getMarkaBazindaSatisTutarGore() != null
                                && query.getMarkaBazindaSatisTutarGore().size() > 2) {
                            markaBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getMarkaBazindaSatisTutarGore().size(); i++) {
                                String[] marka = query.getMarkaBazindaSatisTutarGore().get(i).split("\\|");
                                MarkaBazindaSatisMiktarTutarGore list = new MarkaBazindaSatisMiktarTutarGore();
                                list.setMarkaKodu(marka[0]);
                                list.setMarka(marka[1]);
                                list.setTutar(Float.parseFloat(marka[2]));
                                markaBazindaSatisTutar.add(list);
                            }
                        }
                        if (query.getGrupBazindaSatisMiktarGore() != null
                                && query.getGrupBazindaSatisMiktarGore().size() > 2) {
                            grupBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getGrupBazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getGrupBazindaSatisMiktarGore().get(i).split("\\|");
                                GrupBazindaSatis miktar = new GrupBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                grupBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getGrupBazindaSatisTutarGore() != null
                                && query.getGrupBazindaSatisTutarGore().size() > 2) {
                            grupBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getGrupBazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getGrupBazindaSatisTutarGore().get(i).split("\\|");
                                GrupBazindaSatis miktar = new GrupBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                grupBazindaSatisTutar.add(miktar);
                            }
                        }
                        if (query.getOzelKod1BazindaSatisMiktarGore() != null
                                && query.getOzelKod1BazindaSatisMiktarGore().size() > 2) {
                            ozel1KodBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getOzelKod1BazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getOzelKod1BazindaSatisMiktarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                ozel1KodBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getOzelKod1BazindaSatisTutarGore() != null
                                && query.getOzelKod1BazindaSatisTutarGore().size() > 2) {
                            ozel1KodBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getOzelKod1BazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getOzelKod1BazindaSatisTutarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                ozel1KodBazindaSatisTutar.add(miktar);
                            }
                        }
                        if (query.getOzelKod2BazindaSatisMiktarGore() != null
                                && query.getOzelKod2BazindaSatisMiktarGore().size() > 2) {
                            ozel2KodBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getOzelKod2BazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getOzelKod2BazindaSatisMiktarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                ozel2KodBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getOzelKod2BazindaSatisTutarGore() != null
                                && query.getOzelKod2BazindaSatisTutarGore().size() > 2) {
                            ozel2KodBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getOzelKod2BazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getOzelKod2BazindaSatisTutarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                ozel2KodBazindaSatisTutar.add(miktar);
                            }
                        }
                        if (query.getOzelKod3BazindaSatisMiktarGore() != null
                                && query.getOzelKod3BazindaSatisMiktarGore().size() > 2) {
                            ozel3KodBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getOzelKod3BazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getOzelKod3BazindaSatisMiktarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                ozel3KodBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getOzelKod3BazindaSatisTutarGore() != null
                                && query.getOzelKod3BazindaSatisTutarGore().size() > 2) {
                            ozel3KodBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getOzelKod3BazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getOzelKod3BazindaSatisTutarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                ozel3KodBazindaSatisTutar.add(miktar);
                            }
                        }
                        if (query.getOzelKod4BazindaSatisMiktarGore() != null
                                && query.getOzelKod4BazindaSatisMiktarGore().size() > 2) {
                            ozel4KodBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getOzelKod4BazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getOzelKod4BazindaSatisMiktarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                ozel4KodBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getOzelKod4BazindaSatisTutarGore() != null
                                && query.getOzelKod4BazindaSatisTutarGore().size() > 2) {
                            ozel4KodBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getOzelKod4BazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getOzelKod4BazindaSatisTutarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                ozel4KodBazindaSatisTutar.add(miktar);
                            }
                        }
                        if (query.getOzelKod5BazindaSatisMiktarGore() != null
                                && query.getOzelKod5BazindaSatisMiktarGore().size() > 2) {
                            ozel5KodBazindaSatisMiktar.clear();
                            for (int i = 2; i < query.getOzelKod5BazindaSatisMiktarGore().size(); i++) {
                                String[] grup = query.getOzelKod5BazindaSatisMiktarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setMiktar(Float.parseFloat(grup[2]));
                                ozel5KodBazindaSatisMiktar.add(miktar);
                            }
                        }
                        if (query.getOzelKod5BazindaSatisTutarGore() != null
                                && query.getOzelKod5BazindaSatisTutarGore().size() > 2) {
                            ozel5KodBazindaSatisTutar.clear();
                            for (int i = 2; i < query.getOzelKod5BazindaSatisTutarGore().size(); i++) {
                                String[] grup = query.getOzelKod5BazindaSatisTutarGore().get(i).split("\\|");
                                OzelKodBazindaSatis miktar = new OzelKodBazindaSatis();
                                miktar.setMarkaKodu(grup[0]);
                                miktar.setMarka(grup[1]);
                                miktar.setTutar(Float.parseFloat(grup[2]));
                                ozel5KodBazindaSatisTutar.add(miktar);
                            }
                        }
                    }
                }
            } catch (NullPointerException | IllegalStateException | JsonSyntaxException | IOException e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (hata.isEmpty()) {
                aylaraGoreCariSatisTahsilat(satisTahsilat);
                if (satilanMalMiktar.size() > 0) satilanMalMiktar(satilanMalMiktar);
                if (satilanMalTutar.size() > 0) satilanMalTutar(satilanMalTutar);
                if (markaBazindaSatisMiktar.size() > 0)
                    markaBazindaSatisMiktar(markaBazindaSatisMiktar);
                if (markaBazindaSatisTutar.size() > 0)
                    markaBazindaSatisTutar(markaBazindaSatisTutar);
                if (grupBazindaSatisMiktar.size() > 0)
                    grupBazindaSatisMiktar(grupBazindaSatisMiktar);
                if (grupBazindaSatisTutar.size() > 0) grupBazindaSatisTutar(grupBazindaSatisTutar);
                if (ozel1KodBazindaSatisMiktar.size() > 0)
                    ozelKod1BazindaSatisMiktar(ozel1KodBazindaSatisMiktar);
                if (ozel1KodBazindaSatisTutar.size() > 0)
                    ozelKod1BazindaSatisTutar(ozel1KodBazindaSatisTutar);
                if (ozel2KodBazindaSatisMiktar.size() > 0)
                    ozelKod2BazindaSatisMiktar(ozel2KodBazindaSatisMiktar);
                if (ozel2KodBazindaSatisTutar.size() > 0)
                    ozelKod2BazindaSatisTutar(ozel2KodBazindaSatisTutar);
                if (ozel3KodBazindaSatisMiktar.size() > 0)
                    ozelKod3BazindaSatisMiktar(ozel3KodBazindaSatisMiktar);
                if (ozel3KodBazindaSatisTutar.size() > 0)
                    ozelKod3BazindaSatisTutar(ozel3KodBazindaSatisTutar);
                if (ozel4KodBazindaSatisMiktar.size() > 0)
                    ozelKod4BazindaSatisMiktar(ozel4KodBazindaSatisMiktar);
                if (ozel4KodBazindaSatisTutar.size() > 0)
                    ozelKod4BazindaSatisTutar(ozel4KodBazindaSatisTutar);
                if (ozel5KodBazindaSatisMiktar.size() > 0)
                    ozelKod5BazindaSatisMiktar(ozel5KodBazindaSatisMiktar);
                if (ozel5KodBazindaSatisTutar.size() > 0)
                    ozelKod5BazindaSatisTutar(ozel5KodBazindaSatisTutar);
            } else {
                sendDataToServerFailDialog(hata);
            }
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

    private String parametreGetir(String param, String deger) {
        String parametreDeger = deger;
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(param)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
        }
        return parametreDeger;
    }

    private void sendDataToServerFailDialog(String errorMessage) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(errorMessage);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}