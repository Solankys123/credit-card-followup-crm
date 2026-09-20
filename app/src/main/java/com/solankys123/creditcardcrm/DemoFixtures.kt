package com.solankys123.creditcardcrm

/**
 * Synthetic QA fixtures only.
 * Never use real customer data in source control.
 */
object DemoFixtures {
    val customers = listOf(
        CustomerRecord(
            name = "Aarav Mehta",
            inquiryType = "Long Inquiry",
            status = "Documents Pending",
            priority = "HIGH",
            nextAction = "Call today 4:00 PM",
            phone = "98XXXXXX41",
            applicationNumber = "DEMO-24001",
            pendingReason = "Salary slip pending",
            lastContact = "20 Sep 2026 10:30 AM",
            note = "Lead source: WhatsApp. YONO not available. Customer promised salary slip today."
        ),
        CustomerRecord(
            name = "Priya Nair",
            inquiryType = "Short Inquiry",
            status = "Application Started",
            priority = "MEDIUM",
            nextAction = "Follow up tomorrow 11:00 AM",
            phone = "97XXXXXX52",
            applicationNumber = "DEMO-24002",
            lastContact = "20 Sep 2026 12:15 PM",
            note = "Lead source: Direct. YONO available. Customer requested callback."
        ),
        CustomerRecord(
            name = "Vikram Singh",
            inquiryType = "Long Inquiry",
            status = "On Hold",
            priority = "HIGH",
            nextAction = "Re-follow up in 7 days",
            phone = "96XXXXXX63",
            applicationNumber = "DEMO-24003",
            pendingReason = "Existing blocked card needs clarification",
            lastContact = "19 Sep 2026 05:45 PM",
            note = "Lead source: WhatsApp. Existing SBI Card reported blocked."
        ),
        CustomerRecord(
            name = "Simran Kaur",
            inquiryType = "Long Inquiry",
            status = "Documents Submitted",
            priority = "MEDIUM",
            nextAction = "Check verification tomorrow",
            phone = "95XXXXXX74",
            applicationNumber = "DEMO-24004",
            lastContact = "20 Sep 2026 09:20 AM",
            note = "Lead source: Referral. Documents received. Verification pending."
        ),
        CustomerRecord(
            name = "Rohan Gupta",
            inquiryType = "Short Inquiry",
            status = "Not Interested",
            priority = "LOW",
            nextAction = "Re-follow up after 30 days",
            phone = "94XXXXXX85",
            applicationNumber = "",
            lastContact = "18 Sep 2026 04:10 PM",
            note = "Lead source: Direct. Customer asked to reconnect next month."
        ),
        CustomerRecord(
            name = "Kavya Sharma",
            inquiryType = "Long Inquiry",
            status = "Rejected",
            priority = "LOW",
            nextAction = "Record loss reason",
            phone = "93XXXXXX96",
            applicationNumber = "DEMO-24006",
            pendingReason = "Very poor CIBIL",
            lastContact = "17 Sep 2026 01:30 PM",
            note = "Lead source: WhatsApp. FD-based option may be discussed only where appropriate and permitted."
        ),
        CustomerRecord(
            name = "Aditya Joshi",
            inquiryType = "Short Inquiry",
            status = "Approved",
            priority = "MEDIUM",
            nextAction = "Track dispatch",
            phone = "92XXXXXX07",
            applicationNumber = "DEMO-24007",
            lastContact = "20 Sep 2026 10:05 AM",
            note = "Lead source: Referral. Potential family add-on opportunity noted."
        )
    )

    val followUps = listOf(
        FollowUp("Aarav Mehta", "Salary slip pending", "Today, 4:00 PM", "HIGH"),
        FollowUp("Priya Nair", "Callback requested", "Tomorrow, 11:00 AM", "MEDIUM"),
        FollowUp("Vikram Singh", "Existing blocked card clarification", "27 Sep, 11:00 AM", "HIGH"),
        FollowUp("Simran Kaur", "Verification status", "Tomorrow, 10:00 AM", "MEDIUM"),
        FollowUp("Rohan Gupta", "Customer requested later follow-up", "18 Oct, 5:00 PM", "LOW")
    )
}
