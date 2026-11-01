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

package net.promptt001.leafclaims.event;

import lombok.Getter;
import lombok.Setter;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.claim.ServerWorldClaim;
import net.promptt001.leafclaims.user.OnlineUser;
import net.promptt001.leafclaims.user.User;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class BukkitDeleteAllClaimsEvent extends BukkitPlayerEvent implements DeleteAllClaimsEvent, Cancellable {

    @Nullable
    private final User claimOwner;
    @Getter
    private final Collection<ServerWorldClaim> claims;
    @Getter
    @Setter
    private boolean cancelled;

    protected BukkitDeleteAllClaimsEvent(@NotNull OnlineUser user, @Nullable User claimOwner,
                                         @NotNull Collection<ServerWorldClaim> claims, @NotNull LeafClaims plugin) {
        super(user, plugin);
        this.claimOwner = claimOwner;
        this.claims = claims;
    }

    @Override
    public Optional<User> getClaimOwner() {
        return Optional.ofNullable(claimOwner);
    }
}
