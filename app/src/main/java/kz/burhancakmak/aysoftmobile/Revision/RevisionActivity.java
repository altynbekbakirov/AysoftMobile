package kz.burhancakmak.aysoftmobile.Revision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Products.CameraCapture;
import kz.burhancakmak.aysoftmobile.Products.ProductSearchActivity;
import kz.burhancakmak.aysoftmobile.Products.ProductsActivity;
import kz.burhancakmak.aysoftmobile.R;

public class RevisionActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SearchView searchView;
    MenuItem searchItem;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_revision);
        initView();
    }

    private void initView() {
        databaseHandler = DatabaseHandler.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.main_nav_revision));
        floatingActionButton = findViewById(R.id.fabRevision);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RevisionActivity.this, RevisionDetailsActivity.class));
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.revision_menu, menu);
        searchItem = menu.findItem(R.id.product_search);
        searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                itemsAdapter.getFilter().filter(newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                invalidateOptionsMenu();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*if (item.getItemId() == R.id.product_price) {
            alertPriceChangeDialog();
        }
        if (item.getItemId() == R.id.product_view_type) {
            alertLayoutChangeDialog();
        }
        if (item.getItemId() == R.id.product_barcode_scan) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ProductsActivity.this);
            intentIntegrator.setPrompt(getString(R.string.products_camera_zoom_flash));
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(CameraCapture.class);
            intentIntegrator.initiateScan();
        }
        if (item.getItemId() == R.id.product_update) {
            new ProductsActivity.GetDataFromWeb().execute();
        }
        if (item.getItemId() == R.id.product_filter) {
            alertFilterDialog();
        }
        return super.onOptionsItemSelected(item);*/
        return false;
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

    private String getParameters(String firmaNo, String parametre, String deger) {
        List<CihazlarFirmaParametreler> parametrelerList = databaseHandler.selectParametreGetir(firmaNo, parametre);
        String param;
        if (parametrelerList.size() == 1) {
            param = parametrelerList.get(0).getParametreDegeri();
        } else {
            param = deger;
        }
        return param;
    }
}