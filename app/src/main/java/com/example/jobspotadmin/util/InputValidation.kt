package com.example.jobspotadmin.util

import java.util.regex.Pattern

class InputValidation {
    companion object {
        private val EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$")
        private val PASSWORD_PATTERN =
            Pattern.compile("^.*(?=.{4,})(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!&\$%&? \"]).*\$")
        private val SALARY_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?\$")
        fun checkNullity(input: String): Boolean {
            return input.isNotEmpty();
        }

        fun emailValidation(email: String): Boolean {
            return checkNullity(email) && EMAIL_ADDRESS_PATTERN.matcher(email).matches()
        }

        fun passwordValidation(password: String): Boolean {
            return checkNullity(password) && PASSWORD_PATTERN.matcher(password).matches()
        }

        fun mobileValidation(number: String): Boolean {
            return checkNullity(number) && number.length == 10
        }

        fun salaryValidation(salary: String): Boolean {
            return checkNullity(salary) && SALARY_PATTERN.matcher(salary).matches()
        }

        fun mockDurationValidation(duration: String): Boolean {
            return checkNullity(duration) && (duration.toInt() in 10..60)
        }

        fun optionListValidation(options: List<String>): Boolean {
            options.forEach {
                if (it.isEmpty()) {
                    return false
                }
            }
            return true
        }
    }
}