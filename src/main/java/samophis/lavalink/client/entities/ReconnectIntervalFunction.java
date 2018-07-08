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

package samophis.lavalink.client.entities;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * A function used by LavaClient to determine how to expand the reconnection interval upon the next reconnect attempt.
 * <br><p>By default, LavaClient will expand the old interval by raising 2 to the number of connection attempts,
 * capped at fifteen seconds. You should not automatically cap the result as LavaClient already does that.
 *
 * <br>Additionally, the function must return a positive long, and the provided {@link AudioNode AudioNode}
 * and old reconnect interval are guaranteed to be <b>not-null</b> and <b>positive</b> respectively, if called by LavaClient.</p>
 *
 * @author Samuel Pritchard
 * @since 2.6.0
 */
@FunctionalInterface
public interface ReconnectIntervalFunction {
    /**
     * The actual expander method itself. Accepts an {@link AudioNode AudioNode} as well as the old reconnect interval
     * returned by {@link AudioNode#getReconnectInterval()}.
     * <br><p>Note: When "expanding" intervals, you can return any <b>positive</b> long/integer. This means you can
     * customize the waiting periods to your liking. LavaClient will throw an IllegalArgumentException if the value
     * returned by this method is negative.</p>
     * @param node The {@link AudioNode AudioNode} to attempt a reconnect to.
     * @param oldReconnectInterval The last reconnect interval.
     * @return A <b>not-negative (positive)</b> interval.
     */
    @Nonnegative long expand(@Nonnull AudioNode node, @Nonnegative long oldReconnectInterval);
}