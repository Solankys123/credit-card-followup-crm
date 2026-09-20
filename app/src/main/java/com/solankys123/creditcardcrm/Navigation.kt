package com.solankys123.creditcardcrm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class CrmScreen {
    DASHBOARD, CUSTOMERS, DETAIL, FOLLOWUPS
}

@Composable
fun CrmNavigation(customers: List<CustomerRecord>) {
    var screen by remember { mutableStateOf(CrmScreen.DASHBOARD) }
    var selectedCustomer by remember { mutableStateOf<CustomerRecord?>(null) }

    when (screen) {
        CrmScreen.DASHBOARD -> DashboardScreen(
            customers = customers,
            onCustomers = { screen = CrmScreen.CUSTOMERS },
            onFollowUps = {
                selectedCustomer = null
                screen = CrmScreen.FOLLOWUPS
            },
            onAddCustomer = {},
            onOpenCustomer = {
                selectedCustomer = it
                screen = CrmScreen.DETAIL
            }
        )

        CrmScreen.CUSTOMERS -> CustomerListScreen(
            customers = customers,
            query = "",
            onQueryChange = {},
            onBack = { screen = CrmScreen.DASHBOARD },
            onOpenCustomer = {
                selectedCustomer = it
                screen = CrmScreen.DETAIL
            },
            onOpenFollowUps = {
                screen = CrmScreen.FOLLOWUPS
            }
        )

        CrmScreen.DETAIL -> {
            val customer = selectedCustomer
            if (customer == null) {
                screen = CrmScreen.CUSTOMERS
            } else {
                CustomerDetailScreen(
                    customer = customer,
                    onBack = { screen = CrmScreen.CUSTOMERS },
                    onOpenFollowUps = { screen = CrmScreen.FOLLOWUPS }
                )
            }
        }

        CrmScreen.FOLLOWUPS -> FollowUpScreen(
            followUps = SampleData.followUps,
            onBack = {
                screen = if (selectedCustomer == null) {
                    CrmScreen.DASHBOARD
                } else {
                    CrmScreen.DETAIL
                }
            },
            onOpenCustomer = { name ->
                customers.firstOrNull { it.name == name }?.let {
                    selectedCustomer = it
                    screen = CrmScreen.DETAIL
                }
            }
        )
    }
}
