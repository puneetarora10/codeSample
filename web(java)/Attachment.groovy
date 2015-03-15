/*
 * @(#)Attachment.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package gotsigned

class Attachment {
    /**
     * file's name
     */
    String name

    /**
     * description
     */
    String description

    /**
     * approved
     * whenever a user uploads a video - admin would review it and approve or delete the video
     */
    Boolean approved = false

    /**
     * flag to check if user has deleted the video
     * if true then attachment should be shown on dashboards
     * NOTE - If user deletes the video then it has to be removed from UI that is set deleted flag to true
     * but if admin deleted the video then it has to be deleted from the database
     */
    Boolean deleted = false

    /**
     * deletedBy - save this info to find out who actually deleted the video
     */
    User deletedBy

    /**
     * when the attachment was deleted?
     */
    Date deletedAt

    /**
     *  file's thumbnail or poster
     *  default (user's profile picture)
     */
    String thumbnail

    /**
     * format
     */
    AttachmentFormat attachmentFormat

    /**
     * User who uploaded the file
     */
    User user

    /**
     * path on filesystem
     */
    String path

    /**
     * average of all ratings for this attachment
     */
    BigInteger averageRating = 0.0

    /**
     * number of views
     */
    BigInteger numberOfViews = 0

    /**
     * date attachment was uploaded
     */
    Date uploadedDate = new Date()

    /**
     * attachmentType audio / video
     */
    String attachmentType

    /**
     * number of Likes
     */
    BigInteger numberOfLikes = 0

    /**
     * constraints and mapping
     */
    static constraints = {
        name(nullable: false)
        attachmentFormat(nullable: false)
        user(nullable: false)
        path(nullable: false)
        thumbnail(nullable: true)
        attachmentType(nullable: false)
        deletedBy(nullable: true)
        deletedAt(nullable: true)
    }

    static mapping = {
        table("gs_attachment")
    }
}

