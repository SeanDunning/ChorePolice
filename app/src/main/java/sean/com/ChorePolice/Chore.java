package sean.com.ChorePolice;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Sean on 9/15/2015.
 */
public class Chore {

    private UUID mId;
    private String mTitle;
    private String mDate;
    private boolean mSolved;
    private String mSuspect;



    public Chore() {
        //Generate unique ID
        this(UUID.randomUUID());
    }
    public Chore(UUID id) {
        mId = id;
        String dateFormatted = DateFormat.getDateTimeInstance().format(new Date());
        mDate = dateFormatted;
    } //called by ChoreCursorWrapper

    public String getDate() {
        return mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setDate(String date) {
        //String dateFormatted = DateFormat.getDateTimeInstance().format(new Date());

        mDate = date;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
