package com.babacode.walletexpensetracker.utiles

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*



fun View.show() {
    visibility = View.VISIBLE
}


fun View.hide() {
    visibility = View.GONE
}

inline fun View.showSnackBar(
    @StringRes msg: Int,
    length: Int = Snackbar.LENGTH_LONG,
    action: Snackbar.() -> Unit = {}
) {

    val snackBar = Snackbar.make(this, resources.getString(msg), length)
    action.invoke(snackBar)
    snackBar.show()
}

//For Date Picker
fun TextInputEditText.transformDatePicker(
    context: Context,
    format: String,
    maxDate: Date? = null
) {

    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalender = Calendar.getInstance()

    val datePickerSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.US)
            setText(sdf.format(myCalender.time))

        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerSetListener,
            myCalender.get(Calendar.YEAR),
            myCalender.get(Calendar.MONTH),
            myCalender.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also {
                datePicker.maxDate = it
            }
            show()
        }
    }
}





