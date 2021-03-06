package com.blakebr0.cucumber.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.function.Function;

public class BaseTileEntityBlock extends BaseBlock {
    public BaseTileEntityBlock(Material material, Function<Properties, Properties> properties) {
        super(material, properties);
    }

    public BaseTileEntityBlock(Material material, SoundType sound, float hardness, float resistance) {
        super(material, sound, hardness, resistance);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
