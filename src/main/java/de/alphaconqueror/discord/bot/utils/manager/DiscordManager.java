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

package de.alphaconqueror.discord.bot.utils.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.alphaconqueror.discord.bot.utils.DiscordBotClient;
import de.alphaconqueror.discord.bot.utils.command.abstraction.AbstractCommand;
import de.alphaconqueror.discord.bot.utils.commands.ReloadCommand;
import de.alphaconqueror.discord.bot.utils.commands.RestartCommand;
import de.alphaconqueror.discord.bot.utils.commands.ShutdownCommand;
import de.alphaconqueror.discord.bot.utils.commands.SyncCommand;
import de.alphaconqueror.discord.bot.utils.commands.TestCommand;
import de.alphaconqueror.discord.bot.utils.commands.UnsyncCommand;
import de.alphaconqueror.discord.bot.utils.config.ConfigKeys;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class DiscordManager {

    @NonNull
    protected final DiscordBotClient client;
    @NonNull
    protected final JDA jda;
    @NonNull
    protected final Set<Class<? extends AbstractCommand>> commandClasses;
    @NonNull
    protected final Map<AbstractCommand, CommandData> globalCommands;
    @NonNull
    protected final Map<AbstractCommand, CommandData> guildCommands;
    // commands that are exempt from unsync
    @NonNull
    protected final Map<AbstractCommand, CommandData> keep;

    public DiscordManager(@NonNull final DiscordBotClient client) throws InterruptedException {
        this.client = client;
        this.jda = JDABuilder.createDefault(client.getConfiguration().get(ConfigKeys.TOKEN))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES).setActivity(
                        Activity.customStatus(client.getConfiguration().get(ConfigKeys.STATUS)))
                .build().awaitReady();
        this.commandClasses = ImmutableSet.copyOf(this.constructCommandClasses());

        final Map<AbstractCommand, CommandData> commands = new HashMap<>();

        this.commandClasses.forEach(c -> {
            try {
                final AbstractCommand abstractCommand = c.getConstructor(DiscordBotClient.class)
                        .newInstance(this.client);

                commands.put(abstractCommand, abstractCommand.createData());
            } catch (final InstantiationException | IllegalAccessException |
                           InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        final Map<AbstractCommand, CommandData> globalCommands = new HashMap<>();
        final Map<AbstractCommand, CommandData> guildCommands = new HashMap<>();
        final Map<AbstractCommand, CommandData> keep = new HashMap<>();

        // filter commands by global/guild
        commands.forEach((key, value) -> {
            if (value.isGuildOnly()) {
                guildCommands.put(key, value);
            } else {
                globalCommands.put(key, value);
            }

            if (key.keep()) {
                keep.put(key, value);
            }
        });

        this.globalCommands = ImmutableMap.copyOf(globalCommands);
        this.guildCommands = ImmutableMap.copyOf(guildCommands);
        this.keep = ImmutableMap.copyOf(keep);

        this.syncAllCommands();
    }

    @NotNull
    public JDA getJda() {
        return this.jda;
    }

    @Nullable
    public Guild getGuild() {
        return this.jda.getGuildById(this.client.getConfiguration().get(ConfigKeys.GUILD_ID));
    }

    public boolean syncAllCommands() {
        this.syncGlobalCommands();
        return this.syncGuildCommands();
    }

    public boolean unsyncAllCommands() {
        this.unsyncGlobalCommands();
        return this.unsyncGuildCommands();
    }

    public void syncGlobalCommands() {
        this.jda.updateCommands().addCommands(this.globalCommands.values()).queue();
        this.registerListeners(this.globalCommands.keySet());
        this.client.getLogger().info("Synchronized global commands.");
    }

    public void unsyncGlobalCommands() {
        this.jda.updateCommands().addCommands(
                this.keep.values().stream().filter(command -> !command.isGuildOnly())
                        .collect(Collectors.toList())).queue();

        final Set<AbstractCommand> unregister = new HashSet<>(this.globalCommands.keySet());
        unregister.removeAll(this.keep.keySet());

        this.unregisterListeners(unregister);
        this.client.getLogger().info("Unsynchronized global commands.");
    }

    public boolean syncGuildCommands() {
        final Guild guild = this.getGuild();

        if (guild == null) {
            this.client.getLogger().info("Guild not found, could not synchronize guild commands.");
            return false;
        }

        guild.updateCommands().addCommands(this.guildCommands.values()).queue();
        this.registerListeners(this.guildCommands.keySet());
        this.client.getLogger().info("Synchronized guild commands.");

        return true;
    }

    public boolean unsyncGuildCommands() {
        final Guild guild = this.getGuild();

        if (guild == null) {
            this.client.getLogger()
                    .info("Guild not found, could not unsynchronize guild commands.");
            return false;
        }

        guild.updateCommands().addCommands(
                this.keep.values().stream().filter(CommandData::isGuildOnly)
                        .collect(Collectors.toList())).queue();

        final Set<AbstractCommand> unregister = new HashSet<>(this.guildCommands.keySet());
        unregister.removeAll(this.keep.keySet());

        this.unregisterListeners(unregister);
        this.client.getLogger().info("Unsynchronized guild commands.");

        return true;
    }

    public boolean fixGuildCommands() {
        final Guild guild = this.getGuild();

        if (guild == null) {
            this.client.getLogger().info("Guild not found, could not synchronize guild commands.");
            return false;
        }

        guild.updateCommands().addCommands(this.globalCommands.values()).queue();
        this.client.getLogger().info("Global commands for guilds have been fixed.");

        return true;
    }

    public void registerListeners(final Collection<AbstractCommand> abstractCommands) {
        for (final AbstractCommand abstractCommand : abstractCommands) {
            if (!this.jda.getRegisteredListeners().contains(abstractCommand)) {
                this.jda.addEventListener(abstractCommand);
            }
        }

        this.client.getLogger().info("Registered listeners for commands: {}",
                abstractCommands.stream().map(AbstractCommand::getName)
                        .collect(Collectors.toList()));
    }

    public void unregisterListeners(final Collection<AbstractCommand> abstractCommands) {
        this.jda.removeEventListener(abstractCommands.toArray());
        this.client.getLogger().info("Unregistered listeners for commands: {}",
                abstractCommands.stream().map(AbstractCommand::getName)
                        .collect(Collectors.toList()));
    }

    @NonNull
    private Set<Class<? extends AbstractCommand>> constructCommandClasses() {
        return new HashSet<>(
                Arrays.asList(ReloadCommand.class, RestartCommand.class, ShutdownCommand.class,
                        SyncCommand.class, TestCommand.class, UnsyncCommand.class));
    }
}
