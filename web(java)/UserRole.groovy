/*
 * @(#)UserRole.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

public enum UserRole {
    ARTIST('Artist'),
    WRITER('Writer'),
    PRODUCER('Producer'),
    AANDR('AandR'),
    USER('User'),
    ADMINISTRATOR('Administrator'),
    SALESTEAM('SalesTeam')

    String name

    UserRole(String name) {
        this.name = name
    }
}
