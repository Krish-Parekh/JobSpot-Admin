package com.example.jobspotadmin.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.jobspotadmin.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalTime
import java.util.*

fun TextInputLayout.addTextWatcher() {
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            error = null
        }
    })
}

fun createChip(
    value: String,
    context: Context,
    chipGroup: ChipGroup,
    onChipRemove: (String) -> Unit
) {
    val chip = Chip(context)
    chip.apply {
        text = value
        chipIconSize = 24F
        closeIcon = AppCompatResources.getDrawable(context, R.drawable.ic_cross)
        isCloseIconVisible = true
        chip.chipBackgroundColor =
            ContextCompat.getColorStateList(context, R.color.chip_background_color)
        chip.setTextColor(context.getColor(R.color.chip_text_color))
        chip.chipCornerRadius = 8f
        setOnCloseIconClickListener {
            onChipRemove(value)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }
}

fun TextInputEditText.clearText() {
    setText("")
    clearFocus()
}

fun TextInputEditText.getInputValue(): String {
    return text.toString().trim()
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun convertToShortString(value: Long): String {
    if (value < 1000) {
        return value.toString()
    } else if (value < 100000) {
        return "%.1f K".format(value / 1000.0)
    } else if (value < 10000000) {
        return "%.1f Lac".format(value / 100000.0)
    } else {
        return "${value / 10000000}Cr"
    }
}


fun convertTimeStamp(timestamp: Date): String {
    val timestampDate = timestamp
    val currentDate = Date()

    val elapsedTime = currentDate.time - timestampDate.time

    val elapsedDays = (elapsedTime / (1000 * 60 * 60 * 24)).toInt()

    val elapsedHours = ((elapsedTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toInt()

    val elapsedMinutes = ((elapsedTime % (1000 * 60 * 60)) / (1000 * 60)).toInt()

    val elapsedSeconds = ((elapsedTime % (1000 * 60)) / 1000).toInt()

    var elapsedTimeString = ""

    if (elapsedDays > 0) {
        elapsedTimeString = "$elapsedDays days ago"
    } else if (elapsedHours > 0) {
        elapsedTimeString = "$elapsedHours hours ago"
    } else if (elapsedMinutes > 0) {
        elapsedTimeString = "$elapsedMinutes minutes ago"
    } else if (elapsedSeconds > 0) {
        elapsedTimeString = "$elapsedSeconds seconds ago"
    }
    return elapsedTimeString
}

fun getGreeting(): String {
    val currentTime = LocalTime.now() // get the current time in the local timezone
    val hour = currentTime.hour // get the current hour
    return when(hour) {
        in 0..11 -> "Good Morning!" // if the current hour is between midnight and 11am
        in 12..16 -> "Good Afternoon!" // if the current hour is between noon and 4pm
        in 17..23 -> "Good Evening!" // if the current hour is between 5pm and midnight
        else -> "Hello!" // if the current hour is invalid (e.g. negative or greater than 23)
    }
}