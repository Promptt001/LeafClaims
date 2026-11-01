/*
 * This file is part of LeafClaims, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.promptt001.leafclaims.command;

import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.claim.Claim;
import net.promptt001.leafclaims.claim.ClaimWorld;
import net.promptt001.leafclaims.claim.ClaimWarpVisits;
import net.promptt001.leafclaims.user.CommandUser;
import net.promptt001.leafclaims.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Teleports a player to a public claim warp by name: {@code /visit <name>}.
 * <p>
 * Anti-trap: before teleporting, the claim is checked for privacy/bans, and the
 * anchor position is re-validated for safe ground (the owner may have modified
 * the terrain since the warp was set).
 *
 * @since LeafClaims 1.0.0
 */
public class VisitCommand extends OnlineUserCommand implements TabCompletable {

    protected VisitCommand(@NotNull LeafClaims plugin) {
        super(List.of("visit"), "[name]", plugin);
    }

    @Override
    public void execute(@NotNull OnlineUser executor, @NotNull String[] args) {
        final Optional<String> name = parseStringArg(args, 0);
        if (name.isEmpty()) {
            plugin.getLocales().getLocale("error_invalid_syntax", getUsage())
                    .ifPresent(executor::sendMessage);
            return;
        }

        // Find the claim with a matching PUBLIC warp name, across all claim worlds
        // (private warps are usable only via the GUI's Public Claims menu).
        // stream PUBLIC warp claims by name — getClaimByWarpName takes the
        // first match per world and only then filtering to public let a private
        // same-world duplicate shadow the public one.
        final Optional<Map.Entry<ClaimWorld, Claim>> match = plugin.getClaimWorlds().values().stream()
                .flatMap(w -> w.getPublicWarpClaims().stream()
                        .filter(c -> c.getWarp().getName().equalsIgnoreCase(name.get()))
                        .map(c -> Map.entry(w, c)))
                .findFirst();
        if (match.isEmpty()) {
            plugin.getLocales().getLocale("claim_warp_not_found", name.get())
                    .ifPresent(executor::sendMessage);
            return;
        }

        ClaimWarpVisits.visit(plugin, executor, match.get().getKey(), match.get().getValue());
    }

    @Nullable
    @Override
    public List<String> suggest(@NotNull CommandUser user, @NotNull String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        return plugin.getClaimWorlds().values().stream()
                .flatMap(w -> w.getPublicWarpClaims().stream())
                .map(c -> c.getWarp().getName())
                .distinct()
                .toList();
    }

}
