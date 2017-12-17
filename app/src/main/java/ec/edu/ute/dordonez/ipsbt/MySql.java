package ec.edu.ute.dordonez.ipsbt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySql extends SQLiteOpenHelper {

	public MySql(Context context) {
		super(context, "ipsbt.db", null, 1);
	}

	//https://www.sqlite.org/datatype3.html

	@Override
	public void onCreate(SQLiteDatabase database) {
		String sql = "create table baliza (name text primary key, x int, y int, active boolean);";
		database.execSQL(sql);//ejecuta la instruccion SQL en la base
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS baliza");
		onCreate(database);
	}

}
