package com.shojabon.man10fishing

import ToolMenu.SingleItemStackSelectorMenu
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.TreasureRank
import com.shojabon.man10fishing.menu.TreasureBoxMenu
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import java.util.Date
import java.util.UUID
import java.util.logging.Level
import kotlin.math.min

class Man10FishingListener(private val plugin: Man10Fishing) : Listener {

//    @EventHandler
//    fun onBite(e:)


    @EventHandler
    fun onFish(e: PlayerFishEvent) {

        if(getNumOfRod(e.player) in 2..8){

            e.player.sendMessage("§cおおっと!ホットバーにあるもう一つの釣り竿に糸が絡まって、うまく扱うことができない!")
            e.isCancelled=true
            return
        }
        else if(getNumOfRod(e.player)==9){
            e.player.sendMessage("§cなんてこった！釣り竿を持ちすぎてうまく扱うことができない！他の場所で落ち着いて整理するしかないかも？！")
            e.isCancelled=true
            return
        }

        if(e.player.inventory.itemInOffHand.type==Material.FISHING_ROD
                &&e.player.inventory.itemInMainHand.type==Material.FISHING_ROD){
            e.player.sendMessage("§cなんてこった!両手に持っている釣り竿の糸が絡まってしまっている!")
            e.isCancelled=true
            return
        }

        if(e.state==PlayerFishEvent.State.BITE){
            Man10Fishing.fisherWithBiteRod[e.player.uniqueId]=Date().time
            return
        }

        if(e.state != PlayerFishEvent.State.CAUGHT_FISH) return



            val mainHand = e.player.inventory.itemInMainHand
            if (!FishingRod.isRod(mainHand)) return
            val rodItem = FishingRod(mainHand)
            if (rodItem.getFoodCount() <= 0) return

            e.caught ?: return


            if (Man10Fishing.playersOpeningTreasure.contains(e.player)) {
                plugin.logger.log(Level.WARNING, "${e.player.name}${Man10Fishing.playerAlert}")
                Man10Fishing.instance.server.onlinePlayers.forEach {
                    if(it.hasPermission("mfish.op")){
                        it.sendMessage("§c${e.player.name}${Man10Fishing.playerAlert}")
                    }
                }
            }

//            if (Man10Fishing.fishers[e.player.address.address.hostAddress] != e.player.uniqueId) {
//                return
//            }


        //魚釣り成功
        //ブレが小さいほど釣り成功の受付時間UP
        if(Date().time-(Man10Fishing.fisherWithBiteRod[e.player.uniqueId]?:0L)<=Man10Fishing.biteTime*(2-min(rodItem.currentFood[5]/500.0,1.0))) {

            //////////////
            //宝箱を釣った場合
            if(java.util.Random().nextDouble()<Man10Fishing.probOfTreasure) {
                //ここ名前だけでいい
                val pickedTreasure=Man10Fishing.api.pickTreasure(e.player,e.hook.location)?:return


                (e.caught as Item).itemStack=
                        TreasureRank.getRankIcon(pickedTreasure.rank)
                                .setDisplayName("§cエラーアイテム,拾えたらレポートしてください")
                                .setCustomData(plugin,"Treasure",pickedTreasure.name)
                                .build()

                (e.caught as Item).owner=e.player.uniqueId
            }
            //
            //////////////

            //////////////
            //魚を釣った場合
            else {
                val pickedFish = Man10Fishing.api.pickFish(e.player, rodItem, e.hook.location) ?: return


                val fishParameter: FishParameter = FishParameter().generateFishParameter(e.player, pickedFish)
                val fishedItem = e.caught as Item
                fishedItem.itemStack = pickedFish.getDetailedItem(fishParameter)
                pickedFish.executeOnFish(fishParameter, e.player, rodItem,e.hook.location)
            }
            //
            //////////////
        }
        else{//時間切れ
            (e.caught as Item).itemStack=ItemStack(Material.AIR)
            e.player.sendMessage(Man10Fishing.prefix + "§c逃げられてしまったようだ...")
        }

        if (!rodItem.removeFoodCount(1)){
            e.player.sendMessage(Man10Fishing.prefix + "§c餌がなくなりました")
        }
        Man10Fishing.fisherWithBiteRod.remove(e.player.uniqueId)
        rodItem.updateLore()
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        //釣り竿を持って右クリックすると左クリックも呼び出されるというminecraftのわけわかんねぇバグがあるので
        //左クリックで餌guiを開くみたいなことができません
        if(e.action != Action.LEFT_CLICK_BLOCK)return
        if(e.player.inventory.itemInMainHand.type != Material.FISHING_ROD) return
        if(!FishingRod.isRod(e.player.inventory.itemInMainHand)) return
        e.isCancelled = true

        val menu = SingleItemStackSelectorMenu("餌を選択してください", ItemStack(Material.AIR), plugin)
        menu.setCheckItemFunction { itemStack: ItemStack? ->
            if(itemStack == null) return@setCheckItemFunction false
            return@setCheckItemFunction FishFood.isFood(itemStack)
        }
//        menu.selectTypeItem(true)
        menu.setOnConfirm { itemStack: ItemStack? ->
            if(e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return@setOnConfirm
            if(e.player.inventory.itemInMainHand.type != Material.FISHING_ROD) return@setOnConfirm
            if(!FishingRod.isRod(e.player.inventory.itemInMainHand)) return@setOnConfirm
            if(itemStack == null) return@setOnConfirm
            if(itemStack.type.isAir) return@setOnConfirm
            if(!FishFood.isFood(itemStack)) return@setOnConfirm

            e.player.inventory.removeItemAnySlot(itemStack)

            val rod = FishingRod(e.player.inventory.itemInMainHand)
            val foodType = FishFood(itemStack).getFoodTypeString() ?: return@setOnConfirm

            rod.setFoodCount(itemStack.amount)
            rod.setFoodType(foodType)
            rod.updateLore()
            e.player.sendMessage(Man10Fishing.prefix + "§a餌をセットしました")
            menu.close(e.player)

        }
        menu.open(e.player)
    }

    @EventHandler
    fun onSwap(e:PlayerSwapHandItemsEvent){
        if(e.mainHandItem?.type==Material.FISHING_ROD||e.offHandItem?.type==Material.FISHING_ROD){
            e.player.sendMessage("§cおおっと!あなたは釣り竿を強く握りしめているので、持つ手を入れ替えることができない!")
            e.isCancelled=true
        }
    }

    @EventHandler
    fun onInventorySwap(e:InventoryClickEvent){
        val player=e.whoClicked as Player
        if(e.currentItem?.type!=Material.FISHING_ROD)return
        if(player.inventory.itemInMainHand.type!=Material.FISHING_ROD)return
        if(e.slot==player.inventory.heldItemSlot)e.whoClicked.sendMessage("§cおおっと!あなたは釣り竿を強く握りしめているので、他のスロットに移動させることができない!")
        else e.whoClicked.sendMessage("§cおおっと!あなたは釣り竿を強く握りしめているので、他の釣り竿を移動させることができない!")
        e.isCancelled=true
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        val contest = Man10Fishing.nowContest?:return
        contest.onJoin(e)
    }

    @EventHandler
    fun onPickUp(e:PlayerAttemptPickupItemEvent){
        if(e.item.owner!=e.player.uniqueId)return
        try {
            val treasureName=SItemStack(e.item.itemStack).getCustomData(plugin,"Treasure")
            Man10Fishing.playersOpeningTreasure.add(e.player)
            TreasureBoxMenu(e.player,Man10FishingAPI.treasure[treasureName]!!).open(e.player)
            e.isCancelled=true
            e.item.remove()
        }catch (e:Exception){
            return
        }
    }


    private fun getNumOfRod(player:Player):Int{
        var count=0
        val inv=player.inventory
        for(i in 0 until 9){
            if(inv.getItem(i)?.type==Material.FISHING_ROD)count++
        }
        return count
    }

}