package sean.com.ChorePolice;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Sean on 9/15/2015.
 */
public class ChoreFragment extends Fragment {

    private static final String ARG_CHORE_ID = "chore_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Chore mChore;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;


    public static ChoreFragment newInstance(UUID choreId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHORE_ID, choreId);

        ChoreFragment fragment = new ChoreFragment();
        fragment.setArguments(args);
        return fragment;
    } //New ChoreFragment - called by ChoreActivity who passes an extra(serializable), which ChoreFragment
      //sets as Fragment argument.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UUID choreId = (UUID) getActivity().getIntent().getSerializableExtra(ChoreActivity.EXTRA_CHORE_ID);
        //create UUID variable, get host activity's intent, get its serializable extra, assign it to variable

        UUID choreId = (UUID) getArguments().getSerializable(ARG_CHORE_ID);
        //Encapsulated way of accessing EXTRA. Get EXTRA without knowing how host activity operates.
        setHasOptionsMenu(true);
        mChore = ChoreLab.get(getActivity()).getChore(choreId); //use new variable to find chore from choreLab
    }

    @Override
    public void onPause() {
        super.onPause();

        ChoreLab.get(getActivity()).updateChore(mChore);
    }
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_chore, menu);

    } //inflater populates menu with items defined in fragment_chore_list

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_chore:
                ChoreLab.get(getActivity()).deleteChore(mChore);
                getActivity().finish();
                return true; //add new chore selected
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chore, container, false);

        mTitleField = (EditText) v.findViewById(R.id.chore_title);
        mTitleField.setText(mChore.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //blank intentionally
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChore.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //blank intentionally
            }
        });

        mDateButton = (Button) v.findViewById(R.id.chore_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Date tempDate = new Date();
                DatePickerFragment dialog = DatePickerFragment.newInstance(tempDate/*mChore.getDate()*/);
                //call DatePickerFragment new Instance, supply chores date.
                dialog.setTargetFragment(ChoreFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.chore_solved);
        mSolvedCheckBox.setChecked(mChore.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChore.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.chore_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getChoreReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.chore_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);

            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.chore_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mChore.getSuspect() != null) {
            mSuspectButton.setText(mChore.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        } //case for no contacts app

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {

            Date tempDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String date = DateFormat.getDateTimeInstance().format(tempDate);
            mChore.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) { //result back from contacts intent
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                } //checks for results

                c.moveToFirst(); //info is at first column of row 0
                String suspect = c.getString(0);
                mChore.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mChore.getDate().toString());
    }

    private String getChoreReport() {
        String solvedString = null;
        if (mChore.isSolved()) {
            solvedString = getString(R.string.chore_report_solved);
        } else {
            solvedString = getString(R.string.chore_report_unsolved);
        }

        //String dateFormat = "EEE, MMM dd";
        //String dateString = DateFormat.format(dateFormat, mChore.getDate()).toString();
        String dateString = mChore.getDate();

        String suspect = mChore.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.chore_report_no_suspect);
        } else {
            suspect = getString(R.string.chore_report_suspect, suspect);
        }

        String report = getString(R.string.chore_report,
                mChore.getTitle(), dateString, solvedString, suspect);

        return report;


    }
}
