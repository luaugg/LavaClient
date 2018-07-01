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

package samophis.lavalink.client.exceptions;

/**
 * Thrown in response to the configuration file being badly formatted, such as if certain properties are missing.
 *
 * @since 2.4.0
 * @author Sam Pritchard
 */
@SuppressWarnings("unused")
public class ConfigurationFormatException extends RuntimeException {
    public ConfigurationFormatException(String message) {
        super(message);
    }
    public ConfigurationFormatException(Throwable throwable) {
        super(throwable);
    }
}
