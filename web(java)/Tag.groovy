/*
 * @(#)Tag.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class Tag {
    /**
     * tag's name
     */
    String name

    /**
     * how many times this tag has been searched for
     * while bootStraping "#artist", "#writer", "#producer" have been searchCount of 999 so that they always
     * show up first on the searchDashboard and whenever any tag's searchCount is increased so is the searchCount
     * of these tags
     */
    BigInteger searchCount = 0

    /**
     * count of attachments with this tag
     */
    BigInteger attachmentCount = 0

    /**
     * constraints and mapping
     */
    static constraints = {
        name(nullable: true)
    }

    static mapping = {
        table("gs_tag")
    }
}
