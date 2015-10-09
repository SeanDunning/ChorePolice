package sean.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Sean on 9/20/2015.
 */
public class ChorePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CHORE_ID =
            "com.criminalintent.chore_id"; //key of extra to be passed

    private ViewPager mViewPager;
    private List<Chore> mChores;

    public static Intent newIntent(Context packageContext, UUID choreId) {
        Intent intent = new Intent(packageContext, ChorePagerActivity.class); //new intent
        intent.putExtra(EXTRA_CHORE_ID, choreId);// put choreId as EXTRA into intent, return to caller (ChoreHolder from ChoreListFragment)
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_pager);

        UUID choreId = (UUID) getIntent().getSerializableExtra(EXTRA_CHORE_ID); //call its own extra, assign to choreId

        mViewPager = (ViewPager) findViewById(R.id.activity_chore_pager_view_pager); //Reference to ViewPager

        mChores = ChoreLab.get(this).getChores(); //grab chores from ChoreLab
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) { //configure PagerAdapter
            @Override
            public Fragment getItem(int position) { // get particular chore at position
                Chore chore = mChores.get(position);
                return ChoreFragment.newInstance(chore.getId()); //call new Instance of ChoreFragment, pass chore ID
            }

            @Override
            public int getCount() {
                return mChores.size();
            }
        });

        for (int i = 0; i < mChores.size(); i++) {
            if(mChores.get(i).getId().equals(choreId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }



}
