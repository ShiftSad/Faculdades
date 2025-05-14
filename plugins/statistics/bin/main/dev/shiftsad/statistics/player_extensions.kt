package dev.shiftsad.statistics

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

fun <T : Any, C : Any> Player.write(key: String, value: C, type: PersistentDataType<T, C>) {
    val pdc = persistentDataContainer
    val namespace = NamespacedKey("statistics", key)

    pdc.set(namespace, type, value)
}

fun Player.read(key: String, type: PersistentDataType<*, *>): Any? {
    val pdc = persistentDataContainer
    val namespace = NamespacedKey("statistics", key)

    return pdc.get(namespace, type)
}

fun Player.add(key: String, value: Int) {
    val pdc = persistentDataContainer
    val namespace = NamespacedKey("statistics", key)

    val currentValue = pdc.get(namespace, PersistentDataType.INTEGER) ?: 0
    pdc.set(namespace, PersistentDataType.INTEGER, currentValue + value)
}