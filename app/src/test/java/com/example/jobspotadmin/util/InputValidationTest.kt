package com.example.jobspotadmin.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class InputValidationTest {
    // Username Validation
    @Test
    fun `isUsernameValid should return false when username is empty`() {
        val (valid, message) = InputValidation.isUsernameValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Username cannot be empty.")
    }

    @Test
    fun `isUsernameValid should return false when username is more than 100 characters`() {
        val (valid, message) = InputValidation.isUsernameValid("a".repeat(101))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Username cannot be more than 100 characters.")
    }

    @Test
    fun `isUsernameValid should return false when username contains non-alphanumeric characters`() {
        val (valid, message) = InputValidation.isUsernameValid("user@name")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Username can only contain alphabets and numbers.")
    }

    @Test
    fun `isUsernameValid should return true when username is valid`() {
        val (valid, message) = InputValidation.isUsernameValid("username")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Email Validation
    @Test
    fun `isEmailValid should return false when email is empty`() {
        val (valid, message) = InputValidation.isEmailValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Email cannot be empty.")
    }

    @Test
    fun `isEmailValid should return false when email is not valid`() {
        val (valid, message) = InputValidation.isEmailValid("notvalidemail")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Email is not valid.")
    }

    @Test
    fun `isEmailValid should return true when email is valid`() {
        val (valid, message) = InputValidation.isEmailValid("valid@email.com")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Password Validation
    @Test
    fun `isPasswordValid should return false when password is not valid`() {
        val (valid, message) = InputValidation.isPasswordValid("password")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Password is not valid.")
    }

    @Test
    fun `isPasswordValid should return true when password is valid`() {
        val (valid, message) = InputValidation.isPasswordValid("P@ssw0rd!")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    @Test
    fun `isMobileNumberValid should return true for valid mobile number`() {
        val (valid, message) = InputValidation.isMobileNumberValid("+919876543210")
        assertThat(valid).isTrue()
        assertThat(message).isEmpty()
    }

    @Test
    fun `isMobileNumberValid should return false for empty mobile number`() {
        val (valid, message) = InputValidation.isMobileNumberValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mobile number cannot be empty.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number not starting with +91`() {
        val (valid, message) = InputValidation.isMobileNumberValid("9198765432101")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mobile number must start with +91.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number not being 13 characters long`() {
        val (valid, message) = InputValidation.isMobileNumberValid("+9198765432")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mobile number must be 13 characters.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number containing non-digit characters`() {
        val (valid, message) = InputValidation.isMobileNumberValid("+919876543a01")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mobile number can only contain digits.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number starting with 000`() {
        val (valid, message) = InputValidation.isMobileNumberValid("+910007654321")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mobile number cannot start with 000.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number with all digits same`() {
        val (valid, message) = InputValidation.isMobileNumberValid("+911111111111")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("All the digits in mobile number cannot be same.")
    }

    // DOB Validation
    @Test
    fun `isDOBValid should return true for valid dob`() {
        val (valid, message) = InputValidation.isDOBValid("2003-01-01")
        assertThat(valid).isTrue()
        assertThat(message).isEmpty()
    }

    @Test
    fun `isDOBValid should return false for empty dob`() {
        val (valid, message) = InputValidation.isDOBValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Date of Birth cannot be empty.")
    }

    @Test
    fun `isDOBValid should return false for dob with age less than 18`() {
        val (valid, message) = InputValidation.isDOBValid("2022-01-01")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Age must be 18 years or older.")
    }

    // Stream Validation
    @Test
    fun `isStreamValid should return false for streams longer than 100 characters`() {
        val (valid, message) = InputValidation.isStreamValid("n".repeat(101))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Length should be less than or equal to 100 characters.")
    }

    @Test
    fun `isStreamValid should return false for streams that start with a number`() {
        val (valid, message) = InputValidation.isStreamValid("1This is a stream that starts with a number.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Stream cannot start with a number.")
    }

    @Test
    fun `isStreamValid should return false for streams that contain special characters`() {
        val (valid, message) = InputValidation.isStreamValid("This is a stream! with special characters.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Special characters are not allowed.")
    }

    @Test
    fun `isStreamValid should return false for blank streams`() {
        val (valid, message) = InputValidation.isStreamValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Stream should not be blank.")
    }

    @Test
    fun `isStreamValid should return true for valid streams`() {
        val (valid, message) = InputValidation.isStreamValid("This is a valid stream")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }


    // Experience
    @Test
    fun `isExperienceValid should return false for negative experiences`() {
        val (valid, message) = InputValidation.isExperienceValid("-1")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Experience should not be negative.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that start with leading zero`() {
        val (valid, message) = InputValidation.isExperienceValid("01")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Experience should not start with leading zero.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that are more than 2 digits`() {
        val (valid, message) = InputValidation.isExperienceValid("100")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Experience should not be more than 2 digits.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that are not in decimal`() {
        val (valid, message) = InputValidation.isExperienceValid("not a number")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Experience should be in decimal.")
    }

    @Test
    fun `isExperienceValid should return false for blank experiences`() {
        val (valid, message) = InputValidation.isExperienceValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Experience should not be blank.")
    }

    @Test
    fun `isExperienceValid should return true for valid experiences`() {
        val (valid, message) = InputValidation.isExperienceValid("2.5")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }


    // Bio Validation
    @Test
    fun `isBiographyValid should return false for blank biographies`() {
        val (valid, message) = InputValidation.isBiographyValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Biography should not be blank.")
    }

    @Test
    fun `isBiographyValid should return false for biographies longer than 200 characters`() {
        val (valid, message) = InputValidation.isBiographyValid("n".repeat(201))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Biography should be less than or equal to 200 characters.")
    }

    @Test
    fun `isBiographyValid should return true for valid biographies`() {
        val (valid, message) = InputValidation.isBiographyValid("This is a valid biography.")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Job Title Validation
    @Test
    fun `isJobTitleValid should return false for job titles longer than 100 characters`() {
        val (valid, message) = InputValidation.isJobTitleValid("This is a very long job title that should be more than 100 characters in length to test the validation function.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Title should be less than or equal to 100 characters.")
    }

    @Test
    fun `isJobTitleValid should return false for job titles that contain special characters`() {
        val (valid, message) = InputValidation.isJobTitleValid("This is a job title! with special characters.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Special characters are not allowed in Job Title.")
    }

    @Test
    fun `isJobTitleValid should return false for job titles that start with number`() {
        val (valid, message) = InputValidation.isJobTitleValid("1This is a job title.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Title should not start with number.")
    }

    @Test
    fun `isJobTitleValid should return false for blank job titles`() {
        val (valid, message) = InputValidation.isJobTitleValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Title is required field.")
    }

    @Test
    fun `isJobTitleValid should return true for valid job titles`() {
        val (valid, message) = InputValidation.isJobTitleValid("This is a valid job title")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Company Name Validation
    @Test
    fun `isCompanyNameValid should return false for company names longer than 100 characters`() {
        val (valid, message) = InputValidation.isCompanyNameValid("a".repeat(101))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Company name should be less than or equal to 100 characters.")
    }

    @Test
    fun `isCompanyNameValid should return false for company names that contain special characters`() {
        val (valid, message) = InputValidation.isCompanyNameValid("This is a company name! with special characters.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Special characters are not allowed in Company name.")
    }

    @Test
    fun `isCompanyNameValid should return false for company names that start with number`() {
        val (valid, message) = InputValidation.isCompanyNameValid("1This is a company name.")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Company name should not start with number.")
    }

    @Test
    fun `isCompanyNameValid should return false for blank company names`() {
        val (valid, message) = InputValidation.isCompanyNameValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Company name is required field.")
    }

    @Test
    fun `isCompanyNameValid should return true for valid company names`() {
        val (valid, message) = InputValidation.isCompanyNameValid("This is a valid company name")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // City Validation
    @Test
    fun `isCityValid should return true and an empty string for a valid city`() {
        val (valid, message) = InputValidation.isCityValid("New York City")
        assertThat(valid).isTrue()
        assertThat(message).isEmpty()
    }

    @Test
    fun `isCityValid should return false and the correct error message for an empty city`() {
        val (valid, message) = InputValidation.isCityValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("City cannot be empty.")
    }

    @Test
    fun `isCityValid should return false and the correct error message for a city that contains non-letter characters`() {
        val (valid, message) = InputValidation.isCityValid("New York City123")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("City can only contain letters and spaces.")
    }

    @Test
    fun `isCityValid should return false and the correct error message for a city that is too long`() {
        val (valid, message) = InputValidation.isCityValid("a".repeat(101))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("City cannot be more than 100 characters.")
    }


    // salary Validation
    @Test
    fun `isSalaryValid should return false for blank salary`() {
        val (valid, message) = InputValidation.isSalaryValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Salary is required field.")
    }

    @Test
    fun `isSalaryValid should return false for negative salary`() {
        val (valid, message) = InputValidation.isSalaryValid("-1")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Salary should be positive.")
    }

    @Test
    fun `isSalaryValid should return false for salary less than 100000`() {
        val (valid, message) = InputValidation.isSalaryValid("99999")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Salary should be between 1 lac - 25 lac.")
    }

    @Test
    fun `isSalaryValid should return false for salary more than 2500000`() {
        val (valid, message) = InputValidation.isSalaryValid("2500001")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Salary should be between 1 lac - 25 lac.")
    }

    @Test
    fun `isSalaryValid should return false for non-numeric salary`() {
        val (valid, message) = InputValidation.isSalaryValid("not a number")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Salary should be a number.")
    }

    @Test
    fun `isSalaryValid should return true for valid salary`() {
        val (valid, message) = InputValidation.isSalaryValid("200000")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Job Description Validation
    @Test
    fun `isJobDescriptionValid should return false for blank job description`() {
        val (valid, message) = InputValidation.isJobDescriptionValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Description is required field.")
    }

    @Test
    fun `isJobDescriptionValid should return false for job descriptions less than 100 characters`() {
        val (valid, message) = InputValidation.isJobDescriptionValid("a".repeat(99))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Description should be between 100 to 500 characters.")
    }

    @Test
    fun `isJobDescriptionValid should return false for job descriptions more than 500 characters`() {
        val (valid, message) = InputValidation.isJobDescriptionValid("a".repeat(501))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Job Description should be between 100 to 500 characters.")
    }

    @Test
    fun `isJobDescriptionValid should return true for valid job descriptions`() {
        val (valid, message) = InputValidation.isJobDescriptionValid("a".repeat(101))
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Responsibility Validation
    @Test
    fun `isResponsibilityValid should return false for blank responsibility`() {
        val (valid, message) = InputValidation.isResponsibilityValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Responsibility is required field.")
    }

    @Test
    fun `isResponsibilityValid should return false for responsibility less than 100 characters`() {
        val (valid, message) = InputValidation.isResponsibilityValid("a".repeat(99))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Responsibility should be between 100 to 500 characters.")
    }

    @Test
    fun `isResponsibilityValid should return false for responsibility more than 500 characters`() {
        val (valid, message) = InputValidation.isResponsibilityValid("a".repeat(501))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Responsibility should be between 100 to 500 characters.")
    }

    @Test
    fun `isResponsibilityValid should return true for valid responsibility`() {
        val (valid, message) = InputValidation.isResponsibilityValid("a".repeat(101))
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Skill Set Validation
    @Test
    fun `isSkillSetValid should return false for empty skill set`() {
        val (valid, message) = InputValidation.isSkillSetValid(listOf())
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Skill set should have at least 1 element and should not exceed 20 elements.")
    }

    @Test
    fun `isSkillSetValid should return false for skill sets with more than 20 elements`() {
        val (valid, message) = InputValidation.isSkillSetValid(listOf())
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Skill set should have at least 1 element and should not exceed 20 elements.")
    }

    @Test
    fun `isSkillSetValid should return true for valid skill sets`() {
        val (valid, message) = InputValidation.isSkillSetValid(listOf("skill1", "skill2"))
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    //Mock Title Validation
    @Test
    fun `isMockTitleValid should return false for mock titles more than 100 characters`() {
        val (valid, message) = InputValidation.isMockTitleValid("a".repeat(101))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mock Title should be less than or equal to 100 characters.")
    }

    @Test
    fun `isMockTitleValid should return false for blank mock titles`() {
        val (valid, message) = InputValidation.isMockTitleValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Mock Title is required field.")
    }

    @Test
    fun `isMockTitleValid should return true for valid mock titles`() {
        val (valid, message) = InputValidation.isMockTitleValid("This is a valid mock title.")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Duration Validation
    @Test
    fun `isDurationValid should return false for blank duration`() {
        val (valid, message) = InputValidation.isDurationValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Duration is required field.")
    }

    @Test
    fun `isDurationValid should return false for negative duration`() {
        val (valid, message) = InputValidation.isDurationValid("-1")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Duration should be positive.")
    }

    @Test
    fun `isDurationValid should return false for duration less than 10 minutes`() {
        val (valid, message) = InputValidation.isDurationValid("9")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Duration should be between 10m to 60m.")
    }

    @Test
    fun `isDurationValid should return false for duration more than 60 minutes`() {
        val (valid, message) = InputValidation.isDurationValid("61")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Duration should be between 10m to 60m.")
    }

    @Test
    fun `isDurationValid should return false for non numeric duration`() {
        val (valid, message) = InputValidation.isDurationValid("abc")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Duration should be a number.")
    }

    @Test
    fun `isDurationValid should return true for valid duration`() {
        val (valid, message) = InputValidation.isDurationValid("20")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Question Validation
    @Test
    fun `isQuestionValid should return false for question more than 200 characters`() {
        val (valid, message) = InputValidation.isQuestionValid("a".repeat(201))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Question should be less than or equal to 200 characters.")
    }

    @Test
    fun `isQuestionValid should return false for blank question`() {
        val (valid, message) = InputValidation.isQuestionValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Question is required field.")
    }

    @Test
    fun `isQuestionValid should return true for valid question`() {
        val (valid, message) = InputValidation.isQuestionValid("This is a valid question.")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    @Test
    fun `isOptionsValid should return false for empty options`() {
        val (valid, message) = InputValidation.isOptionsValid(listOf())
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Options is required field.")
    }

    @Test
    fun `isOptionsValid should return false for options more than 200 characters`() {
        val (valid, message) = InputValidation.isOptionsValid(listOf("a".repeat(201), "b".repeat(201), "c".repeat(201), "d".repeat(201)))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Each option in list should be less than or equal to 200 characters.")
    }

    @Test
    fun `isOptionsValid should return false for duplicate options`() {
        val (valid, message) = InputValidation.isOptionsValid(listOf("Option 1", "Option 1", "Option 1", "Option 1"))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Each option in list should be unique.")
    }

    @Test
    fun `isOptionsValid should return true for valid options`() {
        val (valid, message) = InputValidation.isOptionsValid(listOf("Option 1", "Option 2", "Option 3", "Option 4"))
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    @Test
    fun `isFeedbackValid should return false for blank feedback`() {
        val (valid, message) = InputValidation.isFeedbackValid("")
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Feedback is required field.")
    }

    @Test
    fun `isFeedbackValid should return false for feedback more than 200 characters`() {
        val (valid, message) = InputValidation.isFeedbackValid("a".repeat(201))
        assertThat(valid).isFalse()
        assertThat(message).isEqualTo("Feedback should be less than or equal to 200 characters.")
    }

    @Test
    fun `isFeedbackValid should return true for valid feedback`() {
        val (valid, message) = InputValidation.isFeedbackValid("This is a valid feedback.")
        assertThat(valid).isTrue()
        assertThat(message).isEqualTo("")
    }

    // Notification Validation
    @Test
    fun `isNotificationTitleValid should return false for empty title`() {
        val (isValid, errorMessage) = InputValidation.isNotificationTitleValid("")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Title is required field.")
    }

    @Test
    fun `isNotificationTitleValid should return false for title with length more than 100`() {
        val (isValid, errorMessage) = InputValidation.isNotificationTitleValid("a".repeat(101))
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Title should be less than or equal to 100 characters.")
    }

    @Test
    fun `isNotificationTitleValid should return true for title with valid length`() {
        val (isValid, errorMessage) = InputValidation.isNotificationTitleValid("a".repeat(50))
        assertThat(isValid).isTrue()
        assertThat(errorMessage).isEqualTo("")
    }

    @Test
    fun `isNotificationBodyValid should return false for empty body`() {
        val (isValid, errorMessage) = InputValidation.isNotificationBodyValid("")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Body is required field.")
    }

    @Test
    fun `isNotificationBodyValid should return false for body with length more than 200`() {
        val (isValid, errorMessage) = InputValidation.isNotificationBodyValid("a".repeat(201))
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Body should be less than or equal to 200 characters.")
    }

    @Test
    fun `isNotificationBodyValid should return true for body with valid length`() {
        val (isValid, errorMessage) = InputValidation.isNotificationBodyValid("a".repeat(100))
        assertThat(isValid).isTrue()
        assertThat(errorMessage).isEqualTo("")
    }
}