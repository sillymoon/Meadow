package net.satisfyu.meadow.item;

import de.cristelknight.doapi.item.CustomHatItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.Identifier;
import net.satisfyu.meadow.util.MeadowIdentifier;


public class FurHead extends CustomHatItem {
    public FurHead(ArmorMaterial material, Settings settings) {
        super(material, EquipmentSlot.HEAD, settings);
    }

    @Override
    public Identifier getTexture() {
        return new MeadowIdentifier("textures/models/armor/fur.png");
    }

    @Override
    public Float getOffset() {
        return -1.9f;
    }
}