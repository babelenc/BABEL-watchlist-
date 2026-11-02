package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalCinemoraColors

@Composable
fun AddCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, coverUrl: String) -> Unit
) {
    val colors = LocalCinemoraColors.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.cardBackground)
                .border(0.5.dp, colors.border, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "New Collection",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Curate personal masterpieces, comfort watches, or lists.",
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(text = error ?: "", color = colors.statusDropped, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Collection Name *") },
                placeholder = { Text("e.g. Movies That Changed Me") },
                modifier = Modifier.fillMaxWidth().testTag("input_collection_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_collection_description"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = coverUrl,
                onValueChange = { coverUrl = it },
                label = { Text("Cover Poster URL (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_collection_cover"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel", color = colors.textSecondary)
                }

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            error = "Name cannot be empty"
                            return@Button
                        }
                        onConfirm(name.trim(), description.trim(), coverUrl.trim())
                    },
                    modifier = Modifier.weight(1f).testTag("confirm_create_collection_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
