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

package de.alphaconqueror.discord.bot.utils.permission;

import de.alphaconqueror.discord.bot.utils.DiscordBotClient;
import java.util.Map;
import java.util.Set;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PermissionManager {

    private final DiscordBotClient client;

    public PermissionManager(final DiscordBotClient client) {this.client = client;}

    public boolean hasPermission(@NonNull final User user, @NonNull final Permission permission) {
        return this.client.getDiscordManager().getGuild()
                .map(guild -> this.hasPermission(guild.retrieveMember(user).complete(), permission))
                .orElse(false);
    }

    public boolean hasPermission(@Nullable final Member member,
            @NonNull final Permission permission) {
        // no permission needed
        if (permission == DiscordPermission.NONE) {
            return true;
        }

        // member must be part of guild
        if (member == null) {
            return false;
        }

        final Map<Long, Set<Permission>> permissionMap = this.client.getConfig().getPermissions();

        // iterate through each role and
        return member.getRoles().stream().anyMatch(role -> {
            final Set<Permission> obtainedPermissions = permissionMap.get(role.getIdLong());

            // obtainedPermissions might be null when there is no config entry for this role id
            if (obtainedPermissions == null) {
                return false;
            }

            // check if role contains missing permission
            return obtainedPermissions.stream()
                    .anyMatch(p -> p == DiscordPermission.ALL || permission.equals(p));
        });
    }
}
