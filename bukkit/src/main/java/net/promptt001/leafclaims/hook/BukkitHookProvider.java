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

package net.promptt001.leafclaims.hook;

import net.promptt001.leafclaims.BukkitLeafClaims;
import net.promptt001.leafclaims.config.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BukkitHookProvider extends HookProvider {

    @Override
    @NotNull
    default List<Hook> getAvailableHooks() {
        final List<Hook> hooks = HookProvider.super.getAvailableHooks();
        final Settings.HookSettings settings = getPlugin().getSettings().getHooks();

        // Add bukkit hooks
        if (isDependencyAvailable("Vault") && settings.getEconomy().isEnabled()) {
            hooks.add(new BukkitVaultEconomyHook(getPlugin()));
        }
        if (isDependencyAvailable("PlaceholderAPI") && settings.getPlaceholders().isEnabled()) {
            hooks.add(new BukkitPlaceholderAPIHook(getPlugin()));
        }
        if (isDependencyAvailable("WorldGuard") && settings.getWorldGuard().isEnabled()) {
            hooks.add(new BukkitWorldGuardHook(getPlugin()));
        }

        // Add bukkit importers
        hooks.add(new BukkitGriefPreventionImporter(getPlugin()));

        return hooks;
    }

    @Override
    default boolean isDependencyAvailable(@NotNull String name) {
        return getPlugin().getServer().getPluginManager().getPlugin(name) != null;
    }

    @NotNull
    BukkitLeafClaims getPlugin();

}
