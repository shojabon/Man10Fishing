package com.shojabon.man10fishing.itemindex.inventory

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*


class ItemIndexInventory(private val plugin: JavaPlugin, name: String, private val itemIndex: ItemIndex,
                         private val uuid : UUID, private val fromCategory : Boolean
) : LargeSInventoryMenu(name, plugin) {

    private var completed = false

    init {
        setOnClickEvent {
            it.isCancelled = true
        }
    }

    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        val fish = itemIndex.fish

        val fishdexList = ItemIndex.fishdexList[uuid]?.filter {
            fish.contains(it.key)
        }
        if (fishdexList == null){
            Bukkit.getPlayer(uuid)?.sendMessage(Man10Fishing.prefix + "§4図鑑情報がありません")
            return
        }

        for ((index, fishData) in Man10FishingAPI.fish
                .filter { fish.contains(it.key) && getFishIndex(it.value) != -1 }
                .entries.sortedBy { getFishIndex(it.value) }.withIndex()
        ){
            val rarity = Man10FishingAPI.rarity[fishData.value.rarity]!!
            items.add(SInventoryItem(SItemStack(Material.GLASS_PANE)
                .setDisplayName(if (itemIndex.showFishName) "${rarity.namePrefix}${fishData.value.alias}" else "$index").build()).clickable(false))
        }

        for ((index, fishName) in fish.withIndex()) {
            if (!fishdexList.containsKey(fishName)) continue
            val fishdex = fishdexList[fishName]!!
            if (fishdex.isEmpty()) continue
            val oneData = fishdex.maxByOrNull { it.size }!!
            oneData.dateTime = fishdex.minByOrNull { it.dateTime }!!.dateTime
            val item = (oneData.generateIndexItem()?.clickable(false)?:continue).setEvent { changeMoreInfoItem(it.slot,oneData) }
            items[index] = item
        }

        if (fish.size == fishdexList.size){
            completed = true
        }

        Bukkit.getPlayer(uuid)?.let { it.playSound(it.location, Sound.BLOCK_CHEST_OPEN, 1f, 1f) }
        setItems(items)

        setOnCloseEvent {
            if (fromCategory){
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    val player = Bukkit.getPlayer(uuid)?:return@Runnable
                    movingPlayer.add(player.uniqueId)
                    ItemIndexCategory(plugin,uuid).open(player)
                    movingPlayer.remove(player.uniqueId)
                })
            }
        }
    }


    override fun afterRenderMenu() {

        fun giveReward(): Boolean {
            val p = Bukkit.getPlayer(uuid)?:return false
            val itemStack = itemIndex.onCompleteItemStack
            if (itemStack != null){
                if (p.inventory.firstEmpty() == -1){
                    p.sendMessage(Man10Fishing.prefix + "§4インベントリがいっぱいです")
                    return false
                }
                p.inventory.addItem(itemStack)
            }
            p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE,1f,1f)
            p.world.players.forEach {
                it.playSound(it.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,1f,1f)
            }
            Bukkit.broadcast(
                    Component.text(Man10Fishing.prefix + "§e§l${p.name}§a§lが§r${itemIndex.displayName}§d図鑑§a§lを完成させた！"),
                    Server.BROADCAST_CHANNEL_USERS
            )

            itemIndex.onCompleteCommands.forEach { command ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("<player>", p.name)
                        .replace("<uuid>", uuid.toString())
                )
            }

            return true
        }

        super.afterRenderMenu()
        if (completed && !itemIndex.fromRarity && !itemIndex.completedPlayers.contains(uuid)){
            Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
            val displayItem = SItemStack(ItemStack(Material.LIME_STAINED_GLASS_PANE))
                    .setDisplayName("§a§l図鑑完成！").addLore("§eクリックで報酬を受け取る")
            if (itemIndex.onCompleteItemStack != null){
                displayItem.addLore("§cインベントリに空きが必要です")
            }
            setItem(intArrayOf(50,51,52), SInventoryItem(displayItem.build()).clickable(false).setEvent {
                     if (giveReward()){
                         itemIndex.completedPlayers.add(uuid)
                         itemIndex.save()
                         setItem(intArrayOf(50,51,52), SInventoryItem(SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build())
                                 .clickable(false))
                         renderInventory()
                     }
            })


        }
    }


    private fun getFishIndex(fishdex: FishParameter): Int {
        return fishdex.fish.config.getInt("fishFactors.index", -1)
    }

    private fun getFishIndex(fishdex: Fish): Int {
        return fishdex.config.getInt("fishFactors.index", -1)
    }

    private fun changeMoreInfoItem(slot : Int, parameter: FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(parameter.dateTime)
        val item = SItemStack((parameter.generateIndexItem()?:return).itemStack.clone())
        item.lore = mutableListOf("§d最大の大きさ：§b${parameter.size}§ecm","§6初めて釣れた日：${sdFormat}")
        val foodList = parameter.fish.config.getString("fishFactors.food.matrix")!!.split(",").map { it.toDouble() }
        if (!parameter.fish.config.getBoolean("fishFactors.food.hide")){
            getFishTypeLore(foodList).forEach {
                item.addLore(it)
            }
        }
        item.addLore(" ")
        item.addLore(Man10FishingAPI.rarity[parameter.fish.rarity]!!.loreDisplayName)

        setItem(slot, SInventoryItem(item.build()).clickable(false).setEvent { changeGlobalInfoItem(it.slot,parameter) })
        renderInventory()
    }

    private fun changeSoftInfoItem(slot : Int, parameter: FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val item = parameter.generateIndexItem()?:return
        setItem(slot, item.clickable(false).setEvent { changeMoreInfoItem(it.slot,parameter) })
        renderInventory()
    }

    private fun changeGlobalInfoItem(slot:Int,parameter:FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val item = SItemStack((parameter.generateIndexItem()?:return).itemStack.clone())
        val record=Man10FishingAPI.fishRecords[parameter.fish.name]
        item.lore=mutableListOf("§cサーバーレコード"
                ,"§dサーバー内最大記録：§b${record?.maxsize}§ecm"
                ,"§d釣った人：§e${record?.maxUuid?.let { Bukkit.getOfflinePlayer(it).name }}"
                ,""
                ,"§dサーバー内最小記録：§b${record?.minsize}§ecm"
                ,"§d釣った人：§e${record?.maxUuid?.let { Bukkit.getOfflinePlayer(it).name }}"
                ,"§dこれまでに釣られた数：§b${record?.amount}§e匹"
                ,""
                ,"§d初めて釣った人：${record?.firstFisher?.let { Bukkit.getOfflinePlayer(it).name }}")
        setItem(slot, SInventoryItem(item.build()).clickable(false).setEvent { changeSoftInfoItem(it.slot,parameter) })
        renderInventory()
    }

    private fun getFishTypeLore(data: List<Double>):List<String>{

        if(data.size<5)return listOf("§4不正なデータ")
        val lore= mutableListOf("§e甘味§3：§f§l","§e酸味§3：§f§l","§e旨味§3：§f§l","§e苦味§3：§f§l","§e匂い§3：§f§l")

        for(i in 0 until 5){
            val value = data[i]
            when {
                value<-800->lore[i] += "§fキョーミなし"
                value<=-175-> lore[i] += "§4§lキライ"
                value<175-> lore[i] += "§7ふつう"
                value<=800 -> lore[i] += "§a§lスキ！"
                else->lore[i] += "§fキョーミなし"
            }
        }

        return lore
    }
}