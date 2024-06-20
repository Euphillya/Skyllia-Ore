package fr.euphyllia.skylliaore.api;

import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.Map;

public record Generator(String name, List<String> replaceBlocks, List<String> worlds,
                        Map<BlockData, Double> blockChances) {

}