/*
 * MIT License
 *
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.alphaconqueror.discord.bot.utils.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.alphaconqueror.common.utils.config.KeyedConfiguration;
import de.alphaconqueror.common.utils.config.key.ConfigKey;
import de.alphaconqueror.common.utils.config.key.SimpleConfigKey;
import de.alphaconqueror.discord.bot.utils.permission.Permission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static de.alphaconqueror.common.utils.config.key.ConfigKeyFactory.key;
import static de.alphaconqueror.common.utils.config.key.ConfigKeyFactory.longKey;
import static de.alphaconqueror.common.utils.config.key.ConfigKeyFactory.notReloadable;
import static de.alphaconqueror.common.utils.config.key.ConfigKeyFactory.stringKey;

public final class ConfigKeys {

    /**
     * The token for the discord bot.
     */
    public static final ConfigKey<String> TOKEN = notReloadable(stringKey("token", ""));

    /**
     * The custom status of the discord bot.
     */
    public static final ConfigKey<String> STATUS = notReloadable(stringKey("status", ""));

    /**
     * The guild id.
     */
    public static final ConfigKey<Long> GUILD_ID = longKey("guild-id", 0L);

    /**
     * The permissions of each role.
     */
    public static final ConfigKey<Map<Long, Set<Permission>>> PERMISSIONS = key(
            (config, path, def) -> {
                final Map<Long, Set<Permission>> permissionMap = config.getStringMap(path,
                                ImmutableMap.of()).entrySet().stream()
                        .collect(HashMap::new, (map, entry) -> {
                            // only use real longs as key
                            try {
                                final long key = Long.parseLong(entry.getKey());
                                final String value = entry.getValue().replaceAll("\\s", "");
                                final Set<Permission> permissions;

                                if (value.charAt(0) == '['
                                        && value.charAt(value.length() - 1) == ']') {
                                    permissions = Stream.of(
                                                    value.substring(1, value.length() - 1).split(
                                                            ","))
                                            .collect(HashSet::new, (set, s) -> {
                                                // only add existing permissions
                                                try {
                                                    Permission permission = Permission.fromString(
                                                            s.toLowerCase(Locale.ROOT));

                                                    if (permission != Permission.NONE) {
                                                        set.add(permission);
                                                    }
                                                } catch (IllegalArgumentException ignored) {}
                                            }, Set::addAll);
                                } else {
                                    permissions = new HashSet<>();
                                }

                                // make immutable
                                map.put(key, ImmutableSet.copyOf(permissions));
                            } catch (NumberFormatException ignored) {}
                        }, Map::putAll);
                // make immutable
                return ImmutableMap.copyOf(permissionMap);
            }, "permissions", ImmutableMap.of());

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(
            ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}
