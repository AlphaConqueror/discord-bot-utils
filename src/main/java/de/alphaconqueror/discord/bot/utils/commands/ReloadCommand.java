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
import de.alphaconqueror.discord.bot.utils.command.InteractionContext;
import de.alphaconqueror.discord.bot.utils.command.abstraction.AbstractCommand;
import de.alphaconqueror.discord.bot.utils.command.builder.RootCommandBuilder;
import de.alphaconqueror.discord.bot.utils.command.builder.node.RootCommandNode;
import de.alphaconqueror.discord.bot.utils.util.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends AbstractCommand {

    public ReloadCommand(final DiscordBotClient.@NonNull Client client) {
        super(client, "reload", "Reloads the bot.");
    }

    @NonNull
    @Override
    protected RootCommandNode build(@NotNull final RootCommandBuilder data) {
        return data.showFor(DefaultMemberPermissions.enabledFor(Permission.ALL_PERMISSIONS))
                .requires(de.alphaconqueror.discord.bot.utils.permission.Permission.RELOAD)
                .executes(this::reload).build();
    }

    @NonNull
    private WebhookMessageCreateAction<Message> reload(@NonNull final InteractionContext context) {
        try {
            this.client.getConfiguration().reload();
            return context.getEvent().getHook()
                    .sendMessageEmbeds(Embeds.SUCCESS.apply("Reload complete.")).setEphemeral(true);
        } catch (final RuntimeException e) {
            final String message = e.toString();
            final String description =
                    "Reload failed:\n\n```" + message.substring(0, Math.min(message.length(), 4074))
                            + "```";

            return context.getEvent().getHook()
                    .sendMessageEmbeds(new EmbedBuilder().setDescription(description).build())
                    .setEphemeral(true);
        }
    }
}
