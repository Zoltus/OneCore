package io.github.zoltus.onecore.player.nbt;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NBTPlayer {
    private final NBTFile nbt;
    @Getter
    private final NBTStats stats;
    private final World world;

    private final NBTCompound abilities;

    public NBTPlayer(UUID uuid) {
        this.nbt = getNBTFile(uuid);
        this.world = Bukkit.getWorlds().get(0);
        if (nbt == null) {
            throw new NbtApiException("§cPlayers NBTFile not found!", null);
        } else {
            this.abilities = nbt.getCompound("abilities");
            //Loads stats from world json file
            //todo convert to Bukkit.getworldocntainer
            File statsFile = new File(world.getWorldFolder().getAbsolutePath() + "/stats", uuid.toString() + ".json");
            try {
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new FileReader(statsFile));
                this.stats = gson.fromJson(reader, NBTStats.class);
            } catch (FileNotFoundException e) {
                throw new NbtApiException("Error loading nbtfile!", e);
            }
        }
    }

    private NBTFile getNBTFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            File dataFolder = new File(world.getWorldFolder(), "playerdata");
            File playerFile = new File(dataFolder, uuid.toString() + ".dat");
            if (playerFile.exists()) {
                try {
                    return new NBTFile(playerFile);
                } catch (IOException e) {
                    throw new NbtApiException("Error loading player data!", e);
                }
            }
        }
        return null;
    }

    public NBTPlayer(OfflinePlayer player) {
        this(player.getUniqueId());
    }

    @SneakyThrows
    public void save() {
        try {
            nbt.save();
        } catch (IOException e) {
            throw new NbtApiException("Error when saving level data!", e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Double getAttribute(String abilityName) {
        NBTCompoundList list = nbt.getCompoundList("Attributes");
        for (ReadWriteNBT nbt : list) {
            if (nbt.getString("Name").equals(abilityName)) {
                return nbt.getDouble("Base"); //todo
            }
        }
        return null;
    }

    public Double getMaxHealth() {
        return getAttribute(":generic.max_health");
    }

    public void setWalkSpeed(float f) {
        abilities.setFloat("walkSpeed", f);
    }

    public void setFlySpeed(float f) {
        abilities.setFloat("flySpeed", f);
    }

    public boolean getFlying() {
        return abilities.getBoolean("flying");
    }

    public void setFlying(boolean bool) {
        abilities.setBoolean("flying", bool);
    }

    public boolean getMayfly() {
        return abilities.getBoolean("mayfly");
    }

    public void setMayfly(boolean bool) {
        abilities.setBoolean("mayfly", bool);
    }

    public int getplayerGameType() {
        return nbt.getInteger("playerGameType");
    }

    public boolean getInvulnerable() {
        return nbt.getBoolean("Invulnerable");
    }

    public void setInvulnerable(boolean bool) {
        nbt.setBoolean("Invulnerable", bool);
    }

    public Map<Integer, ItemStack> getInventory() {
        return getItems("Inventory");
    }

    public void setItems(Map<Integer, ItemStack> items, String inventory) {
        NBTCompoundList compList = nbt.getCompoundList(inventory);
        items.forEach((slot, stack) -> {
            compList.clear();
            NBTContainer container = NBTItem.convertItemtoNBT(stack);
            compList.addCompound(container);
        });
        //todo check if complist needs to be set back to nbt
    }

    public void setInventoryItems(Map<Integer, ItemStack> items) {
        setItems(items, "Inventory");
    }

    public Map<Integer, ItemStack> getEnderItems() {
        return getItems("EnderItems");
    }

    public void setEnderItems(Map<Integer, ItemStack> items) {
        setItems(items, "EnderItems");
    }


    public Location getLocation() {
        NBTDoubleList doubleList = (NBTDoubleList) nbt.getDoubleList("Pos");
        float yaw = getYaw();
        float pitch = getPitch();
        String worldName = world.getName();
        World world = Bukkit.getWorld(worldName);
        double x = doubleList.get(0);
        double y = doubleList.get(1);
        double z = doubleList.get(2);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public float getYaw() {
        return nbt.getFloatList("Rotation").get(0);
    }

    public float getPitch() {
        return nbt.getFloatList("Rotation").get(1);
    }

    private Map<Integer, ItemStack> getItems(String inventory) {
        Map<Integer, ItemStack> items = new HashMap<>();
        nbt.getCompoundList(inventory).forEach(slotNBT -> {
            int slot = slotNBT.getInteger("Slot");
            ItemStack stack = NBTItem.convertNBTtoItem((NBTCompound) slotNBT);
            items.put(slot, stack);
        });
        return items;
    }

    //todo clean setdimension/world
    public void setLocation(Location loc) {
        loc.setWorld(world);
        NBTDoubleList pos = (NBTDoubleList) nbt.getDoubleList("Pos");
        NBTFloatList rotation = (NBTFloatList) nbt.getFloatList("Rotation");
        NBTDoubleList motion = (NBTDoubleList) nbt.getDoubleList("Motion");
        pos.clear();
        pos.addAll(List.of(loc.getX(), loc.getY(), loc.getZ()));
        rotation.clear();
        rotation.addAll(List.of(loc.getYaw(), loc.getPitch()));
        motion.clear();
        pos.addAll(List.of(0d, 0d, 0d));
    }

    public void setplayerGameType(int i) {
        nbt.setInteger("playerGameType", i);
    }

    public void setPreviousPlayerGameType(int i) {
        nbt.setInteger("previousPlayerGameType", i);
    }

    public void setSaturationLevel(float f) {
        nbt.setFloat("foodSaturationLevel", f);
    }

    public float getSaturationLevel() {
        return nbt.getFloat("foodSaturationLevel");
    }

    public void setFoodLevel(int i) {
        nbt.setInteger("foodLevel", i);
    }

    public float getFoodLevel() {
        return nbt.getFloat("foodLevel");
    }

    public void setHealth(double d) {
        nbt.setDouble("Health", d);
    }

    public void setExhaustionLevel(float f) {
        nbt.setFloat("foodExhaustionLevel", f);
    }

    public float getExhaustionLevel() {
        return nbt.getFloat("foodExhaustionLevel");
    }

    //Copilot create NBTPlayerException
    public static class NbtApiException extends RuntimeException {
        public NbtApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
