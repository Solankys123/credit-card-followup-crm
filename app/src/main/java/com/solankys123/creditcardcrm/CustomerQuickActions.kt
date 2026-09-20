package com.solankys123.creditcardcrm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApplicationSummaryCard(customer: CustomerRecord) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("Application", style = MaterialTheme.typography.titleMedium)
            if (customer.applicationNumber.isBlank()) {
                Text("No application number yet")
            } else {
                Text(customer.applicationNumber)
                Text("Status: " + customer.status)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = customer.inquiryType == "Short Inquiry",
                    onClick = {},
                    label = { Text("Short") }
                )
                FilterChip(
                    selected = customer.inquiryType == "Long Inquiry",
                    onClick = {},
                    label = { Text("Long") }
                )
            }
        }
    }
}

@Composable
fun QuickFollowUpActions() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = {}) { Text("Call") }
        OutlinedButton(onClick = {}) { Text("WhatsApp") }
        OutlinedButton(onClick = {}) { Text("Schedule") }
    }
}
