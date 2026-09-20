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
        if (!isValidCustomer(customer)) return@launch
        repository.addCustomer(customer)
        refresh()
    }

    private fun isValidCustomer(customer: CustomerRecord): Boolean {
        val nameOk = customer.name.trim().matches(Regex("^[A-Za-z .'-]{2,80}$"))
        val phone = customer.phone.trim()
        val phoneOk = phone.isBlank() || phone.matches(Regex("^[6-9][0-9]{9}$")) || phone.matches(Regex("^[0-9Xx]{6,15}$"))
        val priorityOk = customer.priority.uppercase() in setOf("HIGH", "MEDIUM", "LOW")
        return nameOk && phoneOk && priorityOk
    }

    fun updateCustomer(customer: CustomerRecord) = viewModelScope.launch {
        if (customer.id == 0L || !isValidCustomer(customer)) return@launch
        repository.updateCustomer(customer)
        refresh()
    }


    fun addFollowUp(followUp: FollowUp) = viewModelScope.launch {
        if (followUp.customerName.isBlank() || followUp.reason.isBlank() || followUp.dueAt.isBlank()) return@launch
        repository.addFollowUp(followUp)
        refresh()
    }

    fun completeFollowUp(followUp: FollowUp) = viewModelScope.launch {
        repository.completeFollowUp(followUp)
        refresh()
    }

    fun deleteFollowUp(followUp: FollowUp) = viewModelScope.launch {
        repository.deleteFollowUp(followUp)
        refresh()
    }

    fun deleteCustomer(customer: CustomerRecord) = viewModelScope.launch {
        if (customer.id == 0L) return@launch
        repository.deleteCustomer(customer)
        refresh()
    }
}
