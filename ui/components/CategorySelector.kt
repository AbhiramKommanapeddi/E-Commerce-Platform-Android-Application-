package com.tutedude.ecommerce.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tutedude.ecommerce.domain.model.Categories
import com.tutedude.ecommerce.ui.theme.Indigo50
import com.tutedude.ecommerce.ui.theme.Indigo600

@Composable
fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Categories.list.forEach { category ->
            val isSelected = selectedCategory.equals(category.name, ignoreCase = true) ||
                    (selectedCategory.isBlank() && category.name.equals("All", ignoreCase = true))

            FilterChip(
                selected = isSelected,
                onClick = {
                    onCategorySelected(if (category.name.equals("All", ignoreCase = true)) "" else category.name)
                },
                label = {
                    Text(
                        text = category.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Indigo600,
                    selectedLabelColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
