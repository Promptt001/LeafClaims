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

import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.claim.ClaimWorld;
import net.promptt001.leafclaims.position.Position;
import net.promptt001.leafclaims.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handler for when the claim creation and resize tool is used
 */
public interface ClaimsToolHandler {


    // Handle a right click action on a block with a claim tool
    default void onClaimToolUse(@NotNull OperationUser opUser, @NotNull OperationPosition opPosition) {
        final OnlineUser user = (OnlineUser) opUser;
        final Position position = (Position) opPosition;
        final Optional<ClaimWorld> optionalWorld = getPlugin().getClaimWorld(position.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }
        getPlugin().runAsync(() -> getPlugin().handleSelection(user, optionalWorld.get(), position));
    }

    @NotNull
    LeafClaims getPlugin();

}
