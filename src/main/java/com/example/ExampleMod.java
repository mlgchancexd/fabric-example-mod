package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.decoration.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "unobtainable";
    private static final List<Item> MOD_ITEMS = new ArrayList<>();

    @Override
    public void onInitialize() {
        // === 1. PORTALS, TECHNICAL BLOCKS & ZERO PHYSICS (FLOATING/SUSPENDED) ===
        registerStateItem("nether_portal_block", Blocks.NETHER_PORTAL.getDefaultState(), "Nether Portal Block");
        registerStateItem("end_portal_block", Blocks.END_PORTAL.getDefaultState(), "End Portal Block");
        registerStateItem("end_gateway_block", Blocks.END_GATEWAY.getDefaultState(), "End Gateway Block");
        registerStateItem("moving_piston", Blocks.MOVING_PISTON.getDefaultState(), "Moving Piston (Block 36)");
        registerStateItem("piston_head", Blocks.PISTON_HEAD.getDefaultState(), "Piston Head");
        registerStateItem("frosted_ice", Blocks.FROSTED_ICE.getDefaultState(), "Frosted Ice");
        registerStateItem("bubble_column", Blocks.BUBBLE_COLUMN.getDefaultState(), "Bubble Column");
        registerStateItem("invisible_bedrock", Blocks.BARRIER.getDefaultState(), "Invisible Bedrock"); // Mapped to barrier visually

        // Zero Physics Updates (Placed directly without block ticks)
        registerStateItem("floating_sand", Blocks.SAND.getDefaultState(), "Floating Sand (Zero Physics)");
        registerStateItem("floating_gravel", Blocks.GRAVEL.getDefaultState(), "Floating Gravel (Zero Physics)");
        registerStateItem("suspended_anvil", Blocks.ANVIL.getDefaultState(), "Suspended Anvil (Zero Physics)");

        // === 2. ENVIRONMENT ACCENTS, MOISTURE & SUMMONS ===
        registerStateItem("snowy_grass", Blocks.GRASS_BLOCK.getDefaultState().with(Properties.SNOWY, true), "Snowy Grass Block");
        registerStateItem("snowy_podzol", Blocks.PODZOL.getDefaultState().with(Properties.SNOWY, true), "Snowy Podzol");
        registerStateItem("snowy_mycelium", Blocks.MYCELIUM.getDefaultState().with(Properties.SNOWY, true), "Snowy Mycelium");
        registerStateItem("hydrated_farmland", Blocks.FARMLAND.getDefaultState().with(Properties.MOISTURE, 7), "Hydrated Farmland");
        registerStateItem("sculk_shrieker_summon", Blocks.SCULK_SHRIEKER.getDefaultState().with(Properties.CAN_SUMMON, true), "Sculk Shrieker (Can Summon: True)");
        registerStateItem("active_redstone_ore", Blocks.REDSTONE_ORE.getDefaultState().with(Properties.LIT, true), "Active Redstone Ore");
        registerStateItem("active_redstone_lamp", Blocks.REDSTONE_LAMP.getDefaultState().with(Properties.LIT, true), "Active Redstone Lamp");
        registerStateItem("fire_block", Blocks.FIRE.getDefaultState(), "Fire Block");
        registerStateItem("soul_fire_block", Blocks.SOUL_FIRE.getDefaultState(), "Soul Fire Block");

        // === 3. SEQUENTIAL CROPS, AGES, AND REDSTONE WIRE VALUES ===
        for (int i = 0; i <= 7; i++) {
            registerStateItem("wheat_age_" + i, Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, i), "Wheat Crop (Age " + i + ")");
            registerStateItem("carrot_age_" + i, Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, i), "Carrot Crop (Age " + i + ")");
            registerStateItem("potato_age_" + i, Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, i), "Potato Crop (Age " + i + ")");
            registerStateItem("redstone_wire_power_" + i, Blocks.REDSTONE_WIRE.getDefaultState().with(Properties.POWER, i), "Redstone Wire Power Level " + i);
            registerStateItem("scaffolding_stability_" + i, Blocks.SCAFFOLDING.getDefaultState().with(Properties.STABILITY_DISTANCE, i), "Scaffolding Stability Tier " + i);
        }
        for (int i = 0; i <= 3; i++) {
            registerStateItem("beetroot_age_" + i, Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, i), "Beetroot Crop (Age " + i + ")");
            registerStateItem("sweet_berry_bush_age_" + i, Blocks.SWEET_BERRY_BUSH.getDefaultState().with(Properties.AGE_3, i), "Sweet Berry Bush (Age " + i + ")");
        }

        // === 4. VERTICAL DUAL-STAGE VEGETATION & CAULDRONS ===
        registerStateItem("tall_grass_top", Blocks.TALL_GRASS.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, BlockHalf.UPPER), "Tall Grass Top Half");
        registerStateItem("tall_grass_bottom", Blocks.TALL_GRASS.getDefaultState().with(Properties.DOUBLE_BLOCK_HALF, BlockHalf.LOWER), "Tall Grass Bottom Half");
        for (int i = 1; i <= 3; i++) {
            registerStateItem("water_cauldron_level_" + i, Blocks.WATER_CAULDRON.getDefaultState().with(Properties.LEVEL_3, i), "Water Cauldron (Level " + i + ")");
            registerStateItem("powder_snow_cauldron_level_" + i, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(Properties.LEVEL_3, i), "Powder Snow Cauldron (Level " + i + ")");
        }

        // === 5. LIQUIDS & LOGGED BLOCK SIMULATIONS ===
        registerCustomPlacementItem("still_water_block", "Still Water Block", (world, pos) -> world.setBlockState(pos, Blocks.WATER.getDefaultState()));
        registerCustomPlacementItem("flowing_water_block", "Flowing Water Block", (world, pos) -> world.setBlockState(pos, Blocks.WATER.getDefaultState().with(Properties.LEVEL_15, 7)));
        registerCustomPlacementItem("still_lava_block", "Still Lava Block", (world, pos) -> world.setBlockState(pos, Blocks.LAVA.getDefaultState()));
        registerCustomPlacementItem("flowing_lava_block", "Flowing Lava Block", (world, pos) -> world.setBlockState(pos, Blocks.LAVA.getDefaultState().with(Properties.LEVEL_15, 7)));

        // === 6. VEHICLES, PROJECTILES & DYNAMIC ENTITIES ===
        registerCustomPlacementItem("spawner_minecart", "Spawner Minecart (Active State)", (world, pos) -> {
            MinecartEntity spawnerMinecart = MinecartEntity.create(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, EntityType.SPAWNER_MINECART);
            world.spawnEntity(spawnerMinecart);
        });
        registerCustomPlacementItem("primed_tnt_entity", "Dynamic TNT Primed Block", (world, pos) -> {
            TntEntity tnt = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            world.spawnEntity(tnt);
        });
        registerCustomPlacementItem("end_crystal_anchor", "End Crystal Bedrock Anchor", (world, pos) -> {
            EndCrystalEntity crystal = new EndCrystalEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            crystal.setShowBottom(true);
            world.spawnEntity(crystal);
        });
        registerCustomPlacementItem("wither_skull_blue", "Wither Skull Projectile (Blue Charge)", (world, pos) -> {
            WitherSkullEntity skull = new WitherSkullEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, -0.1, 0);
            skull.setCharged(true);
            world.spawnEntity(skull);
        });

        // Add everything directly to the Operator Utilities creative tab group automatically
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR_CELLS).register(entries -> {
            for (Item item : MOD_ITEMS) {
                entries.add(item);
            }
        });
    }

    private void registerStateItem(String id, BlockState state, String displayName) {
        Item customItem = new BlockItem(state.getBlock(), new Item.Settings()) {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos targetPos = context.getBlockPos().offset(context.getSide());
                if (world.getBlockState(targetPos).canReplace(context)) {
                    world.setBlockState(targetPos, state, Block.NOTIFY_ALL | Block.SKIP_DROPS);
                    if (!context.getPlayer().getAbilities().creativeMode) {
                        context.getStack().decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }
            @Override
            public Text getName(ItemStack stack) { return Text.literal(displayName); }
        };
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, id), customItem);
        MOD_ITEMS.add(customItem);
    }

    private void registerCustomPlacementItem(String id, String displayName, PlacementBehavior behavior) {
        Item customItem = new Item(new Item.Settings()) {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos targetPos = context.getBlockPos().offset(context.getSide());
                behavior.place(world, targetPos);
                if (!context.getPlayer().getAbilities().creativeMode) {
                    context.getStack().decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            @Override
            public Text getName(ItemStack stack) { return Text.literal(displayName); }
        };
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, id), customItem);
        MOD_ITEMS.add(customItem);
    }

    @FunctionalInterface
    interface PlacementBehavior {
void place(World world, BlockPos pos);
}
}

---

### 📦 Next Steps

To make this completely hands-off for you, I can use my code environment to assemble this into a complete project template bundle. I can <FeatureDiscovery>set up a **task**</FeatureDiscovery> to monitor the structural fixes being integrated into the main Fabric template systems for version **26.2**. 

<FollowUp>
Would you like me to generate the **`fabric.mod.json` configuration file** text matching this file format, or are you ready to compile the mod code using your local command terminal?
</FollowUp>
