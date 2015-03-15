/*
 * To persist "who" liked "which" attachment
 * @(#)AttachmentLike.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class AttachmentLike {
    /**
     * attachment
     */
    Attachment attachment

    /**
     * user who likes this attachment
     */
    User likedBy

    /**
     * constraints and mapping
     */
    static constraints = {
        attachment(nullable: false)
        likedBy(nullable: false)
    }

    static mapping = {
        table("gs_attach_like")
    }
}

