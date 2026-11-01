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
import net.promptt001.leafclaims.user.CommandUser;
import net.promptt001.leafclaims.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Command to open the built-in claim GUI (/leafclaimgui, /claimgui)
 * <p>
 * A behavioral recreation of the unofficial "LeafClaims GUI addon" navigation menus.
 * The actual inventory menus are platform-specific (Bukkit/Paper); this command
 * delegates to the platform via {@link GuiProvider}, and is only registered when
 * {@code gui.enabled} is set in the config.
 *
 * @since LeafClaims 1.0.0
 */
public class ClaimGuiCommand extends Command {

    protected ClaimGuiCommand(@NotNull LeafClaims plugin) {
        super(
                List.of("leafclaimgui", "claimgui"),
                "",
                plugin
        );
    }

    @Override
    public void execute(@NotNull CommandUser executor, @NotNull String[] args) {
        // The menus are inventory screens, which only a player can open
        if (!(executor instanceof OnlineUser user)) {
            plugin.getLocales().getLocale("error_command_in_game_only")
                    .ifPresent(executor::sendMessage);
            return;
        }
        // Delegate to the platform's own GUI implementation: Bukkit inventory menus,
        // Fabric screens. isGuiAvailable() is checked at call time because the server
        // owner can toggle the GUI in config without a restart.
        // The platform performs its own hop onto the entity thread, so no scheduler
        // hop is needed here.
        if (!(plugin instanceof GuiProvider provider) || !provider.isGuiAvailable()) {
            plugin.getLocales().getLocale("error_no_permission")
                    .ifPresent(executor::sendMessage);
        } else {
            provider.openClaimGui(user);
        }
    }

}
