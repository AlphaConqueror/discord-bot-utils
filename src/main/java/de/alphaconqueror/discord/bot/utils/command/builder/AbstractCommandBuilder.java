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

import de.alphaconqueror.discord.bot.utils.command.builder.node.AbstractCommandNode;
import de.alphaconqueror.discord.bot.utils.permission.Permission;
import javax.annotation.CheckReturnValue;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractCommandBuilder<T extends AbstractCommandNode<?>,
        B extends AbstractCommandBuilder<T, B>> {

    @NonNull
    protected final String name;
    @NonNull
    protected final String description;
    @NonNull
    protected Permission permission = Permission.NONE;

    protected AbstractCommandBuilder(@NonNull final String name,
            @NonNull final String description) {
        this.name = name;
        this.description = description;
    }

    @NonNull
    public abstract T build();

    protected abstract B getThis();

    @NonNull
    @CheckReturnValue
    public B requires(@NonNull final Permission permission) {
        this.permission = permission;
        return this.getThis();
    }
}
