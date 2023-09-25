package com.shojabon.man10fishing.commands.subCommands.food

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.menu.SynthesizeFishFoodMenu
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class SetMixablityCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        val p: Player = sender
        val item=p.inventory.itemInMainHand
        if(!FishFood.isFood(item)){
            sender.sendMessage("§c餌アイテムをメインハンドに持ってください")
            return true
        }
        if(item.itemMeta.persistentDataContainer.get(NamespacedKey(Man10Fishing.instance,"unmixable"), PersistentDataType.INTEGER)==1){
            sender.sendMessage("§c既に合成不可です")
            return true
        }
        val itemMeta=item.itemMeta
        itemMeta.persistentDataContainer.set(NamespacedKey(Man10Fishing.instance,"unmixable"), PersistentDataType.INTEGER,1)

        item.itemMeta = itemMeta

        val sItem=SItemStack(item)
        sItem.addLore("§c合成不可")
        sender.sendMessage("§a合成不可にしました")

        return false
    }
}