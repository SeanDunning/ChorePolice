package sean.com.HousePolice.database;

/**
 * Created by Sean on 9/29/2015.
 */
public class ChoreDBSchema {

    public static final class ChoreTable {
        public static final String NAME = "chores";
        //describe a safe way of accessing name

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESC = "description";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";

        } //describe moving pieces of the database
    }
}
