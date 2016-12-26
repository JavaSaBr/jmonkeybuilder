package com.ss.editor.analytics.google;

/**
 * @author JavaSaBr
 */
public interface GAEvent {

    interface Category {
        String APPLICATION = "Application";
        String DIALOG = "Dialog";
        String EDITOR = "Editor";
    }

    interface Action {
        String LAUNCHED = "Launched";
        String CLOSED = "Closed";
        String EDITOR_OPENED = "Opened";
        String EDITOR_CLOSED = "Closed";
        String DIALOG_OPENED = "Opened";
        String DIALOG_CLOSED = "Closed";
    }

    interface Label {
        String THE_EDITOR_APP_WAS_CLOSED = "The editor application was closed";
        String THE_EDITOR_APP_WAS_LAUNCHED = "The editor application was launched";
        String THE_EDITOR_WAS_OPENED = "The editor was opened for editing a file";
        String THE_EDITOR_WAS_CLOSED = "The editor was closed";
        String THE_DIALOG_WAS_OPENED = "The dialog was opened";
        String THE_DIALOG_WAS_CLOSED = "The dialog was closed";
        String WORKING_ON_AN_EDITOR = "Working on an editor";
        String SHOWING_A_DIALOG = "Showing a dialog";
    }
}
