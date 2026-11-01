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

package net.promptt001.leafclaims.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * One builder for every menu item, and the only one that tags: {@code build}
 * always calls {@code manager.tag()}, so no item reaches a menu without the
 * PDC marker.
 *
 * <p>{@code ItemFlag.values()} is resolved at runtime, so it includes modern
 * flags without referencing them at compile time. It hides
 * attribute/enchant/„When in Main Hand" lines that tool icons would
 * otherwise show.</p>
 *
 * @since LeafClaims 1.0.0
 */
final class GuiItem {

    /**
     * {@code ItemMeta#setHideTooltip(boolean)} exists on Paper but not on the
     * Spigot API this module compiles against, so it is resolved at runtime —
     * the same approach {@code ItemFlag.values()} already uses. {@code null}
     * where the server does not provide it.
     */
    private static final Method HIDE_TOOLTIP = resolveHideTooltip();

    /** Guards {@link #warnOnce(String)} so a per-menu failure is reported once, not per pane. */
    private static final AtomicBoolean WARNED = new AtomicBoolean();

    private final Material material;
    private int amount = 1;
    private String name = "";
    private final List<String> lore = new ArrayList<>();

    private GuiItem(@NotNull Material material) {
        this.material = material;
    }

    @NotNull
    static GuiItem of(@NotNull Material material) {
        return new GuiItem(material);
    }

    @NotNull
    GuiItem amount(int n) {
        this.amount = Math.max(1, Math.min(64, n));
        return this;
    }

    /**
     * Name from a locale key (template coloured, then filled).
     *
     * @param m    the GUI manager (locale access)
     * @param key  the locale key
     * @param args replacement values
     * @return this
     * @since LeafClaims 1.0.0
     */
    @NotNull
    GuiItem name(@NotNull BukkitClaimGuiManager m, @NotNull String key, @NotNull String... args) {
        this.name = GuiText.asName(m.text(key, args));
        return this;
    }

    /**
     * Already-formatted name (e.g. level colour + level name).
     *
     * @param formatted the formatted name text
     * @return this
     * @since LeafClaims 1.0.0
     */
    @NotNull
    GuiItem nameRaw(@NotNull String formatted) {
        this.name = GuiText.asName(formatted);
        return this;
    }

    /**
     * Name made from a value players control, such as a claim or warp name. The value is shown
     * in white exactly as written: section signs are neutralised so they can't start colour codes.
     */
    @NotNull
    GuiItem nameLiteral(@NotNull String value) {
        this.name = GuiText.asName(GuiText.literal(value));
        return this;
    }

    /**
     * Lore from a locale key; {@code \n} in the locale splits lines
     * ({@code getRawLocale} unescapes {@code \n}).
     *
     * @param m    the GUI manager (locale access)
     * @param key  the locale key
     * @param args replacement values
     * @return this
     * @since LeafClaims 1.0.0
     */
    @NotNull
    GuiItem lore(@NotNull BukkitClaimGuiManager m, @NotNull String key, @NotNull String... args) {
        for (String line : m.text(key, args).split("\n", -1)) {
            lore.add(GuiText.asLore(line));
        }
        return this;
    }

    /**
     * Already-formatted lore lines.
     *
     * @param formattedLines the formatted lore lines
     * @return this
     * @since LeafClaims 1.0.0
     */
    @NotNull
    GuiItem loreRaw(@NotNull List<String> formattedLines) {
        formattedLines.forEach(l -> lore.add(GuiText.asLore(l)));
        return this;
    }

    /**
     * A blank lore line (section break).
     *
     * @return this
     * @since LeafClaims 1.0.0
     */
    @NotNull
    GuiItem blank() {
        lore.add("");
        return this;
    }

    /**
     * Build the item: display name, lore, all runtime item flags, and the
     * mandatory PDC tag.
     *
     * @param m the GUI manager (tagging)
     * @return the built, PDC-tagged item
     * @since LeafClaims 1.0.0
     */
    @NotNull
    ItemStack build(@NotNull BukkitClaimGuiManager m) {
        final ItemStack stack = new ItemStack(material, amount);
        final ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            // A blank name must not render as a framed but empty tooltip: give the
            // item a space (so vanilla shows no name of its own) and hide the
            // tooltip outright where the server supports it. That is what makes
            // the filler panes sit flush in the grid instead of drawing a dark
            // box next to the cursor. The test has to ignore colour codes:
            // GuiText.asName() stores the filler's single space as "§f ", which
            // is not blank to String#isBlank().
            final boolean blankName = GuiText.rendersBlank(name);
            meta.setDisplayName(blankName ? " " : name);
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
            meta.addItemFlags(ItemFlag.values()); // runtime enum: hides attribute/enchant/etc. lines
            if (blankName) {
                hideTooltip(meta);
            }
            stack.setItemMeta(meta);
        }
        return m.tag(stack); // every menu item is tagged, so a stray one can always be found and removed
    }

    /**
     * Resolve {@code ItemMeta#setHideTooltip(boolean)} once, tolerating servers
     * whose API predates it.
     *
     * @return the method, or {@code null} when unavailable
     */
    private static Method resolveHideTooltip() {
        try {
            return ItemMeta.class.getMethod("setHideTooltip", boolean.class);
        } catch (NoSuchMethodException | RuntimeException e) {
            return null;
        }
    }

    /**
     * Best-effort suppression of the tooltip frame. Never throws: on a server
     * without the method the item simply renders as it did before.
     *
     * @param meta the meta to modify
     */
    private static void hideTooltip(@NotNull ItemMeta meta) {
        final Method hide = HIDE_TOOLTIP;
        if (hide == null) {
            warnOnce("setHideTooltip is unavailable on this server, so empty menu slots "
                    + "will show an empty tooltip box");
            return;
        }
        try {
            hide.invoke(meta, true);
        } catch (ReflectiveOperationException | RuntimeException e) {
            // Cosmetic only; a menu must never fail to build over a tooltip. But log it once,
            // because silence here previously hid the fact that the fix never ran.
            warnOnce("could not hide an empty menu slot's tooltip: " + e);
        }
    }

    /**
     * Report the first suppressed tooltip failure only. Repeating it for every
     * filler pane in every menu would flood the log.
     *
     * @param message the message to log once
     */
    private static void warnOnce(@NotNull String message) {
        if (WARNED.compareAndSet(false, true)) {
            Bukkit.getLogger().warning("[LeafClaims] " + message);
        }
    }
}
