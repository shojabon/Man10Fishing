package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.factors.FoodFactor
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class Fish (val name: String, val config: ConfigurationSection){


    var alias: String = ""
    var rarity: String = ""
    var weight: Pair<Int, Int> = Pair(0, 0) //min max
    var size: Pair<Int, Int> = Pair(0, 0) //min max
//    var food: IntArray = intArrayOf(0, 0, 0, 0, 0)
//    var foodRange: Int = 0
    var item: ItemStack = ItemStack(Material.ACACIA_PLANKS)

    var loaded: Boolean = false

    //ファクター
    val fishFactors = ArrayList<FishFactor>()

    companion object{
        val settingTypeMap = HashMap<String, Type>()
    }

    lateinit var foodFactor: FoodFactor

    init {
        val result = loadConfig()
        if(result != null){
            warnError(result)
        }
        //load functions
        for (field in javaClass.fields) {
            try {
                if (FishFactor::class.java.isAssignableFrom(field.type)) {
                    field[this] = field.type.getConstructor(Fish::class.java).newInstance(this)

                    //set shop id in setting fields
                    val func: FishFactor = field[this] as FishFactor
                    fishFactors.add(func)

                    for (innerField in func.javaClass.fields) {
                        if (FishSettingVariable::class.java.isAssignableFrom(innerField.type)) {
                            val setting: FishSettingVariable<*> = innerField[func] as FishSettingVariable<*>
                            settingTypeMap[setting.settingId] = (innerField.genericType as ParameterizedType).actualTypeArguments[0]
                            setting.config = config
                        }
                    }
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }
    }

    private fun warnError(error: String){
        Bukkit.getLogger().info(name + "でエラーが発生しました " + error)
    }

    private fun loadConfig(): String? {
        alias = config.getString("alias")?: return "不正alias"
        rarity = config.getString("rarity")?: return "不正レアリティ"
        if(!Man10FishingAPI.rarity.containsKey(rarity)) return "存在しないレアリティ"
        weight = Pair(config.getInt("weight.min"), config.getInt("weight.max"))
        if(weight.first > weight.second || (weight.first < 0 || weight.second < 0)) return "不正重量"
        size = Pair(config.getInt("size.min"), config.getInt("size.max"))
        if(size.first > size.second || (size.first < 0 || size.second < 0)) return "不正サイズ"
//        food = config.getIntegerList("food.index").toIntArray()
//        if(food.size != 5)return "フードインデックス"
//        foodRange = config.getInt("food.range")

        // item
        val material = Material.getMaterial(config.getString("item.material")?: return "不正マテリアル")?: return "不正マテリアル"
        val customModelData = config.getInt("item.customModelData")
        val lore = config.getStringList("item.lore")

        val itemStack = SItemStack(material).setDisplayName(alias)
        itemStack.customModelData = customModelData
        itemStack.lore = lore

        item = itemStack.build()?: return "不正アイテム"


        loaded = true
        return null
    }

    fun isFishEnabled(fisher: Player, rod: FishingRod): Boolean{
        for(factor in fishFactors){
            if(!factor.fishEnabled(this, fisher, rod)){
                return false
            }
        }
        return true
    }

    fun getRarityMultiplier(fisher: Player, rod: FishingRod): Float{
        var current = 1f
        for(factor in fishFactors){
            current = factor.rarityMultiplier(this, current, fisher, rod)
        }
        return current
    }

}