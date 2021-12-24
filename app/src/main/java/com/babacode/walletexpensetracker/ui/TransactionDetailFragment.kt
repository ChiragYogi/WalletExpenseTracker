package com.babacode.walletexpensetracker.ui


fun main(){

    val doubele = 0.000000
    println(doubele.toString().length)
     println(doubele.toString().length)
    println(doubele.toInt())
}


fun checkDoubleLength(value: Double): Boolean{
    return if (value.toString().length >= 9){
        println("value is grater than 9")
        true
    }else{
        println("value is less than 9")
        false
    }
}


fun parseDouble(value: String?) : Double{
    return try {
        if (value == null || value.isEmpty()) Double.NaN else value.toDouble()
    }catch (e: Exception){
        e.printStackTrace()
        return 0.0
    }
}
