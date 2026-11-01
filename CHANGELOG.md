# Changelog

All notable changes to LeafClaims. LeafClaims has its own version line; from 1.0.0 it is
based on HuskClaims 1.5.11 (upstream `master` at `51e80fd`; the latest upstream release is
1.5.10).

LeafClaims was called HuskClaimsPlus up to and including 1.1.0. The 1.0.0 and 1.1.0
entries below are HuskClaimsPlus releases; 2.0.0 is the first release under the LeafClaims
name.

## 2.0.0 — renamed to LeafClaims, self-contained spin-off

LeafClaims no longer depends on any other William278 project. Everything it used to compile
against is now ordinary source in this repository, and no `net.william278` coordinate is
resolved from any Maven repository. A clone builds with no upstream repository configured and
no credentials.

This is a major version because it removes behaviour and changes the plugin's name and data
identity, not only packaging.

### Renamed: HuskClaimsPlus is now LeafClaims
- The plugin, the command, the Java package, the jar names and the documentation all change. The
  command is `/leafclaims`, the package is `net.promptt001.leafclaims`, and the jars are
  `LeafClaims-*`.
- The data identity changes with the name: the data folder is `plugins/LeafClaims`, the tables are
  `leafclaims_metadata`, `leafclaims_users`, `leafclaims_user_groups` and `leafclaims_claim_worlds`,
  and every permission node is under `leafclaims.*`.
- There is deliberately no migration or alias from the HuskClaimsPlus names. A 2.0.0 install
  starts empty; move your settings across by hand and re-grant permissions to your groups.
- Upstream is still HuskClaims and is still referred to by that name. HuskHomes, HuskTowns and
  HuskSync are separate projects and keep their own names.

Three things also stop working: the HuskHomes home restriction, the HuskTowns claim-overlap
check, and cross-server teleport.

### Removed: Husk suite hooks
- The HuskHomes hook is gone. It did two things: it routed claim teleports through the HuskHomes
teleport system, and it cancelled home creation and editing inside claims where the player
lacked `RESIDENT_SET_HOME`. Without it, teleports are instant and that restriction is not
enforced.
- The HuskTowns hook is gone. It cancelled a town claim when the new chunk overlapped a
LeafClaims claim. Overlap is no longer blocked against HuskTowns.
- HuskHomes and HuskTowns are no longer listed in `plugin.yml` or `paper-plugin.yml`.

### Removed: cross-server teleport
- `/leafclaims teleport` now works within one server only. Targeting a player on another server
fails with `error_cross_server_unsupported`, which replaced the `error_huskhomes_not_found`
key in all seven locales. The message text is still English in every locale.

### Changed: libraries absorbed as source
- ClopLib 2.0.13, Toilet 1.1.2, DesertWell 2.0.4 and Paginedown 1.1.2 are now source under
`net.william278.cloplib`, `.toilet`, `.desertwell` and `.paginedown`, and are relocated into
`net.promptt001.leafclaims.libraries.*` in the Paper jar as before.
- Toilet was pinned three different ways (`toilet-common` 1.1.1, `toilet-bukkit` 1.0.15,
`toilet-fabric` 1.1.2+26.2); it is now one 1.1.2 source tree.
- The absorbed trees keep their original Apache-2.0 headers, and `checkLicenseMain` excludes them.

### Fixed: empty tooltip box in the Claim Menu
- On Paper, every empty menu slot holds a filler pane whose display name is a single space. A name
  that renders as nothing still made the client draw an empty tooltip frame, so moving the cursor
  across the grid showed a small dark bordered box with no text inside it. The filler now asks the
  server to suppress the tooltip outright (`ItemMeta#setHideTooltip`), resolved at runtime so the
  plugin still loads on a server whose API predates it. The Fabric menu already hid its tooltips.
- The blank-name test ignores colour codes. Names pass through `GuiText.asName()`, which prefixes
  `§f`, so the filler's single space is stored as `§f ` and a plain `String#isBlank()` check said
  it was not blank. The tooltip was therefore never hidden and the box stayed. `GuiText.rendersBlank`
  now makes that decision, and a unit test covers the coloured-space case. If the server lacks the
  method, or the call fails, the plugin logs one warning instead of failing silently.

### Verified
- All four targets build, and 267 test methods pass with no failures.
- A fresh clone with an empty Gradle cache builds all targets from zero downloads of any
`william278` artifact.

