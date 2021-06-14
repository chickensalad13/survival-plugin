package net.chickensalad.survival.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ArrayHelper {

    @SafeVarargs
    public static <T> @NonNull T @NonNull [] create(final @NonNull T... values) {
        return values;
    }
}
