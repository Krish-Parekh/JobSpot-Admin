package com.example.jobspotadmin.home.fragment.studentFragment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Student
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _students : MutableLiveData<List<Student>> = MutableLiveData(mutableListOf())
    val students : LiveData<List<Student>> = _students

    fun fetchStudents(){
        viewModelScope.launch {
            mFirestore.collection(COLLECTION_PATH_STUDENT)
                .addSnapshotListener { value, error ->

                    if (error != null){
                        return@addSnapshotListener
                    }
                    val documents = value?.documents!!
                    val studentList = documents.map {
                        it.toObject(Student::class.java)!!
                    }
                    _students.postValue(studentList)
                }
        }
    }
}