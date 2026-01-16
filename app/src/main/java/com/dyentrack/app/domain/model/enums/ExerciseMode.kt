package com.dyentrack.app.domain.model.enums

enum class ExerciseMode { NORMAL, LOCK, CHANGE_ORDER;

    fun isNormal(): Boolean = this == NORMAL
    fun isLock(): Boolean = this == LOCK
    fun isChangeOrder(): Boolean = this == CHANGE_ORDER
}