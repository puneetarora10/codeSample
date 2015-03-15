/*
 * @(#)AttachmentFormat.groovy
 * Copyright (c) 2013 Amazing Applications Inc. All rights reserved.
 * Amazing Applications Inc. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package gotsigned

public enum AttachmentFormat {
    MP3('mp3'),
    MP4('mp4'),
    OGG('ogg'),
    WEBM('webm'),
    WAV('wav'),
    WEBMV('webmv'),
    OGV('ogv'),
    M4V('m4v'),
    AVI('avi'),
    FLV('flv'),
    AAC('aac'),
    M4A('m4a'),
    M4B('m4b'),
    M4P('m4p'),
    M4R('m4r'),
    MOV('mov'),
    WMA('wma')

    String name

    AttachmentFormat(String name) {
        this.name = name
    }
}
