# LeafClaims 2.0.0

LeafClaims 2.0.0 is the first release published under this name. It removes every dependency
on another William278 project: each library LeafClaims used to compile against is now ordinary
source in this repository, and the build resolves nothing from `repo.william278.net`.

This is a major version because it removes behaviour and renames the plugin, not only because
of packaging. If you are arriving from HuskClaimsPlus 1.1.0, read the upgrade section before
you install: your existing data and permissions will not carry over.

## Which jar do I need?

The release carries four jars. Which one you want depends on your server software and its
Minecraft version, and the Java requirement is not the same for all of them.

| Jar | For | Minecraft | Java |
|---|---|---|---|
| `LeafClaims-Paper-2.0.0.jar` | Paper servers | 26.3, 26.2 | **25** |
| `LeafClaims-Fabric-2.0.0+mc.26.3.jar` | Fabric, game 26.3 | 26.3 | **25** |
| `LeafClaims-Fabric-2.0.0+mc.26.2.jar` | Fabric, game 26.2 | 26.2 | **25** |
| `LeafClaims-Bukkit-2.0.0.jar` | Spigot-family servers | untested | **21** |

Most people want the first row. On a Paper server, install `LeafClaims-Paper-2.0.0.jar` and
nothing else. It is the complete plugin.

**Use the Fabric jar that matches your game version.** The two Fabric builds contain compiled
code for different Minecraft versions and are not interchangeable. Minecraft 26.3 needs Fabric
Loader 0.19.5 or newer; Minecraft 26.2 needs 0.19.3 or newer. Both need Fabric API installed
separately; everything else the mod needs is inside the jar. Each jar accepts the hotfix
releases of its own version, so a 26.3.x update does not need a new download.

**The Bukkit jar is the base layer that the Paper jar is built on**, published separately
because it also runs on its own. It contains no Paper classes, so a Spigot-family server will
load it. LeafClaims does not test on Spigot and does not claim support for it: treat that
combination as untested. It is compiled for Java 21, which is why its requirement differs from
the others.

Folia is marked supported in the Paper plugin manifest, a declaration inherited from upstream
HuskClaims. LeafClaims has never run on Folia, so treat that marking as untested too.

These figures come from the jars, not from an earlier draft of the documentation: the
Minecraft and loader ranges are read from the `fabric.mod.json` inside each Fabric jar, and the
Java versions from the class-file version of the compiled classes.

## Read this before upgrading

**The plugin is now called LeafClaims.** It was HuskClaimsPlus up to and including 1.1.0, the
last release under that name. The rename is not cosmetic: the command is `/leafclaims`, the
data folder is `plugins/LeafClaims`, the database tables are `leafclaims_*` and every
permission node is under `leafclaims.*`. Existing config, database and permission setups are
**not** migrated; a 2.0.0 install starts empty. Copy your settings across by hand if you want
to keep them, and re-grant permissions to your groups.

Three features no longer work at all:

**Setting or moving a home inside a claim is no longer restricted.** The HuskHomes hook used to
cancel home creation and editing inside claims where the player lacked `RESIDENT_SET_HOME`.
Without HuskHomes that restriction is gone.

**Town claims are no longer checked against LeafClaims claims.** The HuskTowns hook cancelled a
HuskTowns claim when the new chunk overlapped a LeafClaims claim. That check is gone, so
overlapping claims are now possible if both plugins run together.

**Cross-server teleport is gone.** `/leafclaims teleport` works within one server only. Asking
for a player on another server now fails with a clear message instead of routing through
HuskHomes.

If you relied on any of these, stay on HuskClaimsPlus 1.1.0. It remains available and
unchanged.

## What changed in the build

Nothing user-facing. The libraries LeafClaims used to download are now source in this
repository: ClopLib 2.0.13, Toilet 1.1.2, DesertWell 2.0.4 and Paginedown 1.1.2. They are still
shaded and relocated into the shipped jar, so the packaged output behaves as before.

Absorbing Toilet also removed a version mismatch: the build had been using three different
Toilet versions at once — 1.1.1 in the shared module, 1.0.15 for Bukkit and 1.1.2 for Fabric.
All three are now 1.1.2.

A clone builds with no upstream Maven repository configured and no credentials, once the
ordinary public dependencies are cached.

## Fixed since the first 2.0.0 build

**The empty tooltip box in the Claim Menu is gone.** On Paper, each empty menu slot holds a filler
pane whose display name is a single space. A name that renders as nothing still made the client
draw the empty frame around it, so moving the cursor across the grid showed a small dark box with
no text in it. The filler now asks the server to suppress the tooltip entirely, using a method the
plugin looks up at runtime, so it still loads on a server whose API predates it. Only names that
render as blank are suppressed, so every real button keeps its tooltip, and the Fabric menu needed
no change because it already hid its tooltips.

Nothing else changed. The four jars on this release were rebuilt from a clean tree after the fix;
the earlier build that some of you may have been handed did not work around it.

## Verification

All four jars are built from this repository by its own build, and the test suite passes: 267
test methods, no failures. The jar contents were compared against 1.1.0; the only classes
removed are the Husk hook classes, and the only additions are a newer version of Adventure,
which the plugin already bundled.

Gameplay testing on a live server is still outstanding for this release: nobody has played a
full session on it. The Paper jar has been started on Minecraft 26.2 (build 135) with Java 25,
where the plugin enables cleanly and the server reaches "Done" with no errors, and the menu fix
was confirmed against that running server. Paper 26.3, both Fabric builds and Folia have not been
run at all, so those are the areas least exercised. Treat them as untested rather than endorsed.

Check your download against `SHA256SUMS.txt` on the release page.
