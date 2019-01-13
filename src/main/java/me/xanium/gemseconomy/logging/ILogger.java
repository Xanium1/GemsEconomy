/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.logging;

import java.io.File;

public interface ILogger {

    File getLatest();

    File getFolder();

    void zipAndReplace();

    void log(String message);

    void warn(String message);

    void error(String message, Exception ex);

    String getDateAndTime();

    void save();

}
