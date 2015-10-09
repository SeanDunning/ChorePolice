package sean.com.ChorePolice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import sean.com.ChorePolice.database.ChoreBaseHelper;
import sean.com.ChorePolice.database.ChoreCursorWrapper;

import static sean.com.ChorePolice.database.ChoreDBSchema.ChoreTable;

/**
 * Created by Sean on 9/18/2015.
 */
public class ChoreLab {
    private static final String DATABASE_NAME = "choreBase.db";

    private static ChoreLab sChoreLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ChoreLab get(Context context) {
        if (sChoreLab == null) {
            sChoreLab = new ChoreLab(context);
        }
        return sChoreLab;
    }

    private ChoreLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ChoreBaseHelper(mContext).getWritableDatabase();
        //create new db if doesnt exist; if first time created, calls onCreate, saves out latest version
        //If not first time, check version number, if higher, call onUpgrade

    }

    public void addChore(Chore c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(ChoreTable.NAME, null, values);

    }
    public void deleteChore(Chore c) {

        //mDatabase.delete(ChoreTable.NAME, ChoreTable.Cols.UUID + "="+c.getId(),null);
         mDatabase.delete(ChoreTable.NAME, ChoreTable.Cols.UUID + "=?", new String []{ c.getId().toString()});
    }

    public List<Chore> getChores() {
        List<Chore> chores = new ArrayList<>();

        ChoreCursorWrapper cursor = queryChores(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                chores.add(cursor.getChore());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return chores;
    }

    public Chore getChore(UUID id) {

        ChoreCursorWrapper cursor = queryChores(ChoreTable.Cols.UUID + " = ?",
                new String[] {id.toString() }
        );

        try {
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getChore();
        }finally {
            cursor.close();
        }
    }

    public void updateChore(Chore chore) {
        String uuidString = chore.getId().toString();
        ContentValues values = getContentValues(chore);

        mDatabase.update(ChoreTable.NAME, values, ChoreTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Chore chore) {
        ContentValues values = new ContentValues();
        values.put(ChoreTable.Cols.UUID, chore.getId().toString());
        values.put(ChoreTable.Cols.TITLE, chore.getTitle());
        values.put(ChoreTable.Cols.DATE, chore.getDate()/*.getTime()*/);
        values.put(ChoreTable.Cols.SOLVED, chore.isSolved() ? 1 : 0);

        return values;

    }

    private ChoreCursorWrapper queryChores(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ChoreTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy

        );
        return new ChoreCursorWrapper(cursor);
    }
}
