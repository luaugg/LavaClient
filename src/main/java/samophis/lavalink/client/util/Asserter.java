/*
   Copyright 2018 Samuel Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package samophis.lavalink.client.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Asserts that an object has to be not-null or that a number has to be positive.
 * <br><p>Used internally to test the above conditions to ensure some extent of safety.</p>
 *
 * @author SamOphis
 * @since 0.3.3
 */

public class Asserter {
    private Asserter() {}

    /**
     * Asserts that an object must be <b>not-null</b> and returns the provided object if it is.
     * @param object An object to null-check.
     * @param <T> The type of the object.
     * @throws NullPointerException If the provided object was {@code null}.
     * @return The provided object if it <b>is NOT {@code null}</b>.
     */
    @Nonnull
    public static <T> T requireNotNull(T object) {
        if (object == null)
            throw new NullPointerException("Object == null!");
        return object;
    }

    /**
     * Asserts that an integer must be <b>positive (not-negative)</b> and returns the provided integer if it is.
     * @param number The integer to check.
     * @throws IllegalArgumentException If the provided number was negative.
     * @return The integer if it was positive.
     */
    @Nonnegative
    public static int requireNotNegative(int number) {
        if (number < 0)
            throw new IllegalArgumentException("Number == negative!");
        return number;
    }

    /**
     * Asserts that a long must be <b>positive (not-negative)</b> and returns the provided long if it is.
     * @param number The long to check.
     * @throws IllegalArgumentException If the provided number was negative.
     * @return The long if it was positive.
     */
    @Nonnegative
    public static long requireNotNegative(long number) {
        if (number < 0)
            throw new IllegalArgumentException("Number == negative!");
        return number;
    }
}
