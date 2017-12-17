package ec.edu.ute.dordonez.ipsbt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by dordonez on 9/dic/2017.
 */
public class ConfigBeaconsBt extends Activity {
    private SharedPreferences pref;
    private String template = "%s;%s;%s";
    private final int ADD_BEACON = 1;
    private final int MOD_DEL_BEACON = 2;
    private MySql bdd;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ListView lv;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configbeacons_bt);
        pref = getSharedPreferences(LibBt.CONF_BEACONS, Context.MODE_PRIVATE);
        lv = (ListView) findViewById(R.id.lv);

        bdd = new MySql(this);
        database = bdd.getWritableDatabase();
        cursor = database.rawQuery("select name as _id, x, y, active from baliza", null);
        //informacion a presentar en la lista
        String[] from = {"_id", "x", "y", "active"}; //nombres de campos
        int [] to =  {R.id.txtid, R.id.txtx, R.id.txty, R.id.txtactive}; //una vista por campo

        adapter = new SimpleCursorAdapter(this, R.layout.view_registro, cursor, from, to);
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listItemClick);
    }

    private AdapterView.OnItemClickListener listItemClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), AddModDelBeaconBt.class);
            cursor.moveToPosition(position);
            intent.putExtra("name", cursor.getString(0));
            intent.putExtra("x", cursor.getString(1));
            intent.putExtra("y", cursor.getString(2));
            intent.putExtra("active", cursor.getInt(3) == 1 ? true : false);
            startActivityForResult(intent, MOD_DEL_BEACON);
        }
    };

    public void addBeacon(View view) {
        Intent intent = new Intent(this, AddModDelBeaconBt.class);
        intent.putExtra("name", "");
        intent.putExtra("x", "0");
        intent.putExtra("y", "0");
        intent.putExtra("active", false);
        startActivityForResult(intent, ADD_BEACON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            String name = data.getStringExtra("name");
            int x = data.getIntExtra("x", 0);
            int y = data.getIntExtra("y", 0);
            boolean active = data.getBooleanExtra("active", false);
            boolean del = data.getBooleanExtra("delete", false);
            String sql = "";
            switch (requestCode) {
                case ADD_BEACON:
                    //boolean: 0 (false) and 1 (true).
                    sql = String.format("insert into baliza values('%s', %d, %d, %d)",
                            name, x, y, active ? 1 : 0);
                    break;
                case MOD_DEL_BEACON:
                    if(del) {
                        sql = String.format("delete from baliza where name = '%s'", name);
                    } else {
                        sql = String.format("update baliza set x = %d, y = %d, active = %d where name = '%s'",
                                x, y, active ? 1 : 0, name);
                    }
                    break;
            }
            database.execSQL(sql);
            adapter.changeCursor(database.rawQuery("select name as _id, x, y, active from baliza", null));
            cursor = adapter.getCursor();
        }
    }
}
