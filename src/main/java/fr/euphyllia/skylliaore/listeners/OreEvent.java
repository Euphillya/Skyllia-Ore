package fr.euphyllia.skylliaore.listeners;

import fr.euphyllia.skyllia.api.SkylliaAPI;
import fr.euphyllia.skyllia.api.skyblock.Island;
import fr.euphyllia.skylliaore.Main;
import fr.euphyllia.skylliaore.api.Generator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import java.util.Map;
import java.util.Random;

public class OreEvent implements Listener {

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        if (event.isCancelled()) return;

        Location location = event.getBlock().getLocation();
        World world = location.getWorld();

        if (!SkylliaAPI.isWorldSkyblock(world)) return;

        Island island = SkylliaAPI.getIslandByChunk(location.getChunk());
        if (island == null) return;

        handleBlockFormation(event, world, island);
    }

    private void handleBlockFormation(BlockFormEvent event, World world, Island island) {
        String worldName = world.getName();
        Material blockType = event.getNewState().getType();

        Generator generator = Main.getCache().getGeneratorIsland(island.getId());

        if (generator.worlds().contains(worldName)) {
            for (String replace : generator.replaceBlocks()) {
                if (replace.equalsIgnoreCase(blockType.name())) {
                    BlockData blockByChance = getBlockByChance(generator.blockChances());
                    event.getNewState().setBlockData(blockByChance);
                    break;
                }
            }
        }
    }

    private BlockData getBlockByChance(Map<BlockData, Double> blockChances) {
        double totalChance = blockChances.values().stream().mapToDouble(i -> i).sum();
        double randomChance = new Random().nextDouble() * totalChance;

        double currentChance = 0;
        for (Map.Entry<BlockData, Double> entry : blockChances.entrySet()) {
            currentChance += entry.getValue();
            if (randomChance < currentChance) {
                return entry.getKey();
            }
        }
        return Material.COBBLESTONE.createBlockData(); // Default block if no match is found
    }
}
