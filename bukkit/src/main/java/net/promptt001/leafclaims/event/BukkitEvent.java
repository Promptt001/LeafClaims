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
import lombok.RequiredArgsConstructor;
import net.promptt001.leafclaims.LeafClaims;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BukkitEvent extends Event implements net.promptt001.leafclaims.event.Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LeafClaims plugin;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    @NotNull
    public LeafClaims getPlugin() {
        return plugin;
    }

}
