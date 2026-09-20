package com.solankys123.creditcardcrm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CrmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = CrmDatabase.getInstance(context)
        return CrmViewModel(CrmRepository(db)) as T
    }
}
