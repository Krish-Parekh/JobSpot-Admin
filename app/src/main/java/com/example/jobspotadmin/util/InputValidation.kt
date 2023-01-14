package com.example.jobspotadmin.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.regex.Pattern

class InputValidation {
    companion object {
        private val EMAIL_ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$")
        /**
         * PASSWORD_PATTERN is the regular expression that is used to check the following constraints on the password:
         * 1. Password should contain at least 4 characters
         * 2. Password should contain at least one letter
         * 3. Password should contain at least one digit
         * 4. Password should contain at least one special character from the specified set of characters
         */
        private val PASSWORD_PATTERN = Pattern.compile("^.*(?=.{4,})(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!&\$%&?#_@ ]).*\$")
        fun checkNullity(input: String): Boolean {
            return input.isEmpty()
        }

        fun isUsernameValid(username: String): Pair<Boolean, String> {
            if (username.isEmpty()) {
                return Pair(false, "Username cannot be empty.")
            }
            if (username[0].isDigit()) {
                return Pair(false, "Username cannot start with a number.")
            }
            if (username.length > 100) {
                return Pair(false, "Username cannot be more than 100 characters.")
            }
            if (username.matches("^[a-zA-Z0-9]+$".toRegex()).not()) {
                return Pair(false, "Username can only contain alphabets and numbers.")
            }
            return Pair(true, "")
        }

        fun isEmailValid(email: String): Pair<Boolean, String> {
            if (email.isEmpty()) {
                return Pair(false, "Email cannot be empty.")
            }
            if (email[0].isDigit()) {
                return Pair(false, "Email cannot start with a number.")
            }
            if (EMAIL_ADDRESS_PATTERN.matcher(email).matches().not()) {
                return Pair(false, "Email is not valid.")
            }
            return Pair(true, "")
        }

        fun isPasswordValid(password: String): Pair<Boolean, String> {
            if (password.isEmpty()) {
                return Pair(false, "Password cannot be empty.")
            }
            if (PASSWORD_PATTERN.matcher(password).matches().not()) {
                return Pair(false, "Password is not valid.")
            }
            return Pair(true, "")
        }

        fun isMobileNumberValid(mobileNumber: String): Pair<Boolean, String> {
            if (mobileNumber.isEmpty()) {
                return Pair(false, "Mobile number cannot be empty.")
            }
            if (mobileNumber.startsWith("+91").not()) {
                return Pair(false, "Mobile number must start with +91.")
            }
            if (mobileNumber.length != 13) {
                return Pair(false, "Mobile number must be 13 characters.")
            }
            if (mobileNumber.substring(1).matches("^[0-9]+$".toRegex()).not()) {
                return Pair(false, "Mobile number can only contain digits.")
            }
            if (mobileNumber.substring(3, 6).equals("000")) {
                return Pair(false, "Mobile number cannot start with 000.")
            }
            val firstDigit = mobileNumber[3]
            if (mobileNumber.slice(3..12).all { it == firstDigit }) {
                return Pair(false, "All the digits in mobile number cannot be same.")
            }
            return Pair(true, "")
        }

        fun isDOBValid(dob: String): Pair<Boolean, String> {
            if (dob.isEmpty()) {
                return Pair(false, "Date of Birth cannot be empty.")
            }
            val dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val now = LocalDate.now()
            val age = ChronoUnit.YEARS.between(dobDate, now)
            if (age < 18) {
                return Pair(false, "Age must be 18 years or older.")
            }
            return Pair(true, "")
        }



        fun isStreamValid(stream: String): Pair<Boolean, String> {
            if (stream.isBlank()) {
                return Pair(false, "Stream should not be blank.")
            }
            if (stream.length > 100) {
                return Pair(false, "Length should be less than or equal to 100 characters.")
            }
            if (stream[0].isDigit()) {
                return Pair(false, "Stream cannot start with a number.")
            }
            if(stream.matches("^[a-zA-Z0-9\\s]+$".toRegex()).not()) {
                return Pair(false, "Special characters are not allowed.")
            }
            return Pair(true, "")
        }

        fun isExperienceValid(experience: String): Pair<Boolean, String> {
            if (experience.isBlank()) {
                return Pair(false, "Experience should not be blank.")
            }
            if (experience.startsWith("-")) {
                return Pair(false, "Experience should not be negative.")
            }
            if (experience.startsWith("0")) {
                return Pair(false, "Experience should not start with leading zero.")
            }
            try {
                val experienceDouble = experience.toDouble()
                if(experienceDouble > 99){
                    return Pair(false, "Experience should not be more than 2 digits.")
                }
            } catch (e: NumberFormatException) {
                return Pair(false, "Experience should be in decimal.")
            }
            return Pair(true, "")
        }

        fun isBiographyValid(biography: String): Pair<Boolean, String> {
            if (biography.isBlank()) {
                return Pair(false, "Biography should not be blank.")
            }
            if (biography.length > 200) {
                return Pair(false, "Biography should be less than or equal to 200 characters.")
            }
            return Pair(true, "")
        }

        fun isJobTitleValid(jobTitle: String): Pair<Boolean, String> {
            if (jobTitle.isBlank()) {
                return Pair(false, "Job Title is required field.")
            }
            if(jobTitle[0].isDigit()) {
                return Pair(false, "Job Title should not start with number.")
            }
            if (jobTitle.length > 100) {
                return Pair(false, "Job Title should be less than or equal to 100 characters.")
            }
            if(!jobTitle.matches("^[a-zA-Z\\s]+$".toRegex())) {
                return Pair(false, "Special characters are not allowed in Job Title.")
            }


            return Pair(true, "")
        }

        fun isCompanyNameValid(companyName: String): Pair<Boolean, String> {
            if (companyName.isBlank()) {
                return Pair(false, "Company name is required field.")
            }
            if (companyName.length > 100) {
                return Pair(false, "Company name should be less than or equal to 100 characters.")
            }
            if(companyName[0].isDigit()) {
                return Pair(false, "Company name should not start with number.")
            }
            if(!companyName.matches("^[a-zA-Z\\s]+$".toRegex())) {
                return Pair(false, "Special characters are not allowed in Company name.")
            }
            return Pair(true, "")
        }
        fun isCityValid(city: String): Pair<Boolean, String> {
            if (city.isEmpty()) {
                return Pair(false, "City cannot be empty.")
            }
            if (city.matches("^[a-zA-Z ]+$".toRegex()).not()) {
                return Pair(false, "City can only contain letters and spaces.")
            }
            if (city.length > 100) {
                return Pair(false, "City cannot be more than 100 characters.")
            }
            return Pair(true, "")
        }

        fun isSalaryValid(salary: String): Pair<Boolean, String> {
            if (salary.isBlank()) {
                return Pair(false, "Salary is required field.")
            }
            if (salary.startsWith("-")) {
                return Pair(false, "Salary should be positive.")
            }
            try {
                val salaryInt = salary.toInt()
                if (salaryInt < 100000 || salaryInt > 2500000) {
                    return Pair(false, "Salary should be between 1 lac - 25 lac.")
                }
            } catch (e: NumberFormatException) {
                return Pair(false, "Salary should be a number.")
            }
            return Pair(true, "")
        }

        fun isJobDescriptionValid(jobDescription: String): Pair<Boolean, String> {
            if (jobDescription.isBlank()) {
                return Pair(false, "Job Description is required field.")
            }
            if (jobDescription.length < 100 || jobDescription.length > 500) {
                return Pair(false, "Job Description should be between 100 to 500 characters.")
            }
            return Pair(true, "")
        }

        fun isResponsibilityValid(responsibility: String): Pair<Boolean, String> {
            if (responsibility.isBlank()) {
                return Pair(false, "Responsibility is required field.")
            }
            if (responsibility.length < 100 || responsibility.length > 500) {
                return Pair(false, "Responsibility should be between 100 to 500 characters.")
            }
            return Pair(true, "")
        }

        fun isSkillSetValid(skillSet: List<String>): Pair<Boolean, String> {
            if (skillSet.isEmpty() || skillSet.size > 20) {
                return Pair(false, "Skill set should have at least 1 element and should not exceed 20 elements.")
            }
            return Pair(true, "")
        }

        fun isMockTitleValid(mockTitle: String): Pair<Boolean, String> {
            if (mockTitle.length > 100) {
                return Pair(false, "Mock Title should be less than or equal to 100 characters.")
            }
            if (mockTitle.isBlank()) {
                return Pair(false, "Mock Title is required field.")
            }
            return Pair(true, "")
        }

        fun isDurationValid(duration: String): Pair<Boolean, String> {
            if (duration.isBlank()) {
                return Pair(false, "Duration is required field.")
            }
            if (duration.startsWith("-")) {
                return Pair(false, "Duration should be positive.")
            }
            try {
                val durationInt = duration.toInt()
                if (durationInt < 10 || durationInt > 60) {
                    return Pair(false, "Duration should be between 10m to 60m.")
                }
            } catch (e: NumberFormatException) {
                return Pair(false, "Duration should be a number.")
            }
            return Pair(true, "")
        }

        fun isQuestionValid(question: String): Pair<Boolean, String> {
            if (question.length > 200) {
                return Pair(false, "Question should be less than or equal to 200 characters.")
            }
            if (question.isBlank()) {
                return Pair(false, "Question is required field.")
            }
            return Pair(true, "")
        }

        fun isOptionsValid(options: List<String>): Pair<Boolean, String> {
            if (options.isEmpty()) {
                return Pair(false, "Options is required field.")
            }

            val uniqueOptions = HashSet<String>()
            for (option in options) {
                if (option.length > 200) {
                    return Pair(false, "Each option in list should be less than or equal to 200 characters.")
                }
                if (uniqueOptions.add(option).not()) {
                    return Pair(false, "Each option in list should be unique.")
                }
            }
            return Pair(true, "")
        }

        fun isFeedbackValid(feedback: String): Pair<Boolean, String> {
            if (feedback.isBlank()) {
                return Pair(false, "Feedback is required field.")
            }
            if (feedback.length > 200) {
                return Pair(false, "Feedback should be less than or equal to 200 characters.")
            }
            return Pair(true, "")
        }
    }
}