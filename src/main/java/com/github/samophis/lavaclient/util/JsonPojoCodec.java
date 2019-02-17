/*
   Copyright 2019 Sam Pritchard

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
package com.github.samophis.lavaclient.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/*
Credit to catnip development team: https://github.com/mewna/catnip/blob/master/src/main/java/com/mewna/catnip/util/JsonPojoCodec.java
 */

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class JsonPojoCodec<T> implements MessageCodec<T, T> {
	private final Class<T> type;
	private final byte systemCodecID = -1;
	private final String name = "JsonPojoCodec";

	@Override
	public void encodeToWire(final Buffer buffer, final T t) {
		final byte[] data = JsonObject.mapFrom(t).encode().getBytes();
		buffer.appendInt(data.length);
		buffer.appendBytes(data);
	}

	@Override
	public T decodeFromWire(final int pos, final Buffer buffer) {
		final int length = buffer.getInt(pos);
		return new JsonObject(buffer.getString(pos + 4, pos + 4 + length))
				.mapTo(type);
	}

	@Override
	public T transform(final T t) {
		return t;
	}
}
