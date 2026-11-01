<table align="right">
    <thead>
        <tr><th colspan="2">Key</th></tr>
    </thead>
    <tbody>
        <tr><td>✅</td><td>Accessible by all players by default</td></tr>
        <tr><td>❌</td><td>Accessible only by server operators by default</td></tr>
    </tbody>
</table>

LeafClaims provides permissions for restricting access to commands and features. 

These permissions are detailed below.

## Claim Menu (GUI)

| Permission                          | Description                                                                                   | Default |
| ----------------------------------- | --------------------------------------------------------------------------------------------- | ------- |
| `leafclaims.command.leafclaimgui`   | Open the Claim Menu with `/claimgui`.                                                          | ✅       |
| `leafclaims.gui.claim_tool`         | Get a claim tool from the Claim Menu's tool button.                                            | ✅       |
| `leafclaims.gui.public_permissions` | Use the Public Permissions screen (each action also needs `leafclaims.flag.<action>`).         | ✅       |

See [Claim Menu](Claim-Menu.md) for how the GUI re-checks command permissions per click.

## Commands
Please see the [Commands](Commands.md) page reference for a full list of commands and their permissions.

## Inspection
These permissions restrict the ability to inspect claims.

| Permission                          | Description                                                                                   | Default |
|-------------------------------------|-----------------------------------------------------------------------------------------------|---------|
| `leafclaims.inspect`                | Inspect claims with the inspection tool.                                                      | ✅       |
| `leafclaims.inspect.nearby`         | Inspect all nearby claims by holding SNEAK and using the inspection tool.                     | ✅       |
| `leafclaims.inspect.view_last_seen` | When inspecting, whether the user can see how many days since the claim owner last logged on. | ❌       |

## Claims
These permissions restrict the ability to create certain types of claims. See [Claims](Claims.md) for more details on claiming land. It is also possible to disable creating admin/child claims in the plugin config.

| Permission               | Description                                                      | Default |
|--------------------------|------------------------------------------------------------------|:-------:|
| `leafclaims.claim`       | Create regular user-owned claims.                                |    ✅    |
| `leafclaims.admin_claim` | Create admin claims, **and manage all other admin claims.**      |    ❌    |
| `leafclaims.child_claim` | Create child claims; sub-divisions of land within parent claims. |    ✅    |

## Claim Blocks
These numerical permissions let you control the number of [claim blocks](Claim-Blocks.md) to award players each hour, and specify the upper limit. By default, these permission nodes are not granted and the [config](Config.md) file defaults are used for all players.

| Permission                             | Description                                                                            |
|----------------------------------------|----------------------------------------------------------------------------------------|
| `leafclaims.hourly_blocks.(amount)`    | Specify the integer `(amount)` of claim blocks this player should receive hourly.      |
| `leafclaims.max_claim_blocks.(amount)` | Specify the maximum integer `(amount)` of claim blocks this player can have and spend. |


## Trust Tags
These permissions restrict being able to use certain trust tags when granting trust to other players. See [Trust](Trust.md) for more details on trust tags.

| Permission                   | Description                                                                                                    | Default |
|------------------------------|----------------------------------------------------------------------------------------------------------------|:-------:|
| `leafclaims.trust.public`    | Use the `#public` trust tag in claims to grant public access.                                                  |    ✅    |
| `leafclaims.trust.luckperms` | Use the `#role/(name)` trust tags in claims to grant LuckPerms role-based access. Requires the LuckPerms hook. |    ❌    |

You can turn off the permission requirement for using LuckPerms groups in claims in the [config](Config.md) hook settings. 

## Ignore Claims Permissions
These permissions control what users can bypass when they have the ignore claims feature toggled on. The ignore claims feature is stateful and user-toggleable, providing granular control over claim restrictions.

| Permission                                    | Description                                                                                    | Default |
|-----------------------------------------------|------------------------------------------------------------------------------------------------|:-------:|
| `leafclaims.command.ignoreclaims.bans`        | Allow bypassing claim bans when ignoring claims.                                               |    ❌    |
| `leafclaims.command.ignoreclaims.private`     | Allow bypassing private claim restrictions when ignoring claims.                               |    ❌    |
| `leafclaims.command.ignoreclaims.operations.*` | Allow bypassing all operation types when ignoring claims.                                      |    ❌    |
| `leafclaims.command.ignoreclaims.operations.<operation_type>` | Allow bypassing specific operation types when ignoring claims (e.g., `block_break`, `redstone_actuate`). |    ❌    |

## Claim Warps
These permissions restrict the use of [Claim Warps](Claim-Warps.md) — public teleport anchors set by claim owners. Managing a warp additionally requires being the claim owner, or having the `MANAGE_WARP` [Trust](Trust.md) privilege within the claim.

| Permission                    | Description                                                                                      | Default |
|-------------------------------|--------------------------------------------------------------------------------------------------|---------|
| `leafclaims.command.claimwarp` | Use `/claimwarp set/unset/rename` to manage the warp of a claim you can manage.                  | ✅       |
| `leafclaims.command.claimwarp.other` | Set, rename or unset the warp of another user's claim, and use any trusted-only warp from the Public Claims menu (admin override). | ❌       |
| `leafclaims.command.visit`    | Teleport to public claim warps with `/visit <name>`.                                             | ✅       |
| `leafclaims.command.visits`   | Browse the list of public claim warps with `/visits`.                                            | ✅       |

## Flags
These permissions restrict the use of flags.

| Permission                       | Description        | Default |
|----------------------------------|--------------------|:-------:|
| `leafclaims.flag.<name of flag>` | Access to the flag |    ✅    |