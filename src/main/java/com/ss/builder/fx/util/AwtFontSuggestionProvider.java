package com.ss.builder.ui.util;

import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The suggestion provider's implementation to provide available fonts.
 *
 * @author JavaSaBr
 */
public class AwtFontSuggestionProvider implements Callback<ISuggestionRequest, Collection<Font>> {

    private final Array<Font> options;
    private final Array<String> lowerCaseOptions;

    public AwtFontSuggestionProvider(@NotNull Collection<Font> options) {
        this.options = ArrayFactory.newArray(Font.class, options.size());
        this.options.addAll(options);
        this.lowerCaseOptions = options.stream()
                .map(Font::getFontName)
                .map(String::toLowerCase)
                .collect(ArrayCollectors.toArray(String.class));
    }

    @Override
    public Collection<Font> call(@NotNull ISuggestionRequest request) {

        var userText = request.getUserText();

        if (StringUtils.isEmpty(userText)) {
            return Collections.emptyList();
        }

        var lowerInput = userText.toLowerCase();

        var result = new ArrayList<Font>();

        for (int i = 0; i < lowerCaseOptions.size(); i++) {

            var original = options.get(i);
            var fontName = original.getFontName();
            var lowerCase = lowerCaseOptions.get(i);

            if (!fontName.equals(userText) && lowerCase.contains(lowerInput)) {
                result.add(original);
            }
        }

        return result;
    }
}
