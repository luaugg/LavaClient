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

/**
 * Used to fetch the penalties of the attached {@link AudioNode AudioNode} in order to determine the best node to use.
 *
 * @since 0.1
 * @author SamOphis
 */

@SuppressWarnings("unused")
public interface LoadBalancer {
    /**
     * Fetches The {@link AudioNode AudioNode} attached to this LoadBalancer.
     * @return The {@link AudioNode AudioNode} associated with this LoadBalancer.
     */
    AudioNode getNode();

    /**
     * Fetches the penalty of the players imposed on the attached {@link AudioNode AudioNode}.
     * @return The player penalty imposed on the associated {@link AudioNode AudioNode}.
     */
    int getPlayerPenalty();

    /**
     * Fetches the CPU penalty imposed on the attached {@link AudioNode AudioNode}.
     * @return The CPU penalty imposed on the associated {@link AudioNode AudioNode}.
     */
    int getCpuPenalty();

    /**
     * Fetches the deficit frame penalty on the attached {@link AudioNode AudioNode}.
     * @return The deficit frame penalty on the associated {@link AudioNode AudioNode}.
     */
    int getDeficitFramePenalty();

    /**
     * Fetches the null frame penalty on the attached {@link AudioNode AudioNode}.
     * @return The null frame penalty on the associated {@link AudioNode AudioNode}.
     */
    int getNullFramePenalty();

    /**
     * Fetches the total penalty on the attached {@link AudioNode AudioNode}.
     * @return The total amount of penalty imposed on the associated {@link AudioNode AudioNode}.
     */
    int getTotalPenalty();
}