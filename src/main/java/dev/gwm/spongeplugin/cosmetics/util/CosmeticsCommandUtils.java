package dev.gwm.spongeplugin.cosmetics.util;

import dev.gwm.spongeplugin.cosmetics.Cosmetics;
import dev.gwm.spongeplugin.cosmetics.command.HelpCommand;
import dev.gwm.spongeplugin.cosmetics.command.ReloadCommand;
import dev.gwm.spongeplugin.library.util.Language;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public final class CosmeticsCommandUtils {

    private CosmeticsCommandUtils() {
    }

    public static void registerCommands(Cosmetics instance) {
        Language language = instance.getLanguage();
        CommandSpec helpCommand = CommandSpec.builder().
                description(Text.of("Help command")).
                executor(new HelpCommand(language)).
                build();
        CommandSpec reloadCommand = CommandSpec.builder().
                permission("cosmetics.command.reload").
                description(Text.of("Reload the plugin")).
                executor(new ReloadCommand(language)).
                build();
        CommandSpec spec = CommandSpec.builder().
                permission("cosmetics.command.base").
                description(Text.of("The basic command")).
                child(helpCommand, "help").
                child(reloadCommand, "reload").
                build();
        Sponge.getCommandManager().register(Cosmetics.getInstance(), spec,
                "cosmetics", "cosmetic");
    }
}
