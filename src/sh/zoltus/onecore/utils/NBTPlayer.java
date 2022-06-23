package sh.zoltus.onecore.utils;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTListCompound;
import de.tr7zw.nbtapi.data.NBTData;
import de.tr7zw.nbtapi.data.PlayerData;
import lombok.SneakyThrows;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class NBTPlayer {


    private PlayerData data;
    private NBTCompound compound;

    public NBTPlayer(OfflinePlayer offP) {
        this(offP.getUniqueId());
    }

    //todo cleanup
    private NBTPlayer(UUID uuid) {
        Validate.notNull(uuid, "Cannot get null UUID");
        this.data = NBTData.getOfflinePlayerData(uuid);
        this.compound = data.getCompound();
    }

    @SneakyThrows
    public void save() {
        data.saveChanges();
    }

    @SuppressWarnings("SameParameterValue")
    private Double getAttribute(String abilityName) {
        NBTCompoundList list = compound.getCompoundList("Attributes");
        for (NBTListCompound nbt : list) {
            if (nbt.getString("Name").equals(abilityName)) {
                return attribute.k("Base"); //todo
            }
        }
        return null;
    }

    public Double getMaxHealth() {
        return getAttribute("minecraft:generic.max_health");
    }

    public void setWalkSpeed(float f) {
        compound.getCompound("abilities").setFloat("walkSpeed", f);
    }

    public void setFlySpeed(float f) {
        compound.getCompound("abilities").setFloat("flySpeed", f);
    }

    public boolean getFlying() {
        return compound.getCompound("abilities").getBoolean("flying");
    }

    public void setFlying(boolean bool) {
        compound.getCompound("abilities").setBoolean("flying", bool);
    }

    public boolean getMayfly() {
        return compound.getCompound("abilities").getBoolean("mayfly");
    }

    public void setMayfly(boolean bool) {
        compound.getCompound("abilities").setBoolean("mayfly", bool);
    }

    public int getplayerGameType() {
        return compound.getInteger("playerGameType");
    }

    public boolean getInvulnerable() {
        return compound.getBoolean("Invulnerable");
    }

    public void setInvulnerable(boolean bool) {
        compound.setBoolean("Invulnerable", bool);
    }

    public float getHealth() {
        return data.getHealth();
    }

    public void setHealth(double d) {
        compound.setDouble("Health", d);
    }

    public void setFoodExhaustionLevel(float f) {
        compound.setFloat("foodExhaustionLevel", f);
    }

    public int getXpSeed() {
        return compound.getInteger("XpSeed");
    }

    public void setXpSeed(int i) {
        compound.setInteger("XpSeed", i);
    }

    public Map<Integer, CraftItemStack> getInventoryItems() {
        return getItems(compound, "Inventory");
    }

    public void setItems(Map<Integer, ItemStack> items, String tag) {
        NBTTagList list = new NBTTagList();
        items.forEach((slot, stack) -> {
            NBTTagCompound stackTag = stack.getBoolean(stack.v());
            // MinecraftKey minecraftkey = IRegistry.m.setFloat();
            stackTag.setFloat("Slot", (byte) (int) slot);
            list.add(stackTag);
        });
        compound.setFloat(tag, list);
    }

    public void setInventoryItems(Map<Integer, ItemStack> items) {
        setItems(items, "Inventory");
    }

    public Map<Integer, CraftItemStack> getEnderItems() {
        return getItems(compound, "EnderItems");
    }

    public void setEnderItems(Map<Integer, ItemStack> items) {
        setItems(items, "EnderItems");
    }

    public String getDimension() {
        return compound.getString("Dimension");
    }

    public Location getLocation() {
        NBTCompoundList tagList = compound.getCompoundList("Pos");

        float yaw = getYaw();
        float pitch = getPitch();
        String worldName = getWorld().getName();
        World world = Bukkit.getWorld(worldName);
        double x = tagList.h(0);
        double y = tagList.h(1);
        double z = tagList.h(2);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public float getYaw() {
        return (float) compound.getCompoundList("Rotation").get(0).getCompound();
    }

    public float getPitch() {
        return (float) compound.getCompoundList("Rotation").get(1).getCompound();
    }

    public World getWorld() {
        String world = getDimension().split("minecraft:")[1];
        return world.equals("overworld") ? Bukkit.getWorlds().get(0) : Bukkit.getWorld(world);
    }

    private Map<Integer, CraftItemStack> getItems(NBTTagCompound tagC, String tagList) {
        Map<Integer, CraftItemStack> items = new HashMap<>();
        tagC.c(tagList, 10).forEach(nbt -> {
            NBTTagCompound rawitem = (NBTTagCompound) nbt;
            //Loads nbttag to itemstack
            ItemStack nmsitem = ItemStack.a(rawitem);
            //todo check if asBukkitcopy is better see https://www.spigotmc.org/threads/nbttag-doesnt-save-when-converting-nms-itemstack-to-bukkit-itemstack.516451/#post-4218060
            CraftItemStack craftStack = CraftItemStack.asCraftMirror(nmsitem);
            items.put(Byte.toUnsignedInt(rawitem.f("Slot")), craftStack);
        });
        return items;
    }

    //todo setdimension/world
    public void setLocation(Location loc) {
        compound.setFloat("Pos", newDoubleList(loc.getX(), loc.getY(), loc.getZ()));
        compound.setFloat("Rotation", newFloatList(loc.getYaw(), loc.getPitch()));
        compound.setFloat("Motion", newDoubleList(0, 0, 0));
    }

    public void setplayerGameType(int i) {
        compound.setInteger("playerGameType", i);
    }

    public void setPreviousPlayerGameType(int i) {
        compound.setInteger("previousPlayerGameType", i);
    }

    public void setfoodSaturationLevel(float f) {
        compound.setFloat("foodSaturationLevel", f);
    }

    public void setfoodLevel(int i) {
        compound.setInteger("foodLevel", i);
    }

    private NBTTagList newDoubleList(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        for (double v : adouble) {
            nbttaglist.add(NBTTagDouble.setFloat(v));
        }
        return nbttaglist;
    }

    private NBTTagList newFloatList(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        for (float f : afloat) {
            nbttaglist.add(NBTTagFloat.setFloat(f));
        }
        return nbttaglist;
    }
}
