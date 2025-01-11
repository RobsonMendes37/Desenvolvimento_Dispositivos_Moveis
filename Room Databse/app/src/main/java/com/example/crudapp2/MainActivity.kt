package com.example.crudapp2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserApp(viewModel: UserViewModel){
    val usuarios by viewModel.users.observeAsState(emptyList())
    var nome by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }
    var usuarioEditando by remember { mutableStateOf<User?>(null) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title= { Text(text = "Gerenciamento de Usuários")},
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(
                if(usuarioEditando == null) "Adiocnar um novo Usuário"
                else "Editar Usuário",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = nome,
                onValueChange = {nome = it},
                label = { Text(text = "Nome")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = idade,
                onValueChange = {idade = it},
                label = { Text(text = "Idade")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        if (nome.isNotEmpty() && idade.isNotEmpty()){
                            val idadeInt = idade.toInt()
                            if (usuarioEditando == null){
                                viewModel.addUser(User(name = nome, age = idadeInt))
                            }else{
                                viewModel.updateUser(usuarioEditando!!.copy(name = nome, age = idadeInt))
                                usuarioEditando = null
                            }
                            nome = ""
                            idade = ""
                            mensagemErro = ""
                        }
                    }catch (e: NumberFormatException){
                        mensagemErro = "Idade inválida"
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (usuarioEditando == null) "Adicionar usuário" else "Salvar alterações")
            }
            mensagemErro?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
}

