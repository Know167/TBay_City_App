package com.example.tbaycity.ui

class ServiceModel(var servicename:String,var imgid:Int) {
    fun getservicename():String{
        return servicename
    }
    fun setservicename(servicename: String){
        this.servicename = servicename
    }
    fun getimgid():Int{
        return imgid
    }
    fun setimgid(imgid:Int){
        this.imgid = imgid
    }
}