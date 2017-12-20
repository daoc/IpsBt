package ec.edu.ute.dordonez.ipsbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dordonez on 9/dic/2017.
 * Modified by dordonez on 20/dic/2017.
 */
public class ReceiverBt extends Activity {
    private TextView tv;
    private TextView tvLog;
    private ToggleButton tBut;
    private SeekBar sb;
    private int iterCounter;
    private EditText etX;
    private EditText etY;
    private EditText etDelta;
    private Button bAddX;
    private Button bSubX;
    private Button bAddY;
    private Button bSubY;
    private MySql bdd;
    private SQLiteDatabase database;
    private Cursor cursor;
    private Map<String, Pair<String, String>> balizasActivas;
    private String fileName;
    private FileOutputStream fileLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_bt);
        tv = (TextView) findViewById(R.id.tv);
        tBut = (ToggleButton) findViewById(R.id.tBut);
        tvLog = (TextView) findViewById(R.id.tvLog);
        sb = (SeekBar) findViewById(R.id.sb);
        etX = (EditText) findViewById(R.id.etX);
        etY = (EditText) findViewById(R.id.etY);
        etDelta = (EditText) findViewById(R.id.etDelta);
        bAddX = (Button) findViewById(R.id.bAddX);
        bSubX = (Button) findViewById(R.id.bSubX);
        bAddY = (Button) findViewById(R.id.bAddY);
        bSubY = (Button) findViewById(R.id.bSubY);
        fileName = getPreferences(MODE_PRIVATE).getString("filename", "ipsbt.txt");
        new File(Environment.getExternalStoragePublicDirectory("ipsbt"), "").mkdir();
        tv.setText("Recolecta RSSI: " + sb.getProgress() + " (" + fileName + ")");

        try {
            fileLog = new FileOutputStream(
                new File(Environment.getExternalStoragePublicDirectory("ipsbt"), fileName),
                true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LibBt.setBluetoothEnabled(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bdd = new MySql(this);
        database = bdd.getReadableDatabase();
        cursor = database.rawQuery("select * from baliza where active = 1", null);
        balizasActivas = new HashMap<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            balizasActivas.put(cursor.getString(0).toLowerCase(),
                    new Pair<>(cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }

        tBut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sb.setEnabled(false);
                    etX.setEnabled(false);
                    etY.setEnabled(false);
                    etDelta.setEnabled(false);
                    bAddX.setEnabled(false);
                    bSubX.setEnabled(false);
                    bAddY.setEnabled(false);
                    bSubY.setEnabled(false);
                    iterCounter = 0;
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                } else {
                    Toast.makeText(ReceiverBt.this, "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_LONG).show();
                    sb.setEnabled(true);
                    etX.setEnabled(true);
                    etY.setEnabled(true);
                    etDelta.setEnabled(true);
                    bAddX.setEnabled(true);
                    bSubX.setEnabled(true);
                    bAddY.setEnabled(true);
                    bSubY.setEnabled(true);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                }
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText("Recolecta RSSI: " + progress + "(" + fileName + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        LibBt.setBluetoothEnabled(false);
        try {
            fileLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        private final Set<String> balizas = new HashSet<>();
        private final String template = "T:%d;Rn:%s;Rx:%s;Ry:%s;Bn:%s;Br:%d;Bx:%s;By:%s\n";
        private final String myname = BluetoothAdapter.getDefaultAdapter().getName();

        @Override
        public void onReceive(Context context, Intent intent) {
            long millis = System.currentTimeMillis();
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME).toLowerCase();
                    String bx = balizasActivas.get(name) != null ? balizasActivas.get(name).first : "-";
                    String by = balizasActivas.get(name) != null ? balizasActivas.get(name).second : "-";
                    try {
                        fileLog.write(String.format(template,
                                millis, myname, etX.getText(), etY.getText(), name, rssi, bx, by).getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvLog.append(name + ":" + rssi + "\n");
                    balizas.remove(name.toLowerCase());
                    if(balizas.isEmpty()) {
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    balizas.clear();
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()) {
                        balizas.add(cursor.getString(0).toLowerCase());
                        cursor.moveToNext();
                    }
                    iterCounter++;
                    try {
                        fileLog.write(String.format(template,
                                millis, myname, etX.getText(), etY.getText(), "START(" + iterCounter + ")", 0, "-", "-").getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvLog.append("START\n");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    try {
                        //fileLog.write(String.format(template,
                        //        millis, myname, etX.getText(), etY.getText(), "FINISH", 0, "-", "-").getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvLog.append("FINISH\n");
                    if(tBut.isChecked() && iterCounter < sb.getProgress()) {
                        BluetoothAdapter.getDefaultAdapter().startDiscovery();
                    } else {
                        tBut.setChecked(false);
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    try {
                        //fileLog.write(String.format(template,
                        //        millis, myname, etX.getText(), etY.getText(), String.valueOf(state), 0, "-", "-").getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvLog.append(String.valueOf(state) + "\n");
                    break;
            }
            tvLog.setMovementMethod(new ScrollingMovementMethod());
        }
    };

    public void addDeltaX(View v) {
        int x = Integer.parseInt(etX.getText().toString());
        int delta = Integer.parseInt(etDelta.getText().toString());
        etX.setText(String.valueOf(x + delta));
    }

    public void subDeltaX(View v) {
        int x = Integer.parseInt(etX.getText().toString());
        int delta = Integer.parseInt(etDelta.getText().toString());
        etX.setText(String.valueOf(x - delta));
    }
    public void addDeltaY(View v) {
        int y = Integer.parseInt(etY.getText().toString());
        int delta = Integer.parseInt(etDelta.getText().toString());
        etY.setText(String.valueOf(y + delta));
    }

    public void subDeltaY(View v) {
        int y = Integer.parseInt(etY.getText().toString());
        int delta = Integer.parseInt(etDelta.getText().toString());
        etY.setText(String.valueOf(y - delta));
    }

    public void newFile(View v) {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        fileName = "ipsbt" + new Date().getTime() + ".txt";
        pref.edit().putString("filename", fileName).commit();
        tv.setText("Recolecta RSSI: " + sb.getProgress() + "(" + fileName + ")");
        try {
            fileLog.close();
            fileLog = new FileOutputStream(
                    new File(Environment.getExternalStoragePublicDirectory("ipsbt"), fileName),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
