package com.shojabon.man10fishing.dataClass;

import org.bukkit.inventory.ItemStack;

public class FishingRod {

    public ItemStack rod;
    public int remainingFoodCount = 0;
    public int[] currentFood = new int[5];

    public FishingRod(ItemStack rod){
        this.rod = rod;
    }
}
