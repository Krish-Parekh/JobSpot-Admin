package com.example.jobspotadmin.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
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

fun TextInputEditText.getSkillList(context: Context, chipGroup: ChipGroup, onSkillRegister : (String) -> Unit) {

    addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(skillName: CharSequence?, start: Int, before: Int, count: Int) {
            if(!skillName.isNullOrEmpty()){
                if (skillName.last() == ',' && skillName.length > 1){
                    val skill = skillName.substring(0, skillName.length - 1)
                    val skillChip = Chip(context)
                    skillChip.text = skill
                    chipGroup.addView(skillChip)
                    onSkillRegister(skill)
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