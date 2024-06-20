package fr.euphyllia.skylliaore.commands;

import fr.euphyllia.skyllia.api.SkylliaAPI;
import fr.euphyllia.skyllia.api.skyblock.Island;
import fr.euphyllia.skylliaore.Main;
import fr.euphyllia.skylliaore.api.Generator;
import fr.euphyllia.skylliaore.config.DefaultConfig;
import fr.euphyllia.skylliaore.database.MariaDBInit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class OreCommands implements CommandExecutor, TabCompleter {

    /**
     * Executes the given command, returning its success.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /<command> <player> <generator>");
            return false;
        }

        Bukkit.getAsyncScheduler().runNow(Main.getPlugin(Main.class), task -> {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
            CompletableFuture<@Nullable Island> future = SkylliaAPI.getIslandByPlayerId(offPlayer.getUniqueId());
            if (future == null) return;
            Island island = future.join();
            if (island == null) return;

            String nameGenerator = args[1];
            CompletableFuture<Boolean> updateFuture = MariaDBInit.getMariaDbGenerator().updateGenIsland(island.getId(), nameGenerator);
            if (updateFuture.join()) {
                sender.sendMessage("Générateur changé avec succès.");
            } else {
                sender.sendMessage("Une erreur est survenue lors du changement de générateur.");
            }
        });

        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            Main.getPlugin(Main.class);
            DefaultConfig config = Main.getDefaultConfig();
            Map<String, Generator> generators = config.getGenerators();
            return new ArrayList<>(generators.keySet());
        }
        return List.of();
    }
}
