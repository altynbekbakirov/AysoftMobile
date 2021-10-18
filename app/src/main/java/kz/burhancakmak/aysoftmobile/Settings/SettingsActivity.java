package kz.burhancakmak.aysoftmobile.Settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.R;

public class SettingsActivity extends AppCompatActivity {

    SessionManagement session;
    TextInputEditText editTextWeb, editTextId;
    DatabaseHandler databaseHandler;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;
    private boolean phonePermissionGranted;
    public static String uniqueID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManagement(getApplicationContext());
        databaseHandler = DatabaseHandler.getInstance(this);
        editTextWeb = findViewById(R.id.settings_webService);
        editTextId = findViewById(R.id.settings_phoneId);
        ImageView data_drop = findViewById(R.id.settings_data_drop);
        getPhonePermission();
        HashMap<String, String> webSettings = session.getWebSettings();

        if (webSettings.get("web") != null && webSettings.get("uuid") != null) {
            editTextWeb.setText(webSettings.get("web"));
            editTextId.setText(webSettings.get("uuid"));
            uniqueID = webSettings.get("uuid");
        } else {
            uniqueID = getPhoneIMEI();
            if (uniqueID != null) {
                editTextId.setText(uniqueID);
            }
        }

        findViewById(R.id.settings_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextId.getText().toString().isEmpty() && !editTextWeb.getText().toString().isEmpty()) {
                    session.removeKeyFirmaNo();
                    session.removeWebSettings();
                    session.createWebSettings(editTextId.getText().toString(), editTextWeb.getText().toString());
//                    session.createWebSettings("869780039775405", "http://5.199.138.36:34444/NewMobil/");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        final TextInputLayout textInputLayout = findViewById(R.id.settings_textLayout);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("phoneID", uniqueID);
                manager.setPrimaryClip(clipData);

                /*ClipData pasteData = manager.getPrimaryClip();
                ClipData.Item item = pasteData.getItemAt(0);
                String paste = item.getText().toString();*/

                Toast.makeText(SettingsActivity.this, R.string.settings_id_copied, Toast.LENGTH_SHORT).show();
            }
        });

        data_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database_dialog();
            }
        });
    }


    @SuppressLint("HardwareIds")
    private String getPhoneIMEI() {
        String deviceId = null;

        if (phonePermissionGranted) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
                        getApplication().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
                            getApplication().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }

        } else {
            getPhonePermission();
        }
        return deviceId;
    }


    private void getPhonePermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            phonePermissionGranted = true;
            getPhoneIMEI();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        phonePermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phonePermissionGranted = true;
                getPhoneIMEI();
            }
        }
    }

    private void database_dialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.settings_drop_table_title));
        builder.setIcon(R.drawable.ic_arrow_drop_down);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.alert_settings_tables_drop_layout, null);
        builder.setView(view);
        TextInputEditText editTableDrop = view.findViewById(R.id.editTableDrop);
        editTableDrop.requestFocus();
        builder.setPositiveButton(R.string.alert_confirm_ok, null);
        builder.setNegativeButton(R.string.clients_orders_menu_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialogBuilder = builder.create();
        dialogBuilder.show();
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTableDrop.getText().toString().isEmpty()) {
                    int pwd = Integer.parseInt(editTableDrop.getText().toString().trim());
                    if (pwd != 1453) {
                        editTableDrop.setError(getString(R.string.login_password_notcorrect_error));
                        editTableDrop.requestFocus();
                    } else {
                        databaseHandler.dropAllTables();
                        databaseHandler.createParametreTables();
                        dialogBuilder.dismiss();
                        Toast.makeText(SettingsActivity.this, R.string.settings_drop_table_toast, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editTableDrop.setError(getString(R.string.login_password_empty_error));
                    editTableDrop.requestFocus();
                }
            }
        });
    }


}
