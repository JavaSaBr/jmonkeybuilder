package com.ss.editor;

import java.util.ResourceBundle;

/**
 * Набор констант с локализованными сообщениями.
 *
 * @author Ronn
 */
public class Messages {

    public static final String BUNDLE_NAME = "com.ss.launcher.resources.messages.launcher_messages";

    public static final String WINDWOW_TITLE;
    public static final String WINDWOW_TITLE_VERSION;

    public static final String MAIN_PAGE_MAIN_BUTTON_PLAY;
    public static final String MAIN_PAGE_MAIN_BUTTON_UPDATE;
    public static final String MAIN_PAGE_MAIN_BUTTON_CHECKING;
    public static final String MAIN_PAGE_MAIN_BUTTON_DOWNLOAD;

    public static final String MAIN_PAGE_STATUS_CURRENT_VERSION;
    public static final String MAIN_PAGE_STATUS_NEED_UPDATE;
    public static final String MAIN_PAGE_STATUS_PRESS_DOWNLOAD;
    public static final String MAIN_PAGE_STATUS_PREPARE_DOWNLOAD;
    public static final String MAIN_PAGE_STATUS_DOWNLOAD;
    public static final String MAIN_PAGE_STATUS_DOWNLOAD_SUCCESSFUL;
    public static final String MAIN_PAGE_STATUS_UPDATE_CLIENT;
    public static final String MAIN_PAGE_STATUS_UPDATE_SUCCESSFUL;

    public static final String MAIN_PAGE_QUESTION_LABEL;
    public static final String MAIN_PAGE_OPEN_CHOOSER_LABEL;

    public static final String DIRECTORY_CHOOSER_TITLE;

    public static final String ALERT_ERROR_TITLE;
    public static final String ALERT_INFO_TITLE;
    public static final String ALERT_INFO_HEADER_TEXT_NOT_FOUND_CLIENT;
    public static final String ALERT_INFO_HEADER_TEXT_NEED_UPDATE_LAUNCHER;
    public static final String ALERT_INFO_HEADER_TEXT_NEED_RESTART;

    public static final String RUNTIME_EXCEPTION_MESSAGE_CANT_DOWNLOAD;
    public static final String RUNTIME_EXCEPTION_MESSAGE_CONNECT_PROBLEM;

    public static final String NOT_FOUND_CLIENT_EXCEPTION_MESSAGE_NEED_UPDATE;

    public static final String INCORRECT_JAVA_EXCEPTION_MESSAGE_NOT_FOUND_JAVA;
    public static final String INCORRECT_JAVA_EXCEPTION_MESSAGE_NEED_INSTALL_ORACLE;
    public static final String INCORRECT_JAVA_EXCEPTION_MESSAGE_OLD_VERSION;

    public static final String SETTINGS_DIALOG_TITLE;
    public static final String SETTINGS_DIALOG_BUTTON_APPLY;
    public static final String SETTINGS_DIALOG_LABEL_GAME_FOLDER;
    public static final String SETTINGS_DIALOG_LABEL_PROXY_HOST;
    public static final String SETTINGS_DIALOG_LABEL_PROXY_PORT;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());
        WINDWOW_TITLE = bundle.getString("Window.title");
        WINDWOW_TITLE_VERSION = bundle.getString("Window.titleVersion");

        MAIN_PAGE_MAIN_BUTTON_PLAY = bundle.getString("MainPage.mainButton.play");
        MAIN_PAGE_MAIN_BUTTON_UPDATE = bundle.getString("MainPage.mainButton.update");
        MAIN_PAGE_MAIN_BUTTON_CHECKING = bundle.getString("MainPage.mainButton.checking");
        MAIN_PAGE_MAIN_BUTTON_DOWNLOAD = bundle.getString("MainPage.mainButton.download");

        MAIN_PAGE_STATUS_CURRENT_VERSION = bundle.getString("MainPage.status.currentVersion");
        MAIN_PAGE_STATUS_NEED_UPDATE = bundle.getString("MainPage.status.needUpdate");
        MAIN_PAGE_STATUS_PRESS_DOWNLOAD = bundle.getString("MainPage.status.pressDownload");
        MAIN_PAGE_STATUS_PREPARE_DOWNLOAD = bundle.getString("MainPage.status.prepareDownload");
        MAIN_PAGE_STATUS_DOWNLOAD = bundle.getString("MainPage.status.download");
        MAIN_PAGE_STATUS_DOWNLOAD_SUCCESSFUL = bundle.getString("MainPage.status.downloadSucessful");
        MAIN_PAGE_STATUS_UPDATE_CLIENT = bundle.getString("MainPage.status.updateClient");
        MAIN_PAGE_STATUS_UPDATE_SUCCESSFUL = bundle.getString("MainPage.status.updateSucessful");

        MAIN_PAGE_QUESTION_LABEL = bundle.getString("MainPage.questionLabel");
        MAIN_PAGE_OPEN_CHOOSER_LABEL = bundle.getString("MainPage.openChooserLabel");

        DIRECTORY_CHOOSER_TITLE = bundle.getString("DirectoryChooser.title");

        ALERT_ERROR_TITLE = bundle.getString("Alert.error.title");
        ALERT_INFO_TITLE = bundle.getString("Alert.info.title");
        ALERT_INFO_HEADER_TEXT_NOT_FOUND_CLIENT = bundle.getString("Alert.info.headerText.notFoundClient");
        ALERT_INFO_HEADER_TEXT_NEED_UPDATE_LAUNCHER = bundle.getString("Alert.info.headerText.needUpdateLauncher");
        ALERT_INFO_HEADER_TEXT_NEED_RESTART = bundle.getString("Alert.info.headerText.needRestart");

        RUNTIME_EXCEPTION_MESSAGE_CANT_DOWNLOAD = bundle.getString("RuntimeException.message.cantDownload");
        RUNTIME_EXCEPTION_MESSAGE_CONNECT_PROBLEM = bundle.getString("RuntimeException.message.connectProblems");

        NOT_FOUND_CLIENT_EXCEPTION_MESSAGE_NEED_UPDATE = bundle.getString("NotFoundClientException.message.needUpdate");

        INCORRECT_JAVA_EXCEPTION_MESSAGE_NEED_INSTALL_ORACLE = bundle.getString("IncorrectJavaException.message.notFoundJava");
        INCORRECT_JAVA_EXCEPTION_MESSAGE_NOT_FOUND_JAVA = bundle.getString("IncorrectJavaException.message.needInstalOracle");
        INCORRECT_JAVA_EXCEPTION_MESSAGE_OLD_VERSION = bundle.getString("IncorrectJavaException.message.oldVersion");

        SETTINGS_DIALOG_TITLE = bundle.getString("SettingsDialog.title");
        SETTINGS_DIALOG_BUTTON_APPLY = bundle.getString("SettingsDialog.button.apply");
        SETTINGS_DIALOG_LABEL_GAME_FOLDER = bundle.getString("SettingsDialog.label.gameFolder");
        SETTINGS_DIALOG_LABEL_PROXY_HOST = bundle.getString("SettingsDialog.label.proxyHost");
        SETTINGS_DIALOG_LABEL_PROXY_PORT = bundle.getString("SettingsDialog.label.proxyPort");
    }
}
