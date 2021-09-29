package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ClientFragmentAdapter;
import kz.burhancakmak.aysoftmobile.Adapters.FragmentAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;

public class ClientsTasksActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> hashMap;
    private static final String KEY_LANG = "language";
    public static Integer clientKayitNo = null;
    private int position;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;
    ClientFragmentAdapter adapter;
    DatabaseHandler databaseHandler;
    ClCard card;
    String ziyaretSistemiKullanimi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();
        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_client_tasks);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Intent intent = getIntent();
            clientKayitNo = intent.getIntExtra("clientKayitNo", -1);
            position = intent.getIntExtra("position", -1);
            databaseHandler = DatabaseHandler.getInstance(this);

            if (clientKayitNo != -1) {
                card = databaseHandler.selectClientById(clientKayitNo);
                ziyaretSistemiKullanimi = parametreGetir(FIRMA_NO, "ZiyaretSistemiKullanimi", "0");
            }

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(card.getUnvani1());
            toolbar.setElevation(0);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            tabLayout = findViewById(R.id.tab_layout);
            viewPager2 = findViewById(R.id.view_pager2);

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (ziyaretSistemiKullanimi.equals("0")) {
                adapter = new ClientFragmentAdapter(fragmentManager, getLifecycle());
                viewPager2.setAdapter(adapter);
                tabLayout.removeTabAt(1);
                setActiveTab(1);
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            } else {
                fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
                viewPager2.setAdapter(fragmentAdapter);
                setActiveTab(1);
            }


            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager2.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
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

    private void setActiveTab(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        viewPager2.setCurrentItem(pageIndex);
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