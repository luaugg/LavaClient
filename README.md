# LavaClient
[![](https://jitpack.io/v/SamOphis/LavaClient.svg)](https://jitpack.io/#SamOphis/LavaClient)
[![Build Status](https://travis-ci.org/SamOphis/LavaClient.svg?branch=master)](https://travis-ci.org/SamOphis/LavaClient)

[Please check out the Wiki as well, it goes into much more accurate and informative detail!](https://github.com/SamOphis/LavaClient/wiki)

LavaClient is a fast client implementation for [Lavalink v2 and v3](https://github.com/Frederikam/Lavalink) written in Java 8, built from the ground up for speed, abstraction and ease-of-use. Although it can be used normally as is, since it lacks specific references to Java Discord API Libraries, certain things (such as channel objects, etc.) are not represented except for their numeric ID's.

This is used in production along with a non-specific command library by my [Kunou](https://github.com/KunouMain) project. You must have more than "a ping-pong bot" experience/knowledge to properly use LavaClient.

Implementations that extend LavaClient in order to make it easier for certain Java Discord Libraries like [JDA](https://github.com/DV8FromTheWorld/JDA), [Discord4J](https://github.com/austinv11/Discord4J), or [Javacord](https://github.com/BtoBastian/Javacord]) will be listed below this message upon request and reviewal.

### Why choose LavaClient?

LavaClient is built using much of the same logic found in the default [Lavalink JDA Client](https://github.com/Frederikam/Lavalink/tree/master/LavalinkClient) and uses many faster dependencies in order to ensure speed. Identifiers, the data they encode into and the actual `AudioTrack` itself are cached with [Caffeine](https://github.com/ben-manes/caffeine) and expire [15 minutes after the latest access and 20 minutes after the initial write](https://github.com/SamOphis/LavaClient/blob/master/src/main/java/samophis/lavalink/client/entities/LavaClient.java#L26), meaning memory is used efficiently (and both of these values can be modified).

The load balancing equations are the [same as those used in the JDA Client and thus guarantee at least the same degree of load balancing between nodes](https://github.com/SamOphis/LavaClient/blob/master/src/main/java/samophis/lavalink/client/entities/internal/LoadBalancerImpl.java#L51). Additionally, with no dependencies on any Discord API Library and easy-to-read abstractions, you can easily extend LavaClient to make it even more intuitive for beginners of certain Discord Libraries.

JSON Encoding/Decoding, HTTP Requests, WebSocket Connections to Audio Nodes, the Caching of Operations, etc. are all optimized to make sure that no extra resources are wasted. Not only that but the project structure is very simple to wrap your head around, the main files have been documented fully and the coding style isn't too cluttered, making contributions or readings very easy.

All of this wasn't built for no reason -- it was built by me to easily integrate with my [Kunou](https://github.com/KunouMain/KunouBot) bot which also uses no Discord API Libraries, meaning the foundations of the project itself are *built to be able to integrate easily*. The same is true if you're using a Discord API Library too as LavaClient, again, has no breaking dependencies.

**However: Since abstraction is favoured, LavaClient has no way at all of handling the rate limits of different libraries, so you *must* either take advantage of your library's rate limiting or handle it yourself**. An easy example of how to handle this being used in production by Kunou can be found [here](https://gist.github.com/SamOphis/766c62d6fb91bac77ea9f2dea0edb330).

# Building a LavaClient

Building a `LavaClient` is as easy as instantiating a `LavaClientBuilder` and adding `AudioNodes` to it. Due to recent design choice, it's ***very important to not use more than one `LavaClient` in a single project*** (because `AudioNodes` and `LavaPlayers` are stored in static maps, causing possible collisions and node issues if two clients access the same maps).

This isn't a big issue as there's almost always no need to build two completely-separate Discord Clients for one project (sharding included).

Conveniently, LavaClient already has default placeholder values that match the default Lavalink-Server configuration values, however, in practice, you should setup different values in order to ensure security and protection of your `AudioNodes`. A basic example of how to make a `LavaClient` and add a local `AudioNode` is provided below:

```java
/* -- See notes below -- */
LavaClient client = new LavaClientBuilder(true)
        .setShardCount(bot_shard_count)
        .setUserId(bot_user_id)
        .build();
client.addEntry(new AudioNodeEntryBuilder(client)
        .setAddress("localhost")
        .build());
```

#### Notes:
- The `true` value (for the `overrideJson` parameter) tells `LavaClient` to enable dynamic code generation for JSON encoding/decoding. This is by far the fastest option, however it may collide with other projects already using `Jsoniter`. Due to its lack of popularity and speed, it's recommended to keep this set to `true`.
- `bot_shard_count` is the number of shards your bot uses (1+). This value can be hardcoded or requested via an API call to `/gateway/bot`.
- `bot_user_id` is the User ID of the Bot User this `LavaClient` is associated with. This value can be hardcoded or requested via an API call to `/users/@me`.
- `AudioNode` entries are added after building a `LavaClient` since they rely on the default values specified in an `LavaClient` instance.

# Interacting with LavaClient

Ignoring the obvious steps of running Lavalink-Server and adding the `AudioNodeEntry` to your `LavaClient` instance, there are a few steps you ***must*** understand and follow before you can play audio.

Firstly to grab a `LavaPlayer` instance you can call the `LavaClient#getPlayerByGuildId` method. If a `LavaPlayer` instance doesn't already exist, it'll be created automatically, meaning no check is required.

The `LavaPlayer` interface is nearly identical to the default Lavalink-Client `IPlayer` interface or the Lavaplayer `AudioPlayer` class.

> Note: From this point, a developer is expected to have basic knowledge of websockets and/or how the Java Discord Library they're implementing sends/receives Gateway events.

To connect to a voice channel, you must send an `OP 4 Voice State Update` to Discord, preferably through the same shard that is aware of the guild you're trying to update. This update looks like:

```json
{
    "op": 4,
    "d": {
        "guild_id": "41771983423143937",
        "channel_id": "127121515262115840",
        "self_mute": false,
        "self_deaf": false
    }
}
```

Once sent, wait for the incoming `VOICE_STATE_UPDATE` and `VOICE_SERVER_UPDATE` events with your library. It's much better to do this asynchronously, reading messages on a different thread, etc. LavaClient has a class for this (starting from v0.2.2) called `EventWaiter` which takes some of this responsibility away from the end-user.
It has two setters for users to set the Session ID, Voice Token and Voice Server Endpoint from the received events and only connects when the necessary state has been fully filled out, never pausing any thread to wait.

The user still has to set the information in the first place, but the `EventWaiter` class handles the inconsistency of Discord Events without causing a Thread to sleep and hides away the connection logic, making it easier to connect to an AudioNode. Below are two examples -- one using the `EventWaiter` class and one without.

```java
// With the EventWaiter
// Creates an EventWaiter using the AudioNode with the least load on it and the associated Guild ID.
// You can also specify an AudioNode manually with the overload .from(AudioNode, long)
EventWaiter waiter = EventWaiter.from(guild_id);
waiter.setSessionIdAndTryConnect(session_id); // Sets the Session ID and connects ONLY if the Token and Endpoint have already been set.
waiter.setServerAndTryConnect(voice_token, endpoint); // Sets the Token and Endpoint and connects ONLY if the Session ID has already been set.
// As of v1.3, you can specify a callback in the `from` method for follow-up code that preserves order.
```

```java
// Without the EventWaiter
// You need to retrieve the Session ID, Voice Token, Server Endpoint and a Guild ID here.
// Discord Events are not guaranteed to even be sent at all, let alone in the right order.
LavaPlayer player = client.getPlayerByGuildId(guild_id);
player.setNode(LavaClient.getBestNode());

VoiceUpdate update = new VoiceUpdate(guild_id, session_id, token, endpoint);
String data = JsonStream.serialize(update);
player.getConnectedNode().getSocket().sendText(data);
```

Both examples function exactly the same, except the second relies on much more user input to work even in the slightest. It's up to you which option you choose and neither has a noticeable impact on performance.

This will establish a voice connection to the `AudioNode`, and will then allow you to play songs. Once connected, you can use the `LavaPlayer#playTrack` methods to play songs from YouTube, Soundcloud, Bandcamp, etc.

> Note: Playing songs via an identifier has slightly higher latency than playing songs via a Lavaplayer `AudioTrack`, however it also puts slightly less stuff on the bot. This design choice is up to the reader. In all cases, as of v0.2, LavaClient will (by default) cache results in order to cut this down. With default settings, entries expire 15 minutes after latest access and 20 minutes after the initial write, although these can be configured by specifying the parameters in the `LavaClientBuilder` constructor.

## Adding Listeners

Great! You get how to use LavaClient properly now, but what if you want to add listeners like you're used to with Lavaplayer? No worries, LavaClient has full support for all events Lavaplayer exposes, however it wraps the information into an event object instead of passing in parameters.


> Note: The classes provided exist in the `samophis.lavalink.client.entities.events` package, ***they are not Lavaplayer classes***.

These are all the methods defined in the `AudioEventListener` interface, and as always there's also an `AudioEventAdapter` in the same package.

`onTrackStart` - provides a `LavaPlayer` and a `TrackStartEvent`.
<br>`onTrackStuck` - provides a `LavaPlayer` and a `TrackStuckEvent`.
<br>`onTrackEnd` - provides a `LavaPlayer` and a `TrackEndEvent`.
<br>`onTrackException` - provides a `LavaPlayer` and a `TrackExceptionEvent`.
<br>`onPlayerPause` - provides a `LavaPlayer`.
<br>`onPlayerResume` - provides a `LavaPlayer`.

To add a listener, just use the method `LavaPlayer#addListener`. Then, whenever an `AudioNode` or a `LavaPlayer` emits an event, all listeners added to the `LavaPlayer` will have the appropriate method fired.

# Exceptions and Logging

LavaClient wraps most exceptions in a generic `RuntimeException` instance and throws them right back at the caller, and in most cases logs the exception. Wrapping exceptions this way is pretty bad practice, and all traces of this behaviour are steadily being replaced with specific, actually-informative exception classes.

Additionally, LavaClient was built from the ground up with Sentry Logback logging, so just drop in a Logback configuration file (in `src/main/resources` with IntelliJ IDEA) to enable logging via Sentry or just Logback in general.

# Contributions

Contributions are always welcome as long as they've been tested for at least a few hours (preferably a day) continuously and match the general coding style used in this project. Simply open a pull request and wait for it to be approved!

Check out the [full guidelines here!](https://github.com/SamOphis/LavaClient/blob/master/.github/CONTRIBUTING.md)