## 1.1.0 — 2026-10-27 (released as HuskClaimsPlus)

Adds **Minecraft 26.3** support to both editions. There are no changes to commands, permission
nodes, settings, message keys or the database schema, so upgrading from 1.0.0 needs nothing
but the new jar. The 26.2 artifacts still build and ship from the same tag.

### Added: Paper 26.3
- Builds against Paper API `26.3.build.226-stable` (Java 25).
- No source changes were needed in `common`, `bukkit` or `paper`: the 26.2 code compiles and
  runs unchanged against the 26.3 API, which is why the 26.3 Paper jar is the same code as
  the 26.2 one, built against the newer API.

### Added: Fabric for Minecraft 26.3
- `fabric/26.3` build node, releasing `LeafClaims-Fabric-1.1.0+mc.26.3.jar`. Java 25;
  Fabric Loader 0.19.5+; Fabric API 0.161.0+26.3; sgui 2.2.1+26.3; ClopLib fork
  2.0.13.1+26.3. Declares `minecraft ~26.3` and `fabricloader >=0.19.5`.
- The 26.2-specific code paths stay behind hand-written `//#if MC>=260300` guards, which the
  multi-version preprocessor resolves per node. Verified in the generated 26.3 sources:
  `Inventory.dropAll` now calls `Player.createItemStackToDrop(ItemStack, boolean, boolean)`
  (26.3 moved the second boolean into `net.minecraft.util.Prediction`), sign text updates
  take a `SignTextSlot`, `ServerEntity` takes an `UpdateInterval`, and
  `setInvulnerable` became `setPermanentlyInvulnerable`.
- **HuskHomes on Fabric is a deliberate no-op on this node.** No `huskhomes-fabric` build
  exists for 26.3 (verified against the William278 Maven metadata, whose newest Fabric
  artifact is `4.11+26.2`), so the 26.3 node compiles a no-op `FabricHuskHomesHook` and does
  not declare the dependency. The hook is never registered, because it only registers when
  HuskHomes is present. If a 26.3 HuskHomes ships later, delete that override, restore the
  shared hook and re-add the dependency.
- Toilet stays on `1.1.2+26.2`: that is the newest published `toilet-fabric` artifact, and it
  is what the 26.3 node bundles.

### Verified
- `:common:build :bukkit:build` on JDK 21: 264 test methods in 39 classes, no failures.
- `:paper:build` (JDK 25) and `:fabric:26.2:build`, `:fabric:26.3:build` (JDK 25): all
  successful, including the `checkNoClientClasses` and Fabric gametest compilation checks.

### Known issues
- Carried over from 1.0.0 and **still present on the 26.2 jars: LeafClaims does not start
  next to HuskHomes for Fabric.** On Fabric every mod shares one class loader, so a file at the
  same resource path is resolved once for the whole server. Both plugins ship
  `database/sqlite_schema.sql` (and the MySQL/MariaDB schemas, the `migrations/*.sql` scripts,
  `fabric.mod.json` and the `locales/*.yml` files) under identical paths inside their bundled
  common jars. HuskHomes therefore reads LeafClaims' schema and fails at startup with
  `No enum constant net.william278.huskhomes.database.Database.Table.USER_DATA`. Paper is not
  affected, since each plugin has its own class loader. This release does not fix it; the fix
  is to relocate those resources inside the nested jar, as noted for 1.0.1. On the **26.3**
  jar the situation cannot arise, because no HuskHomes build exists for 26.3 and the hook is a
  no-op.
- Menu and warp texts are English in every locale file.
- Gifting claim blocks needs `leafclaims.command.claimblocks.gift`, which is operator-only by
  default (as in upstream).
- Folia is not tested on 26.3.
- Integrations kept from upstream were not re-tested on 26.3 for this release: LuckPerms,
  Vault and Impactor economy, PlaceholderAPI, Plan, HuskTowns, WorldGuard, Geyser/Floodgate,
  BlueMap, Dynmap and Pl3xMap, cross-server sync (MySQL/MariaDB/MongoDB, Redis or plugin
  messaging) and the GriefPrevention importer.

## 1.0.0 — 2026-10-19 (released as HuskClaimsPlus)

