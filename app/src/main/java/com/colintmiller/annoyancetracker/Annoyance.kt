package com.colintmiller.annoyancetracker

import java.util.*

/**
 * Basic object for recording annoyances
 */

data class Annoyance(val level: Int = 0,
                     val created : Long = Date().time) : Comparable<Annoyance> {
    override fun compareTo(other: Annoyance): Int {
        if (this.created > other.created) return 1
        if (this.created < other.created) return -1
        return 0
    }
}