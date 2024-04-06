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

package de.alphaconqueror.discord.bot.utils.commands;

import de.alphaconqueror.discord.bot.utils.DiscordBotClient;
import de.alphaconqueror.discord.bot.utils.command.abstraction.AbstractCommand;
import de.alphaconqueror.discord.bot.utils.command.builder.RootCommandBuilder;
import de.alphaconqueror.discord.bot.utils.command.builder.node.RootCommandNode;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class RestartCommand extends AbstractCommand {

    public RestartCommand(final @NonNull DiscordBotClient client) {
        super(client, "restart", "Restarts the bot.");
    }

    @Override
    protected @NonNull RootCommandNode build(@NotNull final RootCommandBuilder data) {
        return data.showFor(DefaultMemberPermissions.enabledFor(Permission.ALL_PERMISSIONS))
                .requires(de.alphaconqueror.discord.bot.utils.permission.Permission.RESTART)
                .executes(context -> {
                    this.client.restart();
                    return context.getEvent().getHook().sendMessageEmbeds(
                            new EmbedBuilder().setDescription("Restarting...")
                                    .setColor(Color.ORANGE).build()).setEphemeral(true);
                }).build();
    }
}
