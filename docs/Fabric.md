LeafClaims runs on Fabric for Minecraft 26.2 and 26.3 — on dedicated servers, in
singleplayer, and in worlds opened to LAN where friends join with a **plain vanilla
client**. The two game versions need separate jars; pick the one matching your game.

## Install
- **Dedicated server**: put `LeafClaims-Fabric-2.0.0+mc.26.3.jar` (or the `+mc.26.2` jar on a
  26.2 server) and
  [Fabric API](https://modrinth.com/mod/fabric-api) in `mods/`. Files live in
  `config/leafclaims/`.
- **Singleplayer / LAN host**: put the same two jars in your client's `mods/` folder.
  Settings: `.minecraft/config/leafclaims/` (one config shared by all your worlds);
  each world's claims: `saves/<world>/leafclaims/`.

Requirements: Minecraft 26.3 needs Fabric Loader 0.19.5+; Minecraft 26.2 needs Loader
0.19.3+. Both need Fabric API and Java 25. Everything else — including the ClopLib Fabric
port and the server-side GUI library — is bundled inside the jar.

## Where files live
| Installation | Config | Claims database |
|---|---|---|
| Dedicated server | `config/leafclaims/` | `config/leafclaims/` |
| Singleplayer / LAN | `config/leafclaims/` (shared) | `saves/<world>/leafclaims/` (per world) |

## LAN rules
- The world owner always has LeafClaims operator rights in their own world.
- LAN guests never inherit operator rights from "Allow Commands" — `fabric.yml`
  (`lan_guests_follow_operator_level`, default `false`) controls this.
- Vanilla guests install nothing: claiming, `/claimgui` and claim warps all work on
  a vanilla client, through server-side screens and anvil text prompts.
- Note: vanilla commands such as `/fill` bypass any protection mod when cheats are
  enabled, so "Allow Commands" is itself a trust decision.

## Cross-server
Cross-server messaging (Redis or proxy plugin messages) is available on dedicated
servers only. It is disabled on singleplayer/LAN worlds. Every server sharing a
LeafClaims database or broker must run the same LeafClaims version.

## Known limits
- Toggling some settings requires a restart to apply command registrations.
- There are no client-side features; the mod is invisible to vanilla clients apart
  from the screens it opens.
