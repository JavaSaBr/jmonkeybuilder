package com.ss.editor.analytics.google;

import org.jetbrains.annotations.NotNull;

/**
 * The list of hit types.
 *
 * @author JavaSaBr
 */
public enum HitType {
    PAGE_VIEW("pageview"),
    SCREEN_VIEW("screenview"),
    EVENT("event"),
    TRANSACTION("transaction"),
    ITEM("item"),
    SOCIAL("social"),
    EXCEPTION("exception"),
    TIMING("timing");

    @NotNull
    private final String requestValue;

    HitType(@NotNull final String requestValue) {
        this.requestValue = requestValue;
    }

    @NotNull
    @Override
    public String toString() {
        return requestValue;
    }
}
