package com.ss.editor.ui.util;

import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The simple suggestion provider's implementation.
 *
 * @author JavaSaBr
 */
public class SimpleStringSuggestionProvider implements Callback<ISuggestionRequest, Collection<String>> {

    private final Array<String> options;
    private final Array<String> lowerCaseOptions;

    public SimpleStringSuggestionProvider(@NotNull Collection<String> options) {
        this.options = ArrayFactory.newArray(String.class, options.size());
        this.options.addAll(options);
        this.lowerCaseOptions = options.stream()
                .map(String::toLowerCase)
                .collect(ArrayCollectors.toArray(String.class));
    }

    @Override
    public Collection<String> call(@NotNull ISuggestionRequest request) {

        var userText = request.getUserText();

        if (StringUtils.isEmpty(userText)) {
            return Collections.emptyList();
        }

        var lowerInput = userText.toLowerCase();

        var result = new ArrayList<String>();

        for (int i = 0; i < lowerCaseOptions.size(); i++) {

            var original = options.get(i);
            var lowerCase = lowerCaseOptions.get(i);

            if (!original.equals(userText) && lowerCase.contains(lowerInput)) {
                result.add(original);
            }
        }

        return result;
    }
}
