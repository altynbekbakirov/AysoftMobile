package kz.burhancakmak.aysoftmobile.Services;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Locale;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.MainActivity;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaDepolar;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaOdemeSekli;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirma;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarQuery;
import kz.burhancakmak.aysoftmobile.Models.Firms.Doviz;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonServices extends Activity {
    public void setPhoneDefaultLanguage(String code) {
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

    public void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}
