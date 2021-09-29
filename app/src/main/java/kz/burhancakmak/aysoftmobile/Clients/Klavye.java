package kz.burhancakmak.aysoftmobile.Clients;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import kz.burhancakmak.aysoftmobile.R;

public class Klavye extends LinearLayout implements View.OnClickListener {

    // constructors
    public Klavye(Context context) {
        this(context, null, 0);
    }

    public Klavye(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Klavye(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    // Our communication link to the EditText
    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);
        // keyboard keys (buttons)
        Button mButton1 = (Button) findViewById(R.id.button_1);
        Button mButton2 = (Button) findViewById(R.id.button_2);
        Button mButton3 = (Button) findViewById(R.id.button_3);
        Button mButton4 = (Button) findViewById(R.id.button_4);
        Button mButton5 = (Button) findViewById(R.id.button_5);
        Button mButton6 = (Button) findViewById(R.id.button_6);
        Button mButton7 = (Button) findViewById(R.id.button_7);
        Button mButton8 = (Button) findViewById(R.id.button_8);
        Button mButton9 = (Button) findViewById(R.id.button_9);
        Button mButton0 = (Button) findViewById(R.id.button_0);
        Button mButtonDelete = (Button) findViewById(R.id.button_delete);
        /*Button mButtonAdd = findViewById(R.id.buttonAdd);
        Button mButtonSub = findViewById(R.id.button_substract);*/

        // set button click listeners
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);
        /*mButtonAdd.setOnClickListener(this);
        mButtonSub.setOnClickListener(this);*/

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
    }

    @Override
    public void onClick(View v) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }

    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}
