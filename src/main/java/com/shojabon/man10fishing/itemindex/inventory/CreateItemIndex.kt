package com.shojabon.man10fishing.itemindex.inventory

import ToolMenu.LargeSInventoryMenu
import ToolMenu.SingleItemStackSelectorMenu
import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.mcutils.Utils.SConfigFile
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import com.shojabon.mcutils.Utils.SLongTextInput
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

class CreateItemIndex(private val internalName: String, private val data: ItemIndex = ItemIndex()): SInventory("図鑑を作る($internalName)", 6, Man10Fishing.instance) {

    override fun renderMenu() {
        fillItem(SInventoryItem(
                SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build())
                .clickable(false))

        setItem(10, SInventoryItem(
                SItemStack(Material.OAK_SIGN).setDisplayName("§a§l表示名を設定する")
                        .addLore("§d現在の値: §r${data.displayName}").build())
                .clickable(false).setEvent { e ->
                    e.whoClicked.closeInventory()
                    val input = SLongTextInput("表示名を入力してください", Man10Fishing.instance)
                    input.setOnCancel { open(it) }
                    input.setOnConfirm {
                        data.displayName = it.replace("&", "§")
                        open(e.whoClicked as Player)
                    }
                    input.open(e.whoClicked as Player)
                })

        setItem(13, SInventoryItem(
                SItemStack(Material.CHEST).setDisplayName("§e§lアイテムを設定する")
                        .build())
                .clickable(false)
                .setEvent {
                    val inv = object : LargeSInventoryMenu("アイテムを設定する", Man10Fishing.instance) {
                        init {
                            setOnCloseEvent {
                                Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                                    val player = it.player as Player
                                    movingPlayer.add(player.uniqueId)
                                    this@CreateItemIndex.open(player)
                                    movingPlayer.remove(player.uniqueId)
                                })
                            }
                        }

                        override fun renderMenu() {
                            setItems(ArrayList(
                                    Man10FishingAPI.fish.map { map ->
                                        val item = SItemStack(map.value.item.clone())
                                        if (data.fish.contains(map.key)){
                                            item.addEnchantment(Enchantment.LURE, 1)
                                        }
                                        SInventoryItem(item.build()).clickable(false).setEvent { e ->
                                            val p = e.whoClicked as Player
                                            if (data.fish.contains(map.key)){
                                                data.fish.remove(map.key)
                                                p.playSound(p.location, Sound.UI_BUTTON_CLICK, 1f, 0.5f)
                                            } else {
                                                data.fish.add(map.key)
                                                p.playSound(p.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f)
                                            }
                                            renderMenu()
                                            renderInventory()
                                        }
                                    }
                            ))
                        }
                    }

                    inv.open(it.whoClicked as Player)
                }
        )

        setItem(16, SInventoryItem(
                SItemStack(Material.ENDER_CHEST)
                        .setDisplayName("§d§l登録しているアイテムを確認する")
                        .build())
                .clickable(false)
                .setEvent {
                    val inv = object : LargeSInventoryMenu("登録しているアイテムを確認する", Man10Fishing.instance) {
                        init {
                            setOnCloseEvent {
                                Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                                    val player = it.player as Player
                                    movingPlayer.add(player.uniqueId)
                                    this@CreateItemIndex.open(player)
                                    movingPlayer.remove(player.uniqueId)
                                })
                            }
                        }

                        override fun renderMenu() {
                            setItems(ArrayList(
                                    data.fish.map { map ->
                                        val item = SItemStack(Man10FishingAPI.fish[map]?.item?: ItemStack(Material.STONE))
                                        SInventoryItem(item.build()).clickable(false).setEvent { e ->
                                            val p = e.whoClicked as Player
                                            p.playSound(p.location, Sound.UI_BUTTON_CLICK, 1f, 0.5f)
                                            data.fish.remove(map)
                                            renderMenu()
                                            renderInventory()
                                        }
                                    }
                            ))
                        }
                    }

                    inv.open(it.whoClicked as Player)
                }
        )

        setItem(28, SInventoryItem(
                SItemStack(Material.ITEM_FRAME)
                        .setDisplayName("§6§l表示されるアイテム")
                        .build())
                .clickable(false)
                .setEvent {
                    val inv = object : SingleItemStackSelectorMenu("表示されるアイテム", ItemStack(data.displayItem), Man10Fishing.instance){
                        init {
                            selectMaterial(true)
                            setOnConfirm { itemStack ->
                                data.displayItem = itemStack.type
                                it.whoClicked.closeInventory()
                            }

                            setOnCloseEvent {
                                Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                                    val player = it.player as Player
                                    movingPlayer.add(player.uniqueId)
                                    this@CreateItemIndex.open(player)
                                    movingPlayer.remove(player.uniqueId)
                                })
                            }
                        }
                    }

                    inv.open(it.whoClicked as Player)
                }
        )

        setItem(31, SInventoryItem(
                SItemStack(Material.EMERALD_BLOCK)
                        .setDisplayName("§b§lコンプリートしたときに手に入れられるアイテムを設定する")
                        .addLore("§7なしでも構いません")
                        .build())
                .clickable(false)
                .setEvent {
                    val inv = object : SingleItemStackSelectorMenu("コンプリートしたときに手に入れられるアイテム",
                            data.onCompleteItemStack, Man10Fishing.instance)
                    {
                        init {
                            allowNullItem(true)
                            setOnConfirm { itemStack ->
                                data.onCompleteItemStack = itemStack
                                it.whoClicked.closeInventory()
                            }

                            setOnCloseEvent {
                                Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                                    val player = it.player as Player
                                    movingPlayer.add(player.uniqueId)
                                    this@CreateItemIndex.open(player)
                                    movingPlayer.remove(player.uniqueId)
                                })
                            }
                        }
                    }

                    inv.open(it.whoClicked as Player)
                }
        )

        setItem(34, SInventoryItem(
                SItemStack(Material.COMMAND_BLOCK)
                        .setDisplayName("§c§lコンプリートしたときに実行するコマンドを設定する")
                        .build())
                .clickable(false)
                .setEvent { e ->
                    val inv = object : LargeSInventoryMenu("コンプリートしたときに実行するコマンドを設定する", Man10Fishing.instance) {

                        var ignoreCloseEvent = false

                        init {
                            setOnCloseEvent {
                                Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                                    if (ignoreCloseEvent) return@Runnable
                                    val player = it.player as Player
                                    movingPlayer.add(player.uniqueId)
                                    this@CreateItemIndex.open(player)
                                    movingPlayer.remove(player.uniqueId)
                                })
                            }
                        }

                        override fun renderMenu() {
                            val items = ArrayList<SInventoryItem>()
                            items.addAll(data.onCompleteCommands.map { map ->
                                SInventoryItem(SItemStack(Material.REDSTONE_BLOCK)
                                        .setDisplayName(map)
                                        .addLore("§cクリックで削除").build()).clickable(false).setEvent { _ ->
                                    data.onCompleteCommands.remove(map)
                                    renderMenu()
                                    renderInventory()
                                }
                            })
                            items.add(SInventoryItem(SItemStack(Material.EMERALD_BLOCK)
                                    .setDisplayName("§a§lコマンドを追加する").build()).clickable(false).setEvent { _ ->
                                ignoreCloseEvent = true
                                e.whoClicked.closeInventory()
                                val input = SLongTextInput("コマンドを入力してください", Man10Fishing.instance)
                                input.setOnCancel {
                                    open(it)
                                    ignoreCloseEvent = false
                                }
                                input.setOnConfirm { cmd ->
                                    data.onCompleteCommands.add(cmd)
                                    open(e.whoClicked as Player)
                                    ignoreCloseEvent = false
                                }
                                input.open(e.whoClicked as Player)

                            })
                            setItems(items)
                        }
                    }

                    inv.open(e.whoClicked as Player)
                }
        )

        setItem(46, SInventoryItem(
            SItemStack(Material.PAPER)
                .setDisplayName("§a§l未登録の魚の名前を表示するか")
                .addLore("§7現在の値: ${data.showFishName}").build())
            .clickable(false)
            .setEvent {
                data.showFishName = !data.showFishName
                renderMenu()
                renderInventory()
            }
        )

        setItem(49, SInventoryItem(
                SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§b§l図鑑を作成する").build())
                .clickable(false)
                .setEvent {
                    data.save()
                    ItemIndex.itemIndexes[internalName] = data
                    it.whoClicked.sendMessage(Man10Fishing.prefix + "§a§l図鑑を作成しました")
                    it.whoClicked.closeInventory()
                }
        )
    }
}