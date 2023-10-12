package com.example.arrivalcharm.util

import kotlin.math.*

object DistanceManager {
    private const val R = 6372.8 * 1000

    /**
     * 두 좌표의 거리를 계산한다.
     *
     * @param firstLat 위도1
     * @param firstLon 경도1
     * @param secondLat 위도2
     * @param secondLon 경도2
     * @return 두 좌표의 거리(m)
     */
    fun getDistance(firstLat: Double, firstLon: Double, secondLat: Double, secondLon: Double): Int {
        val dLat = Math.toRadians(secondLat - firstLat)
        val dLon = Math.toRadians(secondLon - firstLon)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(firstLat)) * cos(Math.toRadians(secondLat))
        val c = 2 * asin(sqrt(a))
        return (R * c).toInt()
    }
}