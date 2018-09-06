/*
 * Copyright (c) 2016-2017 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package co.aikar.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class PaperCommandManager extends BukkitCommandManager {

    // If we get anything Paper specific
    public PaperCommandManager(Plugin plugin) {
        super(plugin);
        try {
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            plugin.getServer().getPluginManager().registerEvents(new PaperAsyncTabCompleteHandler(this), plugin);
        } catch (ClassNotFoundException ignored) {
            // Ignored
        }
        plugin.getServer().getPluginManager().registerEvents(new PaperLocaleChangeHandler(this), plugin);
        localeTask.cancel();
    }

    @Override
    public synchronized CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new PaperCommandContexts(this);
        }
        return this.contexts;
    }

    @Override
    public synchronized CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new PaperCommandCompletions(this);
        }
        return this.completions;
    }

    void updateIssuerLocale(Player player, String localeString) {
        String[] split = ACFPatterns.UNDERSCORE.split(localeString);
        Locale locale = split.length > 1 ? new Locale(split[0], split[1]) : new Locale(split[0]);
        Locale prev = issuersLocale.put(player.getUniqueId(), locale);
        if (!Objects.equals(locale, prev)) {
            this.notifyLocaleChange(getCommandIssuer(player), prev, locale);
        }
    }
}
