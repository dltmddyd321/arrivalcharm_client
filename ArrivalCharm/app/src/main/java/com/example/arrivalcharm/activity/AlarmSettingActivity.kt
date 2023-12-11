package com.example.arrivalcharm.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.arrivalcharm.activity.ui.theme.ArrivalCharmTheme
import com.example.arrivalcharm.db.datastore.DatastoreViewModel

class AlarmSettingActivity : ComponentActivity() {

    private val dataStoreViewModel: DatastoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArrivalCharmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column {
        Row {
            Text("알람 거리 :")
            Spacer(modifier = Modifier.width(10.dp))
            Text("2")
        }
        Spacer(modifier = Modifier.size(10.dp))
        AlertDialogShow()
    }
}

@Composable
fun AlertDialogShow() {
    val showDialog = remember { mutableStateOf(false) }

    Button(onClick = {
        showDialog.value = true
    }) {
        Text(text = "수정하기")
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "알람 거리 수정") },
            text = { Text(text = "작업을 진행하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        // 확인 동작
                    }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        // 취소 동작
                    }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun DistanceInputDialog(
    showDialog: MutableState<Boolean>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue()) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("알람 거리 수정") },
            text = {
                Column {
                    Text("작업을 진행하시겠습니까?")
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        label = { Text("텍스트 입력") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        onConfirm(text.text)
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        onDismiss()
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArrivalCharmTheme {
        Greeting("Android")
    }
}