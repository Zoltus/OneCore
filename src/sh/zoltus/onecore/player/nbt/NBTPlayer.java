package sh.zoltus.onecore.player.nbt;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.data.NBTData;
import de.tr7zw.nbtapi.data.PlayerData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NBTPlayer {

    private final PlayerData data;
    private final NBTFile nbt;
    @Getter private final NBTStats stats;

    private final NBTCompound abilities;
    public NBTPlayer(UUID uuid) {
        this.data = NBTData.getOfflinePlayerData(uuid);
        this.nbt = data.getFile();
        this.abilities = nbt.getCompound("abilities");
        //Loads stats from world json file
        //todo convert to Bukkit.getworldocntainer
        File statsFile = new File(getWorld().getWorldFolder().getAbsolutePath() + "/stats",uuid.toString() + ".json");
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(statsFile));
            this.stats = gson.fromJson(reader, NBTStats.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public NBTPlayer(OfflinePlayer player) {
        this(player.getUniqueId());
    }

    @SneakyThrows
    public void save() {
        data.saveChanges();
    }

    @SuppressWarnings("SameParameterValue")
    private Double getAttribute(String abilityName) {
        NBTCompoundList list = nbt.getCompoundList("Attributes");
        for (NBTListCompound nbt : list) {
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

    public float getHealth() {
        return data.getHealth();
    }

    public void setHealth(double d) {
        nbt.setDouble("Health", d);
    }

    public void setFoodExhaustionLevel(float f) {
        nbt.setFloat("foodExhaustionLevel", f);
    }

    public Map<Integer, ItemStack> getInventoryItems() {
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
        String worldName = getWorld().getName();
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

    public World getWorld() {
        String world = nbt.getString("Dimension").split(":")[1];
        //todo fix for custom worlds
        //return Bukkit.getWorld(world);
         return world.equals("overworld") ? Bukkit.getWorlds().get(0) : Bukkit.getWorld(world);
    }

    private Map<Integer, ItemStack> getItems(String inventory) {
        Map<Integer, ItemStack> items = new HashMap<>();
        nbt.getCompoundList(inventory).forEach(slotNBT -> {
            int slot = slotNBT.getInteger("Slot");
            ItemStack stack = NBTItem.convertNBTtoItem(slotNBT);
            items.put(slot, stack);
        });
        return items;
    }

    //todo clean setdimension/world
    public void setLocation(Location loc) {
        loc.setWorld(getWorld());
        NBTDoubleList pos = (NBTDoubleList) nbt.getDoubleList("Pos");
        NBTFloatList rotation = (NBTFloatList) nbt.getFloatList("Rotation");
        NBTDoubleList motion = (NBTDoubleList) nbt.getDoubleList("Motion");
        pos.clear();
        pos.addAll(List.of(loc.getX(), loc.getY(), loc.getZ()));
        rotation.clear();
        rotation.addAll(List.of(loc.getYaw(), loc.getPitch()));
        motion.clear();
        pos.addAll(List.of(0d,0d,0d));
    }

    public void setplayerGameType(int i) {
        nbt.setInteger("playerGameType", i);
    }

    public void setPreviousPlayerGameType(int i) {
        nbt.setInteger("previousPlayerGameType", i);
    }

    public void setfoodSaturationLevel(float f) {
        nbt.setFloat("foodSaturationLevel", f);
    }

    public void setfoodLevel(int i) {
        nbt.setInteger("foodLevel", i);
    }
}
