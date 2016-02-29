package com.ss.editor.ui.component.creator;

import java.util.concurrent.Callable;

/**
 * Класс для описания создателя файлов.
 *
 * @author Ronn
 */
public class FileCreatorDescription {

    /**
     * Описание создаваемого файла.
     */
    private String fileDescription;

    /**
     * Конструктор создателя файлов.
     */
    private Callable<FileCreator> constructor;

    /**
     * @param constructor конструктор создателя файлов.
     */
    public void setConstructor(Callable<FileCreator> constructor) {
        this.constructor = constructor;
    }

    /**
     * @param fileDescription описание создаваемого файла.
     */
    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    /**
     * @return конструктор создателя файлов.
     */
    public Callable<FileCreator> getConstructor() {
        return constructor;
    }

    /**
     * @return описание создаваемого файла.
     */
    public String getFileDescription() {
        return fileDescription;
    }

    @Override
    public String toString() {
        return "FileCreatorDescription{" +
                "fileDescription='" + fileDescription + '\'' +
                ", constructor=" + constructor +
                '}';
    }
}
