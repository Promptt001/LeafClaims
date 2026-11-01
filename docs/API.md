The LeafClaims API provides methods for interfacing with and editing claims and users, alongside a selection of API events for listening to when players perform certain actions.

## Compatibility
The API is not published to a Maven repository yet. Build it from source with the Gradle
build in this repository; the coordinates below are the ones it will use when published.

The LeafClaims API shares version numbering with the plugin itself for consistency and convenience. Please note minor and patch plugin releases may make API additions and deprecations, but will not introduce breaking changes without notice.

| API Version | LeafClaims Versions  | Supported |
|:-----------:|:--------------------:|:---------:|
|    v1.x     | _v1.0&mdash;Current_ |     ✅     |


### Platforms
The LeafClaims API is available for the following platforms:

* `bukkit` - Bukkit, Spigot, Paper, etc. Provides Bukkit API event listeners and adapters to `org.bukkit` objects.
* `fabric` - Fabric, Quilt, etc. Provides Fabric API event callbacks and adapters to `net.minecraft` objects.
* `common` - Common API for all platforms.

## Table of contents
1. Adding the API to your project
2. Adding LeafClaims as a dependency
3. Next steps

## API Introduction
### 1.1 Setup with Maven
<details>
<summary>Maven setup information</summary>

Once the API is published, add the repository serving it to your `pom.xml`.
Add the dependency to your `pom.xml` as per below. Replace `PLATFORM` with your target platform (see above) and `VERSION` with the latest version of LeafClaims (without the v): ![Latest version](https://img.shields.io/github/v/tag/Promptt001/LeafClaims?color=%23282828&label=%20&style=flat-square). Note for Fabric you must append the target Minecraft version to the version number (e.g. `1.5+1.21.1`).
```xml
<dependency>
    <groupId>net.promptt001.leafclaims</groupId>
    <artifactId>leafclaims-PLATFORM</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```
</details>

### 1.2 Setup with Gradle
<details>
<summary>Gradle setup information</summary>

Add the dependency as per below to your `build.gradle`, pointing at whichever repository serves the
API once it is published.
Add the dependency as per below. Replace `PLATFORM` with your target platform (see above) and `VERSION` with the latest version of LeafClaims (without the v): ![Latest version](https://img.shields.io/github/v/tag/Promptt001/LeafClaims?color=%23282828&label=%20&style=flat-square). Note for Fabric you must append the target Minecraft version to the version number (e.g. `1.5+1.21.1`).

```groovy
dependencies {
    compileOnly 'net.promptt001.leafclaims:leafclaims-PLATFORM:VERSION'
}
```
</details>

### 2. Adding LeafClaims as a Bukkit plugin dependency
Add LeafClaims to your `softdepend` (if you want to optionally use LeafClaims) or `depend` (if your plugin relies on LeafClaims) section in `plugin.yml` of your project.

```yaml
name: MyPlugin
version: 1.0
main: net.william278.myplugin.MyPlugin
author: William278
description: 'A plugin that hooks with the LeafClaims API!'
softdepend: # Or, use 'depend' here
  - LeafClaims
```

## 3. Creating a class to interface with the API
- Unless your plugin completely relies on LeafClaims, you shouldn't put LeafClaims API calls into your main class, otherwise if LeafClaims is not installed you'll encounter `ClassNotFoundException`s

```java
public class LeafClaimsAPIHook {

    public LeafClaimsAPIHook() {
        // Ready to do stuff with the API
    }

}
```
## 4. Checking if LeafClaims is present and creating the hook
- Check to make sure the LeafClaims plugin is present before instantiating the API hook class

```java
public class MyPlugin extends JavaPlugin {

    public LeafClaimsAPIHook leafClaimsAPIHook;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("LeafClaims") != null) {
            this.leafClaimsAPIHook = new LeafClaimsAPIHook();
        }
    }
}
```

## 5. Getting an instance of the API
- You can now get the API instance by calling `LeafClaimsAPI#getInstance()`

```java
import net.promptt001.leafclaims.api.BukkitLeafClaimsAPI;

public class LeafClaimsAPIHook {

    private final BukkitLeafClaimsAPI leafClaimsAPI;

    public LeafClaimsAPIHook() {
        this.leafClaimsAPI = BukkitLeafClaimsAPI.getInstance(); // Or, LeafClaimsAPI.getInstance() for the common API
    }

}
```

## 6. CompletableFuture and Optional basics
- LeafClaims's API methods often deal with `CompletableFuture`s and `Optional`s.
- A `CompletableFuture` is an asynchronous callback mechanism. The method will be processed asynchronously and the data returned when it has been retrieved. Then, use `CompletableFuture#thenAccept(data -> {})` to do what you want to do with the `data` you requested after it has asynchronously been retrieved, to prevent lag.
- An `Optional` is a null-safe representation of data, or no data. You can check if the Optional is empty via `Optional#isEmpty()` (which will be returned by the API if no data could be found for the call you made). If the optional does contain data, you can get it via `Optional#get().

> **Warning:** You should never call `#join()` on futures returned from the LeafClaims API as futures are processed on server asynchronous tasks, which could lead to thread deadlock and crash your server if you attempt to lock the main thread to process them.

### 7. Next steps
Now that you've got everything ready, you can start doing stuff with the LeafClaims API!

Members added by LeafClaims are marked `@since LeafClaims <version>` (for example
`@since LeafClaims 1.0.0`). Every other `@since` value refers to an upstream HuskClaims
version, which is what it shipped in there.
- [Claims API](Claims-API.md) &mdash; Get, create, resize, & delete [claims](Claims.md), child claims, and admin claims, and manage [claim blocks](Claim-Blocks.md)
- [Trust API](Trust-API.md) &mdash; Trust users, groups and trust tags in claims, and provide custom [trust tags](trust#trust-tags)
- [Highlighter API](Highlighter-API.md) &mdash; Provide a custom claim highlighter
- [Operations API](Operations-API.md) &mdash; Register operation types and create Operations using them to have LeafClaims protect against custom actions 
- [API Events](API-Events.md) &mdash; Listen to events when players perform certain actions
