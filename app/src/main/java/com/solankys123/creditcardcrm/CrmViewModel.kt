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
    private val _activities = MutableStateFlow<List<ActivityEvent>>(emptyList())
    private val _documents = MutableStateFlow<List<DocumentItem>>(emptyList())
    private val _issues = MutableStateFlow<List<IssueItem>>(emptyList())
    val documents: StateFlow<List<DocumentItem>> = _documents.asStateFlow()
    val issues: StateFlow<List<IssueItem>> = _issues.asStateFlow()
    val activities: StateFlow<List<ActivityEvent>> = _activities.asStateFlow()
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


    fun loadDocumentsAndIssues(customerId: Long) = viewModelScope.launch {
        _documents.value = repository.documents(customerId)
        _issues.value = repository.issues(customerId)
    }

    fun addDocument(item: DocumentItem) = viewModelScope.launch {
        repository.addDocument(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun updateDocument(item: DocumentItem) = viewModelScope.launch {
        repository.updateDocument(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun deleteDocument(item: DocumentItem) = viewModelScope.launch {
        repository.deleteDocument(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun addIssue(item: IssueItem) = viewModelScope.launch {
        repository.addIssue(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun updateIssue(item: IssueItem) = viewModelScope.launch {
        repository.updateIssue(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun deleteIssue(item: IssueItem) = viewModelScope.launch {
        repository.deleteIssue(item)
        loadDocumentsAndIssues(item.customerId)
    }

    fun loadActivities(customerId: Long) = viewModelScope.launch {
        _activities.value = repository.activities(customerId)
    }

    fun logActivity(event: ActivityEvent) = viewModelScope.launch {
        repository.addActivity(event)
        loadActivities(event.customerId)
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
