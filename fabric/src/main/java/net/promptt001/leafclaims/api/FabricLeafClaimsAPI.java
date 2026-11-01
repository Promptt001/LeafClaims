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

package net.promptt001.leafclaims.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import net.promptt001.leafclaims.FabricLeafClaims;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.position.Position;
import net.promptt001.leafclaims.position.World;
import net.promptt001.leafclaims.user.OnlineUser;
import net.promptt001.leafclaims.util.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The Fabric implementation of the LeafClaims API. Get the instance with {@link #getInstance()}.
 */
@SuppressWarnings("unused")
public class FabricLeafClaimsAPI extends LeafClaimsAPI {

    /**
     * <b>(Internal use only)</b> - Constructor, instantiating the API.
     *
     * @param plugin The LeafClaims plugin instance
     * @since 1.5
     */
    protected FabricLeafClaimsAPI(@NotNull LeafClaims plugin) {
        super(plugin);
    }

    /**
     * Get an instance of the LeafClaims API.
     *
     * @return instance of the LeafClaims API
     * @throws NotRegisteredException if the API has not yet been registered.
     * @since 1.5
     */
    @NotNull
    public static FabricLeafClaimsAPI getInstance() throws NotRegisteredException {
        return (FabricLeafClaimsAPI) LeafClaimsAPI.getInstance();
    }

    /**
     * <b>(Internal use only)</b> - Unregister the API instance.
     *
     * @since 1.5
     */
    @ApiStatus.Internal
    public static void register(@NotNull FabricLeafClaims plugin) {
        instance = new FabricLeafClaimsAPI(plugin);
    }

    /**
     * Get the {@link World} object for a given {@link net.minecraft.world.level.Level}
     *
     * @param world The Minecraft world object
     * @return The LeafClaims world
     * @since 1.5
     */
    @NotNull
    public World getWorld(@NotNull net.minecraft.world.level.Level world) {
        return FabricLeafClaims.Adapter.adapt(world);
    }

    /**
     * Get the {@link net.minecraft.world.level.Level} object for a given {@link World}
     *
     * @param world The LeafClaims world
     * @return The Minecraft world object
     * @since 1.5
     */
    @NotNull
    public net.minecraft.world.level.Level getWorld(@NotNull World world) {
        return FabricLeafClaims.Adapter.adapt(world, ((FabricLeafClaims) getPlugin()).requireServer());
    }

    /**
     * Get the {@link Position} object for a given {@link BlockPos block position} in a {@link net.minecraft.world.level.Level}
     *
     * @param world    the Minecraft world
     * @param position the Minecraft vector block position
     * @return The LeafClaims position
     * @since 1.5
     */
    @NotNull
    public Position getPosition(@NotNull net.minecraft.world.level.Level world, @NotNull BlockPos position) {
        return FabricLeafClaims.Adapter.adapt(world, Vec3.atCenterOf(position), 0.0f, 0.0f);
    }

    /**
     * Get the {@link Position} object for a given {@link Vec3 vector position} in a {@link net.minecraft.world.level.Level}
     *
     * @param world    the Minecraft world
     * @param position the Minecraft vector coordinate position
     * @return The LeafClaims position
     * @since 1.5
     */
    @NotNull
    public Position getPosition(@NotNull net.minecraft.world.level.Level world, @NotNull Vec3 position) {
        return FabricLeafClaims.Adapter.adapt(world, position, 0.0f, 0.0f);
    }

    /**
     * Get the {@link Position} object for a given {@link Vec3 vector position} in a {@link net.minecraft.world.level.Level}
     *
     * @param world    the Minecraft world
     * @param position the Minecraft vector coordinate position
     * @param yaw      the positional yaw angle
     * @param pitch    the positional pitch angle
     * @return The LeafClaims position
     * @since 1.5
     */
    @NotNull
    public Position getPosition(@NotNull net.minecraft.world.level.Level world, @NotNull Vec3 position,
                                float yaw, float pitch) {
        return FabricLeafClaims.Adapter.adapt(world, position, yaw, pitch);
    }

    /**
     * Get the {@link Location} containing Minecraft positional object data for a {@link Position}
     *
     * @param position the LeafClaims position
     * @return The LeafClaims position
     * @since 1.5
     */
    @NotNull
    public Location getLocation(@NotNull Position position) {
        return FabricLeafClaims.Adapter.adapt(position, ((FabricLeafClaims) getPlugin()).requireServer());
    }

    /**
     * Get a {@link TeleportTransition} for a {@link Position}
     *
     * @param position the LeafClaims position
     * @return a {@link TeleportTransition teleportation target} object
     * @since 1.5
     */
    @NotNull
    public TeleportTransition getTeleportTarget(@NotNull Position position) {
        return getLocation(position).teleportTarget();
    }

    /**
     * Get the {@link net.minecraft.world.level.Level} and {@link Vec3} pair for a {@link Position}
     *
     * @param position the LeafClaims position
     * @return The {@link net.minecraft.world.level.Level}-{@link Vec3} {@link Pair}
     * @since 1.5
     */
    @NotNull
    public Pair<ServerLevel, Vec3> getWorldVec3dPair(@NotNull Position position) {
        final Location loc = getLocation(position);
        return new Pair<>(loc.world(), loc.pos());
    }

    /**
     * Get the {@link net.minecraft.world.level.Level} and {@link Vec3} pair for a {@link Position}
     *
     * @param position the LeafClaims position
     * @return The {@link net.minecraft.world.level.Level}-{@link BlockPos} {@link Pair}
     * @since 1.5
     */
    @NotNull
    public Pair<ServerLevel, BlockPos> getWorldBlockPosPair(@NotNull Position position) {
        final Location loc = getLocation(position);
        return new Pair<>(loc.world(), loc.blockPos());
    }

    /**
     * Get the {@link OnlineUser} object for a given {@link ServerPlayer}
     *
     * @param player The Minecraft {@link ServerPlayer server player entity}
     * @return The LeafClaims online user
     * @since 1.5
     */
    @NotNull
    public OnlineUser getOnlineUser(@NotNull ServerPlayer player) {
        return ((FabricLeafClaims) plugin).getOnlineUser(player);
    }

}
