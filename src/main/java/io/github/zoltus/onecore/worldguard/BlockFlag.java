package io.github.zoltus.onecore.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class BlockFlag extends Flag<Material> {
    public BlockFlag(String name) {
        super(name);
    }

    @Override
    public Material unmarshal(@Nullable Object object) {
        return Material.getMaterial(String.valueOf(object));
    }

    @Override
    public Object marshal(Material material) {
        return material.name();
    }

    public Material parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        Material mat = Material.getMaterial(input.toUpperCase());
        if (mat == null) {
            throw new InvalidFlagFormat(input + " is not a valid material name.");
        }
        if (!mat.isBlock()) {
            throw new InvalidFlagFormat(mat.name() + " is not a block.");
        }
        return mat;
    }
}
