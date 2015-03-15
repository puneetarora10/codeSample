package com.gotsigned.amazing1;


import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;


//
//  MainActivity.java
//  GotSigned
//
//  Created by Puneet Arora on 10/10/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

public class MainActivity extends Activity implements HomeFragment.OnFragmentInteractionListener, HashTagsFragment.OnFragmentInteractionListener, MeFragment.OnFragmentInteractionListener, UploadFragment.OnFragmentInteractionListener {

    // SectionsPagerAdapter
    SectionsPagerAdapter mSectionsPagerAdapter;
    // ActionBar
    ActionBar actionBar;

    // ViewPager
    ViewPager mViewPager;

    // searchActionBarMenuItem
    private MenuItem searchActionBarMenuItem;
    // searchTerm to be sent to HomeFragment
    private String searchTerm = "";
    // tabPosition
    private int tabPosition = 0;
    // loading progress dialog
    private ProgressDialog loadingProgressDialog;
    // SharedPreferences
    SharedPreferences sharedPreferences;
    // showChangePasswordActivity to be sent to MeFragment
    Boolean showChangePasswordActivity = false;
    // email to be sent ChangePasswordActivity
    String email;
    // flashMessage to be sent ChangePasswordActivity
    String flashMessage;

    //////////////////////////// to be sent to UploadStatusActivity ////////////////////////////////
    // fileToBeUploadedUri
    Uri fileToBeUploadedUri;
    // filePosterUri
    Uri filePosterUri;
    // file poster's extension
    String filePosterExtension;
    // fileToUpload's extension
    String fileToBeUploadedExtension;
    // fileToUpload's size should not be more than 150MB
    private static final int MAX_ATTACHMENT_SIZE = 150;
    String loggedInUsersEmail;
    String fileName;
    String fileDescription;
    String fileHashTags;
    String fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        actionBar = getActionBar();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
                //searchActionBarMenuItem.setVisible(false);
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // add tabs
        actionBar.addTab(actionBar.newTab().setText(R.string.home_tab).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.upload_tab).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.me_tab).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.hashTags_tab).setTabListener(tabListener));
        // set tabPosition
        actionBar.setSelectedNavigationItem(tabPosition);
        mViewPager.setCurrentItem(tabPosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchActionBarMenuItem = menu.findItem(R.id.action_search);
        searchActionBarMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // no need to do anything right now
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // no need to do anything right now
                return true;
            }
        });
        SearchView searchView = (SearchView) searchActionBarMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;
            if (position == 0) {
                fragment = HomeFragment.newInstance(searchTerm);
            } else if (position == 1) {
                fragment = UploadFragment.newInstance(HelperService.getDefaultInstance().checkIfUserIsLoggedIn(sharedPreferences),
                        HelperService.getDefaultInstance().checkIfLoggedInUserIsATalent(sharedPreferences),
                        HelperService.getDefaultInstance().checkIfLoggedInUserIsAnAdministrator(sharedPreferences),
                        HelperService.getDefaultInstance().returnLoggedInUsersEmail(sharedPreferences),
                        HelperService.getDefaultInstance().returnFileUploadFailed(sharedPreferences),
                        HelperService.getDefaultInstance().returnFileUploadInProgress(sharedPreferences),
                        HelperService.getDefaultInstance().returnFileUploadComplete(sharedPreferences));
            } else if (position == 2) {
                String profilePictureURLString, fullName, email;
                profilePictureURLString = sharedPreferences.getString("profilePictureURLString", "DEFAULT");
                fullName = sharedPreferences.getString("fullName", "DEFAULT");
                email = sharedPreferences.getString("email", "DEFAULT");
                fragment = MeFragment.newInstance(profilePictureURLString, fullName, email, showChangePasswordActivity);
            } else if (position == 3) {
                fragment = HashTagsFragment.newInstance();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    /**
     * HomeFragment.OnFragmentInteractionListener
     *
     * @param attachment
     */
    public void playAttachment(Attachment attachment) {
        Intent i = new Intent(MainActivity.this, PlayAttachmentActivity.class);
        i.putExtra("attachment", attachment);
        startActivity(i);
    }

    /**
     * HomeFragment.OnFragmentInteractionListener
     * updates Main Activity's title
     */
    public void updateMainActivityTitle() {
        updateMyTitle("");
    }

    /**
     * shows loading progress dialog
     * for all fragments
     */
    public void showLoadingProgressDialog() {
        loadingProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Loading ...", true);
    }

    /**
     * hides loading progress dialog
     * for all fragments
     */
    public void hideLoadingProgressDialog() {
        if (loadingProgressDialog != null) {
            loadingProgressDialog.dismiss();
        }
    }

    /**
     * HashTagsFragment.OnFragmentInteractionListener
     *
     * @param updatedSearchTerm
     */
    public void redirectToHomeFragment(String updatedSearchTerm) {
        searchTerm = updatedSearchTerm;
        HomeFragment.searchTerm = updatedSearchTerm;
        mViewPager.setCurrentItem(0);
        actionBar.setSelectedNavigationItem(0);
        // update title
        updateMyTitle(updatedSearchTerm);
    }

    /**
     * MeFragment.OnFragmentInteractionListener
     * shows LoginActivity
     */
    public void showLoginActivity() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    /**
     * MeFragment.OnFragmentInteractionListener
     * shows SignUpActivity
     */
    public void showSignUpActivity() {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    /**
     * MeFragment.OnFragmentInteractionListener
     * clears SharedPreferences
     */
    public void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    /**
     * MeFragment.OnFragmentInteractionListener
     * shows ChangePasswordActivity
     */
    public void showChangePasswordActivity() {
        Intent i = new Intent(MainActivity.this, ChangePasswordActivity.class);
        i.putExtra("flashMessage", flashMessage);
        i.putExtra("email", email);
        startActivity(i);
    }

    /**
     * UploadFragment.OnFragmentInteractionListener
     * shows showUploadStatusActivity
     */
    public void showUploadStatusActivity() {
        Intent i = new Intent(MainActivity.this, UploadStatusActivity.class);
        if (fileName != null) {
            i.putExtra("fileName", fileName);
        }
        startActivity(i);
    }

    /**
     * UploadFragment.OnFragmentInteractionListener
     * shows showUploadStatusActivity
     */
    public void showUploadStatusActivity(String _loggedInUsersEmail, String _fileName, String _fileDescription, String _fileHashTags, String _fileType) {
        loggedInUsersEmail = _loggedInUsersEmail;
        fileName = _fileName;
        fileDescription = _fileDescription;
        fileHashTags = _fileHashTags;
        fileType = _fileType;
        if (fileToBeUploadedUri == null) { //fileToBeUploaded has not been selected
            HelperService.getDefaultInstance().showAlertDialog("Error", "Choose File to be Uploaded", "OK", MainActivity.this);
        } else { // fileToBeUploaded has been selected
            // find size of audioOrVideoFile
            long audioOrVideoFileSizeInBytes = HelperService.getDefaultInstance().returnSizeOfFileInBytesWithMediaUri(getApplicationContext(), fileToBeUploadedUri);
            long audioOrVideoFileSizeInMB = HelperService.getDefaultInstance().returnSizeOfFileInMB(audioOrVideoFileSizeInBytes);
            // if audioOrVideoFileSizeInMB is greater than 150 MB show an alertDialog "File size should be less than 150 MB. Please reduce the file size and upload again. Thanks!"
            if (audioOrVideoFileSizeInMB > MAX_ATTACHMENT_SIZE) {
                HelperService.getDefaultInstance().showAlertDialog("Error", "Choose a smaller size File", "OK", MainActivity.this);
            } else {
                // check if required fields are empty or file has not been chosen
                if (fileName == null || fileName.isEmpty()) {// firstNameTextField is empty
                    HelperService.getDefaultInstance().showAlertDialog("Error", "File Name is Required", "OK", MainActivity.this);
                } else if (fileType == null || fileType.isEmpty()) {// fileTypeTextField is empty
                    HelperService.getDefaultInstance().showAlertDialog("Error", "Type is Required", "OK", MainActivity.this);
                } else { // start UploadStatusActivity which would make webService call to upload audio or video
                    Intent i = new Intent(MainActivity.this, UploadStatusActivity.class);
                    i.putExtra("loggedInUsersEmail", _loggedInUsersEmail);
                    i.putExtra("fileName", _fileName);
                    i.putExtra("fileDescription", _fileDescription);
                    i.putExtra("fileHashTags", _fileHashTags);
                    i.putExtra("fileType", _fileType);
                    i.putExtra("fileToBeUploadedUri", fileToBeUploadedUri);
                    i.putExtra("filePosterUri", filePosterUri);
                    i.putExtra("filePosterExtension", filePosterExtension);
                    i.putExtra("fileToBeUploadedExtension", fileToBeUploadedExtension);
                    i.putExtra("audioOrVideoFileSizeInBytes", audioOrVideoFileSizeInBytes);
                    startActivity(i);
                }
            }
        }
    }

    /**
     * UploadFragment.OnFragmentInteractionListener
     * redirectToMainActivityMeFragment
     */
    public void redirectToMainActivityMeFragment(String alertDialogsTitle, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(alertDialogsTitle);
        builder.setMessage(errorMessage);
        builder.setCancelable(true);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        actionBar.setSelectedNavigationItem(2);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // to be used for selecting file's poster
    private final int SELECT_PHOTO = 1;
    Button choosePosterForFileButton;
    String choosePosterForFileButtonText;

    /**
     * UploadFragment.OnFragmentInteractionListener
     * showImagePickerIntent
     */
    public void showImagePickerIntent(Button button, String buttonText) {
        choosePosterForFileButton = button;
        choosePosterForFileButtonText = buttonText;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    // to be used for selecting fileToBeUploaded
    private final int SELECT_AUDIOorVIDEO = 2;
    Button chooseFileToUploadButton;
    String chooseFileToUploadButtonText;

    /**
     * UploadFragment.OnFragmentInteractionListener
     * showAudioOrVideoPickerIntent
     */
    public void showAudioOrVideoPickerIntent(Button button, String buttonText, String type) {
        chooseFileToUploadButton = button;
        chooseFileToUploadButtonText = buttonText;
        Intent audioOrVideoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        audioOrVideoPickerIntent.setType(type + "/*");
        startActivityForResult(audioOrVideoPickerIntent, SELECT_AUDIOorVIDEO);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        filePosterUri = intent.getData();
                        choosePosterForFileButton.setText(choosePosterForFileButtonText);
                        // find filePosterExtension
                        String realMediaPath = HelperService.getDefaultInstance().returnRealPathOfMediaFromUri(getApplicationContext(), filePosterUri);
                        filePosterExtension = HelperService.getDefaultInstance().returnExtensionOfAFile(realMediaPath);
                    } catch (Exception e) {
                        // no need to do anything right now
                    }
                }
                break;
            case SELECT_AUDIOorVIDEO:
                if (resultCode == RESULT_OK) {
                    try {
                        fileToBeUploadedUri = intent.getData();
                        chooseFileToUploadButton.setText(chooseFileToUploadButtonText);
                        // find fileToBeUploadedExtension
                        String realMediaPath = HelperService.getDefaultInstance().returnRealPathOfMediaFromUri(getApplicationContext(), fileToBeUploadedUri);
                        fileToBeUploadedExtension = HelperService.getDefaultInstance().returnExtensionOfAFile(realMediaPath);
                    } catch (Exception e) {
                        // no need to do anything right now
                    }
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // get searchTerm
            searchTerm = intent.getStringExtra(SearchManager.QUERY);
            updateMyTitle(searchTerm);
        }
        // get tabPosition
        tabPosition = intent.getIntExtra("tabPosition", 0);
        // get showChangePasswordActivity
        showChangePasswordActivity = intent.getBooleanExtra("showChangePasswordActivity", false);
        if (showChangePasswordActivity) { // get email and flashMessage
            email = intent.getStringExtra("email");
            flashMessage = intent.getStringExtra("flashMessage");
        }
        // get sharedPreferences
        sharedPreferences = getSharedPreferences("com.gotsigned.amazing1", MODE_PRIVATE);
    }

    /**
     * updates activity's title
     *
     * @param appendToTitle
     */
    private void updateMyTitle(String appendToTitle) {
        if (!appendToTitle.toUpperCase().equals("#ALL")) { // don't append #all
            this.setTitle(getString(R.string.app_name) + " " + appendToTitle);
        }
    }
}
