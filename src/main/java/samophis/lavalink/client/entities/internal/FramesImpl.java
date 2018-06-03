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

package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.Statistics;

@SuppressWarnings("WeakerAccess")
public class FramesImpl implements Statistics.Frames {
    private final int sent, nulled, deficit;
    public FramesImpl(int sent, int nulled, int deficit) {
        this.sent = sent;
        this.nulled = nulled;
        this.deficit = deficit;
    }
    @Override
    public int getAverageFramesSentPerMinute() {
        return sent;
    }
    @Override
    public int getAverageFramesNulledPerMinute() {
        return nulled;
    }
    @Override
    public int getAverageFrameDeficitPerMinute() {
        return deficit;
    }
}
