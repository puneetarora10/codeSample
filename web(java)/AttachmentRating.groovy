/*
 * @(#)AttachmentRating.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class AttachmentRating {
    /**
     * attachment
     */
    Attachment attachment

    /**
     * rating
     */
    Rating rating

    /**
     * constraints and mapping
     */
    static constraints = {
        attachment(nullable: false)
        rating(nullable: false)
    }

    static mapping = {
        table("gs_attach_rating")
    }
}

