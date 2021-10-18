package kz.burhancakmak.aysoftmobile.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagement {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AysoftMobile";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_WEB = "web";
    private static final String KEY_VISIT = "visit";
    private static final String KEY_FIRMA_NO = "firmano";
    private static final String KEY_FIYAT1 = "fiyat1";
    private static final String KEY_FIYAT2 = "fiyat2";
    private static final String KEY_RESIM_ADRESI = "resimAdresi";


    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void createLoginSession(String name, String password, String language) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_LANG, language);
        editor.commit();
    }

    public void createWebSettings(String uuid, String address) {
        editor.putString(KEY_UUID, uuid);
        editor.putString(KEY_WEB, address);
        editor.commit();
    }

    public void createVisit(Long visit) {
        editor.putLong(KEY_VISIT, visit);
        editor.commit();
    }

    public void createFirmaNo(String firma) {
        editor.putString(KEY_FIRMA_NO, firma);
        editor.commit();
    }

    public void changeLanguage(String language) {
        editor.putString(KEY_LANG, language);
        editor.commit();
    }


    public String getKeyLang() {
        return pref.getString(KEY_LANG, "en");
    }


    public HashMap<String, String> getWebSettings() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(KEY_UUID, pref.getString(KEY_UUID, null));
        hashMap.put(KEY_WEB, pref.getString(KEY_WEB, null));
        return hashMap;
    }

    public Long getKeyVisit() {
        return pref.getLong(KEY_VISIT, -1);
    }

    public String getKeyFirmaNo() {
        return pref.getString(KEY_FIRMA_NO, "");
    }

    public void removeKeyVisit() {
        editor.remove(KEY_VISIT);
        editor.commit();
    }

    public void removeWebSettings() {
        editor.remove(KEY_UUID);
        editor.remove(KEY_WEB);
        editor.commit();
    }

    public void removeKeyFirmaNo() {
        editor.remove(KEY_FIRMA_NO);
        editor.commit();
    }


    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(KEY_NAME, pref.getString(KEY_NAME, null));
        hashMap.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        hashMap.put(KEY_LANG, pref.getString(KEY_LANG, null));
        return hashMap;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void logoutUserDetails() {
        /*editor.remove(KEY_NAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_LANG);*/
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
