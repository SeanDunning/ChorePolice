package sean.com.HousePolice;

import android.support.v4.app.Fragment;

/**
 * Created by Sean on 9/18/2015.
 */
public class ChoreListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ChoreListFragment();
    }
}
