/*
 * ALL admin related methods
 * @(#)AdminService.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class AdminService {
    // include beans
    HelperService helperService

    /**
     * sets approved flag to true
     * @param attachment
     */
    public void approveAttachment(Attachment attachment) {
        // set approved flag to true
        attachment.approved = true
        // save attachment
        helperService.saveGotSignedObject(attachment)
    }

    /**
     * uploads media/ image file on to the server
     * NOTE - attachment object is not created as it has already been created when the user had uploaded the attachment
     * @param request
     * @param params
     * @param dirPathImage
     * @param dirPathMedia
     * @param poster
     */
    public void uploadAttachment(request, params, dirPathImage, dirPathMedia, Boolean poster) {
        Map messages = [:]
        // get the attachment
        def file = request.getFile('fileToBeUploaded')
        // get media file's original name
        String fDescription = file?.getOriginalFilename()

        createFileOnServer(file, fDescription, poster, dirPathImage, dirPathMedia)
    }

    /**
     * create a file on the file server
     * @param file
     * @param fDescription
     * @return path of the file created
     */
    private void createFileOnServer(file, fDescription, Boolean poster, dirPathImage, dirPathMedia) {
        String dirPath = ""
        // check if poster then use image directory path else media
        if (poster) {
            dirPath = dirPathImage
        }
        else {
            dirPath = dirPathMedia
        }
        // check if directory exists
        File dir = new File(dirPath)
        if (!dir.exists()) {
            assert dir.mkdir()
        }
        // transfer the file to the directory
        file.transferTo(new File(dir, fDescription))
    }
}