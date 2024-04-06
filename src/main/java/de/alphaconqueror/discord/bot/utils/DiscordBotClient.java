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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import net.dv8tion.jda.api.JDA;

public abstract class DiscordBotClient {

    private final LoggerFactory logger = this.provideLoggerFactory();
    private final ConfigFactory configFactory = this.provideConfigFactory();
    private PermissionManager permissionManager;
    private DiscordManager discordManager;


    protected abstract LoggerFactory provideLoggerFactory();

    protected abstract ConfigFactory provideConfigFactory();

    public void enable() {
        final Instant startupTime = Instant.now();
        this.logger.info("Starting discord bot...");
        // load configuration
        this.logger.info("Loading configuration...");

        this.permissionManager = new PermissionManager(this);

        try {
            this.discordManager = new DiscordManager(this);
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

        System.exit(0);
    }

    public LoggerFactory getLogger() {
        return this.logger;
    }

    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public ConfigFactory getConfigFactory() {
        return this.configFactory;
    }

    protected void onEnable() {}

    protected void onDisable() {}

    private Path resolveConfig(final String fileName) {
        final Path configFile = Paths.get(fileName);

        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            if (configFile.getParent() != null) {
                try {
                    Files.createDirectories(configFile.getParent());
                } catch (final IOException ignored) {}
            }

            try (final InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream(fileName)) {
                if (is == null) {
                    this.logger.warn("Could not find file '{}' in the resources.", fileName);
                } else {
                    Files.copy(is, configFile);
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }
}
