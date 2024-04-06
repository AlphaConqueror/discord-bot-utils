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

package de.alphaconqueror.discord.bot.utils.command;

import de.alphaconqueror.discord.bot.utils.DiscordBotClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class InteractionContext {

    @NonNull
    private final DiscordBotClient client;
    @NonNull
    private final SlashCommandInteractionEvent event;

    public InteractionContext(@NotNull final DiscordBotClient client,
            final @NotNull SlashCommandInteractionEvent event) {
        this.client = client;
        this.event = event;
    }

    public @NonNull DiscordBotClient getClient() {
        return this.client;
    }

    @NonNull
    public SlashCommandInteractionEvent getEvent() {
        return this.event;
    }

    @NonNull
    public OptionMapping getOption(@NonNull final String name) {
        final OptionMapping option = this.event.getOption(name);

        if (option == null) {
            throw new CommandErrorException("Could not find option mapping.");
        }

        return option;
    }
}
