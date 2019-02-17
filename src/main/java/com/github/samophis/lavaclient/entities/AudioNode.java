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
package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface AudioNode {
	@Nullable
	@CheckReturnValue
	Statistics statistics();

	@Nonnull
	@CheckReturnValue
	LoadBalancer loadBalancer();

	@Nonnull
	@CheckReturnValue
	LavaClient client();

	@Nonnull
	@CheckReturnValue
	String baseUrl();

	@Nonnull
	@CheckReturnValue
	String websocketUrl();

	@Nonnull
	@CheckReturnValue
	String restUrl();

	@Nonnull
	@CheckReturnValue
	String password();

	@Nonnegative
	@CheckReturnValue
	int port();

	@CheckReturnValue
	boolean available();

	void openConnection();

	void openConnection(@Nonnull final Runnable callback);

	void closeConnection();

	void closeConnection(@Nonnull final Runnable callback);
}