First release of HuskClaimsPlus, for **Paper 26.2** and **Fabric 26.2** (dedicated
servers, singleplayer and Open to LAN). Drop-in upgrade from HuskClaims 1.5.x and from
HuskClaims with Visits 1.5.11-visits.1: same plugin name, data folder, database, commands
and permission nodes.

That release was published from a repository whose history included the upstream commits. The
LeafClaims repository begins at 2.0.0 with a fresh history, so a commit-by-commit comparison
is no longer available here.
Tests: 250 automated test methods (upstream has 3), plus a Fabric game test that runs a
real singleplayer world and an Open to LAN session.

### Added: Claim Menu (Paper and Fabric)
- `/claimgui` and `/leafclaimgui` open the Claim Menu (`gui.enabled`, default on;
  permission `leafclaims.command.leafclaimgui`, everyone).
- Main menu: Public Claims (while warps are enabled), Friends & Trust, My Claims, Claim
  tool, Claim Blocks.
- My Claims: paged, sortable, child claims, Create Claim. Claim settings: rename, privacy,
  Friends & Trust, Public Permissions, trusted players, child claims, resize (edge picker
  and stepper), claim warp, unclaim. Friends & Trust hub, trusted-players list and
  trust-level picker; in the menu, trusting everyone is capped at
  `gui.public_access.max_level` (default `container`; the `/trust` command is unchanged).
  Public Permissions: single actions for everyone (each needs
  `leafclaims.flag.<action>` and `leafclaims.gui.public_permissions`) and the warp's
  public/trusted-only switch. Claim Blocks: balance, buy, gift. Public Claims: warp
  browser. A confirmation screen before anything that changes a claim or spends blocks.
- Create and resize from the menu with a live claim-block cost; by default without
  holding the claim tool (`gui.create_claim.bypass_tool_requirement`); the `claim`
  command permission is always required.
- Claim tool button (`leafclaims.gui.claim_tool`): 60 s cooldown that survives relogging
  (`gui.claim_tool_cooldown_seconds`), never while you already carry one; a handed-out
  tool cannot be smelted, used as fuel, or repaired into an unmarked tool.
- Every button runs the real LeafClaims command or claim editor with the same permission,
  cooldown and trust-privilege checks, against the claim shown in the menu, looked up
  again on every click. Buttons do not ask for input when you lack the permission the
  command will check.
- Configurable layout: slots, rows, icons (`gui.icon_materials`) and filler. A layout
  version backs up `config.yml` before new defaults replace changed layout settings.
- Paper: inventory menus, chat prompts (`gui.input_timeout_seconds`; `gui.allow_name_prefix`
  for Geyser/Floodgate name prefixes). Fabric: server-side screens (sgui) and anvil
  prompts; vanilla clients need nothing.
- 373 new message keys (350 for the menu) in all 7 locale files, English text
  for now. Existing message files gain missing keys automatically; when the bundled menu
  texts change (`gui_locale_version`), menu texts are refreshed after a backup. Other
  customised messages are never touched.

### Added: claim warps
- `/claimwarp set <name>`, `/claimwarp rename <name>`, `/claimwarp unset`: claim owner or
  `MANAGE_WARP` trust (`leafclaims.command.claimwarp`; admin override
  `leafclaims.command.claimwarp.other`). A warp set inside a child claim belongs to the
  parent claim.
- `/visit <name>` (`leafclaims.command.visit`) and `/visits [page]`
  (`leafclaims.command.visits`) for public warps. The Public Claims menu lists public warps
  plus the trusted-only warps the player may use.
- Safe-landing check when a warp is set and before every visit; ban, private-claim and
  trusted-only checks before every visit; a warp left outside its claim after a resize is
  refused; per-player visit cooldown (`claims.warps.visit_cooldown_seconds`, 3 s).
- Public or trusted-only warps; server-unique names made of letters, numbers, `_`, `.` and
  `-` (`claims.warps.max_name_length` 32, `claims.warps.enforce_unique_names` on).
- New trust privilege `MANAGE_WARP`, included in the default Manage level.
- Stored with the claim (`claim_warp`); claims without a warp are unchanged.
- `claims.warps.enabled` (default on) removes `/claimwarp`, `/visit` and `/visits` when off.

### Added: claim names
- Top-level player claims get default names (`<owner>_<NN>`) you can rename from the menu;
  setting a warp names the claim after it unless you chose a name. Stored in claim metadata.

