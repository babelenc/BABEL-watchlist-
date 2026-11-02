package com.example.data.model

enum class WatchStatus(val key: String, val label: String) {
    PLAN_TO_WATCH("plan_to_watch", "Plan to Watch"),
    WATCHING("watching", "Watching"),
    COMPLETED("completed", "Completed"),
    ON_HOLD("on_hold", "On Hold"),
    DROPPED("dropped", "Dropped");

    companion object {
        fun fromKey(key: String): WatchStatus {
            return entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: PLAN_TO_WATCH
        }
    }
}
