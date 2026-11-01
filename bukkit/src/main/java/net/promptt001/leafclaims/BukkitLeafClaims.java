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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.william278.cloplib.operation.OperationType;
import net.william278.desertwell.util.Version;
import net.promptt001.leafclaims.api.BukkitLeafClaimsAPI;
import net.promptt001.leafclaims.claim.ClaimWorld;
import net.promptt001.leafclaims.command.BukkitCommand;
import net.promptt001.leafclaims.command.GuiProvider;
import net.promptt001.leafclaims.gui.BukkitClaimGuiManager;
import net.promptt001.leafclaims.command.Command;
import net.promptt001.leafclaims.config.Locales;
import net.promptt001.leafclaims.config.Server;
import net.promptt001.leafclaims.config.Settings;
import net.promptt001.leafclaims.config.TrustLevels;
import net.promptt001.leafclaims.database.Database;
import net.promptt001.leafclaims.event.BukkitEventDispatcher;
import net.promptt001.leafclaims.highlighter.Highlighter;
import net.promptt001.leafclaims.hook.BukkitHookProvider;
import net.promptt001.leafclaims.hook.Hook;
import net.promptt001.leafclaims.listener.BukkitListener;
import net.promptt001.leafclaims.listener.ClaimsListener;
import net.promptt001.leafclaims.network.Broker;
import net.promptt001.leafclaims.network.PluginMessageBroker;
import net.promptt001.leafclaims.pet.BukkitPetHandler;
import net.promptt001.leafclaims.position.Position;
import net.promptt001.leafclaims.position.World;
import net.promptt001.leafclaims.trust.TrustLevel;
import net.promptt001.leafclaims.trust.TrustTag;
import net.promptt001.leafclaims.trust.UserGroup;
import net.promptt001.leafclaims.user.*;
import net.promptt001.leafclaims.util.*;
import net.william278.toilet.BukkitToilet;
import net.william278.toilet.Toilet;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.morepaperlib.MorePaperLib;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

