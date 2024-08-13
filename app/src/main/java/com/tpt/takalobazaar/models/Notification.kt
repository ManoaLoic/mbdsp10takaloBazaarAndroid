package com.tpt.takalobazaar.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tpt.takalobazaar.sealed.Screen


@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: Int,
    val body: String,
    val target: String,
    val targetId: Int,
    val time: String,
) {
}