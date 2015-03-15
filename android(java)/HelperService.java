package com.gotsigned.amazing1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
//  HelperService.java
//  GotSigned
//
//  Created by Puneet Arora on 10/5/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

public class HelperService {
    // global variables
    private final static String GENERIC_ERROR_MESSAGE = "Sorry, Slow Internet Connection on your device!!";
    private final static String FULL_NAME_KEY = "fullName";
    private final static String EMAIL_KEY = "email";
    private final static String PROFILE_PICTURE_URL_KEY = "profilePictureURLString";
    private final static String USER_SINCE_KEY = "userSince";
    private final static String USER_ROLE_KEY = "userRole";
    private final static String ATTACHMENTS_COUNT_KEY = "attachmentsCount";

    // returns a default instance of HelperService to implement singleton
    // in other words only one instance of HelperService object exists in the application
    private static HelperService helperService;

    public static HelperService getDefaultInstance() {
        if (helperService == null) {
            helperService = new HelperService();
        }
        return helperService;
    }

    // returns server's URL string
    public String returnServersURLString() {
        // localhost
        //return "http://192.168.1.5:8080/gotsigned";
        // webserver
        return "http://gotsigned.com/gotsigned";
    }

    // returns GENERIC_ERROR_MESSAGE
    public String returnGenericErrorMessage() {
        return GENERIC_ERROR_MESSAGE;
    }

