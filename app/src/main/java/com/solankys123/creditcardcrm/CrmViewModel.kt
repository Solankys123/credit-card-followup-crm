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

    fun addCustomer(customer: CustomerRecord) = viewModelScope.launch {
        if (customer.name.isBlank()) return@launch
        repository.addCustomer(customer)
        refresh()
    }

    fun updateCustomer(customer: CustomerRecord) = viewModelScope.launch {
        if (customer.id == 0L || customer.name.isBlank()) return@launch
        repository.updateCustomer(customer)
        refresh()
    }

    fun deleteCustomer(customer: CustomerRecord) = viewModelScope.launch {
        if (customer.id == 0L) return@launch
        repository.deleteCustomer(customer)
        refresh()
    }
}
