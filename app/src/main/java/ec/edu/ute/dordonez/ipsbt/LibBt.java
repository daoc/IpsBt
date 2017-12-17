package ec.edu.ute.dordonez.ipsbt;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by ordon on 12/9/2017.
 */

public class LibBt {

    public static final String CONF_BEACONS = "CONF_BEACONS";

    public static boolean setBluetoothEnabled(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            bluetoothAdapter.enable();
            while(!bluetoothAdapter.isEnabled()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return bluetoothAdapter.isEnabled();
        }
        else if(!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

}
