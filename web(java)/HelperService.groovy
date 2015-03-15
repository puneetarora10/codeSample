/*
 * Contains helper methods (eg. Regex, convert to roman numbers etc...)
 * @(#)HelperService.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.servlet.ServletRequest

class HelperService {

    /**
     * gets used before creating an attachment on server/ database
     * @param stringToUpdate
     * @return updatedString with all spaces replaced by _
     */
    public String replaceSpacesWithUnderscores(String stringToUpdate) {
        String updatedString = stringToUpdate?.replaceAll("\\s", "_")

        return updatedString
    }

    /**
     * generic method to save an object
     * saves any object
     * @param gsObject
     */
    public void saveGotSignedObject(def gsObject) {
        // save attachment
        if (!gsObject.save(flush: true)) {
            log.error("-----> ${gsObject} with class ${gsObject.getClass()} not saved")
        } else {
            log.debug("-----> ${gsObject} with class ${gsObject.getClass()} saved/ updated")
        }
    }

    /**
     *
     * @param fileName eg. foo.mp3
     * @return extension of file eg. mp3
     */
    public String returnExtensionOfAFile(String fileName) {
        int mid = fileName.lastIndexOf(".")
        String extension = fileName.substring(mid + 1, fileName.length())

        return extension.toUpperCase()
    }

    /**
     * creates a file on the file server
     * @param file
     * @param fDescription
     * @return path of the file created
     */
    public String createFileOnServer(file, fDescription, Boolean poster, dirPathImage, dirPathMedia) {
        String dirPath = ""
        // check if poster then use image directory path else media
        if (poster) {
            dirPath = dirPathImage
        } else {
            dirPath = dirPathMedia
        }
        // check if directory exists
        File dir = new File(dirPath)
        if (!dir.exists()) {
            assert dir.mkdir()
        }
        // transfer the file to the directory
        file.transferTo(new File(dir, fDescription))

        return "${dirPath}/${fDescription}"
    }

    /*
    *  Determine browser information and return to user
    */

    static Map getBrowserInfo(ServletRequest request) {

        //code is derived from...
        //http://www.wthr.us/2010/02/03/simple-grails-browser-detection/


        def CHROME = "chrome"
        def FIREFOX = "firefox"
        def SAFARI = "safari"
        def OTHER = "other"
        def MSIE = "msie"
        def UNKNOWN = "unknown"
        def BLACKBERRY = "blackberry"
        def SEAMONKEY = "seamonkey"

        def CLIENT_CHROME = 0
        def CLIENT_FIREFOX = 1
        def CLIENT_SAFARI = 2
        def CLIENT_OTHER = 3
        def CLIENT_MSIE = 4
        def CLIENT_UNKNOWN = 5
        def CLIENT_BLACKBERRY = 6
        def CLIENT_SEAMONKEY = 7

        def userAgent = request.getHeader("user-agent")
        def agentInfo = [:]

        def browserVersion
        def browserType
        def browserName
        def operatingSystem

        def platform
        def security = "unknown"
        def language = "en-US"

        if (userAgent == null) {
            agentInfo.browserType = CLIENT_UNKNOWN
            return agentInfo
        }

        browserType = CLIENT_OTHER;

        int pos = -1;

        if ((pos = userAgent.indexOf("Firefox")) >= 0) {
            browserType = CLIENT_FIREFOX;
            browserName = FIREFOX;
            browserVersion = userAgent.substring(pos + 8).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
        } else if ((pos = userAgent.indexOf("Chrome")) >= 0) {
            browserType = CLIENT_CHROME;
            browserName = CHROME;
            browserVersion = userAgent.substring(pos + 7).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
        } else if ((pos = userAgent.indexOf("Safari")) >= 0 && (userAgent.indexOf("Chrome") == -1)) {
            browserType = CLIENT_SAFARI;
            browserName = SAFARI;
            browserVersion = userAgent.substring(pos + 7).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
        } else if ((pos = userAgent.indexOf("BlackBerry")) >= 0) {
            browserType = CLIENT_BLACKBERRY;
            browserName = BLACKBERRY;
            browserVersion = userAgent.substring(userAgent.indexOf("/")).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
        } else if ((pos = userAgent.indexOf("SeaMonkey")) >= 0) {
            browserType = CLIENT_SEAMONKEY;
            browserName = SEAMONKEY;
            browserVersion = userAgent.substring(userAgent.indexOf("/")).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
        } else if ((pos = userAgent.indexOf("MSIE")) >= 0) {
            browserType = CLIENT_MSIE;
            browserName = MSIE;
            browserVersion = userAgent.substring(pos + 5).trim();
            if (browserVersion.indexOf(" ") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(" "));
            }
            if (browserVersion.indexOf(";") > 0) {
                browserVersion = browserVersion.substring(0, browserVersion.indexOf(";"));
            }
        }

        // Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.63 Safari/534.3
        // Mozilla/5.0 (Macintosh; Intel Mac OS X 10.5; rv:2.0b6) Gecko/20100101 Firefox/4.0b6
        // Mozilla/5.0 (Windows NT 5.2; rv:2.0b5) Gecko/20100101 Firefox/4.0b5

        /**
         * @Todo: Review the following code, as this is pretty much non-standard as there is no real standard to
         */

        if (userAgent.indexOf("(") > 0) {
            String osInfo = userAgent.substring(userAgent.indexOf("(") + 1);
            osInfo = osInfo.substring(0, osInfo.indexOf(")"));

            String[] infoParts = osInfo.split("; ");
            if (infoParts.length >= 1) {
                platform = infoParts[0];
            }

            if (infoParts.length >= 3) {
                operatingSystem = infoParts[2];
            }


            if (browserType != CLIENT_MSIE) {
                if (infoParts.length >= 2) {
                    if (infoParts[1].equals("U"))
                        security = "strong";
                    if (infoParts[1].equals("I"))
                        security = "weak";
                    if (infoParts[1].equals("N"))
                        security = "none";
                }

                if (infoParts.length >= 4) {
                    language = infoParts[3];
                }
            }

        } else {
            if (browserType == CLIENT_BLACKBERRY) {
                operatingSystem = "BlackBerry " + browserVersion;
            }
        }

        agentInfo.browserVersion = browserVersion
        agentInfo.browserType = browserType
        agentInfo.browserName = browserName
        agentInfo.operatingSystem = operatingSystem
        agentInfo.platform = platform
        agentInfo.security = security
        agentInfo.language = language
        agentInfo.agentString = userAgent

        return agentInfo
    }

    /**
     *
     * @return extension to use for audio and media files
     */
    public Map returnExtensionOfMediaFileUsingBrowsersInfo(request) {
        Map mediaExtensionForBrowser = [:]
        def browserInfo = getBrowserInfo(request)
        String browserName = browserInfo.get('browserName')
        //
        if (browserName.contains("msie")) {
            // get browserVersion
            BigDecimal ieVersion = browserInfo.get('browserVersion')?.toString()?.toBigDecimal()
            if (ieVersion < 9.0) {
                mediaExtensionForBrowser.put("browserNotSupported", true)
            } else {
                mediaExtensionForBrowser.put("audio", "mp3")
                mediaExtensionForBrowser.put("video", "mp4")
                mediaExtensionForBrowser.put("flashMessage", "If you are not able to play the media file. Please Enable ActiveX Controls. Thanks!!")
            }
        } else if (browserName.contains("safari")) {
            mediaExtensionForBrowser.put("audio", "mp3")
            mediaExtensionForBrowser.put("video", "mp4")
        } else {
            mediaExtensionForBrowser.put("audio", "ogg")
            mediaExtensionForBrowser.put("video", "webm")
        }

        return mediaExtensionForBrowser
    }

    /**
     *
     * @return requestIsFromMobileOrTablet or not
     */
    public Boolean checkIfRequestFromMobileOrTablet(request) {
        Boolean requestIsFromMobileOrTablet = false
        // get browser's info
        def browserInfo = getBrowserInfo(request)
        String platform = browserInfo.get('platform')?.toString()?.toUpperCase()
        if (platform == 'IPHONE' || platform == 'ANDROID' || platform == 'IPAD') {
            requestIsFromMobileOrTablet = true
        }

        return requestIsFromMobileOrTablet
    }

    /**
     * replaces all characters which are neither an alphabet or a digit
     * @param stringToBeReplaced
     * @return replaced string which would have only alphabets and digits
     */
    public String replaceAllNonAlphaAndNonDigitsExceptDots(String stringToBeReplaced) {
        stringToBeReplaced = stringToBeReplaced.replaceAll("[^a-zA-Z0-9.]+", "")

        return stringToBeReplaced
    }

    /**
     * opens a file and appends data to it
     * @param filePath where file is
     * @param fileToBeAppendedToFile
     * @return true if data was appended to file else false
     */
    public Boolean openFileAndAppendFileToIt(String filePath, CommonsMultipartFile fileToBeAppendedToFile) {
        Boolean fileAppended = false
        // get reader and writer
        BufferedOutputStream writer = null
        BufferedInputStream reader = null
        // read from fileToBeAppendedToFile and write to file with attachmentPath
        try {
            String currentLine
            reader = new BufferedInputStream(fileToBeAppendedToFile?.getInputStream())
            writer = new BufferedOutputStream(new FileOutputStream(filePath, true))
            byte[] buffer = new byte[8192]
            for (int length = 0; (length = reader.read(buffer)) > 0;) {
                writer.write(buffer)
            }
            fileAppended = true
        }
        catch (Exception e) {
            log.error "----> exception is ${e}"
        }
        finally {
            if (reader) {
                reader.close()
            }
            if (writer) {
                writer.close()
            }
        }

        return fileAppended
    }

    /**
     * creates a file if the file doesn't already exist
     * @param filePath
     */
    public void createFile(filePath) {
        File file = new File(filePath)
        if (!filePath) {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))
            // close outputStream
            outputStream.close()
        }
    }
}
