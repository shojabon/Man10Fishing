package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import com.shojabon.man10fishing.dataClass.FishingRod
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player


@Author(author = "tororo_1066")
@FishFactorDefinition(name = "サウンド",
    iconMaterial = Material.NOTE_BLOCK,
    explanation = ["サウンドロジックの定義"],
    settable = true)
/**
 * @author tororo_1066
 * サウンドロジック
 * sound:
 *   name: <サウンド名(大文字、.を_にする必要あり)>
 *   volume: 0f~2f
 *   pitch: 0f~2f
 */
class SoundFactor(fish : Fish) : FishFactor(fish) {

    val sound = FishSettingVariable("sound.name","none")
    val volume = FishSettingVariable("sound.volume", 1f)
    val pitch = FishSettingVariable("sound.pitch", 1f)

    override fun onFish(fish: Fish, fisher: Player, rod: FishingRod) {
        if (!Sound.values().equals(sound.get()))return
        fisher.playSound(fisher.location,Sound.valueOf(sound.get()),volume.get(),pitch.get())
    }
}