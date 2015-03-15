/*
 * @(#)User.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

class User {

    /**
     * username
     */
    String username

    /**
     * TODO:
     * encrypt and store
     */
    String passwordHash

    /**
     * fullName
     */
    String fullName

    /**
     * firstName
     */
    String firstName

    /**
     * lastName
     */
    String lastName

    /**
     * middleName
     */
    String middleName

    /**
     * email
     */
    String email

    /**
     * phone
     */
    String phone

    /**
     * website (for producer)
     */
    String website

    /**
     * dates
     */
    // date on which user is created
    Date createDate = new Date()
    // date on which the user is enabled
    Date enableDate = new Date()
    // date on which the user is disabled
    Date disableDate
    // date on which the user's account is up for review
    Date reviewDate = new Date() + 365
    // date on which the user is terminated
    Date terminateDate

    /**
     * userRole
     */
    UserRole userRole

    /**
     * profile picture's path
     */
    String profilePicture

    /**
     * flag to check if user has been deleted
     * if true then user should not be allowed to login
     * NOTE- If the user is deleted then it has to be removed from UI that is set deleted flag to true
     * but if admin deleted the user then it has to be deleted from the database
     */
    Boolean deleted = false

    /**
     * deletedBy - save this info to find out who actually deleted the user
     */
    User deletedBy

    /**
     * when the user was deleted?
     */
    Date deletedAt

    /**
     * referredBy -> salesTeam's person's name
     */
    String referredBy

    /**
     * constraints and mapping
     */
    static constraints = {
        username(blank: false, unique: true)
        passwordHash(nullable: false)
        fullName(nullable: false, autocomplete: true)
        firstName(nullable: false)
        lastName(nullable: false)
        middleName(nullable: true)
        email(nullable: true, email: true)
        phone(nullable: true)
        disableDate(nullable: true)
        terminateDate(nullable: true)
        userRole(nullable: false)
        profilePicture(nullable: true)
        deletedBy(nullable: true)
        deletedAt(nullable: true)
        website(nullable: true)
        referredBy(nullable: true)
    }

    static mapping = {
        table("gs_user")
    }
}

