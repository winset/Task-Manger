package com.example.michail.fragmentedc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TimeFragment extends android.support.v4.app.Fragment {
    private Time mTime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mContactButton;
    private ImageButton mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private String mPhoneNumber;
    private static final String ARG_TIME_ID = "time_Id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID timeId = (UUID) getArguments().getSerializable(ARG_TIME_ID);
        mTime = TimeLab.get(getActivity()).getTime(timeId);
        mPhotoFile = TimeLab.get(getActivity()).getPhotoFile(mTime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_time, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_task:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.delete_dialog_title).setMessage(R.string.delete_dialog_message);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = mTime.getId().toString();
                        TimeLab.get(getActivity()).deleteTask(id);
                        getActivity().finish();
                    }
                });
                alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TimeLab.get(getActivity()).updateTime(mTime);
    }

    public static TimeFragment newInstance(UUID timeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_ID, timeId);
        TimeFragment fragment = new TimeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time, container, false);
        mTitleField = v.findViewById(R.id.task_title);
        mTitleField.setText(mTime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton = v.findViewById(R.id.task_date);
        mTimeButton = v.findViewById(R.id.task_time);
        updateDate();
        updateTime();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment().newInstance(mTime.getDate());
                dialog.setTargetFragment(TimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = new TimePickerFragment().newInstanse(mTime.getClockTime());
                dialog.setTargetFragment(TimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });


        mSolvedCheckBox = v.findViewById(R.id.task_solved);
        mSolvedCheckBox.setChecked(mTime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTime.setSolved(isChecked);
            }
        });

        mReportButton = v.findViewById(R.id.task_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getTaskReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.task_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickerContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactButton = v.findViewById(R.id.task_contact);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickerContact, REQUEST_CONTACT);
            }
        });
        if (mTime.getContact() != null) {
            mContactButton.setText(mTime.getContact());
        }


        mCallButton = v.findViewById(R.id.task_contact_call);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri call = Uri.parse("tel:" + mPhoneNumber);
                Intent contactCall = new Intent(Intent.ACTION_CALL, call);
                startActivity(contactCall);
            }
        });


        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickerContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactButton.setEnabled(false);
        }

        mPhotoButton = v.findViewById(R.id.task_camera);
        mPhotoView = v.findViewById(R.id.contact_photo);
        updatePhotoView();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.michail.fragmentedc.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            mTime.setDate(date);
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);

            mTime.setClockTime(date);
            updateTime();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String contact = c.getString(0);
                String contactID = c.getString(1);
                mTime.setContact(contact);
                mContactButton.setText(contact);

                mCallButton.setEnabled(true);
                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);
                while (phones.moveToNext()) {
                    mPhoneNumber = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i("Number", mPhoneNumber);
                }

                phones.close();

            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.michail.fragmentedc.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    public void updateDate() {
        SimpleDateFormat sd = new SimpleDateFormat("EEEE");
        String dayofweek = sd.format(mTime.getDate());
        String dateString = DateFormat.getDateInstance().format(mTime.getDate());

        mDateButton.setText(dayofweek + "," + " " + dateString);
    }

    public void updateTime() {
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
        String clocktime = sd.format(mTime.getClockTime());

        mTimeButton.setText(clocktime);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }


    private String getTaskReport() {
        String solvedString = null;
        if (mTime.isSolved()) {
            solvedString = getString(R.string.task_report_solved);
        } else {
            solvedString = getString(R.string.task_report_unsolved);
        }


        SimpleDateFormat sd1 = new SimpleDateFormat("HH:mm");
        String clocktime = sd1.format(mTime.getClockTime());

        SimpleDateFormat sd = new SimpleDateFormat("EEEE");
        String dayofweek = sd.format(mTime.getDate());
        String date = DateFormat.getDateInstance().format(mTime.getDate());


        //String dateFormat = "EEE,MMM dd";
        String dateString = dayofweek + "," + " " + date + " " + clocktime;


        String contact = mTime.getContact();
        if (contact == null) {
            contact = getString(R.string.task_report_no_contact);
        } else {
            contact = getString(R.string.task_report_contact, contact);
        }

        String report = getString(R.string.task_report, mTime.getTitle(), dateString, solvedString, contact);

        return report;
    }
}
