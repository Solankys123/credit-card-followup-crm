package com.solankys123.creditcardcrm

class CrmRepository(private val db: CrmDatabase) {
    suspend fun customers(): List<CustomerRecord> = db.customerDao().getAll().map {
        CustomerRecord(
            id = it.id,
            name = it.name,
            inquiryType = it.inquiryType,
            status = it.status,
            priority = it.priority,
            nextAction = it.nextAction,
            phone = it.phone,
            applicationNumber = it.applicationNumber,
            pendingReason = it.pendingReason,
            lastContact = it.lastContact,
            note = it.note
        )
    }

    suspend fun followUps(): List<FollowUp> = db.followUpDao().getAll().map {
        FollowUp(it.customerName,it.reason,it.dueAt,it.priority,it.completed)
    }

    suspend fun seedIfEmpty() {
        if (db.customerDao().count() == 0) {
            SampleData.customers.forEach {
                db.customerDao().insert(CustomerEntity(
                    name=it.name,inquiryType=it.inquiryType,status=it.status,priority=it.priority,
                    nextAction=it.nextAction,phone=it.phone,applicationNumber=it.applicationNumber,
                    pendingReason=it.pendingReason,lastContact=it.lastContact,note=it.note
                ))
            }
            SampleData.followUps.forEach {
                db.followUpDao().insert(FollowUpEntity(
                    customerName=it.customerName,reason=it.reason,dueAt=it.dueAt,
                    priority=it.priority,completed=it.completed
                ))
            }
        }
    }

    suspend fun addCustomer(customer: CustomerRecord) {
        db.customerDao().insert(CustomerEntity(
            name=customer.name.trim(),
            inquiryType=customer.inquiryType,
            status=customer.status,
            priority=customer.priority,
            nextAction=customer.nextAction,
            phone=customer.phone,
            applicationNumber=customer.applicationNumber,
            pendingReason=customer.pendingReason,
            lastContact=customer.lastContact,
            note=customer.note
        ))
    }

    suspend fun updateCustomer(customer: CustomerRecord) {
        db.customerDao().update(CustomerEntity(
            id=customer.id,
            name=customer.name.trim(),
            inquiryType=customer.inquiryType,
            status=customer.status,
            priority=customer.priority,
            nextAction=customer.nextAction,
            phone=customer.phone,
            applicationNumber=customer.applicationNumber,
            pendingReason=customer.pendingReason,
            lastContact=customer.lastContact,
            note=customer.note
        ))
    }

    suspend fun deleteCustomer(customer: CustomerRecord) {
        db.followUpDao().getAll()
            .filter { it.customerName == customer.name }
            .forEach { db.followUpDao().delete(it) }
        db.customerDao().delete(CustomerEntity(
            id=customer.id,
            name=customer.name,
            inquiryType=customer.inquiryType,
            status=customer.status,
            priority=customer.priority,
            nextAction=customer.nextAction,
            phone=customer.phone,
            applicationNumber=customer.applicationNumber,
            pendingReason=customer.pendingReason,
            lastContact=customer.lastContact,
            note=customer.note
        ))
    }
}
