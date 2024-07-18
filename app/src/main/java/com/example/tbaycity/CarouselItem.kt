package com.example.tbaycity

class CarouselItem (
    var title:String?,
    var description:String?,
    var imgUrl:String?,
    val date: com.google.firebase.Timestamp? = null,
    val location: String? = null,
    val contactNumber: String? = null,
    val blogURL: String? = null,
    val startTimeEndTime: String? = null,
){}