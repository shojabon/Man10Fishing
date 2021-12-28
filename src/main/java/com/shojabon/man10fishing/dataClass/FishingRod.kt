package com.shojabon.man10fishing.dataClass;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class FishingRod {

    public ItemStack rod;
    public int remainingFoodCount = 0;
    public ArrayList<Double> currentFood = new ArrayList<>(Arrays.asList(0d, 0d, 0d, 0d, 0d));


    public FishingRod(ItemStack rod){
        this.rod = rod;
    }
}