### Added: Fabric for Minecraft 26.2
- `fabric/26.2` build node on Mojang-named 26.x (the `fabric/26.3` node existed in the tree but
  shipped no artifacts in this release); Java 25; Fabric Loader 0.19.3+; Fabric API. Loads on
  26.2.x hotfixes (`~26.2`).
- Runs on dedicated servers **and** on the singleplayer client (`environment: "*"`):
  - a per-world database on integrated servers (`saves/<world>/leafclaims/`), one shared
    config per installation;
  - LAN permission policy: the world owner always has operator rights in LeafClaims; LAN
    guests never inherit them from "Allow Commands" (`fabric.yml`:
    `lan_guests_follow_operator_level`, default false);
  - a full session reset between worlds (listeners, commands, hooks, scheduler, caches,
    menus), with background work drained before the database closes;
  - cross-server messaging is off on singleplayer/LAN worlds.
- Bundles a ClopLib fork for 26.2 as a nested jar
  ([Promptt001/ClopLib 2.0.13.1](https://github.com/Promptt001/ClopLib/releases/tag/2.0.13.1)).

### Added: Paper 26.2
- Builds against Paper API 26.2 (Java 25).
- Vendored MineDown, patched for Adventure 5 (clickable chat messages crashed on 26.2).

### Changed
- New installs: the default Access and Container trust levels no longer allow placing or
  breaking vehicles (Build and Manage still do). Existing `trust_levels.yml` files are not
  changed.
- Update checks use LeafClaims's GitHub releases; no bStats metrics are sent.
- Display name LeafClaims in descriptors, logs, config headers and the about screen;
  plugin name, mod id, data folders, commands and permission nodes are unchanged. Support
  links point to this repository.
- Removed the Fabric 1.21.1/1.21.4/1.21.5/1.21.7 build nodes (use upstream for those).
- Upstream's CI and publishing workflows are replaced by a build-and-test workflow.

### Fixed (defects inherited from upstream)
- SQLite: every claim world created in one session got database id 1 (the row count was
  used as the id), so claim worlds could overwrite each other's rows. Now uses `RETURNING id`.
- Fabric: item placement (blocks, buckets, vehicles, spawn eggs) was checked at the
  player's position instead of the targeted block, letting players outside a claim place
  into it (and blocking players inside from placing anywhere).
- Fabric: block placement checked the wrong block for clicks on east, south and top faces.
- Fabric: the claim outline was drawn one block towards west/north.
- Fabric: seeds and crops on a block click were checked as block placement, not farming.
- `/leafclaims status` crashed on server versions such as `26.2.build.133-stable`; plugin
  versions with build metadata (`1.0.0+mc.26.2`) are parsed safely (upstream's Fabric
  version parsing fails on a release-shaped version).
- Build: version tokens in `plugin.yml` went stale across builds and failed on Gradle 9
  (non-string token map).

### Included from upstream HuskClaims since 1.5.10
The latest upstream HuskClaims release is 1.5.10 (July 2025). LeafClaims 2.0.0 is built on
upstream `master` at `51e80fd` (August 2026), so servers coming from 1.5.10 also get 36
upstream commits that no LeafClaims release contains yet. The ones a server owner or player
will notice (upstream pull request numbers in brackets):

- Added `/reduceclaim <blocks>`, the opposite of `/extendclaim`
  (`leafclaims.command.reduceclaim`, everyone; `.other` for other players' claims) [#480].
- Added MongoDB as a database type [#474].
- Added `claims.maximum_claim_radius` (default 100) for `/claim <radius>` [#456].
- `/ignoreclaims [on|off]` takes an explicit state, and what it bypasses is now controlled
  by permissions: `leafclaims.command.ignoreclaims.bans`, `.private` and
  `.operations.<operation_type>` (or `.operations.*`) [#439]. See the upgrade notes.
- Transferring a claim also moves its claim-block balance to the new owner [#447].
- Claims carry a persistent metadata map [#498] (LeafClaims stores claim names in it).
- `claims.unclaimable_worlds` accepts wildcards such as `speedrun_*` [#505].
- PlaceholderAPI: formatted and accrued claim-block placeholders [#446, #492].
- Player names in commands are case-insensitive [#485]; vanished players are hidden from
  tab completion [#467].
- Folia support [#340, #468] (not tested by LeafClaims).
- API: `BukkitHourlyClaimBlocksEvent` [#495].
- Fixed: end crystal explosions ignored the `explosion_damage_terrain` flag [#494];
  the `/transferclaim` permission check was inverted [#488]; `/extendclaim` showed the
  total blocks instead of the blocks still needed [#483]; water flow and similar operations
  were blocked between two claims of the same owner [#482]; the highest hourly claim-block
  permission was not always the one applied [#481]; SQLite concurrency and a database lookup
  race [#491, #496]; a claim scheduler race and an offset in claim outlines [#438].

The full upstream list: run `git log 1.5.10..51e80fd` in a clone of HuskClaims, or see the
[upstream compare view](https://github.com/WiIIiam278/HuskClaims/compare/1.5.10...51e80fd).

The commits behind that comparison are not in this repository: LeafClaims 2.0.0 is published
with a fresh history, so upstream hashes are only visible in the HuskClaims repository.

### Known issues
- Menu and warp texts are English in every locale file.
- Fabric: a claim made with the golden shovel gets its default name the first time you
  open My Claims (on Paper, at creation).
- Gifting claim blocks needs `leafclaims.command.claimblocks.gift`, which is
  operator-only by default (as in upstream).
- Folia is not tested.
- **Fabric: HuskClaimsPlus 1.0.0 does not start next to HuskHomes for Fabric.** On Fabric every
  mod shares one class loader, so a file at the same resource path is resolved once for the
  whole server. Both plugins ship `database/sqlite_schema.sql` (and the MySQL/MariaDB schemas,
  the `migrations/0-*-add_metadata_table.sql` scripts, `fabric.mod.json` and the `locales/*.yml`
  files) under identical paths inside their bundled common jars. HuskHomes therefore reads
  LeafClaims' schema and fails at startup with
  `No enum constant net.william278.huskhomes.database.Database.Table.USER_DATA`; it logs
  "HuskHomes has failed to enable successfully" and LeafClaims keeps running with its
  HuskHomes hook unregistered. Reproduced with HuskHomes 4.11 (Fabric 26.2): alone it enables
  cleanly, with LeafClaims installed it does not. **Paper is not affected** (each plugin
  has its own class loader). Fix in 1.0.1: relocate the schemas and locale files inside the
  nested jar. Until then, run HuskHomes on a Paper server or leave it out of Fabric mods.
- Integrations kept from upstream were not re-tested on 26.2 for this release: LuckPerms,
  Vault and Impactor economy, PlaceholderAPI, Plan, HuskTowns, WorldGuard, Geyser/Floodgate,
  BlueMap, Dynmap and Pl3xMap, cross-server sync (MySQL/MariaDB/MongoDB, Redis or plugin
  messaging) and the GriefPrevention importer. Their code is upstream's; please report
  anything that breaks. HuskHomes on Fabric was tested and is listed above under Known
  issues; HuskHomes on Paper was not re-tested.
- Cross-server messaging is not available on singleplayer/LAN worlds (by design).

### Upgrade notes
- Drop-in from HuskClaims 1.5.x. New config sections and messages are added on first start.
- From HuskClaims 1.5.10: staff who use `/ignoreclaims` through a permissions plugin, rather
  than as server operators, now also need `leafclaims.command.ignoreclaims.operations.*`
  (and `.bans` and `.private` if wanted). Without them `/ignoreclaims` turns on but
  bypasses nothing. Operators are not affected.
- From upstream HuskClaims 1.5.10: `/reduceclaim` is new and available to everyone by default, like
  `/extendclaim`. Deny `leafclaims.command.reduceclaim` if you do not want it.
- To let Manage-trusted players manage warps on an existing server, add `MANAGE_WARP` to the
  `manage` level's `privileges` in `trust_levels.yml`. Remove it again before switching back
  to upstream.
- All servers sharing a database or broker must run the same LeafClaims version;
  HuskClaimsPlus 1.0 and upstream 1.5 use different network channels on purpose.
- Switching back to upstream drops warps when upstream next saves a claim world.

## History
- **1.5.11-visits.1** (2026-10-13, "LeafClaims with Visits", repository since replaced):
  claim warps for Paper 26.2 only (`/claimwarp set|rename|unset` with
  `leafclaims.command.claimwarp`, `/visit` with `leafclaims.command.visit`, `/visits` with
  `leafclaims.command.visits`; admin override `leafclaims.command.claimwarp.other`).
  Superseded by 1.0.0, which keeps its data.
