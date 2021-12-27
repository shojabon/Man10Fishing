package com.shojabon.man10fishing.annotations

import org.bukkit.Material

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class FoodFactorDefinition(val name: String = "無名",
                                        val iconMaterial: Material = Material.DIAMOND,
                                        val explanation: Array<String> = [],
                                        val settable: Boolean = false,
                                        val enabled: Boolean = true)
