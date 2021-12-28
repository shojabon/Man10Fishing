package com.shojabon.man10fishing.factors;

import com.shojabon.man10fishing.Man10Fishing;
import com.shojabon.man10fishing.annotations.FoodFactorDefinition;
import com.shojabon.man10fishing.dataClass.Fish;
import com.shojabon.man10fishing.dataClass.FishFactor;
import com.shojabon.man10fishing.dataClass.FishSettingVariable;
import com.shojabon.man10fishing.dataClass.FishingRod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FoodFactorDefinition(
        name = "フード",
        iconMaterial = Material.MELON_SEEDS,
        explanation = {"食べ物ロジックの定義"},
        settable = false
)
public class FoodFactor extends FishFactor {

    public FishSettingVariable<List<Double>> matrix = new FishSettingVariable<>("food.matrix", Arrays.asList(0d,0d,0d,0d,0d));
    public FishSettingVariable<Double> range = new FishSettingVariable<>("food.range", 0d);


    public FoodFactor(Fish fish) {
        super(fish);
    }

    @Override
    public float rarityMultiplier(Fish fish, float currentMultiplier, Player fisher, FishingRod rod) {
        Bukkit.broadcastMessage(range.get() + "");
        if(nDimensionDistanceSquared(matrix.get(), rod.currentFood) <= Math.pow(range.get(), 2)){
            return currentMultiplier * Man10Fishing.Companion.getFoodInRangeMultiplier();
        }
        return 1f;
    }

    public double nDimensionDistanceSquared(List<Double> origin, List<Double> target){
        double result = 0;
        if(origin.size() != target.size()) return -1;
        for(int i = 0; i < origin.size(); i++){
            result += Math.pow(target.get(i) - origin.get(i), 2);
        }
        return result;
    }
}
