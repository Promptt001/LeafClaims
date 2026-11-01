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

package net.promptt001.leafclaims.config;

import com.google.common.collect.Maps;
import de.exlll.configlib.Configuration;
import de.themoep.minedown.adventure.MineDown;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.promptt001.leafclaims.LeafClaims;
import net.promptt001.leafclaims.command.LeafClaimsCommand;
import net.promptt001.leafclaims.position.BlockPosition;
import net.promptt001.leafclaims.position.ServerWorld;
import net.promptt001.leafclaims.user.CommandUser;
import net.promptt001.leafclaims.user.OnlineUser;
import net.promptt001.leafclaims.util.PaginatedListProvider;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Plugin locale configuration
 *
 * @since 1.0
 */
@SuppressWarnings("FieldMayBeFinal")
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Locales implements PaginatedListProvider {

    static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            ┃ LeafClaims · Locales
            ┃ Based on HuskClaims by William278 (Apache-2.0)
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            ┣╸ LeafClaims: https://github.com/Promptt001/LeafClaims
            ┣╸ See the plugin's about menu for international locale credits
            ┣╸ Formatted in MineDown: https://github.com/Phoenix616/MineDown
            ┗╸ Documentation: https://github.com/Promptt001/LeafClaims/tree/main/docs
            """;

    protected static final String DEFAULT_LOCALE = "en-gb";

    // The raw set of locales loaded from YAML
    Map<String, String> locales = Maps.newTreeMap();

    /**
     * Returns a raw, unformatted locale loaded from the locales file
     *
     * @param localeId String identifier of the locale, corresponding to a key in the file
     * @return An {@link Optional} containing the locale corresponding to the id, if it exists
     */
    public Optional<String> getRawLocale(@NotNull String localeId) {
        return Optional.ofNullable(locales.get(localeId)).map(StringEscapeUtils::unescapeJava);
    }

    /**
     * Returns a raw, un-formatted locale loaded from the locales file, with replacements applied
     * <p>
     * Note that replacements will not be MineDown-escaped; use {@link #escapeText(String)} to escape replacements
     *
     * @param localeId     String identifier of the locale, corresponding to a key in the file
     * @param replacements Ordered array of replacement strings to fill in placeholders with
     * @return An {@link Optional} containing the replacement-applied locale corresponding to the id, if it exists
     */
    public Optional<String> getRawLocale(@NotNull String localeId, @NotNull String... replacements) {
        return getRawLocale(localeId).map(locale -> applyReplacements(locale, replacements));
    }

    /**
     * Returns a MineDown-formatted locale from the locales file
     *
     * @param localeId String identifier of the locale, corresponding to a key in the file
     * @return An {@link Optional} containing the formatted locale corresponding to the id, if it exists
     */
    public Optional<MineDown> getLocale(@NotNull String localeId) {
        return getRawLocale(localeId).map(this::format);
    }

    /**
     * Returns a MineDown-formatted locale from the locales file, with replacements applied
     * <p>
     * Note that replacements will be MineDown-escaped before application
     *
     * @param localeId     String identifier of the locale, corresponding to a key in the file
     * @param replacements Ordered array of replacement strings to fill in placeholders with
     * @return An {@link Optional} containing the replacement-applied, formatted locale corresponding to the id, if it exists
     */
    public Optional<MineDown> getLocale(@NotNull String localeId, @NotNull String... replacements) {
        return getRawLocale(localeId, Arrays.stream(replacements).map(Locales::escapeText)
                .toArray(String[]::new)).map(this::format);
    }

    /**
     * Returns a MineDown-formatted string
     *
     * @param text The text to format
     * @return A {@link MineDown} object containing the formatted text
     */
    @NotNull
    public MineDown format(@NotNull String text) {
        return new MineDown(text);
    }

    /**
     * Apply placeholder replacements to a raw locale
     *
     * @param rawLocale    The raw, unparsed locale
     * @param replacements Ordered array of replacement strings to fill in placeholders with
     * @return the raw locale, with inserted placeholders
     */
    @NotNull
    private String applyReplacements(@NotNull String rawLocale, @NotNull String... replacements) {
        int replacementIndexer = 1;
        for (String replacement : replacements) {
            String replacementString = "%" + replacementIndexer + "%";
            rawLocale = rawLocale.replace(replacementString, replacement);
            replacementIndexer += 1;
        }
        return rawLocale;
    }

    /**
     * Escape a string from {@link MineDown} formatting for use in a MineDown-formatted locale
     *
     * @param string The string to escape
     * @return The escaped string
     */
    @NotNull
    public static String escapeText(@NotNull String string) {
        final StringBuilder value = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            boolean isEscape = c == '\\';
            boolean isColorCode = i + 1 < string.length() && (c == 167 || c == '&');
            boolean isEvent = c == '[' || c == ']' || c == '(' || c == ')';
            if (isEscape || isColorCode || isEvent) {
                value.append('\\');
            }

            value.append(c);
        }
        return value.toString().replace("__", "_\\_");
    }


    @NotNull
    public String getPositionText(@NotNull BlockPosition position, int yPosition, @NotNull ServerWorld serverWorld,
                                  @NotNull CommandUser user, @NotNull LeafClaims plugin) {
        final boolean crossServer = plugin.getSettings().getCrossServer().isEnabled();
        final String environment = serverWorld.world().getEnvironment().toLowerCase(Locale.ENGLISH);
        return plugin.getLocales().getRawLocale(
                switch (environment) {
                    case "overworld", "normal" -> "position_overworld";
                    case "the_nether", "nether" -> "position_nether";
                    case "the_end", "end" -> "position_end";
                    default -> "position_custom";
                },
                crossServer ? serverWorld.toString() : serverWorld.world().getName(),
                Integer.toString(position.getBlockX()),
                Integer.toString(position.getBlockZ()),
                getRawLocale(
                        "position_%sworld_tooltip".formatted(!crossServer ? "" : "server_")
                ).orElse(""),
                user instanceof OnlineUser online ? getTeleportText(
                        position, yPosition, serverWorld, online, plugin
                ) : "",
                escapeText(WordUtils.capitalizeFully(environment.replaceAll("[-_]", " ")))
        ).orElse("");
    }


    @NotNull
    private String getTeleportText(@NotNull BlockPosition position, int yPosition, @NotNull ServerWorld serverWorld,
                                   @NotNull CommandUser user, @NotNull LeafClaims plugin) {
        // Teleports are now handled in-house, so the tooltip depends only on permission.
        if (!plugin.canUseCommand(LeafClaimsCommand.class, user, "teleport")) {
            return "";
        }
        return String.format(
                "%s run_command=/leafclaims teleport %s %s %s %s %s",
                getRawLocale("position_teleport_tooltip").orElse(""),
                serverWorld.server(),
                position.getBlockX(),
                yPosition,
                position.getBlockZ(),
                serverWorld.world().getName()
        );
    }

    @NotNull
    public String truncateText(@NotNull String string, int truncateAfter) {
        if (string.isBlank()) {
            return string;
        }
        return string.length() > truncateAfter ? string.substring(0, truncateAfter) + "…" : string;
    }

    @NotNull
    public String getNotApplicable() {
        return getRawLocale("not_applicable").orElse("N/A");
    }

    @NotNull
    public String getListJoiner() {
        return getRawLocale("list_separator").orElse(", ");
    }

    @NotNull
    public String getNone() {
        return getRawLocale("none").orElse("(none)");
    }

}
