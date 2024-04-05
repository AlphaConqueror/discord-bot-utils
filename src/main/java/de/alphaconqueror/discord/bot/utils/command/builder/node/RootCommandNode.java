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

package de.alphaconqueror.discord.bot.utils.command.builder.node;

import de.alphaconqueror.discord.bot.utils.command.CommandErrorException;
import de.alphaconqueror.discord.bot.utils.command.InteractionContext;
import de.alphaconqueror.discord.bot.utils.command.abstraction.CommandFunction;
import de.alphaconqueror.discord.bot.utils.command.abstraction.CommandResult;
import de.alphaconqueror.discord.bot.utils.command.abstraction.OptionHandler;
import de.alphaconqueror.discord.bot.utils.permission.Permission;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class RootCommandNode extends AbstractCommandNode<SlashCommandData> implements OptionHandler {

    @NonNull
    private final DefaultMemberPermissions defaultPermissions;
    private final boolean guildOnly;
    @NonNull
    private final Map<String, SubcommandGroupNode> subcommandGroups;
    @NonNull
    private final Map<String, SubcommandNode> subcommands;
    @NonNull
    private final Map<String, OptionNode> options;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public RootCommandNode(@NonNull final String name, @NonNull final String description,
            @NonNull final DefaultMemberPermissions defaultPermissions, final boolean guildOnly,
            @NonNull final Permission requiredPermission,
            @NonNull final Map<String, SubcommandGroupNode> subcommandGroups,
            @NonNull final Map<String, SubcommandNode> subcommands,
            @NonNull final Map<String, OptionNode> options,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, description, requiredPermission);
        this.defaultPermissions = defaultPermissions;
        this.guildOnly = guildOnly;
        this.subcommands = subcommands;
        this.subcommandGroups = subcommandGroups;
        this.options = options;
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public SlashCommandData create() {
        return Commands.slash(this.name, this.description)
                .setDefaultPermissions(this.defaultPermissions).setGuildOnly(this.guildOnly)
                .addSubcommandGroups(
                        this.subcommandGroups.values().stream().map(SubcommandGroupNode::create)
                                .collect(Collectors.toList())).addSubcommands(
                        this.subcommands.values().stream().map(SubcommandNode::create)
                                .collect(Collectors.toList())).addOptions(
                        this.options.values().stream().map(OptionNode::create)
                                .collect(Collectors.toList()));
    }

    @Override
    public boolean hasFunction() {
        return this.function != null;
    }

    @Override
    public @NonNull CommandFunction getFunction() {
        return this.function == null ? this.getAlternativeFunction() : this.function;
    }

    @Override
    public @Nullable Runnable getExecuteAfter() {
        return this.executeAfter;
    }

    @Override
    public @NonNull Map<String, OptionNode> getOptions() {
        return this.options;
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext context) {
        final SlashCommandInteractionEvent event = context.getEvent();

        if (event.getSubcommandGroup() != null) {
            final SubcommandGroupNode node = this.subcommandGroups.get(event.getSubcommandGroup());

            if (node == null) {
                throw new CommandErrorException("Could not find subcommand group.");
            }

            return node.interact(context);
        } else if (event.getSubcommandName() != null) {
            final SubcommandNode node = this.subcommands.get(event.getSubcommandName());

            if (node == null) {
                throw new CommandErrorException("Could not find subcommand.");
            }

            return node.interact(context);
        }

        return this.interactOptions(context);
    }
}
