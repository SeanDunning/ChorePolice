package sean.com.ChorePolice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sean on 9/18/2015.
 */
public class ChoreListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mChoreRecyclerView;
    private ChoreAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    } //tell fragmentManager that ChoreListFragment needs to receive menu callbacks

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chore_list, container, false);

        mChoreRecyclerView = (RecyclerView) view.findViewById(R.id.chore_recycler_view);
        mChoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_chore_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }//check if show subtitle selected, display accordingly
    } //inflater populates menu with items defined in fragment_chore_list

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_chore:
                Chore chore = new Chore();
                ChoreLab.get(getActivity()).addChore(chore);
                Intent intent = ChorePagerActivity.newIntent(getActivity(), chore.getId());
                startActivity(intent);
                return true; //add new chore selected
            //case R.id.menu_item_delete_chore:
            //    ChoreLab.get(getActivity()).deleteChore(chore);
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true; //show or hide subtitle selected
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        ChoreLab choreLab = ChoreLab.get(getActivity());
        int choreCount = choreLab.getChores().size();
        String subtitle = getString(R.string.subtitle_format, choreCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        ChoreLab choreLab = ChoreLab.get(getActivity());
        List<Chore> chores = choreLab.getChores(); //grabbing list of chores from ChoreLab
        Context context = this.getContext();
        mChoreRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getContext()
        )); //add divider between list items
        if (mAdapter == null) {
            mAdapter = new ChoreAdapter(chores, context);
            mChoreRecyclerView.setAdapter(mAdapter); //making ChoreAdapter
        } else {
            mAdapter.setChores(chores);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }



    private class ChoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckbox;
        private Chore mChore;

        public ChoreHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_chore_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_chore_date_text_view);
            mSolvedCheckbox = (CheckBox) itemView.findViewById(R.id.list_item_chore_solved_check_box);

        } //ChoreHolder - find the textViews and CheckBox for list

        @Override
        public void onClick(View v) {
            Intent intent = ChorePagerActivity.newIntent(getActivity(), mChore.getId());
            //getActivity gets the hosting activity of this fragment (ChoreListActivity),
            // passes host activity and chores ID to new Intent in ChoreActivity
            startActivity(intent); //start ChoreActivity on click
        } //ChoreHolder onClick

        public void bindChore(Chore chore) {

            mChore = chore;
            mTitleTextView.setText(mChore.getTitle());
            //String date = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date());
            //String date = DateFormat.getDateTimeInstance().format(new Date());
            //mDateTextView.setText(date);
            mDateTextView.setText(mChore.getDate());
            mSolvedCheckbox.setChecked(mChore.isSolved());
        } //bindChore - set list item values correctly; called by ChoreAdapter

    }//ChoreHolder - holds view for chore details (2 textViews, 1 checkBox)

    private class ChoreAdapter extends RecyclerView.Adapter<ChoreHolder> {
        private List<Chore> mChores;
        private int lastPosition = -1;
        private Context context;
        public ChoreAdapter(List<Chore> chores, Context context) {
            mChores = chores;
            this.context = context;
        }

        @Override
        public ChoreHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_chore, parent, false);
            return new ChoreHolder(view);
        }

        @Override
        public void onBindViewHolder(ChoreHolder holder, int position) {
            Chore chore = mChores.get(position);
            holder.bindChore(chore);
            //call setAnimation for title and date (slide in from the left)
            setAnimation(holder.mTitleTextView, position);
            lastPosition = -1;
            setAnimation(holder.mDateTextView, position);


        }
      // public void unbindView(int position) {
       //    mChores.remove(position);
       //    mAdapter.notifyDataSetChanged();
      // }
        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition){
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
        @Override
        public int getItemCount() {
            return mChores.size();
        }

        public void setChores(List<Chore> chores) {
            mChores = chores;
        }


    }//ChoreAdapter - make ChoreHolders to hold the views, give chore's values to holder; call bindChore to set proper values
}
