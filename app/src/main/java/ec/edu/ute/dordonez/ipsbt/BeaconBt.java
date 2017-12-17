package ec.edu.ute.dordonez.ipsbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dordonez on 9/dic/2017.
 */
public class BeaconBt extends Activity {
    ToggleButton tBut;
    EditText etSeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_bt);
        LibBt.setBluetoothEnabled(true);
        tBut = (ToggleButton) findViewById(R.id.tBut);
        etSeg = (EditText) findViewById(R.id.etSeg);
        int defTimeDisc = 300;
        if(Build.VERSION.SDK_INT >= 18) {//4.3
            defTimeDisc = 0;
        } else if(Build.VERSION.SDK_INT >= 16) {//4.1
            defTimeDisc = 3600;
        }
        etSeg.setText(String.valueOf(defTimeDisc));
        tBut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int defTimeDisc = 300;
                    try {
                        defTimeDisc = Integer.parseInt(etSeg.getText().toString());
                    } catch (Exception e) {
                        etSeg.setText(String.valueOf(defTimeDisc));
                        Toast.makeText(BeaconBt.this, "No puso un entero (seteado a 300)", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, defTimeDisc);
                    startActivity(intent);
                } else {
                    if(BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                        startActivity(intent);
                    }

                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        LibBt.setBluetoothEnabled(false);

    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    if(BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        tBut.setChecked(false);
                        Toast.makeText(context, "Fin de SCAN_MODE_CONNECTABLE_DISCOVERABLE", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

}
