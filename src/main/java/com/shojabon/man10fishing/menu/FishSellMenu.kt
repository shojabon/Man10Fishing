package com.shojabon.man10fishing.menu

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import com.shojabon.mcutils.Utils.VaultAPI
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class FishSellMenu: SInventory("§b魚を売る", 4, Man10Fishing.instance) {

    init {
        setOnClickEvent {
            it.isCancelled = true

            if (it.clickedInventory is PlayerInventory){
                val item = it.currentItem?:return@setOnClickEvent
                val isFish = item.itemMeta.persistentDataContainer.has(
                    NamespacedKey(Man10Fishing.instance, "fish"), PersistentDataType.STRING
                )
                if (!isFish) return@setOnClickEvent

                it.clickedInventory!!.setItem(it.slot, null)
                val emptySlot = (0..26).firstOrNull { map ->
                    activeInventory.getItem(map) == null
                }?:return@setOnClickEvent

                activeInventory.setItem(emptySlot, item)

                renderMenu()
                renderInventory()
            } else if (it.clickedInventory != null){
                if (it.slot !in (0..26)) return@setOnClickEvent
                val item = it.currentItem?:return@setOnClickEvent
                activeInventory.setItem(it.slot, null)
                val dropItems = it.whoClicked.inventory.addItem(item)
                dropItems.values.forEach { dropItem ->
                    it.whoClicked.world.dropItem(it.whoClicked.location, dropItem) { item ->
                        item.pickupDelay = 0
                        item.owner = it.whoClicked.uniqueId
                        item.setCanMobPickup(false)
                    }
                }

                renderMenu()
                renderInventory()
            }

        }

        setOnCloseEvent {
            val player = it.player
            val inventory = it.inventory
            val items = (0..26).mapNotNull { map ->
                val item = inventory.getItem(map)?:return@mapNotNull null
                item
            }

            items.forEach { item ->
                val dropItems = player.inventory.addItem(item)
                dropItems.values.forEach { dropItem ->
                    player.world.dropItem(player.location, dropItem) { item ->
                        item.pickupDelay = 0
                        item.owner = player.uniqueId
                        item.setCanMobPickup(false)
                    }
                }
            }
        }
    }

    override fun renderMenu() {
        setItem((27..35).toList().toIntArray(), SInventoryItem(SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))


        setItem((30..32).toList().toIntArray(), SInventoryItem(
            SItemStack(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§c§l売却").addLore("§a${getSumSellPrice()}円で売る").build()
        ).clickable(false)
            .setEvent {
                val money = getSumSellPrice()

                (0..26).forEach { map ->
                    it.inventory.setItem(map, null)
                }

                Man10Fishing.vault.deposit(it.whoClicked.uniqueId, money)
                it.whoClicked.closeInventory()
            })
    }

    private fun getSumSellPrice(): Double {
        if (activeInventory == null) return 0.0
        return (0..26).mapNotNull { map ->
            val item = activeInventory.getItem(map)?:return@mapNotNull null
            val fishName = item.itemMeta.persistentDataContainer.get(
                NamespacedKey(Man10Fishing.instance, "fish"), PersistentDataType.STRING
            )?:return@mapNotNull null
            val fish = Man10FishingAPI.fish[fishName]?:return@mapNotNull null
            val size = item.itemMeta.persistentDataContainer.get(
                NamespacedKey(Man10Fishing.instance, "size"), PersistentDataType.DOUBLE
            )?:return@mapNotNull null

            val parameter = FishParameter().apply {
                this.fish = fish
                this.size = size
            }

            getSellPrice(parameter)
        }.sum()
    }

    private fun getSellPrice(fish: FishParameter): Double {
        val minSize = fish.fish.sizeFactor.min.get()
        val maxSize = fish.fish.sizeFactor.max.get()
        val size = fish.size
        val rarity = Man10FishingAPI.rarity[fish.fish.rarity]?:return 0.0
        //sizeが大きさの幅の何%にあるか
        val sizePercentage = if(maxSize==minSize)0.0 else(max(min((size - minSize) / (maxSize - minSize) * 100,100.0),0.0))
        return floor(rarity.minSellPrice + (rarity.priceMultiplier * sizePercentage))
    }
}