package com.solankys123.creditcardcrm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DocumentStatus(
    val name: String,
    val received: Boolean,
    val issue: String = ""
)

data class CustomerIssue(
    val title: String,
    val status: String,
    val detail: String
)

data class TimelineEvent(
    val date: String,
    val title: String,
    val detail: String
)

data class CustomerDetail(
    val base: CustomerRecord,
    val category: String,
    val yonoAvailable: Boolean,
    val documents: List<DocumentStatus>,
    val issues: List<CustomerIssue>,
    val timeline: List<TimelineEvent>
)

object CustomerDetailFixtures {
    fun forCustomer(customer: CustomerRecord): CustomerDetail {
        return when (customer.name) {
            "Aarav Mehta" -> CustomerDetail(
                base = customer,
                category = "Gold",
                yonoAvailable = false,
                documents = listOf(
                    DocumentStatus("PAN", true),
                    DocumentStatus("Address Proof", true),
                    DocumentStatus("Salary Slip", false, "Customer promised today")
                ),
                issues = listOf(
                    CustomerIssue("Document Pending", "OPEN", "Salary slip not yet received")
                ),
                timeline = listOf(
                    TimelineEvent("20 Sep 2026 10:30", "Called customer", "Salary slip requested"),
                    TimelineEvent("20 Sep 2026 10:35", "Follow-up created", "Today at 4:00 PM"),
                    TimelineEvent("19 Sep 2026", "Application submitted", "Reference DEMO-24001")
                )
            )
            "Vikram Singh" -> CustomerDetail(
                base = customer,
                category = "Silver",
                yonoAvailable = false,
                documents = listOf(
                    DocumentStatus("PAN", true),
                    DocumentStatus("Address Proof", false, "Clarification required")
                ),
                issues = listOf(
                    CustomerIssue("Existing Card", "OPEN", "Previous card reported blocked")
                ),
                timeline = listOf(
                    TimelineEvent("19 Sep 2026 17:45", "Customer contacted", "Existing blocked card discussed"),
                    TimelineEvent("19 Sep 2026 18:00", "Follow-up created", "Re-follow up in 7 days")
                )
            )
            else -> CustomerDetail(
                base = customer,
                category = "Standard",
                yonoAvailable = true,
                documents = listOf(
                    DocumentStatus("PAN", true),
                    DocumentStatus("Address Proof", true),
                    DocumentStatus("Income Proof", true)
                ),
                issues = emptyList(),
                timeline = listOf(
                    TimelineEvent(customer.lastContact.ifBlank { "Recent" }, "Customer added", customer.note)
                )
            )
        }
    }
}

@Composable
fun CustomerDetailScreen(
    customer: CustomerRecord,
    onBack: () -> Unit,
    onEdit: (CustomerRecord) -> Unit,
    onDelete: () -> Unit,
    onOpenFollowUps: () -> Unit,
    activities: List<ActivityEvent> = emptyList(),
    onLogActivity: (ActivityEvent) -> Unit = {}
) {
    val detail = CustomerDetailFixtures.forCustomer(customer)

    fun logActivity(type: String, detailText: String) {
        onLogActivity(ActivityEvent(0, customer.id, customer.name, type, detailText, java.time.LocalDateTime.now().toString()))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail.base.name) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(detail.base.inquiryType, style = MaterialTheme.typography.labelLarge)
                        Text(detail.base.status, style = MaterialTheme.typography.titleLarge)
                        Text("Priority: " + detail.base.priority)
                        Text("Category: " + detail.category)
                        Text("YONO: " + if (detail.yonoAvailable) "Available" else "Not available")
                        if (detail.base.applicationNumber.isNotBlank()) {
                            Text("Application: " + detail.base.applicationNumber)
                        }
                    }
                }
            }

            item {
                Text("Next Action", style = MaterialTheme.typography.titleMedium)
                Text(detail.base.nextAction)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        logActivity("CALL", "Call initiated")
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customer.phone))
                        // The screen remains safe if no dialer is available.
                    }) { Text("Call") }
                    OutlinedButton(onClick = { onEdit(customer) }) { Text("Edit") }
                    OutlinedButton(onClick = onDelete) { Text("Delete") }
                    OutlinedButton(onClick = onOpenFollowUps) { Text("Follow-up") }
                }
            }

            item {
                Text("Contact", style = MaterialTheme.typography.titleMedium)
                Text("Phone: " + detail.base.phone)
                Text("Last contact: " + detail.base.lastContact)
            }

            item {
                Text("Documents", style = MaterialTheme.typography.titleMedium)
            }

            items(detail.documents) { document ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text((if (document.received) "✓ " else "○ ") + document.name)
                        if (document.issue.isNotBlank()) {
                            Text("Issue: " + document.issue)
                        }
                    }
                }
            }

            item {
                Text("Issues", style = MaterialTheme.typography.titleMedium)
            }

            if (detail.issues.isEmpty()) {
                item { Text("No open issues") }
            } else {
                items(detail.issues) { issue ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(issue.title, style = MaterialTheme.typography.titleMedium)
                            Text("Status: " + issue.status)
                            Text(issue.detail)
                        }
                    }
                }
            }

            item {
                Text("Activity Timeline", style = MaterialTheme.typography.titleMedium)
            }

            items(activities) { event ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(event.date, style = MaterialTheme.typography.labelMedium)
                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                        Text(event.detail)
                    }
                }
            }
        }
    }
}
