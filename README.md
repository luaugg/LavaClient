# LavaClient
[![JitPack](https://jitpack.io/v/SamOphis/LavaClient.svg)](https://jitpack.io/#SamOphis/LavaClient)
[![Build Status](https://travis-ci.org/SamOphis/LavaClient.svg?branch=master)](https://travis-ci.org/SamOphis/LavaClient)

[![LavaClient Guild](https://discordapp.com/api/guilds/455002103757406218/embed.png?style=banner2)](https://discord.gg/dvUhTJX)

LavaClient is a fast, feature-complete **and abstract** client implementation for [Lavalink v2.x and v3.x](https://github.com/Frederikam/Lavalink) that
doesn't depend on any Discord API Library. Although being abstract, it supports [JDA](https://github.com/DV8FromTheWorld/JDA) via the [LavaClient-JDA](https://github.com/SamOphis/LavaClient-JDA) project.

This page doesn't go into too much detail, so please [check out the Wiki instead.](https://github.com/SamOphis/LavaClient/wiki)  
The [Documentation for the Latest Version can also be found here.](https://samophis.github.io/LavaClient)

Projects made using LavaClient:
* [Kyoko](https://github.com/KyokoBot/Kyoko) - A cool, modular Discord Bot made in [JDA](https://github.com/DV8FromTheWorld/JDA)
* [LavaClient-JDA](https://github.com/SamOphis/LavaClient-JDA) - Official LavaClient integration for [JDA](https://github.com/DV8FromTheWorld/JDA).

> Note: People migrating from LavaClient to LavaClient v2 now must add [Lavaplayer](https://github.com/sedmelluq/lavaplayer) to their build file themselves.

# Why Choose LavaClient?

As of writing this (9th June 2018), LavaClient is the most frequently updated Lavalink Client Implementation with documentation of every public
class and support for every documented [Lavalink Feature from v2.0 onwards.](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md)

Additionally, it:

* Uses faster and lighter dependencies.
* Uses Sentry Logback Logging frequently allowing for super simple error/warning management.
* Uses the same load balancing logic found in the [Lavalink JDA Client](https://github.com/Frederikam/Lavalink/tree/master/LavalinkClient).
* Has self-explanatory and easy-to-use features.
* Has a **configurable** cache to reduce load on both nodes and clients.
* Has neat features such as `SocketInitializer`s and `AudioWrapper`s to allow low-level socket configuration as well as complete compatibility between
  Lavalink v2.0 and v3.0, meaning you don't have to change one bit of code even when using mixtures of nodes running v2.0 or v3.0 of Lavalink.
* Has easy-to-understand [guides found in the Wiki.](https://github.com/SamOphis/LavaClient/wiki)
* Has more minor features not listed here.

However, there is one big con of LavaClient which is how abstract it is. LavaClient doesn't rely on [JDA](https://github.com/DV8FromTheWorld/JDA),
[Discord4J](https://github.com/austinv11/Discord4J) or any other Discord API Library, and so things such as rate limiting or the handling of events
needed to connect to Lavalink have to be handled by the user. As a result, it's recommended that you rely on a well-established library that allows
you to do this easily such as the aforementioned ones, then you can take advantage of their built-in rate limiting and event handling.

If you really cannot use those libraries though, you'll need at the very bare minimum a semi-complete Discord Gateway Implementation before you can
even begin using LavaClient. An example of how you can handle just the rate limiting part [can be found here.](https://gist.github.com/SamOphis/766c62d6fb91bac77ea9f2dea0edb330)

As of the 14th June 2018, a [LavaClient integration for JDA](https://github.com/SamOphis/LavaClient-JDA) exists however the problem still remains for other libraries.

# Creating and Adding Nodes to a LavaClient Instance

You can create any number of `LavaClient` instances (although you only need one to handle a bot account and all of its shards) with this code:

```java
LavaClient client = new LavaClientBuilder()
        .setUserId(bot_user_id)
        .setShardCount(bot_shard_count)
        .build();
```

In the `LavaClientBuilder` constructor you can specify 0-3 parameters: `overrideJson`, `expireWriteMs` and `expireAccessMs`. If you don't specify
any, the default values are: `true`, `1200000`, `900000`.

Their meanings are:
* Should LavaClient enable the fastest Jsoniter Encoding/Decoding option (uses Javassist).
* How long should LavaClient wait to remove Cache Entries after being written.
* How long should LavaClient wait to remove Cache Entries after being accessed (resets write time).

Then, you can actually add `AudioNode`s to a client instance with the code:

```java
AudioNodeEntry node = new AudioNodeEntryBuilder(client)
        .setAddress("localhost")
        .build();
client.addEntry(node);
```

> Note: Many more methods are available in the builders, but these are the bare minimums needed to connect.

The node is automatically connected to however any created `LavaPlayer`s don't send Voice Updates until their `connect` method is called.
It's strongly encouraged to not use that method as it is and instead use the `EventWaiter`.

More information can be found in the Wiki, so please check that out!

# Contributing

Contributions are always welcome if your contribution follows the [Contribution Guidelines](https://github.com/SamOphis/LavaClient/blob/master/.github/CONTRIBUTING.md).