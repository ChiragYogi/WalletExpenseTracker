package com.babacode.walletexpensetracker.utiles

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

import java.text.SimpleDateFormat

import java.util.*
import kotlin.Exception


val <T> T.exhaustive: T
    get() = this


fun View.show() {
    visibility = View.VISIBLE
}



fun View.hide() {
    visibility = View.GONE
}

//For Date Picker
fun TextInputEditText.transformDatePicker(
    context: Context,
    format: String,
    maxDate: Date? = null
){

    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalender = Calendar.getInstance()

    val datePickerSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.UK)
            setText(sdf.format(myCalender.time))

        }

    setOnClickListener {
        DatePickerDialog(
            context,datePickerSetListener,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),
            myCalender.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also {
                datePicker.maxDate = it }
            show()
        }
    }
}


