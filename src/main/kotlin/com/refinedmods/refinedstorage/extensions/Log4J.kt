package com.refinedmods.refinedstorage.extensions

import com.refinedmods.refinedstorage.RS
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KClass

fun getCustomLogger(clazz: KClass<*>): Logger {
    return LogManager.getLogger("${RS.ID}.${clazz.simpleName}")
}