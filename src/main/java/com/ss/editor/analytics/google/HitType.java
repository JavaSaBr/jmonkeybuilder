package com.ss.editor.analytics.google;

import org.jetbrains.annotations.NotNull;

/**
 * The list of hit types.
 *
 * @author JavaSaBr
 */
public enum HitType {
    /**
     * Page view hit type.
     */
    PAGE_VIEW("pageview"),
    /**
     * Screen view hit type.
     */
    SCREEN_VIEW("screenview"),
    /**
     * Event hit type.
     */
    EVENT("event"),
    /**
     * Transaction hit type.
     */
    TRANSACTION("transaction"),
    /**
     * Item hit type.
     */
    ITEM("item"),
    /**
     * Social hit type.
     */
    SOCIAL("social"),
    /**
     * Exception hit type.
     */
    EXCEPTION("exception"),
    /**
     * Timing hit type.
     */
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
