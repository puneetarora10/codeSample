/*
 * @(#)QueryService.groovy
 * Copyright (c) 2008-2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class QueryService {
    /**
     * builds a query which when run would compare
     * searchTerm with ['attachmentTag', name, user]
     * default sortedBy numberOfViews
     * @params searchTerm
     * @return
     */
    public String returnAttachmentsQuery(String searchTerm, Boolean count, String sortBy) {
        String qs = ""
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct at.attachment.name)"
        } else {
            selectClause = "select distinct at.attachment"
        }

        String fromClause = "from AttachmentTag at"
        String whereClause = "where at.attachment.approved = true and at.attachment.deleted = false and"
        // append all comparisons
        // tag.name
        whereClause += " (upper(at.tag.name) like '%${searchTerm}' or upper(at.tag.name) like '${searchTerm}' or upper(at.tag.name) like '${searchTerm}%'  or upper(at.tag.name) like '%${searchTerm}%'"
        // file name
        whereClause += " or upper(at.attachment.name) like '%${searchTerm}' or upper(at.attachment.name) like '${searchTerm}' or upper(at.attachment.name) like '${searchTerm}%' or upper(at.attachment.name) like '%${searchTerm}%'"
        // file's owner
        whereClause += " or upper(at.attachment.user.username) like '%${searchTerm}' or upper(at.attachment.user.username) like '${searchTerm}' or upper(at.attachment.user.username) like '${searchTerm}%' or upper(at.attachment.user.username) like '%${searchTerm}%')"

        // default sorting by numberOfViews
        if (!sortBy) {
            sortBy = "numberOfViews"
        }
        String orderByClause = "order by at.attachment.${sortBy} desc"

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"

        return qs
    }

    /**
     * builds a query which when run would compare
     * searchTerm with ['attachmentTag', name, user]
     * default sortedBy numberOfViews
     * @params searchTerm
     * @return
     */
    public String returnAttachmentsQuery(String searchType, String searchTerm, Boolean count, String sortBy) {
        String qs = ""
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct at.attachment.name)"
        } else {
            selectClause = "select distinct at.attachment"
        }

        String fromClause = "from AttachmentTag at"
        String whereClause = "where at.attachment.approved = true and at.attachment.deleted = false and"
        // append all comparisons
        // if searchType == "USERROLE" then compare with attachment's user's userRole
        if (searchType == "USERROLE") {
            whereClause += " upper(at.attachment.user.userRole) = '${searchTerm}'"
        }

        // default sorting by numberOfViews
        if (!sortBy) {
            sortBy = "numberOfViews"
        }
        String orderByClause = "order by at.attachment.${sortBy} desc"

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"
        
        return qs
    }

    /**
     *
     * @return query which would return attachment objects
     */
    public String returnAttachmentsQuery(Boolean count, String sortBy) {
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct attachment.name)"
        } else {
            selectClause = "select distinct attachment"
        }

        String fromClause = "from Attachment attachment"

        String whereClause = "where attachment.approved = true and attachment.deleted = false"

        // default sorting by numberOfViews
        if (!sortBy) {
            sortBy = "numberOfViews"
        }
        String orderByClause = "order by attachment.${sortBy} desc"

        String qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"
        
        return qs
    }

    /**
     *
     * @param tags
     * @param count
     * @param sortBy
     * @return queryString
     */
    public String returnAttachmentsQuery(List tags, Boolean count, String sortBy, String notToInclude) {
        String qs = ""
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct at.attachment.name)"
        } else {
            selectClause = "select distinct at.attachment"
        }

        String fromClause = "from AttachmentTag at"
        String whereClause = "where  at.attachment.approved = true and at.attachment.deleted = false and upper(at.attachment.name) <> '${notToInclude?.toUpperCase()}'"
        // check if attachment has any tags
        if (tags.size() > 0) {
            // append all comparisons
            whereClause += " and (upper(at.tag.name) in ("
            tags.eachWithIndex { tagValue, idx ->
                if (idx == tags.size() - 1) {
                    whereClause += "'" + tagValue?.toString()?.toUpperCase() + "'"
                } else {
                    whereClause += "'" + tagValue?.toString()?.toUpperCase() + "'"
                    whereClause += ','
                }
            }
            whereClause += "))"
        }
        // default sorting by numberOfViews
        if (!sortBy) {
            sortBy = "numberOfViews"
        }
        String orderByClause = "order by at.attachment.${sortBy} desc"

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"

        return qs
    }

    /**
     * builds a query which when run would return attachments of a user with userId
     * default sortedBy numberOfViews
     * @params userId
     * @params count if true would return counts query
     * @params sortBy
     * @return
     */
    public String returnAttachmentsQuery(Long userId, Boolean count, String sortBy) {
        String qs = ""
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct at.attachment.name)"
        } else {
            selectClause = "select distinct at.attachment"
        }

        String fromClause = "from AttachmentTag at"
        String whereClause = "where at.attachment.approved = true and at.attachment.deleted = false and"
        // file's owner
        whereClause += " at.attachment.user.id = ${userId}"

        // default sorting by numberOfViews
        if (!sortBy) {
            sortBy = "numberOfViews"
        }
        String orderByClause = "order by at.attachment.${sortBy} desc"

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"

        return qs
    }

    /**
     *
     * @return queryString for returning Tags ordered by attachmentCount in descending order
     */
    public String returnTagsByAttachmentCountQuery() {
        String qs = ""
        String selectClause = "select t.name"
        String fromClause = "from Tag t"
        String whereClause = ""
        String orderByClause = "order by t.attachmentCount desc"

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause} ${orderByClause}"

        return qs
    }

    /**
     * queryString for returning attachments for which attachment.user.referredBy is not null
     * @param params
     * @param count
     * @return
     */
    public String returnReferredAttachmentsQuery(Map params, Boolean count) {
        String qs = ""
        String selectClause = ""
        if (count) {
            selectClause = "select count(distinct at.attachment.name)"
        } else {
            selectClause = "select distinct attachment"
        }

        String fromClause = "from Attachment attachment"
        String whereClause = "where  attachment.user.referredBy <> '' and attachment.user.referredBy is not null"
        // check if searchTerm exists
        if (params?.searchTerm) {
            String searchTerm = params?.searchTerm?.toString()?.toUpperCase()
            whereClause += " and (upper(attachment.user.referredBy) like '%${searchTerm}' or upper(attachment.user.referredBy) like '${searchTerm}%' or upper(attachment.user.referredBy) like '${searchTerm}')"
        }

        // append to qs
        qs = "${selectClause} ${fromClause} ${whereClause}"

        return qs
    }
}
