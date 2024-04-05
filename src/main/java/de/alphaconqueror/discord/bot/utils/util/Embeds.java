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

package de.alphaconqueror.discord.bot.utils.util;

import de.alphaconqueror.discord.bot.utils.permission.Permission;
import java.awt.Color;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public interface Embeds {

    Supplier<MessageEmbed> AN_ERROR_OCCURRED = () -> new EmbedBuilder().setDescription(
            "An error occurred during command execution.").setColor(Color.RED).build();

    Function<net.dv8tion.jda.api.Permission, MessageEmbed> BOT_NO_PERMISSION =
            permission -> new EmbedBuilder().setDescription(
            "The bot is missing the permission `" + permission + "` to execute this "
                    + "command! Please contact an admin.").setColor(Color.RED).build();

    Function<String, MessageEmbed> FAILURE = message -> new EmbedBuilder().setDescription(message)
            .setColor(Color.RED).build();

    Function<Permission, MessageEmbed> NO_PERMISSION =
            permission -> new EmbedBuilder().setDescription(
                    "You are missing the permission `" + permission + "` to execute this command!")
            .setColor(Color.RED).build();

    Function<String, MessageEmbed> SUCCESS = message -> new EmbedBuilder().setDescription(message)
            .setColor(Color.GREEN).build();

    Supplier<MessageEmbed> THIS_SHOULDNT_HAVE_HAPPENED = () -> new EmbedBuilder().setDescription(
            "This shouldn't have happened. Please contact an admin.").setColor(Color.RED).build();

    Function<String, MessageEmbed> WARNING = message -> new EmbedBuilder().setDescription(message)
            .setColor(Color.YELLOW).build();
}
