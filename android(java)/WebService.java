package com.gotsigned.amazing1;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

//
//  WebService.java
//  GotSigned
//
//  Created by Puneet Arora on 10/10/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//

public class WebService {
    // returns a default instance of WebService to implement singleton
    // in other words only one instance of WebService object exists in the application
    private static WebService webService;

    public static WebService getDefaultInstance() {
        if (webService == null) {
            webService = new WebService();
        }
        return webService;
    }

    /**
     * web service call to download attachmentsData
     *
     * @param searchTerm
     * @return dataToBeReturned
     * @throws IOException
     */
    public static Object returnAttachmentsData(String searchTerm) throws IOException {
        Object dataToBeReturned = null;
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/returnAttachmentsDataAsJSON";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            if (searchTerm != null && !searchTerm.equals("#all")) {// check if searchTerm exists and not equal to "all"
                // create params to be send in POST request
                String params = "searchTerm=" + searchTerm;
                outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                outputStream.writeBytes(params);
            }
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    if (((JSONObject) dataReturnedFromServer).getString("errorMessage") != null) {
                        LinkedHashMap<String, String> _dataToBeReturned = new LinkedHashMap<String, String>();
                        _dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                        dataToBeReturned = _dataToBeReturned;
                    }
                } else if (dataReturnedFromServer instanceof JSONArray) {
                    dataToBeReturned = dataReturnedFromServer;
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    dataToBeReturned = new LinkedHashMap<String, String>();
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            dataToBeReturned = new LinkedHashMap<String, String>();
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        
        return dataToBeReturned;
    }

    /**
     * web service call to get trending hashTags
     *
     * @return dataToBeReturned
     */
    public LinkedHashMap returnTrendingHashTags() {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/returnTrendingHashTags";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONArray) {
                    dataToBeReturned.put("hashTags", (JSONArray) dataReturnedFromServer);
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to download attachmentThumbnail using attachmentThumbnailURLString
     *
     * @param attachmentThumbnailURL
     * @return dataToBeReturned
     * @throws IOException
     */
    public static Bitmap returnAttachmentsThumbnail(URL attachmentThumbnailURL) throws IOException {
        Bitmap attachmentThumbnail = null;
        HttpURLConnection httpURLConnection = null;
        try {
            //Log.d("WebService","attachmentThumbnailURL is"+attachmentThumbnailURL);
            httpURLConnection = (HttpURLConnection) attachmentThumbnailURL.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                attachmentThumbnail = HelperService.decodeSampledBitmapFromInputStream(httpURLConnection.getInputStream(), 100, 70);
            }
        } catch (Exception e) {
            // no need to do anything right now
        } catch (OutOfMemoryError error) {// call garbage collector
            System.gc();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return attachmentThumbnail;
    }

    /**
     * web service call to update numberOfViews on Attachment with id=aid
     *
     * @param aid
     * @throws IOException
     * @throws MalformedURLException
     */
    public void updateNumberOfViewsForAttachmentWithId(String aid) throws IOException {
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/updateNumberOfViews";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // create params to be send in POST request
            httpURLConnection.setRequestMethod("POST");
            // no need to set timeout interval as this web service call is made on a different thread
            //httpURLConnection.setConnectTimeout(15000);
            String params = "aid=" + aid;
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // no need to do anything right now
                // needed to make the request
            }
        } catch (Exception e) {
            // no need to do anything right now
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * make a web service call to check if user exists in the system
     * if yes userDetails would be returned by the server
     * else errorMessage
     *
     * @param email
     * @param password
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap authenticateUser(String email, String password) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/authenticateUser";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "inputEmail=" + email;
            params += "&inputPassword=" + password;
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("attachmentsCount", (String) ((JSONObject) dataReturnedFromServer).getString("attachmentsCount"));
                        dataToBeReturned.put("profilePictureURLString", (String) ((JSONObject) dataReturnedFromServer).getString("profilePictureURLString"));
                        dataToBeReturned.put("userRole", (String) ((JSONObject) dataReturnedFromServer).getString("userRole"));
                        dataToBeReturned.put("fullName", (String) ((JSONObject) dataReturnedFromServer).getString("fullName"));
                        dataToBeReturned.put("userSince", (String) ((JSONObject) dataReturnedFromServer).getString("userSince"));
                        dataToBeReturned.put("email", (String) ((JSONObject) dataReturnedFromServer).getString("email"));
                    }
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call when user has forgotten his password and clicks on "Forgot Password?"
     *
     * @param email
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap forgotPasswordForEmail(String email) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/forgotPassword";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "username=" + email;
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("flashMessage", (String) ((JSONObject) dataReturnedFromServer).getString("flashMessage"));
                    }
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to download user's profilePicture using profilePictureURL
     *
     * @param profilePictureURL
     * @return profilePicture
     * @throws IOException
     */
    public Bitmap returnUsersProfilePicture(URL profilePictureURL) throws IOException {
        Bitmap profilePicture = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) profilePictureURL.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                profilePicture = HelperService.decodeSampledBitmapFromInputStream(httpURLConnection.getInputStream(), 150, 150);
            }
        } catch (Exception e) {
            // no need to do anything right now
        } catch (OutOfMemoryError error) {
            // call garbage collector
            System.gc();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return profilePicture;
    }

    /**
     * creates user
     * if any of the required textFields are empty then appends "errorMessage" to dataToBeReturned
     * else makes a web service call and if user gets created sets userCreated to YES in dataToBeReturned
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param roleOrDescription
     * @param referredBy
     * @param profilePictureExtension
     * @return dataToBeReturned
     */
    public LinkedHashMap createUser(String firstName, String lastName, String email, String roleOrDescription, String referredBy, String profilePictureExtension) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/createUser";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "firstName=" + firstName;
            params += "&lastName=" + lastName;
            params += "&email=" + email;
            params += "&userRole=" + roleOrDescription;
            params += "&referredBy=" + referredBy;
            // if profilePictureExtension exists append it to params
            if (profilePictureExtension != null) {
                params += "&profilePictureExtension=" + profilePictureExtension;
            }
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("flashMessage", (String) ((JSONObject) dataReturnedFromServer).getString("flashMessage"));
                        dataToBeReturned.put("userCreated", "YES");
                    }
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * changes user's password
     * if any of the required textFields are empty then appends "errorMessage" to dataToBeReturned
     * else makes a web service call and if user gets created sets passwordChanged to YES in dataToBeReturned
     *
     * @param email
     * @param password
     * @param temporaryPassword
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap changePasswordForUser(String email, String password, String temporaryPassword) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/changePassword";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "username=" + email;
            params += "&newPassword=" + password;
            params += "&temporaryPassword=" + temporaryPassword;

            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("passwordChanged", "YES");
                    }
                } else {// attachment's data couldn't be downloaded // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * updates users' profile picture
     *
     * @param username
     * @param profilePictureData
     * @param profilePictureExtension
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap updateUsersProfilePicture(String username, byte[] profilePictureData, String profilePictureExtension) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/updateUsersProfilePicture?username=" + username + "&profilePictureExtension=" + profilePictureExtension;
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // post
            httpURLConnection.setRequestMethod("POST");
            // no need
            //httpURLConnection.setConnectTimeout(15000);

            // set Content-Type in HTTP header
            // some random number
            String boundary = "56738788";
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // open output stream
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            // add image data
            if (profilePictureData != null) {
                outputStream.writeBytes("--" + boundary + "\r\n");
                // append profilePictureData
                HelperService.getDefaultInstance().appendFormData("fileProfilePicture", "profilePicture.jpg", "image/jpeg", profilePictureData, null, outputStream);
            }
            outputStream.writeBytes("--" + boundary + "--\r\n");

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("profilePicturePath", (String) ((JSONObject) dataReturnedFromServer).getString("profilePicturePath"));
                    }
                } else { // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }


    /**
     * web service call to upload an attachment (audio or video file)
     * fileNameOnSystem (in other words name which would be used to create the file on the system)
     * and fileNameOnSystem_Poster would be used for poster's system name (in other words name which would be used to create the file's poster on the system)
     *
     * @param email
     * @param fileName
     * @param fileDescription
     * @param filePosterData
     * @param filePosterURL
     * @param fileToBeUploadedData
     * @param fileNameOnSystem
     * @param filePosterExtension
     * @param fileToBeUploadedExtension
     * @param fileToBeUploadedType
     * @param fileHashTags
     * @return
     * @throws IOException
     */
    public LinkedHashMap uploadAttachment(String email, String fileName, String fileDescription, byte[] filePosterData, String filePosterURL, byte[] fileToBeUploadedData, String fileNameOnSystem, String filePosterExtension, String fileToBeUploadedExtension, String fileToBeUploadedType, String fileHashTags) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/uploadAttachment";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // post
            httpURLConnection.setRequestMethod("POST");
            // no need
            //httpURLConnection.setConnectTimeout(15000);

            // set Content-Type in HTTP header
            // some random number
            String boundary = "56738788";
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // open output stream
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            outputStream.writeBytes("--" + boundary + "\r\n");

            // append filePoster
            HelperService.getDefaultInstance().appendFormData("filePoster", fileNameOnSystem + "_Poster." + filePosterExtension, "image/" + filePosterExtension, filePosterData, boundary, outputStream);

            // append filePosterURL
            HelperService.getDefaultInstance().appendFormData("filePosterURL", filePosterURL, boundary, outputStream);

            //append fileToBeUploaded or attachment to be uploaded on the server
            HelperService.getDefaultInstance().appendFormData("fileToBeUploaded", fileNameOnSystem + "." + fileToBeUploadedExtension, fileToBeUploadedType + "/" + fileToBeUploadedExtension, fileToBeUploadedData, boundary, outputStream);

            // append fileName
            HelperService.getDefaultInstance().appendFormData("fileName", fileName, boundary, outputStream);

            // append fileDescription
            HelperService.getDefaultInstance().appendFormData("fileDescription", fileDescription, boundary, outputStream);

            // append fileHashTags or hashTags
            HelperService.getDefaultInstance().appendFormData("hashTags", fileHashTags, boundary, outputStream);

            // append username or email
            HelperService.getDefaultInstance().appendFormData("username", email, boundary, outputStream);

            // append attachmentType
            HelperService.getDefaultInstance().appendFormData("attachmentType", fileToBeUploadedType, null, outputStream);

            // end boundary
            outputStream.writeBytes("--" + boundary + "--\r\n");

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else { // file has been uploaded
                        dataToBeReturned.put("flashMessage", "Thanks! We will send an email when your media file approved!");
                        dataToBeReturned.put("attachmentId", (String) ((JSONObject) dataReturnedFromServer).getString("attachmentId"));
                    }
                } else {// append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to append data to attachment's poster
     *
     * @param attachmentId
     * @param filePosterData
     * @param filePosterExtension
     * @param fileNameOnSystem
     * @return
     * @throws IOException
     */
    public LinkedHashMap appendAttachmentPosterData(String attachmentId, byte[] filePosterData, String filePosterExtension, String fileNameOnSystem) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/appendDataToAttachmentsThumbnail";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // post
            httpURLConnection.setRequestMethod("POST");
            // no need
            //httpURLConnection.setConnectTimeout(15000);

            // set Content-Type in HTTP header
            // some random number
            String boundary = "56738788";
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // open output stream
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            outputStream.writeBytes("--" + boundary + "\r\n");

            // append chunk of filePoster to be uploaded
            HelperService.getDefaultInstance().appendFormData("dataToBeAppendedToThumbnail", fileNameOnSystem + "_Poster." + filePosterExtension, "image/" + filePosterExtension, filePosterData, boundary, outputStream);

            // append attachmentId
            HelperService.getDefaultInstance().appendFormData("attachmentId", attachmentId, boundary, outputStream);

            // append filePosterExtension
            HelperService.getDefaultInstance().appendFormData("filePosterExtension", filePosterExtension, null, outputStream);

            // end boundary
            outputStream.writeBytes("--" + boundary + "--\r\n");


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) { // append generic
                        HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                    } else { // profilePicture has been updated
                        // check if dataReturnedFromServer contains a profilePicturePath
                        String profilePicturePath = ((JSONObject) dataReturnedFromServer).optString("profilePicturePath");
                        if (!profilePicturePath.isEmpty()) {
                            dataToBeReturned.put("profilePicturePath", (String) ((JSONObject) dataReturnedFromServer).getString("profilePicturePath"));
                        }
                    }
                } else {// append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to append data to attachment
     *
     * @param attachmentId
     * @param fileNameOnSystem
     * @param fileData
     * @param fileToBeUploadedExtension
     * @param fileToBeUploadedType
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap appendDataToAttachment(String attachmentId, String fileNameOnSystem, byte[] fileData, String fileToBeUploadedExtension, String fileToBeUploadedType) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/appendDataToAttachment";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // post
            httpURLConnection.setRequestMethod("POST");
            // no need
            //httpURLConnection.setConnectTimeout(15000);

            // set Content-Type in HTTP header
            // some random number
            String boundary = "56738788";
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // open output stream
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            outputStream.writeBytes("--" + boundary + "\r\n");

            //append fileData or attachment's chunks of data to be uploaded to be file on the server
            HelperService.getDefaultInstance().appendFormData("dataToBeAppendedToFile", fileNameOnSystem + "." + fileToBeUploadedExtension, fileToBeUploadedType + "/" + fileToBeUploadedExtension, fileData, boundary, outputStream);

            // append attachmentId
            HelperService.getDefaultInstance().appendFormData("attachmentId", attachmentId, null, outputStream);

            // end boundary
            outputStream.writeBytes("--" + boundary + "--\r\n");


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) { // append generic
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    }
                } else {// append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to get already authenticated user's properties
     *
     * @param email
     * @return dataToBeReturned
     * @throws IOException
     */
    public LinkedHashMap returnPropertiesOfAuthenticatedUser(String email) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/returnAuthenticatedUsersProperties";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // make a post request to get already authenticated user's properties
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds timeout so that the request should be finished in 15 seconds (successfully or unsuccessfully)
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "username=" + email;
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        dataToBeReturned.put("attachmentsCount", (String) ((JSONObject) dataReturnedFromServer).getString("attachmentsCount"));
                        dataToBeReturned.put("profilePictureURLString", (String) ((JSONObject) dataReturnedFromServer).getString("profilePictureURLString"));
                        dataToBeReturned.put("userRole", (String) ((JSONObject) dataReturnedFromServer).getString("userRole"));
                        dataToBeReturned.put("fullName", (String) ((JSONObject) dataReturnedFromServer).getString("fullName"));
                        dataToBeReturned.put("userSince", (String) ((JSONObject) dataReturnedFromServer).getString("userSince"));
                        dataToBeReturned.put("email", (String) ((JSONObject) dataReturnedFromServer).getString("email"));
                    }
                } else { // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }

    /**
     * web service call to send message to talent or in other words create conversation
     *
     * @param messageText
     * @param receiver
     * @param sender
     * @return
     * @throws IOException
     */
    public LinkedHashMap sendMessageToTalent(String messageText, String receiver, String sender) throws IOException {
        // data to be returned
        LinkedHashMap dataToBeReturned = new LinkedHashMap();
        String urlString = HelperService.getDefaultInstance().returnServersURLString() + "/mobileOrTablet/createConversation";
        HttpURLConnection httpURLConnection = null;
        DataOutputStream outputStream = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // make a post request to get already authenticated user's properties
            httpURLConnection.setRequestMethod("POST");
            // 15 seconds timeout so that the request should be finished in 15 seconds (successfully or unsuccessfully)
            httpURLConnection.setConnectTimeout(15000);
            // create params to be send in POST request
            String params = "messageText=" + messageText;
            params += "&receiver=" + receiver;
            params += "&sender=" + sender;
            outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            outputStream.writeBytes(params);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()), 8192);
                String strLine = null;
                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
                Object dataReturnedFromServer = new JSONTokener(response.toString()).nextValue();

                if (dataReturnedFromServer instanceof JSONObject) {
                    // check if dataReturnedFromServer contains an errorMessage
                    String errorMessage = ((JSONObject) dataReturnedFromServer).optString("errorMessage");
                    if (!errorMessage.isEmpty()) {
                        dataToBeReturned.put("errorMessage", (String) ((JSONObject) dataReturnedFromServer).getString("errorMessage"));
                    } else {
                        String flashMessage = ((JSONObject) dataReturnedFromServer).optString("flashMessage");
                        if (!flashMessage.isEmpty()) {
                            dataToBeReturned.put("flashMessage", (String) ((JSONObject) dataReturnedFromServer).getString("flashMessage"));
                        }
                    }
                } else { // append generic errorMessage
                    HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
                }
            }
        } catch (Exception e) {
            // append generic errorMessage
            HelperService.getDefaultInstance().appendGenericErrorMessage((LinkedHashMap<String, String>) dataToBeReturned);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return dataToBeReturned;
    }
}
