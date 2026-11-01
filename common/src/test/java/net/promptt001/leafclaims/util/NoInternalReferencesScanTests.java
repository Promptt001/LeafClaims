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
package net.promptt001.leafclaims.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Source hygiene")
class NoInternalReferencesScanTests {

    /**
     * Identifiers and notes that only make sense inside a maintainer's private notes.
     * Everything here belongs in a tracker, not in published source: record ids, section
     * references, work-slice labels, local build labels, and commentary about how a change
     * was found or verified.
     */
    private static final Pattern INTERNAL = Pattern.compile(String.join("|",
            "\\b[GCTFMVIRQDLW]-0\\d{2}\\b",                        // tracker record ids
            "\\((P|K|D|S|G|R|F|V|C|Q)\\d{1,2}\\)",                      // a plan tag in brackets
            // a plan tag in prose, in any capitalisation ("Old K6 bug", "the P1 case").
            // If a legitimate parenthetical ever trips the rule above, reword the prose
            // rather than weakening the check.
            "(?i:\\b(?:the|old|see|per|fix|from)\\s+[PKDSGRFCQV]\\d{1,2}\\b)",
            "\\b(?:the|old|see|per|fix|from)\\s+[PKDSGRFCQV]\\d{1,2}\\b",  // a plan tag in prose
            "(^|[\\s(/])\\u00a7 ?\\d+(\\.\\d+)*\\b",               // section refs (not \u00a7 colour codes)
            "\\bGuide ?\\d", "\\b[Rr]eview ?#?\\d", "\\breview\\d\\b",
            "\\b(F\\d{1,2}[a-z]?|S\\d[a-z]?|G\\d|R\\d|P1\\d|C\\d{1,2}|K\\d)\\b ?(fix|slice|batch|port|:|\\)|/)",
            "\\bP13\\b", "\\bT\\d{1,2}\\b", "\\bTESTONLY", "\\btestonly\\.[A-Z]",
            "\\bwip/local-only\\b", "\\bcp-\\d+\\b", "\\bCHE\\b",
            "javap", "yarn leftover", "[Rr]eversible", "investigation patch", "our root patch",
            "\\bhandover\\b", "[Ff]ork note", "fork addition", "user decision", "CommandBridge",
            "not-yet-migrated", "golden dumps?\\b", "boot-caught", "live (row|report)", "PORT:",
            "@since 1\\.5\\.11",
            // Scars left by deleting a record id out of prose: "FIRST D7)" or "hop/D6 fix)".
            // The id is gone but the punctuation that used to wrap it is not, so the comment
            // reads as a typo. Catch that shape so a later cleanup cannot half-delete again.
            "\\b[A-Z]\\d{1,2}\\)", "/[A-Z]\\d{1,2}\\b"));

    @Test
    @DisplayName("Sources, tests and build files carry no private workspace references")
    void noInternalReferences() throws IOException {
        final List<Path> roots = List.of(Path.of("src"), Path.of("../bukkit"), Path.of("../paper"),
                Path.of("../fabric"), Path.of("../build.gradle"), Path.of("../settings.gradle"),
                Path.of("../gradle.properties"), Path.of("../README.md"), Path.of("../NOTICE"),
                Path.of("../CHANGELOG.md"), Path.of("../THIRD_PARTY_LICENSES.md"), Path.of("../docs"));
        final List<String> hits = new ArrayList<>();
        int scanned = 0;
        for (Path root : roots) {
            if (!Files.exists(root)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(root)) {
                for (Path f : walk.filter(Files::isRegularFile).toList()) {
                    final String name = f.toString().replace('\\', '/');
                    if (!name.matches(".*\\.(java|gradle|properties|json|yml|toml|md)$")
                            || name.contains("/build/") || name.contains("/.gradle/") || name.contains("/run/")
                            || name.contains("de/themoep/minedown/") || name.contains("com/pokeskies/")
                            // Release notes and the publishing runbook are written for one release and
                            // are not published documentation; they describe local build steps.
                            || name.endsWith("PUBLISHING_2.0.0.md") || name.endsWith("RELEASE_NOTES_2.0.0.md")
                            || name.endsWith("NoInternalReferencesScanTests.java")) {
                        continue;
                    }
                    scanned++;
                    final List<String> lines = Files.readAllLines(f);
                    for (int i = 0; i < lines.size(); i++) {
                        if (INTERNAL.matcher(lines.get(i)).find()) {
                            hits.add(name + ":" + (i + 1) + "  " + lines.get(i).strip());
                        }
                    }
                }
            }
        }
        assertTrue(scanned > 300, "scan found too few files (wrong working directory?): " + scanned);
        assertTrue(hits.isEmpty(), hits.size() + " internal reference(s):\n" + String.join("\n", hits));
    }

}
