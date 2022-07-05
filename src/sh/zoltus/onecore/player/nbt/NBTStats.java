package sh.zoltus.onecore.player.nbt;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class NBTStats {
    @SerializedName("stats")
    private Data data;
    @SerializedName("DataVersion")
    private Integer dataVersion;

    @lombok.Data
    public static class Data {
        @SerializedName("minecraft:picked_up")
        private MinecraftPickedUp pickedUp;
        @SerializedName("minecraft:custom")
        private MinecraftCustom custom;
        @SerializedName("minecraft:killed")
        private MinecraftKilled killed;
        @SerializedName("minecraft:used")
        private MinecraftUsed used;
        @SerializedName("minecraft:dropped")
        private MinecraftDropped dropped;

        @lombok.Data
        public static class MinecraftCustom {
            @SerializedName("minecraft:time_since_rest")
             private Integer timeSinceRest;
            @SerializedName("minecraft:crouch_one_cm")
             private Integer crouchOneCm;
            @SerializedName("minecraft:play_time")
             private Integer playTime;
            @SerializedName("minecraft:sprint_one_cm")
             private Integer sprintOneCm;
            @SerializedName("minecraft:damage_taken")
             private Integer damageTaken;
            @SerializedName("minecraft:deaths")
             private Integer deaths;
            @SerializedName("minecraft:walk_one_cm")
             private Integer walkOneCm;
            @SerializedName("minecraft:sneak_time")
             private Integer sneakTime;
            @SerializedName("minecraft:walk_under_water_one_cm")
             private Integer walkUnderWaterOneCm;
            @SerializedName("minecraft:mob_kills")
             private Integer mobKills;
            @SerializedName("minecraft:drop")
             private Integer drop;
            @SerializedName("minecraft:jump")
             private Integer jump;
            @SerializedName("minecraft:damage_dealt")
             private Integer damageDealt;
            @SerializedName("minecraft:leave_game")
             private Integer leaveGame;
            @SerializedName("minecraft:walk_on_water_one_cm")
             private Integer walkOnWaterOneCm;
            @SerializedName("minecraft:time_since_death")
             private Integer timeSinceDeath;
            @SerializedName("minecraft:enchant_item")
             private Integer enchantItem;
            @SerializedName("minecraft:climb_one_cm")
             private Integer climbOneCm;
            @SerializedName("minecraft:total_world_time")
             private Integer totalWorldTime;
            @SerializedName("minecraft:swim_one_cm")
             private Integer swimOneCm;
            @SerializedName("minecraft:fall_one_cm")
             private Integer fallOneCm;
            @SerializedName("minecraft:fly_one_cm")
             private Integer flyOneCm;
            @SerializedName("minecraft:horse_one_cm")
             private Integer horseOneCm;
        }

        @lombok.Data
        public static class MinecraftDropped {
            @SerializedName("minecraft:moss_block")
             private Integer mossBlock;
        }

        @lombok.Data
        public static class MinecraftKilled {
            @SerializedName("minecraft:axolotl")
             private Integer axolotl;
        }

        @lombok.Data
        public static class MinecraftPickedUp {
            @SerializedName("minecraft:cooked_mutton")
             private Integer cookedMutton;
            @SerializedName("minecraft:brown_wool")
             private Integer brownWool;
            @SerializedName("minecraft:moss_block")
             private Integer mossBlock;
            @SerializedName("minecraft:sunflower")
             private Integer sunflower;
        }

        @lombok.Data
        public static class MinecraftUsed {
            @SerializedName("minecraft:enchanting_table")
             private Integer enchantingTable;
            @SerializedName("minecraft:flint_and_steel")
             private Integer flintAndSteel;
            @SerializedName("minecraft:tnt")
             private Integer tnt;
            @SerializedName("minecraft:acacia_slab")
             private Integer acaciaSlab;
            @SerializedName("minecraft:horse_spawn_egg")
             private Integer horseSpawnEgg;
        }
    }
}