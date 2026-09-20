package com.solankys123.creditcardcrm

class CrmRepository(private val db: CrmDatabase) {
    suspend fun customers(): List<CustomerRecord> = db.customerDao().getAll().map {
        CustomerRecord(it.name,it.inquiryType,it.status,it.priority,it.nextAction,it.phone,it.applicationNumber,it.pendingReason,it.lastContact,it.note)
    }

    suspend fun followUps(): List<FollowUp> = db.followUpDao().getAll().map {
        FollowUp(it.customerName,it.reason,it.dueAt,it.priority,it.completed)
    }

    suspend fun seedIfEmpty() {
        if (db.customerDao().count() == 0) {
            SampleData.customers.forEach {
                db.customerDao().insert(CustomerEntity(name=it.name,inquiryType=it.inquiryType,status=it.status,priority=it.priority,nextAction=it.nextAction,phone=it.phone,applicationNumber=it.applicationNumber,pendingReason=it.pendingReason,lastContact=it.lastContact,note=it.note))
            }
            SampleData.followUps.forEach {
                db.followUpDao().insert(FollowUpEntity(customerName=it.customerName,reason=it.reason,dueAt=it.dueAt,priority=it.priority,completed=it.completed))
            }
        }
    }

    suspend fun addCustomer(name: String, inquiry: String) {
        db.customerDao().insert(CustomerEntity(name=name.trim(),inquiryType=inquiry,status="New Inquiry",priority="MEDIUM",nextAction="Contact customer"))
    }
}
