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

package de.alphaconqueror.discord.bot.utils;

import de.alphaconqueror.discord.bot.utils.config.ConfigFactory;
import de.alphaconqueror.discord.bot.utils.logging.LoggerFactory;
import de.alphaconqueror.discord.bot.utils.manager.DiscordManager;
import de.alphaconqueror.discord.bot.utils.permission.PermissionManager;
import java.time.Duration;
import java.time.Instant;
import net.dv8tion.jda.api.JDA;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public abstract class DiscordBotClient {

    @NonNull
    private final LoggerFactory logger = this.provideLoggerFactory();
    @NonNull
    private final ConfigFactory configFactory = this.provideConfigFactory();
    @Nullable
    private PermissionManager permissionManager;
    @Nullable
    private DiscordManager discordManager;

    @NonNull
    protected abstract LoggerFactory provideLoggerFactory();

    @NonNull
    protected abstract ConfigFactory provideConfigFactory();

    @NonNull
    protected abstract PermissionManager providePermissionManager();

    @NonNull
    protected abstract DiscordManager provideDiscordManager() throws InterruptedException;

    public void enable() {
        final Instant startupTime = Instant.now();
        this.logger.info("Starting discord bot...");
        // load configuration
        this.logger.info("Loading configuration...");

        this.permissionManager = this.providePermissionManager();

        try {
            this.discordManager = this.provideDiscordManager();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.onEnable();

        final Duration timeTaken = Duration.between(startupTime, Instant.now());
        this.logger.info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public void disable() {
        this.logger.info("Starting shutdown process...");
        this.onDisable();
        this.logger.info("Goodbye!");
    }

    public void restart() {
        this.logger.info("Restarting...");
        this.disable();
        this.enable();
    }

    public void shutdown() {
        this.disable();
        this.logger.info("Shutting down JDA...");

        if (this.discordManager != null) {
            final JDA jda = this.discordManager.getJda();

            jda.shutdown();
            this.logger.info("Waiting for JDA to shutdown...");

            try {
                // Allow at most 10 seconds for remaining requests to finish
                if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
                    this.logger.info("Forcing shutdown...");
                    jda.shutdownNow(); // Cancel all remaining requests
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.exit(0);
    }

    @NotNull
    public LoggerFactory getLogger() {
        return this.logger;
    }

    @NotNull
    public ConfigFactory getConfigFactory() {
        return this.configFactory;
    }

    @Nullable
    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    @Nullable
    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    protected void onEnable() {}

    protected void onDisable() {}
}
