package sean.com.ChorePolice.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import sean.com.ChorePolice.Chore;

import static sean.com.ChorePolice.database.ChoreDBSchema.ChoreTable;

/**
 * Created by Sean on 9/29/2015.
 */
public class ChoreCursorWrapper extends CursorWrapper {

    public ChoreCursorWrapper(Cursor cursor) {
        super(cursor);
    } //allows us to add new methods to the cursor

    public Chore getChore() {
        String uuidString = getString(getColumnIndex(ChoreTable.Cols.UUID));
        String title = getString(getColumnIndex(ChoreTable.Cols.TITLE));
        String date = getString(getColumnIndex(ChoreTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(ChoreTable.Cols.SOLVED));

        Chore chore = new Chore(UUID.fromString(uuidString));
        chore.setTitle(title);
        chore.setDate(date);
        chore.setSolved(isSolved != 0);

        return chore;

    }



}

