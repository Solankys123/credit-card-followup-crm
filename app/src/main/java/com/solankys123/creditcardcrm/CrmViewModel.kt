package com.solankys123.creditcardcrm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrmViewModel(private val repository: CrmRepository) : ViewModel() {
    private val _customers = MutableStateFlow<List<CustomerRecord>>(emptyList())
    val customers: StateFlow<List<CustomerRecord>> = _customers.asStateFlow()

    private val _followUps = MutableStateFlow<List<FollowUp>>(emptyList())
    val followUps: StateFlow<List<FollowUp>> = _followUps.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        repository.seedIfEmpty()
        _customers.value = repository.customers()
        _followUps.value = repository.followUps()
    }

    fun addCustomer(name: String, inquiry: String) = viewModelScope.launch {
        if (name.isBlank()) return@launch
        repository.addCustomer(name, inquiry)
        refresh()
    }
}
