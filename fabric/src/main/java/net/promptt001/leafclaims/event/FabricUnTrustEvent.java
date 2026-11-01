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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.claim.Claim;
import net.promptt001.leafclaims.claim.ClaimWorld;
import net.promptt001.leafclaims.trust.TrustLevel;
import net.promptt001.leafclaims.trust.Trustable;
import net.promptt001.leafclaims.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

public interface FabricUnTrustEvent extends FabricEventCallback<UnTrustEvent> {

    @NotNull
    Event<FabricUnTrustEvent> EVENT = EventFactory.createArrayBacked(FabricUnTrustEvent.class,
            (listeners) -> (event) -> {
                for (FabricUnTrustEvent listener : listeners) {
                    final InteractionResult result = listener.invoke(event);
                    if (event.isCancelled()) {
                        return InteractionResult.CONSUME;
                    } else if (result != InteractionResult.PASS) {
                        event.setCancelled(true);
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    final class Callback implements UnTrustEvent {

        private final OnlineUser onlineUser;
        private final Claim claim;
        private final ClaimWorld claimWorld;
        private final TrustLevel trustLevel;
        private final Trustable trusted;
        private final LeafClaims plugin;

        @Setter
        private boolean cancelled = false;

        @NotNull
        public Event<FabricUnTrustEvent> getEvent() {
            return EVENT;
        }

    }

}
