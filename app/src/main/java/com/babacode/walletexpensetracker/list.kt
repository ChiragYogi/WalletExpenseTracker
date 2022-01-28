package com.babacode.walletexpensetracker


fun main() {
    val list = listOf<Int>(10, 20, 1, 7, 2, 9, 2, 50, -20)
     sortTheListByAsccending(list)
}

fun sortTheListByAsccending(list: List<Int>) {

    for (i in list.indices) {
        for (j in i + 1 until list.size) {
            if (list[i] > list[j]) {
                val newList = list[i]
                println(newList)
            }

        }

    }
}
