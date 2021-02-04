package io.featurehouse.spm.items;

import io.featurehouse.spm.SPMMain;
import io.featurehouse.spm.SweetPotatoStatus;
import io.featurehouse.spm.SweetPotatoType;
import io.featurehouse.spm.util.NbtUtils;
import io.featurehouse.spm.util.inventory.PeelInserter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class EnchantedSweetPotatoItem extends EnchantedItem implements WithStatus {
    @Override
    public boolean isFood() {
        return true;
    }

    private final SweetPotatoType sweetPotatoType;

    public EnchantedSweetPotatoItem(Settings settings, SweetPotatoType type) {
        super(settings.food(Objects.requireNonNull(type.getComponent(SweetPotatoStatus.ENCHANTED)).asFoodComponent()));
        this.sweetPotatoType = type;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) user;
            playerEntity.incrementStat(SPMMain.SWEET_POTATO_EATEN);
            if (!((PlayerEntity) user).abilities.creativeMode)
                PeelInserter.run(playerEntity);
        }

        Optional<List<StatusEffectInstance>> statusEffectInstances = calcEffect(stack);
        statusEffectInstances.ifPresent(set -> set.forEach(user::applyStatusEffect));

        return stack;
    }
    protected static Optional<List<StatusEffectInstance>> calcEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof EnchantedSweetPotatoItem)) return Optional.empty();
        CompoundTag compoundTag = stack.getOrCreateTag();
        Tag statusEffectsTag = compoundTag.get("statusEffects");
        if (NbtUtils.notListTag(statusEffectsTag)) return Optional.empty();
        ListTag statusEffects = (ListTag) statusEffectsTag;

        List<StatusEffectInstance> effectInstances = new ObjectArrayList<>();
        for (Tag oneStatusEffect: statusEffects) {
            if (NbtUtils.notCompoundTag(oneStatusEffect)) continue;
            CompoundTag compoundTag1 = (CompoundTag) oneStatusEffect;
            StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag1);
            effectInstances.add(statusEffectInstance);
        }
        return Optional.of(effectInstances);
    }

    @Deprecated
    private static StatusEffectInstance calcEffect() {
        return new StatusEffectInstance(StatusEffects.LUCK, 200, 1);    // Luck II 10s
        // Remember, this is just a trial.
        // The REAL calculation should be added later.
        // teddyxlandlee, please decide the details with your group. 13 Jun 2020 night
    }

    @Override
    public SweetPotatoStatus getStatus() {
        return SweetPotatoStatus.ENCHANTED;
    }

    @Override
    public SweetPotatoType asType() {
        return this.sweetPotatoType;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        CompoundTag root;
        BaseText mainTip = new TranslatableText("tooltip.sweet_potato.enchanted_sweet_potato.effects");
        if ((root = stack.getOrCreateTag()).isEmpty()) {
            mainTip.append(new LiteralText("???").formatted(Formatting.ITALIC));
            return;
        }
        Tag tag = root.get("displayIndex");
        if (NbtUtils.notShortTag(tag)) {
            mainTip.append(new LiteralText("???").formatted(Formatting.ITALIC));
            return;
        }
        short index = ((ShortTag) tag).getShort();
        Optional<List<StatusEffectInstance>> statusEffectInstances = calcEffect(stack);
        if (!statusEffectInstances.isPresent()) {
            mainTip.append(new LiteralText("???").formatted(Formatting.ITALIC));
            return;
        }
        StatusEffectInstance toBeShown = statusEffectInstances.get().get(index);
        mainTip.append(new TranslatableText(toBeShown.getTranslationKey()).formatted(Formatting.ITALIC)
            .append(new LiteralText(" ...").formatted(Formatting.ITALIC)));
    }
}
