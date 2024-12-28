package com.escandan.firebasetestapp2.model

data class DrivingDetail(
    val stationName: String = "",
    val stationAddress: String = "",
    val tirednessCount: Int = 0,
    var date: Long = 0L
) {
    // Firestore'un ihtiyaç duyduğu parametresiz yapıcı
    constructor() : this("", "", 0, 0)
}
