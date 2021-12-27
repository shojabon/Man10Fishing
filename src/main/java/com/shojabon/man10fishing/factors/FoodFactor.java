package com.shojabon.man10fishing.factors;

import com.shojabon.man10fishing.Man10Fishing;
import com.shojabon.man10fishing.annotations.FoodFactorDefinition;
import com.shojabon.man10fishing.dataClass.Fish;
import com.shojabon.man10fishing.dataClass.FishFactor;
import com.shojabon.man10fishing.dataClass.FishingRod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FoodFactorDefinition(
        name = "フード",
        settingPrefix = "food",
        iconMaterial = Material.MELON_SEEDS,
        explanation = {"食べ物ロジックの定義"},
        settable = false
)
public class FoodFactor extends FishFactor {

    public FoodFactor(Fish fish) {
        super(fish);
    }

    @Override
    public float rarityMultiplier(Fish fish, float currentMultiplier, Player fisher, FishingRod rod) {
        if(nDimensionDistanceSquared(fish.getFood(), rod.currentFood) <= Math.pow(fish.getFoodRange(), 2)){
            return currentMultiplier * Man10Fishing.Companion.getFoodInRangeMultiplier();
        }
        return 1f;
    }

    public double nDimensionDistanceSquared(int[] origin, int[] target){
        double result = 0;
        if(origin.length != target.length) return -1;
        for(int i = 0; i < origin.length; i++){
            result += Math.pow(target[i] - origin[i], 2);
        }
        return result;
    }
}
