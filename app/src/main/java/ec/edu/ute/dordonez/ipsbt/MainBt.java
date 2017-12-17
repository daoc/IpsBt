package ec.edu.ute.dordonez.ipsbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

/**
 * Created by dordonez on 9/dic/2017.
 */
public class MainBt extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void toBeacon(View view) {
        Intent intent = new Intent(this, BeaconBt.class);
        startActivity(intent);
    }

    public void toReceiver(View view) {
        Intent intent = new Intent(this, ReceiverBt.class);
        startActivity(intent);
    }

    public void toConfigBeacons(View view) {
        Intent intent = new Intent(this, ConfigBeaconsBt.class);
        startActivity(intent);
    }

}
