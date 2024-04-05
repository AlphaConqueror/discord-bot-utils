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
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class OptionNode extends AbstractCommandNode<OptionData> implements FunctionHandler {

    @NonNull
    private final OptionType type;
    @NonNull
    private final Map<String, ChoiceNode> choices;
    private final boolean isRequired;
    private final boolean isAutoComplete;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public OptionNode(@NonNull final String name, @NonNull final String description,
            @NonNull final Permission requiredPermission, @NonNull final OptionType type,
            final boolean isRequired, final boolean isAutoComplete,
            @NonNull final Map<String, ChoiceNode> choices,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, description, requiredPermission);
        this.type = type;
        this.isRequired = isRequired;
        this.isAutoComplete = isAutoComplete;
        this.choices = choices;
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public OptionData create() {
        return new OptionData(this.type, this.name, this.description, this.isRequired,
                this.isAutoComplete).addChoices(
                this.choices.values().stream().map(ChoiceNode::create)
                        .collect(Collectors.toList()));
    }

    @Override
    public boolean hasFunction() {
        return this.function != null;
    }

    @NonNull
    @Override
    public CommandFunction getFunction() {
        return this.function == null ? this.getAlternativeFunction() : this.function;
    }

    @Override
    public @Nullable Runnable getExecuteAfter() {
        return this.executeAfter;
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext context) {
        final ChoiceNode choice = this.choices.get(context.getOption(this.name).getAsString());

        if (choice == null) {
            return new CommandResult(this.getFunction().apply(context), this.executeAfter);
        }

        return choice.interact(context);
    }
}
