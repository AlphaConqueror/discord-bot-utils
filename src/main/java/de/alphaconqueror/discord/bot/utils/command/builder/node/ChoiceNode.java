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

import de.alphaconqueror.discord.bot.utils.command.InteractionContext;
import de.alphaconqueror.discord.bot.utils.command.abstraction.CommandFunction;
import de.alphaconqueror.discord.bot.utils.command.abstraction.CommandResult;
import de.alphaconqueror.discord.bot.utils.command.abstraction.FunctionHandler;
import de.alphaconqueror.discord.bot.utils.permission.Permission;
import java.util.Locale;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ChoiceNode extends AbstractCommandNode<Command.Choice> implements FunctionHandler {

    @NonNull
    private final String value;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public ChoiceNode(@NonNull final String name, @NonNull final Permission permission,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, "A choice node.", permission);
        this.value = name.toLowerCase(Locale.ROOT);
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public Command.Choice create() {
        return new Command.Choice(this.name, this.value);
    }

    @NotNull
    public String getValue() {
        return this.value;
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
    protected CommandResult onInteraction(@NonNull final InteractionContext context) {
        return new CommandResult(this.getFunction().apply(context), this.executeAfter);
    }
}
