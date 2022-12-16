package com.example.jobspotadmin.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.example.jobspotadmin.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.addTextWatcher(){
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            error = null
        }
    })
}

fun TextInputEditText.getSkillList(context: Context, chipGroup: ChipGroup, onSkillAdded : (String) -> Unit, onSkillRemove : (String) -> Unit) {

    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
            if(!value.isNullOrEmpty()){
                if (value.last() == ',' && value.length > 1){
                    val skill = value.substring(0, value.length - 1)
                    val skillChip = Chip(context)
                    skillChip.apply {
                        text = skill
                        chipIconSize = 24F
                        closeIcon = AppCompatResources.getDrawable(context, R.drawable.ic_cross)
                        isCloseIconVisible = true

                        setOnCloseIconClickListener {
                            onSkillRemove(skillChip.text.toString())
                            chipGroup.removeView(skillChip)
                        }
                        chipGroup.addView(skillChip)

                        onSkillAdded(skill)
                    }
                    text?.clear()
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}

    })
}

fun checkField(
    field: String,
    errorMessage: String,
    errorField: TextInputLayout
): Boolean {
    return if (!InputValidation.checkNullity(field)) {
        errorField.error = errorMessage
        false
    } else {
        true
    }
}

fun TextInputEditText.clearText(){
    setText("")
    clearFocus()
}
fun TextInputEditText.getInputValue() : String{
    return text.toString().trim()
}

fun showToast(context : Context, message : String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}