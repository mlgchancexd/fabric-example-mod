package com.example; // This matches your exact folder path!

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
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
        // === 1. PORTALS & TECHNICAL BLOCKS ===
        registerStateItem("nether_portal_block", Blocks.NETHER_PORTAL.getDefaultState(), "Nether Portal Block");
        registerStateItem("end_portal_block", Blocks.END_PORTAL.getDefaultState(), "End Portal Block");
        registerStateItem("end_gateway_block", Blocks.END_GATEWAY.getDefaultState(), "End Gateway Block");
        registerStateItem("moving_piston", Blocks.MOVING_PISTON.getDefaultState(), "Moving Piston (Block 36)");
        registerStateItem("piston_head", Blocks.PISTON_HEAD.getDefaultState(), "Piston Head");
        registerStateItem("frosted_ice", Blocks.FROSTED_ICE.getDefaultState(), "Frosted Ice");

        // === 2. ENVIRONMENT EXTRACTION STATES ===
        registerStateItem("snowy_grass", Blocks.GRASS_BLOCK.getDefaultState().with(Properties.SNOWY, true), "Snowy Grass Block");
        registerStateItem("snowy_podzol", Blocks.PODZOL.getDefaultState().with(Properties.SNOWY, true), "Snowy Podzol");
        registerStateItem("snowy_mycelium", Blocks.MYCELIUM.getDefaultState().with(Properties.SNOWY, true), "Snowy Mycelium");
        registerStateItem("hydrated_farmland", Blocks.FARMLAND.getDefaultState().with(Properties.MOISTURE, 7), "Hydrated Farmland");
        registerStateItem("sculk_shrieker_can_summon", Blocks.SCULK_SHRIEKER.getDefaultState().with(Properties.CAN_SUMMON, true), "Sculk Shrieker (Can Summon: True)");
        registerStateItem("active_redstone_ore", Blocks.REDSTONE_ORE.getDefaultState().with(Properties.LIT, true), "Active Redstone Ore");

        // === 3. CROPS WITH AGE AGENTS ===
        for (int i = 0; i <= 7; i++) {
            registerStateItem("wheat_age_" + i, Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, i), "Wheat Crop (Age " + i + ")");
            registerStateItem("carrot_age_" + i, Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, i), "Carrot Crop (Age " + i + ")");
            registerStateItem("potato_age_" + i, Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, i), "Potato Crop (Age " + i + ")");
        }

        // === 4. TECHNICAL LIQUIDS ===
        registerCustomPlacementItem("still_water_block", "Still Water Block", (world, pos) -> world.setBlockState(pos, Blocks.WATER.getDefaultState()));
        registerCustomPlacementItem("flowing_water_block", "Flowing Water Block", (world, pos) -> world.setBlockState(pos, Blocks.WATER.getDefaultState().with(Properties.LEVEL_15, 7)));
        registerCustomPlacementItem("still_lava_block", "Still Lava Block", (world, pos) -> world.setBlockState(pos, Blocks.LAVA.getDefaultState()));
        registerCustomPlacementItem("flowing_lava_block", "Flowing Lava Block", (world, pos) -> world.setBlockState(pos, Blocks.LAVA.getDefaultState().with(Properties.LEVEL_15, 7)));

        // === 5. CUSTOM ENTITY VEHICLES ===
        registerCustomPlacementItem("spawner_minecart_item", "Spawner Minecart", (world, pos) -> {
            MinecartEntity spawnerMinecart = MinecartEntity.create(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, EntityType.SPAWNER_MINECART);
            world.spawnEntity(spawnerMinecart);
        });

        // Add items directly to your structural Operator cells tab group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR_CELLS).register(entries -> {
            for (Item item : MOD_ITEMS) {
                entries.add(item);
            }
        });
    }

    private void registerStateItem(String id, net.minecraft.block.BlockState state, String displayName) {
        Item customItem = new BlockItem(state.getBlock(), new Item.Settings()) {
            @Override
            public ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos targetPos = context.getBlockPos().offset(context.getSide());
                if (world.getBlockState(targetPos).canReplace(context)) {
                    world.setBlockState(targetPos, state, Block.NOTIFY_ALL);
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
            public ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
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
