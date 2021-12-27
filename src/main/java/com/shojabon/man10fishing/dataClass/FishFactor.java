package com.shojabon.man10fishing.dataClass;

import com.shojabon.man10fishing.annotations.FoodFactorDefinition;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class FishFactor {

    Fish fish;

    public FishFactor(Fish fish){
        this.fish = fish;
    }

    public boolean fishEnabled(Fish fish, Player fisher, FishingRod rod){
        return true;
    }

    public float rarityMultiplier(Fish fish, float currentMultiplier, Player fisher, FishingRod rod){
        return 1f;
    }

    public FoodFactorDefinition getDefinition(){
        if(!this.getClass().isAnnotationPresent(FoodFactorDefinition.class)) return null;
        return this.getClass().getAnnotation(FoodFactorDefinition.class);
    }
}
