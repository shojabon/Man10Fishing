package com.shojabon.man10fishing.dataClass.enums

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material

enum class TreasureRank {

    GOLD,
    SILVER,
    WOOD,
    ERROR;


    companion object{

        private val rankIcon=HashMap<TreasureRank,SItemStack>()

        init {
            loadIcon()
        }
        fun stringToTreasureRank(rank:String):TreasureRank{
            return try {
                TreasureRank.valueOf(rank.toUpperCase())
            }catch (e:Exception){
                Man10Fishing.instance.logger.warning("トレジャーランク${rank}は存在しません。")
                ERROR
            }
        }

        private fun loadIcon(){
            rankIcon.clear()
            val conSection=Man10Fishing.instance.config.getConfigurationSection("treasureIcons")
            for(rank in TreasureRank.values()){
                rankIcon[rank]=conSection?.let { SItemStack(Material.valueOf(it.getString("${rank}.material")?:"STONE"))
                        .setDisplayName(it.getString("${rank}.name","未設定"))
                        .setCustomModelData(it.getInt("${rank}.customModelData",0)) }?: SItemStack(Material.BARRIER).setDisplayName("§cエラー")
            }
        }

        fun getRankIcon(rank: TreasureRank):SItemStack{
            //cloneの代わり
            return SItemStack(rankIcon[rank]!!.build().clone())
        }
    }

}