@NoArgsConstructor
@Getter
public class BukkitLeafClaims extends JavaPlugin implements LeafClaims, BukkitTask.Supplier, BukkitUserProvider,
        BukkitBlockProvider, BukkitSafeTeleportProvider, BukkitPetHandler, BukkitEventDispatcher, BukkitHookProvider,
        PluginMessageListener, GuiProvider {

    @Getter
    private BukkitClaimGuiManager claimGuiManager;

    private MorePaperLib morePaperLib;
    private AudienceProvider audiences;
    private Toilet toilet;
    private final Gson gson = getGsonBuilder().create();
    private final Set<TrustTag> trustTags = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<UUID, List<DroppedItem>> markedDrops = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, Set<GroundStack>> trackedItems = Maps.newConcurrentMap();
    private final ConcurrentMap<String, List<User>> globalUserList = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, ClaimSelection> claimSelections = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, OnlineUser> onlineUserMap = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, SavedUser> userCache = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, Highlighter> highlighterCache = Maps.newConcurrentMap();
    private final List<Command> commands = Lists.newArrayList();
    private final List<Highlighter> highlighters = Lists.newArrayList();
    private final HashMap<String, ClaimWorld> claimWorlds = Maps.newHashMap();
    private final Queue<Task.Async> taskQueue = Queues.newConcurrentLinkedQueue();

    @Setter
    private Map<UUID, Set<UserGroup>> userGroups = Maps.newConcurrentMap();
    @Setter
    private Set<Hook> hooks = Sets.newHashSet();
    @Setter
    private ClaimsListener operationListener;
    @Setter
    private Database database;
    @Setter
    private Settings settings;
    @Setter
    private TrustLevels trustLevels;
    @Setter
    private Locales locales;
    @Setter
    @Nullable
    private Broker broker;
    @Setter
    @Nullable
    private Server serverName;

    @Override
    public void onLoad() {
        this.load();
    }

    @Override
    public void onEnable() {
        this.audiences = BukkitAudiences.create(this);
        this.morePaperLib = new MorePaperLib(this);
        this.toilet = BukkitToilet.create(getDumpOptions());
        // Create the claim GUI manager before enable() (commands may delegate to it)
        this.claimGuiManager = new BukkitClaimGuiManager(this);
        this.enable();
        // ALWAYS register the GUI listener when the plugin is still running: it
        // protects and purges even when the GUI is disabled, so stray GUI items
        // can never persist. A failed enable() already disables the plugin and
        // logged its reason — registering a listener then would throw a
        // IllegalPluginAccessException that buries that reason in a stack trace.
        if (isEnabled()) {
            this.claimGuiManager.register();
        }
    }

    @Override
    public void onDisable() {
        // Close open claim menus before anything else is torn down. Once the listeners
        // are unregistered an open menu stops being guarded, so a player could still loot
        // from it during shutdown.
        if (this.claimGuiManager != null) {
            this.claimGuiManager.closeAll();
        }
        this.shutdown();
        if (this.audiences != null) {
            this.audiences.close();
        }
    }

    @Override
    public void loadAPI() {
        BukkitLeafClaimsAPI.register(this);
    }

    @Override
    public void loadMetrics() {
        if (BSTATS_PLUGIN_ID <= 0) {
            return; // no bStats id is registered for this fork; never report under another plugin's id
        }
        try {
            final Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
            metrics.addCustomChart(new SimplePie("is_cross_server",
                    () -> Boolean.toString(settings.getCrossServer().isEnabled()))
            );
            metrics.addCustomChart(new SimplePie("language",
                    () -> settings.getLanguage().toLowerCase(Locale.ENGLISH))
            );
            metrics.addCustomChart(new SimplePie("database_type",
                    () -> settings.getDatabase().getType().getDisplayName())
            );
            metrics.addCustomChart(new SimplePie("highlighter_type",
                    () -> settings.getHighlighter().isBlockDisplays() ? "Display Entities" : "Block Updates")
            );
            getBroker().ifPresent(broker -> metrics.addCustomChart(new SimplePie("broker_type",
                    () -> settings.getCrossServer().getBrokerType().getDisplayName()
            )));
        } catch (Exception e) {
            log(Level.WARNING, "Failed to initialize plugin metrics", e);
        }
    }

    @Override
    public void disablePlugin() {
        log(Level.INFO, "Disabling LeafClaims...");
        getServer().getPluginManager().disablePlugin(this);
    }

    @NotNull
    @Override
    public Version getPluginVersion() {
        return PluginVersions.plugin(getDescription().getVersion());
    }

    @Override
    @NotNull
    public String getServerType() {
        return String.format("%s/%s", getServer().getName(), getServer().getVersion());
    }

    @Override
    @NotNull
    public Version getMinecraftVersion() {
        return PluginVersions.minecraft(getServer().getBukkitVersion());
    }

    @NotNull
    @Override
    public List<World> getWorlds() {
        return getServer().getWorlds().stream().map(Adapter::adapt).toList();
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        if (exceptions.length > 0) {
            getLogger().log(level, message, exceptions[0]);
            return;
        }
        getLogger().log(level, message);
    }

    @NotNull
    @Override
    public String getServerName() {
        return serverName != null ? serverName.getName() : "server";
    }

    @NotNull
    @Override
    public List<TrustLevel> getTrustLevels() {
        return trustLevels.getTrustLevels();
    }

    @Override
    public @NotNull Set<OperationType> allowedOwnerOperations() {
        return trustLevels.getAllowedOwnerOperations();
    }

    @NotNull
    @Override
    public Path getConfigDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public Optional<Broker> getBroker() {
        return Optional.ofNullable(broker);
    }

    @Override
    public void closeBroker() {
        if (broker != null) {
            broker.close();
        }
    }

    @Override
    public void registerCommands(@NotNull List<Command> commands) {
        commands.forEach(command -> {
            new BukkitCommand(command, this).register();
            this.commands.add(command);
        });
    }

    @Override
    @NotNull
    public ConsoleUser getConsole() {
        return ConsoleUser.wrap(audiences.console());
    }

    @Override
    public void setupPluginMessagingChannels() {
        final String channelId = PluginMessageBroker.BUNGEE_CHANNEL_ID;
        getServer().getMessenger().registerIncomingPluginChannel(this, channelId, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, channelId);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (broker != null && broker instanceof PluginMessageBroker pluginMessenger
            && getSettings().getCrossServer().getBrokerType() == Broker.Type.PLUGIN_MESSAGE) {
            pluginMessenger.onReceive(channel, this.getOnlineUser(player), message);
        }
    }

    @Override
    public void setClaimWorlds(@NotNull HashMap<World, ClaimWorld> claimWorlds) {
        claimWorlds.forEach((world, claimWorld) -> this.claimWorlds.put(world.getName(), claimWorld));
    }

    @NotNull
    @Override
    public ClaimsListener createListener() {
        return new BukkitListener(this);
    }

    @Override
    public boolean isGuiAvailable() {
        return getSettings().getGui().isEnabled() && claimGuiManager != null;
    }

    @Override
    public void openClaimGui(@NotNull OnlineUser user) {
        if (claimGuiManager != null) {
            claimGuiManager.openClaimGui(user);
        }
    }

    @Override
    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public void sendBlockUpdates(@NotNull OnlineUser user, @NotNull Map<Position, MaterialBlock> blocks) {
        final Player player = ((BukkitUser) user).getBukkitPlayer();
        blocks.forEach((position, materialBlock) -> player.sendBlockChange(
                Adapter.adapt(position),
                Objects.requireNonNull(Material.matchMaterial(materialBlock.getMaterialKey()),
                        "Invalid material: " + materialBlock.getMaterialKey()
                ).createBlockData()
        ));
    }

    @Override
    @NotNull
    public BukkitLeafClaims getPlugin() {
        return this;
    }

    public static class Adapter {
        @NotNull
        public static Position adapt(@NotNull Location location) {
            return Position.at(
                    location.getX(), location.getY(), location.getZ(),
                    location.getYaw(), location.getPitch(),
                    adapt(Objects.requireNonNull(location.getWorld(), "Location world is null"))
            );
        }

        @NotNull
        public static Location adapt(@NotNull Position position) {
            return new Location(
                    adapt(position.getWorld()),
                    position.getX(), position.getY(), position.getZ(),
                    position.getYaw(), position.getPitch()
            );
        }

        @NotNull
        public static org.bukkit.World adapt(@NotNull World world) {
            return Optional.ofNullable(Bukkit.getWorld(world.getUuid()))
                    .or(() -> Optional.ofNullable(Bukkit.getWorld(world.getName())))
                    .orElseThrow(() -> new IllegalArgumentException("World not found"));
        }

        @NotNull
        public static World adapt(@NotNull org.bukkit.World world) {
            return World.of(world.getName(), world.getUID(), world.getEnvironment().name().toLowerCase(Locale.ENGLISH));
        }

        @NotNull
        public static Map<Location, BlockData> adapt(@NotNull Map<Position, BlockProvider.MaterialBlock> blocks) {
            final Map<Location, BlockData> blockData = Maps.newHashMap();
            blocks.forEach((position, materialBlock) -> blockData.put(
                    adapt(position),
                    Objects.requireNonNull(Material.matchMaterial(materialBlock.getMaterialKey()),
                            "Invalid material: " + materialBlock.getMaterialKey()
                    ).createBlockData()
            ));
            return blockData;
        }
    }

}
