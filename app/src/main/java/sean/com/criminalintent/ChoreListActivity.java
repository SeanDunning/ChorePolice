package sean.com.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Sean on 9/18/2015.
 */
public class ChoreListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ChoreListFragment();
    }
}
