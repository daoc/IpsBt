package ec.edu.ute.dordonez.ipsbt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by dordonez on 12/9/2017.
 */

public class AddModDelBeaconBt extends Activity {
    private EditText etName;
    private EditText etX;
    private EditText etY;
    private CheckBox cbActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoddelbeacon_bt);
        Intent intent = getIntent();
        etName = (EditText) findViewById(R.id.etName);
        etName.setText(intent.getStringExtra("name"));
        etX = (EditText) findViewById(R.id.etX);
        etX.setText(intent.getStringExtra("x"));
        etY = (EditText) findViewById(R.id.etY);
        etY.setText(intent.getStringExtra("y"));
        cbActive = (CheckBox) findViewById(R.id.cbActive);
        cbActive.setChecked(intent.getBooleanExtra("active", false));
    }

    public void updateBeacon(View view) {
        Intent intent = new Intent();
        intent.putExtra("name", etName.getText().toString());
        intent.putExtra("x", Integer.parseInt(etX.getText().toString()));
        intent.putExtra("y", Integer.parseInt(etY.getText().toString()));
        intent.putExtra("active", cbActive.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelBeacon(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void deleteBeacon(View view) {
        Intent intent = new Intent();
        intent.putExtra("name", etName.getText().toString());
        intent.putExtra("delete", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
