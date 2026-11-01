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

package net.promptt001.leafclaims.listener;

import com.google.common.collect.Lists;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.claim.Claim;
import net.promptt001.leafclaims.claim.ClaimWorld;
import net.promptt001.leafclaims.claim.Region;
import net.promptt001.leafclaims.config.Settings;
import net.promptt001.leafclaims.position.Position;
import net.promptt001.leafclaims.user.OnlineUser;
import net.promptt001.leafclaims.user.SavedUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Handler for when the claim creation and resize tool is used
 */
public interface InspectionToolHandler {

    String INSPECT_PERMISSION = "leafclaims.inspect";
    String INSPECT_NEARBY_PERMISSION = "leafclaims.inspect.nearby";
    String VIEW_LAST_SEEN_PERMISSION = "leafclaims.inspect.view_last_seen";

    // When the inspection tool (default: stick) is used
    default void onInspectionToolUse(@NotNull OperationUser opUser, @NotNull OperationPosition opPosition) {
        final OnlineUser user = (OnlineUser) opUser;
        final Position position = (Position) opPosition;
        if (!user.hasPermission(INSPECT_PERMISSION, true)) {
            getPlugin().getLocales().getLocale("no_inspecting_permission")
                    .ifPresent(user::sendMessage);
            return;
        }

        // Check that the world is claimable
        final Optional<ClaimWorld> optionalWorld = getPlugin().getClaimWorld(position.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }
        final ClaimWorld claimWorld = optionalWorld.get();

        getPlugin().runAsync(() -> {
            // Handle nearby claim inspecting
            final Settings.ClaimSettings settings = getPlugin().getSettings().getClaims();
            if (user.isSneaking() && settings.isAllowNearbyClaimInspection()
                    && user.hasPermission(INSPECT_NEARBY_PERMISSION, true)) {
                final int distance = settings.getInspectionDistance();

                final List<Claim> claims = claimWorld.getParentClaimsOverlapping(Region.around(position, distance));
                if (claims.isEmpty()) {
                    getPlugin().getLocales().getLocale("no_nearby_claims", Integer.toString(distance))
                            .ifPresent(user::sendMessage);
                    return;
                }
                highlightClaims(user, claims, distance);
                return;
            }

            // Handle single claim inspecting
            final Optional<Claim> claim = claimWorld.getClaimAt(position);
            if (claim.isEmpty()) {
                getPlugin().getLocales().getLocale("land_not_claimed")
                        .ifPresent(user::sendMessage);
                return;
            }
            highlightClaim(user, claim.get(), claimWorld);
        });
    }

    // Highlight the claim for the user and send a message
    private void highlightClaim(@NotNull OnlineUser user, @NotNull Claim claim, @NotNull ClaimWorld world) {
        final List<Claim> claims = Lists.newArrayList(claim);
        claims.addAll(claim.getChildren());
        getPlugin().getHighlighter(user).startHighlighting(user, user.getWorld(), claims);
        getPlugin().getLocales().getLocale("land_claimed_by", claim.getOwnerName(world, getPlugin()))
                .ifPresent(user::sendMessage);

        // Send "last seen..." message if the user has permission
        if (user.hasPermission(VIEW_LAST_SEEN_PERMISSION, false)) {
            claim.getOwner()
                    .flatMap(owner -> getPlugin().getSavedUser(owner))
                    .flatMap(saved -> getPlugin().isUserOnline(saved.getUser()) ? Optional.empty() : Optional.of(saved))
                    .map(SavedUser::getDaysSinceLastLogin)
                    .flatMap(days -> getPlugin().getLocales()
                            .getLocale((days < 1 ? "today_last_login" : "days_since_last_login"), Long.toString(days)))
                    .ifPresent(user::sendMessage);
        }
    }

    // Highlight multiple claims
    private void highlightClaims(@NotNull OnlineUser user, @NotNull List<Claim> claims, int distance) {
        getPlugin().getHighlighter(user).startHighlighting(user, user.getWorld(), claims);
        getPlugin().getLocales().getLocale("nearby_claims", Integer.toString(claims.size()),
                Integer.toString(distance)).ifPresent(user::sendMessage);
    }

    @NotNull
    LeafClaims getPlugin();

}
