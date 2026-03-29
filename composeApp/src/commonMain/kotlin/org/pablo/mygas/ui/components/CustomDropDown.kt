package org.pablo.mygas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.pablo.mygas.data.remote.MunicipioItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDownMenu(
    municipio: String,
    itemList: List<MunicipioItem>,
    onValueChange: (MunicipioItem) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(itemList[0]) }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .padding(12.dp, 8.dp)
                    .fillMaxWidth(),
                value = municipio,
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                label = { Text("Selecciona un municipio") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },

                ) {
                itemList.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option.municipio,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            text = option
                            expanded = false
                            onValueChange(option)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }

}

