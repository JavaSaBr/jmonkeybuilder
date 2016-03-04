package com.ss.editor.file;

import java.util.ArrayList;
import java.util.List;

/**
 * Файл для тестирования пост эффетков.
 *
 * @author Ronn
 */
public class PostFilterViewFile {

    /**
     * Список материалов.
     */
    private final List<String> materials;

    public PostFilterViewFile() {
        this.materials = new ArrayList<>();
    }

    /**
     * @return список материалов.
     */
    public List<String> getMaterials() {
        return materials;
    }

    /**
     * Добавить материал.
     */
    public void addMaterial(final String material) {
        this.materials.add(material);
    }

    /**
     * Удалить материал.
     */
    public void removeMaterial(String material) {
        this.materials.remove(material);
    }
}
