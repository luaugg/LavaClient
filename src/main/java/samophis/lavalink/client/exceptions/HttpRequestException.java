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
 * Represents an exception thrown by LavaClient caused by an issue with a HTTP Request to the Lavalink Server.
 *
 * @author SamOphis
 * @since 0.2
 */

@SuppressWarnings("unused")
public class HttpRequestException extends RuntimeException {
    public HttpRequestException(Exception exception) {
        super(exception);
    }
    public HttpRequestException(String message) {
        super(message);
    }
}
