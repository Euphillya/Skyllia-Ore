package fr.euphyllia.skylliaore.config;

import fr.euphyllia.skylliaore.api.Generator;
import io.th0rgal.oraxen.api.OraxenBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultConfig {

    private final Map<String, Generator> generators = new HashMap<>();
    private String defaultGenerator = "";


    public void loadConfiguration(FileConfiguration config) {
        defaultGenerator = config.getString("generator_default", "test");
        List<Map<?, ?>> generatorList = config.getMapList("generators");

        for (Map<?, ?> genData : generatorList) {
            String name = (String) genData.get("name");
            List<String> replaceBlocks = ((List<String>) genData.get("replace_block"))
                    .stream().map(String::toUpperCase).toList();
            List<String> worlds = (List<String>) genData.get("world");
            List<Map<?, ?>> blockByChance = (List<Map<?, ?>>) genData.get("block_chance");

            Map<BlockData, Double> blockChances = new HashMap<>();
            for (Map<?, ?> blockData : blockByChance) {
                String blockMaterial = (String) blockData.get("block");
                BlockData block;
                if (blockMaterial.startsWith("oraxen:")) {
                    if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
                        String oraxenBlock = blockMaterial.split(":")[1];
                        BlockData data = OraxenBlocks.getOraxenBlockData(oraxenBlock);
                        if (data == null) continue;
                        block = data;
                    } else {
                        block = Material.STONE.createBlockData();
                    }
                } else {

                    block = Material.valueOf(blockMaterial.toUpperCase()).createBlockData();
                }
                double chance = Double.parseDouble(String.valueOf(blockData.get("chance")));
                blockChances.put(block, chance);
            }

            generators.put(name, new Generator(name, replaceBlocks, worlds, blockChances));
        }
    }

    public Map<String, Generator> getGenerators() {
        return generators;
    }

    public Generator getDefaultGenerator() {
        return getGenerators().getOrDefault(defaultGenerator, new Generator("null",
                List.of("COBBLESTONE"),
                List.of("sky-overworld"),
                Map.of(Material.COBBLESTONE.createBlockData(), 100.0)));
    }
}