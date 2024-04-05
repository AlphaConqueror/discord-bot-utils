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

package de.alphaconqueror.discord.bot.utils.command.abstraction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class CommandResult {

    @NonNull
    private final WebhookMessageCreateAction<Message> message;
    @Nullable
    private Runnable executeAfter;

    public CommandResult(@NonNull final WebhookMessageCreateAction<Message> message,
            @Nullable final Runnable executeAfter) {
        this.message = message;
        this.executeAfter = executeAfter;
    }

    public CommandResult(@NonNull final WebhookMessageCreateAction<Message> message) {
        this.message = message;
    }

    public @NotNull WebhookMessageCreateAction<Message> getMessage() {
        return this.message;
    }

    public void executeAfter() {
        if (this.executeAfter != null) {
            this.executeAfter.run();
        }
    }
}
