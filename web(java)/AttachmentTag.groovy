/*
 * @(#)AttachmentTag.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class AttachmentTag {
    /**
     * attachment
     */
    Attachment attachment

    /**
     * tag
     */
    Tag tag

    /**
     * constraints and mapping
     */
    static constraints = {
        attachment(nullable: false)
        tag(nullable: false)
    }

    static mapping = {
        table("gs_attach_tag")
    }
}
