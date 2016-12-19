package com.ss.editor;

import com.sun.javafx.css.Stylesheet;

import java.io.File;
import java.io.IOException;

/**
 * Created by ronn on 17.12.16.
 */
public class UpdateBSS {

    public static void main(String[] args) throws IOException {
        Stylesheet.convertToBinary(new File("./resources/ui/css/base.css"), new File("./resources/ui/css/base.bss"));
        Stylesheet.convertToBinary(new File("./resources/ui/css/external.css"), new File("./resources/ui/css/external.bss"));
        Stylesheet.convertToBinary(new File("./resources/ui/css/custom_ids.css"), new File("./resources/ui/css/custom_ids.bss"));
        Stylesheet.convertToBinary(new File("./resources/ui/css/custom_classes.css"), new File("./resources/ui/css/custom_classes.bss"));
    }
}
