/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.app.models;

public enum UserAction {

    ABOUT("about"),
    REPORT_ISSUE_FROM_TRANSCRIPT("report issue from transcript");

    private final String name;

    UserAction(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
