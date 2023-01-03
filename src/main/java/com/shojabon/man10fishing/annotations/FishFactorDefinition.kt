package com.shojabon.man10fishing.annotations

import org.bukkit.Material

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FishFactorDefinition(val name: String = "無名",
                                        val iconMaterial: Material = Material.DIAMOND,
                                        val explanation: Array<String> = [],
                                        val settable: Boolean = false,
                                        val adminSetting: Boolean = true,
                                        val enabled: Boolean = true)
