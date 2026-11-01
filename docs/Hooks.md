LeafClaims offers several built-in hooks providing support for other plugins. These hooks can be enabled or disabled in the `hooks` section of the plugin [config](Config.md).

| Name                               | Description                                   | Link                                              |
|------------------------------------|-----------------------------------------------|---------------------------------------------------|
| [Vault](#vault)                    | Economy support for claim blocks              | https://www.spigotmc.org/resources/vault.34315/   |
| [LuckPerms](#luckperms)            | Trust tags for LuckPerms groups               | https://luckperms.net/                            |
| [Plan](#plan)                      | Display claim analytics in Plan               | https://www.playeranalytics.net/                  |
| [PlaceholderAPI](#placeholderapi)  | Provides LeafClaims placeholders              | https://placeholderapi.com/                       |
| [WorldGuard](#worldguard)          | Prevent claiming over flag-restricted regions | https://enginehub.org/worldguard                  |
| [Dynmap](#dynmap-pl3xmap-bluemap)  | Add claim markers to your Dynmap              | https://www.spigotmc.org/resources/dynmap.274/    |
| [Pl3xMap](#dynmap-pl3xmap-bluemap) | Add claim markers to your Pl3xMap             | https://modrinth.com/plugin/pl3xmap/              |
| [BlueMap](#dynmap-pl3xmap-bluemap) | Add claim markers to your BlueMap             | https://www.spigotmc.org/resources/bluemap.83557/ |

## Vault
If Vault (and a compatible economy plugin) is installed, the `/buyclaimblocks` command will be enabled allowing users to [purchase claim blocks for money](claim-blocks#buying-claim-blocks).

## LuckPerms
If LuckPerms is installed, LeafClaims will register [Trust Tags](trust#trust-tags) for every defined LuckPerms group in the format `#role/(group_name)` &mdash; e.g. `#role/admin`. This allows you to easily grant trust to all members of a LuckPerms group, particularly useful in admin claims.

## Plan
If Plan is installed, LeafClaims will display LeafClaims analytics (such as Claim Blocks and Claims created) in the Plan web interface on user and server pages.

## PlaceholderAPI
If PlaceholderAPI is installed, LeafClaims will register a PlaceholderAPI expansion allowing you to use LeafClaims placeholders in other plugins that support PlaceholderAPI. The following placeholders are available:

| Placeholder                              | Description                                                                | Example                           |
|------------------------------------------|----------------------------------------------------------------------------|-----------------------------------|
| `%leafclaims_claim_blocks%`              | The number of [claim blocks](Claim-Blocks.md) the player has                              | `100`, `1234`, etc.               |
| `%leafclaims_current_is_claimed%`        | Whether the player is currently standing in a claim                        | `true` or `false`                 |
| `%leafclaims_current_claim_owner%`       | The owner of the claim the player is standing in                           | `Steve`, `an administrator`, etc. |
| `%leafclaims_current_claim_is_trusted%`  | Whether the player has a [trust level](Trust.md) in the claim they are standing in | `true` or `false`                 |
| `%leafclaims_current_claim_trust_level%` | The trust level of the player in the claim they are standing in            | `Access`, `Container`, etc.       |
| `%leafclaims_can_build%`                 | Whether the player can build in the claim they are standing in             | `true` or `false`                 |
| `%leafclaims_can_open_containers%`       | Whether the player can open containers in the claim they are standing in   | `true` or `false`                 |
| `%leafclaims_can_interact%`              | Whether the player can interact in the claim they are standing in          | `true` or `false`                 |

## WorldGuard
If WorldGuard is installed, LeafClaims will register a third party flag (`leafclaims-claim`), which when set to "Deny" in a WorldGuard region will prevent players from creating or resizing a claim over that region.

## Dynmap, Pl3xMap, BlueMap
If one of the supported mapping plugins is installed, LeafClaims will add region markers for claims on your server map:

* [Dynmap](https://www.spigotmc.org/resources/dynmap.274/) (v3.2+)
* [Pl3xMap](https://modrinth.com/plugin/pl3xmap/) (v1.20.4+)
* [BlueMap](https://www.spigotmc.org/resources/bluemap.83557/) (v3.20+)

You can configure the marker label name and which types of claims to show in the config file.