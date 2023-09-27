package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.enums.SizeRank
import com.shojabon.man10fishing.factors.*
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Suppress("UNUSED")
class Fish (val name: String, val config: ConfigurationSection){


    var alias: String = ""
    var rarity: String = ""
    var item: ItemStack = ItemStack(Material.ACACIA_PLANKS)


    var loaded: Boolean = false

    //ファクター
    val fishFactors = ArrayList<FishFactor>()

    companion object{
        val settingTypeMap = HashMap<String, Type>()
    }

    lateinit var foodFactor: FoodFactor
    lateinit var broadcastFactor: BroadcastFactor
    lateinit var areaFactor: AreaFactor
    lateinit var soundFactor: SoundFactor
    lateinit var timeFactor:TimeFactor
    lateinit var commandFactor: CommandFactor
    lateinit var seasonFactor: SeasonFactor
    lateinit var sizeFactor: SizeFactor
    lateinit var loggingFactor: LoggingFactor
    lateinit var itemIndexFactor: ItemIndexFactor


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

                    for (innerField in func.javaClass.declaredFields) {
                        if (FishSettingVariable::class.java.isAssignableFrom(innerField.type)) {
                            innerField.isAccessible = true
                            val setting: FishSettingVariable<*> = innerField[func] as FishSettingVariable<*>
                            settingTypeMap[setting.settingId] = (innerField.genericType as ParameterizedType).actualTypeArguments[0]
                            setting.config = config
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        loaded = true
    }

    private fun warnError(error: String){
        Bukkit.getLogger().info(name + "でエラーが発生しました " + error)
    }

    private fun loadConfig(): String? {
        alias = config.getString("alias")?: return "不正alias"
        rarity = config.getString("rarity")?: return "不正レアリティ"
        if(!Man10FishingAPI.rarity.containsKey(rarity)) return "存在しないレアリティ"
//        food = config.getIntegerList("food.index").toIntArray()
//        if(food.size != 5)return "フードインデックス"
//        foodRange = config.getInt("food.range")

        // item
        val rarityData=Man10FishingAPI.rarity[rarity]!!
        val material = Material.getMaterial(config.getString("item.material")?: return "不正マテリアル")?: return "不正マテリアル"
        val customModelData = config.getInt("item.customModelData")
        val lore = config.getStringList("item.lore")
        lore.add(rarityData.loreDisplayName)

        val itemStack = SItemStack(material).setDisplayName(rarityData.namePrefix+alias)
        itemStack.customModelData = customModelData
        itemStack.lore = lore

        item = itemStack.build()?: return "不正アイテム"
        val itemMeta=item.itemMeta
        itemMeta.persistentDataContainer.set(NamespacedKey(Man10Fishing.instance,"fish"), PersistentDataType.STRING,name)

        item.itemMeta=itemMeta

        return null
    }

    fun isFishEnabled(fisher: Player, rod: FishingRod,hookLocation: Location): Boolean{
        for(factor in fishFactors){
            if(!factor.fishEnabled(this, fisher, rod,hookLocation)){
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

    fun executeOnFish(parameter: FishParameter, fisher: Player, rod: FishingRod){
        for(factor in fishFactors){
            factor.onFish(this, parameter, fisher, rod)
        }

        val contest = Man10Fishing.nowContest
        contest?.caughtFish(fisher,parameter)
    }

    fun getDetailedItem(parameter:FishParameter):ItemStack{
        val sItem=SItemStack(item.clone())
        val text="§f大きさ: ${parameter.size}cm"+ when(parameter.sizeRank){
            SizeRank.SMALL-> " §e§lMINISIZE!"
            SizeRank.BIG->" §e§lBIGSIZE!"
            else->""
        }
        sItem.addLore(text)
        val fishItem=sItem.build()
        val itemMeta=fishItem.itemMeta
        itemMeta.persistentDataContainer.set(NamespacedKey(Man10Fishing.instance,"size"), PersistentDataType.DOUBLE,parameter.size)
        fishItem.itemMeta = itemMeta
        return fishItem
    }

}