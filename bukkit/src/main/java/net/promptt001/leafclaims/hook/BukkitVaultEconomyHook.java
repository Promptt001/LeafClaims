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

import net.milkbowl.vault.economy.Economy;
import net.promptt001.leafclaims.BukkitLeafClaims;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.user.BukkitUser;
import net.promptt001.leafclaims.user.OnlineUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

@PluginHook(
        name = "Vault",
        register = PluginHook.Register.ON_ENABLE
)
public class BukkitVaultEconomyHook extends EconomyHook {

    private Economy economy;

    public BukkitVaultEconomyHook(@NotNull LeafClaims plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        final RegisteredServiceProvider<Economy> provider = ((BukkitLeafClaims) plugin).getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (provider != null) {
            economy = provider.getProvider();
        }
    }

    @Override
    public void unload() {
        economy = null;
    }

    @Override
    public boolean takeMoney(@NotNull OnlineUser user, double amount, @NotNull EconomyReason reason) {
        final Player player = ((BukkitUser) user).getBukkitPlayer();
        if (!economy.has(player, amount)) {
            return false;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    @Override
    @NotNull
    public String format(double amount) {
        return economy.format(amount);
    }

}
