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

import de.alphaconqueror.discord.bot.utils.command.CommandErrorException;
import de.alphaconqueror.discord.bot.utils.command.InteractionContext;
import de.alphaconqueror.discord.bot.utils.command.builder.node.OptionNode;
import java.util.Map;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Contains options to be handled.
 */
public interface OptionHandler extends FunctionHandler {

    @NonNull Map<String, OptionNode> getOptions();

    /**
     * Check if options are applicable.
     */
    default void checkConditions() {
        if (this.getOptions().size() > 1 && !this.hasFunction()) {
            throw new IllegalArgumentException(
                    "Counted multiple options. These options need to be handled by their parent "
                            + "node.");
        }
    }

    /**
     * Interact with options.
     *
     * @param context the context
     * @return the resulting action
     */
    @NonNull
    default CommandResult interactOptions(@NonNull final InteractionContext context) {
        if (this.getOptions().size() != 1) {
            return new CommandResult(this.getFunction().apply(context), this.getExecuteAfter());
        }

        for (final OptionMapping optionMapping : context.getEvent().getOptions()) {
            final OptionNode option = this.getOptions().get(optionMapping.getName());

            if (option != null) {
                return option.interact(context);
            }
        }

        throw new CommandErrorException("Could not find option.");
    }
}