    // appends generic errorMessage
    public void appendGenericErrorMessage(LinkedHashMap<String, String> appendGenericErrorMessageToIt) {
        appendGenericErrorMessageToIt.put("errorMessage", GENERIC_ERROR_MESSAGE);
    }


    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return SampleSize for
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * @param inputStream
     * @param reqWidth
     * @param reqHeight
     * @return Bitmap
     */
    public static Bitmap decodeSampledBitmapFromInputStream(InputStream inputStream, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Rect rect = new Rect(-1, -1, -1, -1);

        return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream, rect, options), reqWidth, reqHeight, true);
    }

    /**
     * show an alertDialog with a title, a message and a cancel button with buttonTitle
     *
     * @param title
     * @param message
     * @param cancelButtonTitle
     * @param showInActivity
     */
    public void showAlertDialog(String title, String message, String cancelButtonTitle, Activity showInActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(showInActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(cancelButtonTitle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * returns YES if the email address looks valid
     *
     * @param email
     * @return
     */
    public Boolean checkIfEmailIsValid(String email) {
        Boolean isValid = false;
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * saves users' data after user has successfully loggedIn in to SharedPreferences
     * usersData = {"profilePictureURLString":"", "fullName": "", "email": "", "userSince":"", "userRole":"", "attachmentsCounts":""}
     *
     * @param sharedPreferences
     * @param usersData
     */
    public void saveUsersData(SharedPreferences sharedPreferences, LinkedHashMap<String, String> usersData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : usersData.keySet()) {
            String value = usersData.get(key);
            editor.putString(key, value);
        }
        // Commit the edits!
        editor.apply();
    }

    /**
     * updates NSUserDefaults' profilePictureURLString
     *
     * @param sharedPreferences
     * @param profilePictureURLString
     */
    public void updateProfilePictureURLString(SharedPreferences sharedPreferences, String profilePictureURLString) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // update profilePictureURLString
        editor.putString(PROFILE_PICTURE_URL_KEY, profilePictureURLString);
        // Commit the edits!
        editor.apply();
    }


    /**
     * appends (key/value) to data in the format used by browsers to submit forms
     *
     * @param key
     * @param value
     * @param boundary
     * @param appendToIt
     */
    public void appendFormData(String key, String value, String boundary, DataOutputStream appendToIt) {
        if (value != null) {
            try {
                appendToIt.writeBytes("Content-Disposition: form-data; name=\"" + key + "\" \r\n");
                appendToIt.writeBytes("\r\n");
                appendToIt.writeBytes(value);
                appendToIt.writeBytes("\r\n");
                if (boundary != null) { // if boundary exists
                    appendToIt.writeBytes("--" + boundary + "\r\n");
                }
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }

    /**
     * appends file to data in the format used by browsers to submit forms
     *
     * @param key
     * @param fileName
     * @param contentType
     * @param fileToBeUploadedData
     * @param boundary
     * @param appendToIt
     */
    public void appendFormData(String key, String fileName, String contentType, byte[] fileToBeUploadedData, String boundary, DataOutputStream appendToIt) {
        if (fileToBeUploadedData != null) {
            try {
                appendToIt.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n");
                appendToIt.writeBytes("Content-Type: " + contentType + "\r\n\r\n");
                appendToIt.write(fileToBeUploadedData);
                appendToIt.writeBytes("\r\n");
                if (boundary != null) { // if boundary exists
                    appendToIt.writeBytes("--" + boundary + "\r\n");
                }
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }

    /**
     * @param sharedPreferences
     * @return true if user is logged in
     */
    public Boolean checkIfUserIsLoggedIn(SharedPreferences sharedPreferences) {
        Boolean userIsLoggedIn = false;
        // check if "fullName" key exists
        if (!sharedPreferences.getString(FULL_NAME_KEY, "DEFAULT").equals("DEFAULT")) {// userDetails are present
            userIsLoggedIn = true;
        }

        return userIsLoggedIn;
    }

    /**
     * @param sharedPreferences
     * @return email of logged in user using NSUserDefaults
     */
    public String returnLoggedInUsersEmail(SharedPreferences sharedPreferences) {
        // return email
        return sharedPreferences.getString(EMAIL_KEY, "DEFAULT");
    }

    /**
     * @param sharedPreferences
     * @return attachmentsCount of logged in user using NSUserDefaults
     */
    public String returnLoggedInUsersAttachmentsCount(SharedPreferences sharedPreferences) {
        // return attachmentsCount
        return sharedPreferences.getString(ATTACHMENTS_COUNT_KEY, "DEFAULT");
    }

    /**
     * @param sharedPreferences
     * @return true if logged in user is an "ARTIST" or in other terms has the userRole = "ARTIST"
     */
    public Boolean checkIfLoggedInUserIsAnArtist(SharedPreferences sharedPreferences) {
        Boolean isLoggedInUserAnArtist = false;

        if (sharedPreferences.getString(USER_ROLE_KEY, "DEFAULT").equals("ARTIST")) {// user's role is "ARTIST"
            isLoggedInUserAnArtist = true;
        }

        return isLoggedInUserAnArtist;
    }

    /**
     * @param sharedPreferences
     * @return true if logged in user is an "ADMINISTRATOR" or in other terms has the userRole = "ADMINISTRATOR"
     */
    public Boolean checkIfLoggedInUserIsAnAdministrator(SharedPreferences sharedPreferences) {
        Boolean isLoggedInUserAnAdministrator = false;

        if (sharedPreferences.getString(USER_ROLE_KEY, "DEFAULT").equals("ADMINISTRATOR")) {// user's role is "ADMINISTRATOR"
            isLoggedInUserAnAdministrator = true;
        }

        return isLoggedInUserAnAdministrator;
    }

    /**
     * @param sharedPreferences
     * @return true if logged in user is a "TALENT" or in other terms has the userRole = "ARTIST" or "WRITER" OR "PRODUCER"
     */
    public Boolean checkIfLoggedInUserIsATalent(SharedPreferences sharedPreferences) {
        Boolean isLoggedInUserATalent = false;

        // find userRole
        String userRole = sharedPreferences.getString(USER_ROLE_KEY, "DEFAULT");
        if (userRole.equals("ARTIST") || userRole.equals("WRITER") || userRole.equals("PRODUCER")) {// user's role is "ARTIST" or "WRITER" OR "PRODUCER"
            isLoggedInUserATalent = true;
        }

        return isLoggedInUserATalent;
    }

    /**
     * @param sharedPreferences
     * @return true if logged in user is an "AandR" or in other terms has the userRole = "AandR"
     */
    public Boolean checkIfLoggedInUserIsAnAandR(SharedPreferences sharedPreferences) {
        Boolean isLoggedInUserAnAandR = false;

        if (sharedPreferences.getString(USER_ROLE_KEY, "DEFAULT").equals("AANDR")) {// user's role is "AANDR"
            isLoggedInUserAnAandR = true;
        }

        return isLoggedInUserAnAandR;
    }

    /**
     * @param context
     * @param mediaUri
     * @return path of the file using mediaUri
     */
    public String returnRealPathOfMediaFromUri(Context context, Uri mediaUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        // cursorLoader
        CursorLoader cursorLoader = new CursorLoader(context, mediaUri, projection, null, null, null);
        // cursor
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    /**
     * returns size of file in MB
     *
     * @param context
     * @param mediaUri
     * @return
     */
    public long returnSizeOfFileInMBWithMediaUri(Context context, Uri mediaUri) {
        // create file
        File file = new File(returnRealPathOfMediaFromUri(context, mediaUri));
        long fileSizeInMB = 0;
        if (file.exists()) {// file exists
            // find length
            long fileSize = file.length();
            // in KB
            long fileSizeInKB = fileSize / 1024;
            // in MB
            fileSizeInMB = fileSizeInKB / 1024;
        }

        return fileSizeInMB;
    }

    /**
     * returns size of file in Bytes
     *
     * @param context
     * @param mediaUri
     * @return
     */
    public long returnSizeOfFileInBytesWithMediaUri(Context context, Uri mediaUri) {
        // create file
        File file = new File(returnRealPathOfMediaFromUri(context, mediaUri));
        long fileSizeInBytes = 0;
        if (file.exists()) {// file exists
            // find length
            fileSizeInBytes = file.length();
        }

        return fileSizeInBytes;
    }

    /**
     * @param fileSizeInBytes / 1024 /1024
     * @return
     */
    public long returnSizeOfFileInMB(long fileSizeInBytes) {
        long fileSizeInMB = 0;
        if (fileSizeInBytes > 0) {
            // in KB
            long fileSizeInKB = fileSizeInBytes / 1024;
            // in MB
            fileSizeInMB = fileSizeInKB / 1024;
        }

        return fileSizeInMB;
    }

    /**
     * @param fileName eg. foo.mp3
     * @return extension of file eg. mp3
     */
    public String returnExtensionOfAFile(String fileName) {
        int mid = fileName.lastIndexOf(".");
        String extension = fileName.substring(mid + 1, fileName.length());

        return extension.toUpperCase();
    }

    /**
     * saves fileUploadInProgress in to sharedPreferences
     *
     * @param sharedPreferences
     * @param fileUploadInProgress
     */
    public void saveFileUploadInProgress(SharedPreferences sharedPreferences, Boolean fileUploadInProgress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // update fileUploadInProgress
        editor.putBoolean("fileUploadInProgress", fileUploadInProgress);
        // Commit the edits!
        editor.apply();
    }

    /**
     * saves fileUploadFailed in to sharedPreferences
     *
     * @param sharedPreferences
     * @param fileUploadFailed
     */
    public void saveFileUploadFailed(SharedPreferences sharedPreferences, Boolean fileUploadFailed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // update fileUploadFailed
        editor.putBoolean("fileUploadFailed", fileUploadFailed);
        // Commit the edits!
        editor.apply();
    }

    /**
     * saves fileUploadComplete in to sharedPreferences
     *
     * @param sharedPreferences
     * @param fileUploadComplete
     */
    public void saveFileUploadComplete(SharedPreferences sharedPreferences, Boolean fileUploadComplete) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // update fileUploadComplete
        editor.putBoolean("fileUploadComplete", fileUploadComplete);
        // Commit the edits!
        editor.apply();
    }

    /**
     * @param sharedPreferences
     * @return fileUploadInProgress using sharedPreferences (default false)
     */
    public Boolean returnFileUploadInProgress(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("fileUploadInProgress", false);
    }

    /**
     * @param sharedPreferences
     * @return fileUploadFailed using sharedPreferences (default false)
     */
    public Boolean returnFileUploadFailed(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("fileUploadFailed", false);
    }

    /**
     * @param sharedPreferences
     * @return fileUploadComplete using sharedPreferences (default false)
     */
    public Boolean returnFileUploadComplete(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("fileUploadComplete", false);
    }

    /**
     * replaces \n (new line characters) in string with "string to replace with"
     *
     * @param stringToModify
     * @param stringToReplaceNewLineCharactersWith
     * @return
     */
    public String replaceNewLineCharacters(String stringToModify, String stringToReplaceNewLineCharactersWith) {
        return stringToModify.replaceAll("(\\r|\\n|\\r\\n)+", stringToReplaceNewLineCharactersWith);
    }
}
