package com.ss.builder.analytics.google;

import org.jetbrains.annotations.NotNull;

/**
 * The interface with constants of GA events.
 *
 * @author JavaSaBr
 */
public interface GAEvent {

    /**
     * The list of categories.
     */
    interface Category {
        /**
         * The constant APPLICATION.
         */
        @NotNull String APPLICATION = "Application";
        /**
         * The constant DIALOG.
         */
        @NotNull String DIALOG = "Dialog";
        /**
         * The constant EDITOR.
         */
        @NotNull String EDITOR = "Editor";
    }

    /**
     * The list of actions.
     */
    interface Action {
        /**
         * The constant APPLICATION_LAUNCHED.
         */
        @NotNull String APPLICATION_LAUNCHED = "Application launched";
        /**
         * The constant APPLICATION_CLOSED.
         */
        @NotNull String APPLICATION_CLOSED = "Application closed";
        /**
         * The constant EDITOR_OPENED.
         */
        @NotNull String EDITOR_OPENED = "Editor opened";
        /**
         * The constant EDITOR_CLOSED.
         */
        @NotNull String EDITOR_CLOSED = "Editor closed";
        /**
         * The constant DIALOG_OPENED.
         */
        @NotNull String DIALOG_OPENED = "Dialog opened";
        /**
         * The constant DIALOG_CLOSED.
         */
        @NotNull String DIALOG_CLOSED = "Dialog closed";
        /**
         * The constant EXECUTE_NODE_ACTION.
         */
        @NotNull String EXECUTE_NODE_ACTION = "Execute node action";
    }

    /**
     * The list of labels.
     */
    interface Label {
        /**
         * The constant THE_EDITOR_APP_WAS_CLOSED.
         */
        @NotNull String THE_EDITOR_APP_WAS_CLOSED = "The editor application was closed";
        /**
         * The constant THE_EDITOR_APP_WAS_LAUNCHED.
         */
        @NotNull String THE_EDITOR_APP_WAS_LAUNCHED = "The editor application was launched";
        /**
         * The constant WORKING_ON_AN_EDITOR.
         */
        @NotNull String WORKING_ON_AN_EDITOR = "Working on an editor";
        /**
         * The constant SHOWING_A_DIALOG.
         */
        @NotNull String SHOWING_A_DIALOG = "Showing a dialog";
    }
}
