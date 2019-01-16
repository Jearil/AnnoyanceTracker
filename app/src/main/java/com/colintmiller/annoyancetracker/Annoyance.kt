package com.colintmiller.annoyancetracker

import java.util.*

/**
 * Basic object for recording annoyances
 */

class Annoyance(levelVal : Int) {
    val level = levelVal
    val created = Date().time
}