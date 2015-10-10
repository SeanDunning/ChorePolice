package sean.com.ChorePolice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static sean.com.ChorePolice.database.ChoreDBSchema.ChoreTable;

/**
 * Created by Sean on 9/29/2015.
 */
public class ChoreBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "choreBase.db";

    public ChoreBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL("create table " + ChoreTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ChoreTable.Cols.UUID + ", " +
                ChoreTable.Cols.TITLE + ", " +
                ChoreTable.Cols.DATE + ", " +
                ChoreTable.Cols.SOLVED + "," +
                ChoreTable.Cols.SUSPECT +
                ")"
        );
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
