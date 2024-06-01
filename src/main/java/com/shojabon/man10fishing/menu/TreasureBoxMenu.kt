package com.shojabon.man10fishing.menu

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.enums.TreasureRank
import com.shojabon.man10fishing.dataClass.treasure.Treasure
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class TreasureBoxMenu(private val player:Player, private val treasure:Treasure): SInventory("§e宝箱", 5, Man10Fishing.instance) {


    private val numOfRedPanel=5
    private var numOfClickedRedPanel=0
    private var isOpened=false

    companion object{

        val icons=HashMap<TreasureRank,SItemStack>()
        val unlockedIcons=HashMap<TreasureRank,SItemStack>()

        init {


            //TreasureMenuの宝箱アイコンの読み込み
            val errorIcon=SItemStack(Material.BARRIER).setDisplayName("§cアイコン未指定").addLore("§a周りの赤パネルを全てクリック!")
            val conSection=Man10Fishing.instance.config.getConfigurationSection("treasureIcons")
            for(rank in TreasureRank.values()){
                icons[rank]=conSection?.let { getIconData(it,rank) }?:errorIcon

                unlockedIcons[rank]=conSection?.let { getUnlockedIconData(it,rank) }?:errorIcon
            }



        }

        private fun getIconData(conSection:ConfigurationSection,rank:TreasureRank):SItemStack{
            return try {
                val item=SItemStack(Material.valueOf(conSection.getString("${rank}.material")?:"STONE"))
                        .setDisplayName(conSection.getString("${rank}.name","未設定"))
                        .setCustomModelData(conSection.getInt("${rank}.customModelData",0))
                if(conSection.contains("${rank}.lore")){
                    item.lore = conSection.getStringList("${rank}.lore")
                }
                item
            }catch (e:Exception){
                SItemStack(Material.BARRIER).setDisplayName("§cMaterialエラー")
            }
                    .addLore("§a周りの赤パネルを全てクリック!")
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES)
        }

        private fun getUnlockedIconData(conSection:ConfigurationSection,rank:TreasureRank):SItemStack{
            return try {
                val item=SItemStack(Material.valueOf(conSection.getString("${rank}.material")?:"STONE"))
                        .setDisplayName(conSection.getString("${rank}.name","未設定"))
                        .setCustomModelData(conSection.getInt("${rank}.customModelData",0))
                if(conSection.contains("${rank}.lore")){
                    item.lore = conSection.getStringList("${rank}.lore")
                }
                item
            }catch (e:Exception) {
                SItemStack(Material.BARRIER).setDisplayName("§cMaterialエラー")
            }
                    .addLore("§aクリックで開封!")
                    .addEnchantment(Enchantment.LOOT_BONUS_BLOCKS,1)
                    .addFlag(ItemFlag.HIDE_ENCHANTS)
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES)
        }

    }


    init {
        setBaseMenu()
        setRedPanel()

        setOnCloseEvent {
            open(player)
        }
    }

    private fun setBaseMenu() {

        fillItem(SInventoryItem(SItemStack(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("").build())
                .clickable(false))

        setItem(22,SInventoryItem(icons[treasure.rank]!!.build()).clickable(false))

    }

    private fun setUnlockedTreasureBoxIcon(){

        player.playSound(player.location,Sound.BLOCK_ENCHANTMENT_TABLE_USE,1F,1F)

        setItem(22,
        SInventoryItem(unlockedIcons[treasure.rank]!!.build())
                .setEvent {
                    if(isOpened)return@setEvent
                    isOpened=true
                    player.playSound(player.location, Sound.BLOCK_CHEST_OPEN,1F,1F)
                    Thread{
                        Thread.sleep(1000)
                        Man10Fishing.instance.server.scheduler.runTask(Man10Fishing.instance,Runnable{
                            player.playSound(player.location,Sound.ENTITY_PLAYER_LEVELUP,1F,1F)

                            setResultIcon()
                        })
                    }.start()
                }
                .clickable(false)
        )

        renderInventory()

    }

    private fun setResultIcon(){
        val result=SInventoryItem(treasure.getItem())
                .setEvent {
                    player.playSound(player.location,Sound.ENTITY_PLAYER_LEVELUP,1F,2F)
                    player.inventory.addItem(treasure.getItem())
                    closeNoEvent(player,Man10Fishing.instance)
                }
                .clickable(false)
        setItem(22,result)

        renderInventory()
    }

    private fun setRedPanel(){

        val limePanel=SInventoryItem(SItemStack(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§aクリック済")
                .build())
                .clickable(false)
        val redPanel=SInventoryItem(SItemStack(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("§eクリック!").build())
                .setEvent {
                    setItem(it.slot,limePanel)
                    numOfClickedRedPanel++
                    if(numOfClickedRedPanel==numOfRedPanel){
                        setUnlockedTreasureBoxIcon()
                    }
                    player.playSound(player.location,Sound.BLOCK_ENCHANTMENT_TABLE_USE,1F,2F)
                    renderInventory()
                }
                .clickable(false)

        val basePositions=((0..21)+(23..44)).toMutableList()
        for(i in 0 until numOfRedPanel){
            val pos=basePositions.random()
            setItem(pos,redPanel)
            basePositions.remove(pos)
        }

        renderInventory()

    }







}