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
import androidx.compose.ui.platform.LocalContext
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
    documents: List<DocumentItem> = emptyList(),
    issues: List<IssueItem> = emptyList(),
    onLogActivity: (ActivityEvent) -> Unit = {},
    onAddDocument: (DocumentItem) -> Unit = {},
    onAddIssue: (IssueItem) -> Unit = {}
) {
    val detail = CustomerDetailFixtures.forCustomer(customer)
    val context = LocalContext.current

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
                        runCatching { context.startActivity(intent) }
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
                Spacer(Modifier.height(6.dp))
                if (documents.isEmpty()) Text("No documents tracked yet")
                else documents.forEach { document ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(document.type, style = MaterialTheme.typography.titleMedium)
                            Text("Status: " + document.status)
                            if (document.note.isNotBlank()) Text(document.note)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = {
                    onAddDocument(DocumentItem(customerId=customer.id, type="Document", status="PENDING"))
                }) { Text("Add document") }
            }

            item {
                Text("Issues", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                if (issues.isEmpty()) Text("No issues tracked")
                else issues.forEach { issue ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(issue.title, style = MaterialTheme.typography.titleMedium)
                            Text("Status: " + issue.status)
                            if (issue.note.isNotBlank()) Text(issue.note)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = {
                    onAddIssue(IssueItem(customerId=customer.id, title="New issue", status="OPEN"))
                }) { Text("Add issue") }
            }

            item {
                Text("Activity Timeline", style = MaterialTheme.typography.titleMedium)
            }

            items(activities) { event ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(event.createdAt, style = MaterialTheme.typography.labelMedium)
                        Text(event.type, style = MaterialTheme.typography.titleMedium)
                        Text(event.detail)
                    }
                }
            }
        }
    }
}
