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

import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.user.BukkitUser;
import net.promptt001.leafclaims.user.OnlineUser;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class BukkitPlayerEvent extends PlayerEvent implements OnlineUserEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final LeafClaims plugin;
    private final OnlineUser user;

    public BukkitPlayerEvent(@NotNull OnlineUser user, @NotNull LeafClaims plugin) {
        super(((BukkitUser) user).getBukkitPlayer());
        this.user = user;
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public OnlineUser getOnlineUser() {
        return user;
    }

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
