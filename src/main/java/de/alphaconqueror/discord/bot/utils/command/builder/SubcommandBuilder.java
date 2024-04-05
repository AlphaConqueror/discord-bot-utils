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

package de.alphaconqueror.discord.bot.utils.command.builder;

import de.alphaconqueror.discord.bot.utils.command.abstraction.CommandFunction;
import de.alphaconqueror.discord.bot.utils.command.builder.node.OptionNode;
import de.alphaconqueror.discord.bot.utils.command.builder.node.SubcommandNode;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.CheckReturnValue;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class SubcommandBuilder extends AbstractCommandBuilder<SubcommandNode, SubcommandBuilder> {

    private final Map<String, OptionNode> options = new LinkedHashMap<>();
    @Nullable
    private CommandFunction function;
    @Nullable
    private Runnable executeAfter;

    protected SubcommandBuilder(@NonNull final String name, @NonNull final String description) {
        super(name, description);
    }

    @Override
    public @NotNull SubcommandNode build() {
        return new SubcommandNode(this.name, this.description, this.permission, this.options,
                this.function, this.executeAfter);
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder then(final OptionBuilder child) {
        final OptionNode node = child.build();

        if (this.options.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has child with name '" + node.getName() + "'.");
        }

        this.options.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder executes(@NonNull final CommandFunction function) {
        this.function = function;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder executesAfter(@NonNull final Runnable run) {
        this.executeAfter = run;
        return this;
    }

    @Override
    protected SubcommandBuilder getThis() {
        return this;
    }
}
