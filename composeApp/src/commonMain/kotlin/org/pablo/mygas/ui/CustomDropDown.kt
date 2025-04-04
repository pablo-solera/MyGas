package org.pablo.mygas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.pablo.mygas.data.ListaEESSPrecio
import org.pablo.mygas.data.MunicipioItem
import kotlin.math.exp

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

