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

package net.promptt001.leafclaims;

import net.kyori.adventure.key.Key;
import net.promptt001.leafclaims.api.LeafClaimsAPI;
import net.promptt001.leafclaims.claim.ClaimManager;
import net.promptt001.leafclaims.command.CommandProvider;
import net.promptt001.leafclaims.config.ConfigProvider;
import net.promptt001.leafclaims.database.DatabaseProvider;
import net.promptt001.leafclaims.event.EventDispatcher;
import net.promptt001.leafclaims.highlighter.HighlighterProvider;
import net.promptt001.leafclaims.hook.HookProvider;
import net.promptt001.leafclaims.hook.PluginHook;
import net.promptt001.leafclaims.listener.ListenerProvider;
import net.promptt001.leafclaims.moderation.DropsHandler;
import net.promptt001.leafclaims.moderation.SignNotifier;
import net.promptt001.leafclaims.network.BrokerProvider;
import net.promptt001.leafclaims.pet.PetHandler;
import net.promptt001.leafclaims.trust.GroupManager;
import net.promptt001.leafclaims.trust.TrustTagManager;
import net.promptt001.leafclaims.user.SavedUserProvider;
import net.promptt001.leafclaims.user.UserProvider;
import net.promptt001.leafclaims.util.*;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Common interface for the LeafClaims plugin
 *
 * @since 1.0
 */
public interface LeafClaims extends Task.Supplier, ConfigProvider, UserProvider, SavedUserProvider, DatabaseProvider,
        GsonProvider, SignNotifier, ClaimManager, GroupManager, TrustTagManager, ListenerProvider, CommandProvider,
        PetHandler, DropsHandler, BrokerProvider, TextValidator, AudiencesProvider, BlockProvider, SafeTeleportProvider,
        MetaProvider, EventDispatcher, HookProvider, HighlighterProvider, DumpProvider {

    /**
     * Load plugin systems
     *
     * @since 1.3.2
     */
    default void load() {
        try {
            loadSettings();
            loadServer();
            loadTrustLevels();
            loadLocales();
            loadHooks(PluginHook.Register.values());
            registerHooks(PluginHook.Register.ON_LOAD);
        } catch (Throwable e) {
            log(Level.SEVERE, "An error occurred whilst loading LeafClaims", e);
            disablePlugin();
            return;
        }
        log(Level.INFO, String.format("Successfully loaded LeafClaims v%s", getPluginVersion()));
    }

    /**
     * Enable all plugin systems
     *
     * @since 1.0
     */
    default void enable() {
        try {
            loadDatabase();
            loadClaimWorlds();
            loadHighlighters();
            loadUserGroups();
            loadTrustTags();
            loadBroker();
            loadCommands();
            loadListeners();
            loadClaimBlockScheduler();
            registerHooks(PluginHook.Register.ON_ENABLE);
            loadAPI();
            loadMetrics();
            startQueuePoller();
        } catch (Throwable e) {
            log(Level.SEVERE, "An error occurred whilst enabling LeafClaims", e);
            disablePlugin();
            return;
        }
        log(Level.INFO, String.format("Successfully enabled LeafClaims v%s", getPluginVersion()));
        checkForUpdates();
    }

    /**
     * Shutdown plugin subsystems
     *
     * @since 1.0
     */
    default void shutdown() {
        log(Level.INFO, String.format("Disabling LeafClaims v%s...", getPluginVersion()));
        try {
            unloadHooks(PluginHook.Register.values());
            closeBroker();
            closeDatabase();
            cancelTasks();
            unloadAPI();
        } catch (Throwable e) {
            log(Level.SEVERE, "An error occurred whilst disabling LeafClaims", e);
        }
        log(Level.INFO, String.format("Successfully disabled LeafClaims v%s", getPluginVersion()));
    }

    /**
     * Register the API instance
     *
     * @since 1.0
     */
    void loadAPI();

    /**
     * Unregister the API instance
     *
     * @since 1.0
     */
    default void unloadAPI() {
        LeafClaimsAPI.unregister();
    }

    /**
     * Load plugin metrics
     *
     * @since 1.0
     */
    void loadMetrics();

    /**
     * Disable the plugin
     *
     * @since 1.0
     */
    void disablePlugin();

    /**
     * Log a message to the console.
     *
     * @param level      the level to log at
     * @param message    the message to log
     * @param exceptions any exceptions to log
     * @since 1.0
     */
    void log(@NotNull Level level, @NotNull String message, Throwable... exceptions);

    /**
     * Get a {@link Key} for the plugin
     *
     * @param value the value of the key
     * @return the key
     */
    @NotNull
    default Key getKey(@NotNull String... value) {
        @Subst("bar") String text = String.join("/", value);
        return Key.key("leafclaims", text);
    }

    /**
     * Get the plugin instance
     *
     * @return the plugin instance
     * @since 1.0
     */
    @NotNull
    default LeafClaims getPlugin() {
        return this;
    }

}
