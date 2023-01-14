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
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("+919876543210")
        assertThat(isValid).isTrue()
        assertThat(errorMessage).isEmpty()
    }

    @Test
    fun `isMobileNumberValid should return false for empty mobile number`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Mobile number cannot be empty.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number not starting with +91`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("919876543210")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Mobile number must start with +91.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number not being 13 characters long`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("+9198765432")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Mobile number must be 13 characters.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number containing non-digit characters`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("+919876543a01")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Mobile number can only contain digits.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number starting with 000`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("+910007654321")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Mobile number cannot start with 000.")
    }

    @Test
    fun `isMobileNumberValid should return false for mobile number with all digits same`() {
        val (isValid, errorMessage) = InputValidation.isMobileNumberValid("+911111111111")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("All the digits in mobile number cannot be same.")
    }

    // DOB Validation
    @Test
    fun `isDOBValid should return true for valid dob`() {
        val (isValid, errorMessage) = InputValidation.isDOBValid("2003-01-01")
        assertThat(isValid).isTrue()
        assertThat(errorMessage).isEmpty()
    }

    @Test
    fun `isDOBValid should return false for empty dob`() {
        val (isValid, errorMessage) = InputValidation.isDOBValid("")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Date of Birth cannot be empty.")
    }

    @Test
    fun `isDOBValid should return false for dob with age less than 18`() {
        val (isValid, errorMessage) = InputValidation.isDOBValid("2022-01-01")
        assertThat(isValid).isFalse()
        assertThat(errorMessage).isEqualTo("Age must be 18 years or older.")
    }

    // Stream Validation
    @Test
    fun `isStreamValid should return false for streams longer than 100 characters`() {
        val result = InputValidation.isStreamValid("n".repeat(101))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Length should be less than or equal to 100 characters.")
    }

    @Test
    fun `isStreamValid should return false for streams that start with a number`() {
        val result = InputValidation.isStreamValid("1This is a stream that starts with a number.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Stream cannot start with a number.")
    }

    @Test
    fun `isStreamValid should return false for streams that contain special characters`() {
        val result = InputValidation.isStreamValid("This is a stream! with special characters.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Special characters are not allowed.")
    }

    @Test
    fun `isStreamValid should return false for blank streams`() {
        val result = InputValidation.isStreamValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Stream should not be blank.")
    }

    @Test
    fun `isStreamValid should return true for valid streams`() {
        val result = InputValidation.isStreamValid("This is a valid stream")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }


    // Experience
    @Test
    fun `isExperienceValid should return false for negative experiences`() {
        val result = InputValidation.isExperienceValid("-1")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Experience should not be negative.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that start with leading zero`() {
        val result = InputValidation.isExperienceValid("01")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Experience should not start with leading zero.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that are more than 2 digits`() {
        val result = InputValidation.isExperienceValid("100")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Experience should not be more than 2 digits.")
    }

    @Test
    fun `isExperienceValid should return false for experiences that are not in decimal`() {
        val result = InputValidation.isExperienceValid("not a number")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Experience should be in decimal.")
    }

    @Test
    fun `isExperienceValid should return false for blank experiences`() {
        val result = InputValidation.isExperienceValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Experience should not be blank.")
    }

    @Test
    fun `isExperienceValid should return true for valid experiences`() {
        val result = InputValidation.isExperienceValid("2.5")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }


    // Bio Validation
    @Test
    fun `isBiographyValid should return false for blank biographies`() {
        val result = InputValidation.isBiographyValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Biography should not be blank.")
    }

    @Test
    fun `isBiographyValid should return false for biographies longer than 200 characters`() {
        val result = InputValidation.isBiographyValid("n".repeat(201))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Biography should be less than or equal to 200 characters.")
    }

    @Test
    fun `isBiographyValid should return true for valid biographies`() {
        val result = InputValidation.isBiographyValid("This is a valid biography.")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Job Title Validation
    @Test
    fun `isJobTitleValid should return false for job titles longer than 100 characters`() {
        val result = InputValidation.isJobTitleValid("This is a very long job title that should be more than 100 characters in length to test the validation function.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Title should be less than or equal to 100 characters.")
    }

    @Test
    fun `isJobTitleValid should return false for job titles that contain special characters`() {
        val result = InputValidation.isJobTitleValid("This is a job title! with special characters.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Special characters are not allowed in Job Title.")
    }

    @Test
    fun `isJobTitleValid should return false for job titles that start with number`() {
        val result = InputValidation.isJobTitleValid("1This is a job title.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Title should not start with number.")
    }

    @Test
    fun `isJobTitleValid should return false for blank job titles`() {
        val result = InputValidation.isJobTitleValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Title is required field.")
    }

    @Test
    fun `isJobTitleValid should return true for valid job titles`() {
        val result = InputValidation.isJobTitleValid("This is a valid job title")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Company Name Validation
    @Test
    fun `isCompanyNameValid should return false for company names longer than 100 characters`() {
        val result = InputValidation.isCompanyNameValid("a".repeat(101))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Company name should be less than or equal to 100 characters.")
    }

    @Test
    fun `isCompanyNameValid should return false for company names that contain special characters`() {
        val result = InputValidation.isCompanyNameValid("This is a company name! with special characters.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Special characters are not allowed in Company name.")
    }

    @Test
    fun `isCompanyNameValid should return false for company names that start with number`() {
        val result = InputValidation.isCompanyNameValid("1This is a company name.")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Company name should not start with number.")
    }

    @Test
    fun `isCompanyNameValid should return false for blank company names`() {
        val result = InputValidation.isCompanyNameValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Company name is required field.")
    }

    @Test
    fun `isCompanyNameValid should return true for valid company names`() {
        val result = InputValidation.isCompanyNameValid("This is a valid company name")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // City Validation
    @Test
    fun `isCityValid should return true and an empty string for a valid city`() {
        val (isValid, message) = InputValidation.isCityValid("New York City")
        assertThat(isValid).isTrue()
        assertThat(message).isEmpty()
    }

    @Test
    fun `isCityValid should return false and the correct error message for an empty city`() {
        val (isValid, message) = InputValidation.isCityValid("")
        assertThat(isValid).isFalse()
        assertThat(message).isEqualTo("City cannot be empty.")
    }

    @Test
    fun `isCityValid should return false and the correct error message for a city that contains non-letter characters`() {
        val (isValid, message) = InputValidation.isCityValid("New York City123")
        assertThat(isValid).isFalse()
        assertThat(message).isEqualTo("City can only contain letters and spaces.")
    }

    @Test
    fun `isCityValid should return false and the correct error message for a city that is too long`() {
        val (isValid, message) = InputValidation.isCityValid("a".repeat(101))
        assertThat(isValid).isFalse()
        assertThat(message).isEqualTo("City cannot be more than 100 characters.")
    }


    // salary Validation
    @Test
    fun `isSalaryValid should return false for blank salary`() {
        val result = InputValidation.isSalaryValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Salary is required field.")
    }

    @Test
    fun `isSalaryValid should return false for negative salary`() {
        val result = InputValidation.isSalaryValid("-1")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Salary should be positive.")
    }

    @Test
    fun `isSalaryValid should return false for salary less than 100000`() {
        val result = InputValidation.isSalaryValid("99999")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Salary should be between 1 lac - 25 lac.")
    }

    @Test
    fun `isSalaryValid should return false for salary more than 2500000`() {
        val result = InputValidation.isSalaryValid("2500001")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Salary should be between 1 lac - 25 lac.")
    }

    @Test
    fun `isSalaryValid should return false for non-numeric salary`() {
        val result = InputValidation.isSalaryValid("not a number")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Salary should be a number.")
    }

    fun `isSalaryValid should return true for valid salary`() {
        val result = InputValidation.isSalaryValid("200000")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Job Description Validation

    @Test
    fun `isJobDescriptionValid should return false for blank job description`() {
        val result = InputValidation.isJobDescriptionValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Description is required field.")
    }

    @Test
    fun `isJobDescriptionValid should return false for job descriptions less than 100 characters`() {
        val result = InputValidation.isJobDescriptionValid("a".repeat(99))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Description should be between 100 to 500 characters.")
    }

    @Test
    fun `isJobDescriptionValid should return false for job descriptions more than 500 characters`() {
        val result = InputValidation.isJobDescriptionValid("a".repeat(501))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Job Description should be between 100 to 500 characters.")
    }

    @Test
    fun `isJobDescriptionValid should return true for valid job descriptions`() {
        val result = InputValidation.isJobDescriptionValid("a".repeat(101))
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Responsibility Validation
    @Test
    fun `isResponsibilityValid should return false for blank responsibility`() {
        val result = InputValidation.isResponsibilityValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Responsibility is required field.")
    }

    @Test
    fun `isResponsibilityValid should return false for responsibility less than 100 characters`() {
        val result = InputValidation.isResponsibilityValid("a".repeat(99))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Responsibility should be between 100 to 500 characters.")
    }

    @Test
    fun `isResponsibilityValid should return false for responsibility more than 500 characters`() {
        val result = InputValidation.isResponsibilityValid("a".repeat(501))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Responsibility should be between 100 to 500 characters.")
    }

    @Test
    fun `isResponsibilityValid should return true for valid responsibility`() {
        val result = InputValidation.isResponsibilityValid("a".repeat(101))
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Skill Set Validation
    @Test
    fun `isSkillSetValid should return false for empty skill set`() {
        val result = InputValidation.isSkillSetValid(listOf())
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Skill set should have at least 1 element and should not exceed 20 elements.")
    }

    @Test
    fun `isSkillSetValid should return false for skill sets with more than 20 elements`() {
        val result = InputValidation.isSkillSetValid(listOf())
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Skill set should have at least 1 element and should not exceed 20 elements.")
    }

    @Test
    fun `isSkillSetValid should return true for valid skill sets`() {
        val result = InputValidation.isSkillSetValid(listOf("skill1", "skill2"))
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    //Mock Title Validation
    @Test
    fun `isMockTitleValid should return false for mock titles more than 100 characters`() {
        val result = InputValidation.isMockTitleValid("a".repeat(101))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Mock Title should be less than or equal to 100 characters.")
    }

    @Test
    fun `isMockTitleValid should return false for blank mock titles`() {
        val result = InputValidation.isMockTitleValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Mock Title is required field.")
    }

    @Test
    fun `isMockTitleValid should return true for valid mock titles`() {
        val result = InputValidation.isMockTitleValid("This is a valid mock title.")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Duration Validation
    @Test
    fun `isDurationValid should return false for blank duration`() {
        val result = InputValidation.isDurationValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Duration is required field.")
    }

    @Test
    fun `isDurationValid should return false for negative duration`() {
        val result = InputValidation.isDurationValid("-1")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Duration should be positive.")
    }

    @Test
    fun `isDurationValid should return false for duration less than 10 minutes`() {
        val result = InputValidation.isDurationValid("9")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Duration should be between 10m to 60m.")
    }

    @Test
    fun `isDurationValid should return false for duration more than 60 minutes`() {
        val result = InputValidation.isDurationValid("61")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Duration should be between 10m to 60m.")
    }

    @Test
    fun `isDurationValid should return false for non numeric duration`() {
        val result = InputValidation.isDurationValid("abc")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Duration should be a number.")
    }

    @Test
    fun `isDurationValid should return true for valid duration`() {
        val result = InputValidation.isDurationValid("20")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    // Question Validation
    @Test
    fun `isQuestionValid should return false for question more than 200 characters`() {
        val result = InputValidation.isQuestionValid("a".repeat(201))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Question should be less than or equal to 200 characters.")
    }

    @Test
    fun `isQuestionValid should return false for blank question`() {
        val result = InputValidation.isQuestionValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Question is required field.")
    }

    @Test
    fun `isQuestionValid should return true for valid question`() {
        val result = InputValidation.isQuestionValid("This is a valid question.")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    @Test
    fun `isOptionsValid should return false for empty options`() {
        val result = InputValidation.isOptionsValid(listOf())
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Options is required field.")
    }

    @Test
    fun `isOptionsValid should return false for options more than 200 characters`() {
        val result = InputValidation.isOptionsValid(listOf("a".repeat(201)))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Each option in list should be less than or equal to 200 characters.")
    }

    @Test
    fun `isOptionsValid should return false for duplicate options`() {
        val result = InputValidation.isOptionsValid(listOf("Option 1", "Option 1"))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Each option in list should be unique.")
    }

    @Test
    fun `isOptionsValid should return true for valid options`() {
        val result = InputValidation.isOptionsValid(listOf("Option 1", "Option 2"))
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }

    @Test
    fun `isFeedbackValid should return false for blank feedback`() {
        val result = InputValidation.isFeedbackValid("")
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Feedback is required field.")
    }

    @Test
    fun `isFeedbackValid should return false for feedback more than 200 characters`() {
        val result = InputValidation.isFeedbackValid("a".repeat(201))
        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("Feedback should be less than or equal to 200 characters.")
    }

    @Test
    fun `isFeedbackValid should return true for valid feedback`() {
        val result = InputValidation.isFeedbackValid("This is a valid feedback.")
        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("")
    }
}