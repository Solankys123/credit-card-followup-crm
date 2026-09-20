package com.solankys123.creditcardcrm

data class CustomerRecord(
    val name: String,
    val inquiryType: String,
    val status: String,
    val priority: String,
    val nextAction: String,
    val phone: String = "",
    val applicationNumber: String = "",
    val pendingReason: String = "",
    val lastContact: String = "",
    val note: String = ""
)

data class FollowUp(
    val customerName: String,
    val reason: String,
    val dueAt: String,
    val priority: String,
    val completed: Boolean = false
)

object SampleData {
    val customers = listOf(
        CustomerRecord(
            name = "Rahul Sharma",
            inquiryType = "Long Inquiry",
            status = "Documents Pending",
            priority = "HIGH",
            nextAction = "Call today 4:00 PM",
            phone = "98XXXXXX10",
            applicationNumber = "APP-1001",
            pendingReason = "Salary slip pending",
            lastContact = "Today 10:30 AM",
            note = "Customer promised document today"
        ),
        CustomerRecord(
            name = "Amit Verma",
            inquiryType = "Short Inquiry",
            status = "Application Started",
            priority = "MEDIUM",
            nextAction = "Follow up today",
            phone = "97XXXXXX21",
            applicationNumber = "APP-1002",
            lastContact = "Yesterday 5:20 PM",
            note = "Customer asked for callback"
        ),
        CustomerRecord(
            name = "Neha Singh",
            inquiryType = "Long Inquiry",
            status = "Verification",
            priority = "MEDIUM",
            nextAction = "Check status",
            phone = "96XXXXXX32",
            applicationNumber = "APP-1003",
            lastContact = "Yesterday 11:00 AM",
            note = "YONO available"
        )
    )

    val followUps = listOf(
        FollowUp("Rahul Sharma", "Salary slip pending", "Today, 4:00 PM", "HIGH"),
        FollowUp("Amit Verma", "Callback requested", "Today, 5:00 PM", "MEDIUM"),
        FollowUp("Neha Singh", "Verification status", "Tomorrow, 11:00 AM", "MEDIUM")
    )
}
