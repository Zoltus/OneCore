package sh.zoltus.onecore.utils;

import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class NBTPlayer {

    private final File datFile;
    private NBTTagCompound compound;

    public NBTPlayer(OfflinePlayer offP) {
        this(offP.getUniqueId());
    }

    //todo cleanup
    private NBTPlayer(UUID uuid) {
        Validate.notNull(uuid, "Cannot get null UUID");
        World w = Bukkit.getWorlds().get(0);
        datFile = new File(w.getWorldFolder().getAbsolutePath() + "/playerdata/" + uuid.toString().toLowerCase() + ".dat");

        if (!datFile.exists()) {
            System.out.println("File does not exist: " + datFile.getAbsolutePath());
        } else if (datFile.isDirectory()) {
            System.out.println("File is a directory not player.dat file: " + datFile.getAbsolutePath());
        } else {
            try {
                compound = NBTCompressedStreamTools.a(new FileInputStream(datFile));
            } catch (IOException ex) {
                System.out.println("Error trying to create NBTTagCompund for player : " + uuid + " :" + ex.getMessage());
            }
        }
    }

    @SneakyThrows
    public void save() {
        NBTCompressedStreamTools.a(compound, datFile);
    }

    //TODO add all attributes for player, luck armortoughtness ect
    @SuppressWarnings("SameParameterValue")
    private Double getAttribute(String abilityName) {
        NBTTagList list = compound.c("Attributes", 10);
        for (NBTBase nbt : list) {
            NBTTagCompound attribute = (NBTTagCompound) nbt;
            if (attribute.l("Name").equals(abilityName)) {
                return attribute.k("Base");
            }
        }
        return null;
    }

    public Double getMaxHealth() {
        return getAttribute("minecraft:generic.max_health");
    }

    public void setWalkSpeed(float f) {
        compound.p("abilities").a("walkSpeed", f);
    }

    public void setFlySpeed(float f) {
        compound.p("abilities").a("flySpeed", f);
    }

    public boolean getFlying() {
        return compound.p("abilities").b("flying");
    }

    public void setFlying(boolean bool) {
        compound.p("abilities").a("flying", bool);
    }

    public boolean getMayfly() {
        return compound.p("abilities").b("mayfly");
    }

    public void setMayfly(boolean bool) {
        compound.p("abilities").a("mayfly", bool);
    }

    public int getplayerGameType() {
        return compound.h("playerGameType");
    }

    public boolean getInvulnerable() {
        return compound.b("Invulnerable");
    }

    public void setInvulnerable(boolean bool) {
        compound.a("Invulnerable", bool);
    }

    public float getHealth() {
        return compound.j("Health");
    }

    public void setHealth(double d) {
        compound.a("Health", d);
    }

    public void setFoodExhaustionLevel(float f) {
        compound.a("foodExhaustionLevel", f);
    }

    public int getXpSeed() {
        return compound.h("XpSeed");
    }

    public void setXpSeed(int i) {
        compound.a("XpSeed", i);
    }

    public Map<Integer, CraftItemStack> getInventoryItems() {
        return getItems(compound, "Inventory");
    }

    public void setItems(Map<Integer, ItemStack> items, String tag) {
        NBTTagList list = new NBTTagList();
        items.forEach((slot, stack) -> {
            NBTTagCompound stackTag = stack.b(stack.t());
            // MinecraftKey minecraftkey = IRegistry.m.a();
            stackTag.a("Slot", (byte) (int) slot);
            list.add(stackTag);
        });
        compound.a(tag, list);
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
        return compound.l("Dimension");
    }

    public void setDimension(String s) {
        compound.a("Dimension", s);
    }

    public Location getLocation() {
        NBTTagList tagList = compound.c("Pos", 6);
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
        return compound.c("Rotation", 5).i(0);
    }

    public float getPitch() {
        return compound.c("Rotation", 5).i(1);
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
        compound.a("Pos", newDoubleList(loc.getX(), loc.getY(), loc.getZ()));
        compound.a("Rotation", newFloatList(loc.getYaw(), loc.getPitch()));
        compound.a("Motion", newDoubleList(0, 0, 0));
    }

    public void setplayerGameType(int i) {
        compound.a("playerGameType", i);
    }

    public void setPreviousPlayerGameType(int i) {
        compound.a("previousPlayerGameType", i);
    }

    public void setfoodSaturationLevel(float f) {
        compound.a("foodSaturationLevel", f);
    }

    public void setfoodLevel(int i) {
        compound.a("foodLevel", i);
    }

    private NBTTagList newDoubleList(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        for (double v : adouble) {
            nbttaglist.add(NBTTagDouble.a(v));
        }
        return nbttaglist;
    }

    private NBTTagList newFloatList(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        for (float f : afloat) {
            nbttaglist.add(NBTTagFloat.a(f));
        }
        return nbttaglist;
    }
}
