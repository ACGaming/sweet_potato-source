package io.github.teddyxlandlee.sweet_potato.util.registries;

import io.github.teddyxlandlee.annotation.FabricApiRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static io.github.teddyxlandlee.sweet_potato.SPMMain.MODID;

public interface RegistryHelper {
    static Identifier id(String id) {
        return new Identifier(MODID, id);
    }

    static Item item(String id, Item item2) {
        Identifier id2 = id(id);
        return Registry.register(Registry.ITEM, id2, item2);
    }

    static Item defaultItem(String id, @Nonnull Item.Settings settings) {
        return item(id, new Item(settings));
    }

    static Block block(String id, Block block2) {
        Identifier id2 = id(id);
        return Registry.register(Registry.BLOCK, id2, block2);
    }

    static BlockItem blockItem(String id, Block block2, @Nonnull Item.Settings settings) {
        Identifier id2 = id(id);
        return Registry.register(Registry.ITEM, id2, new BlockItem(block2, settings));
    }

    static <E extends BlockEntity> BlockEntityType<E> blockEntity(String id, FabricBlockEntityTypeBuilder.Factory<E> supplier, Block... blocks) {
        Identifier id2 = id(id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id2, FabricBlockEntityTypeBuilder.create(supplier, blocks).build(null));
    }

    //@Environment(EnvType.CLIENT)
    static SoundEvent sound(String id) {
        Identifier id2 = id(id);
        return Registry.register(Registry.SOUND_EVENT, id2, new SoundEvent(id2));
    }

    static <T extends Recipe<Inventory>> RecipeType<T> recipeType(String id) {
        Identifier id2 = id(id);
        return Registry.register(Registry.RECIPE_TYPE, id2, new RecipeType<T>() {
            @Override
            public String toString() {
                return id2.toString();
            }
        });
    }

    static <S extends RecipeSerializer<?>> S recipeSerializer(String id, Supplier<S> serializerSupplier) {
        Identifier id2 = id(id);
        return Registry.register(Registry.RECIPE_SERIALIZER, id2, serializerSupplier.get());
    }

    @FabricApiRegistry
    static <H extends ScreenHandler> ScreenHandlerType<H> simpleScreenHandler(String id, ScreenHandlerRegistry.SimpleClientHandlerFactory<H> factory) {
        Identifier id2 = id(id);
        return ScreenHandlerRegistry.registerSimple(id2, factory);
    }

    @FabricApiRegistry
    static Tag<Item> itemTag(String id) {
        Identifier id2 = id(id);
        return TagRegistry.item(id2);
    }
}
