package com.example.clubdeportivodam

import androidx.lifecycle.ViewModel


//Almacena temporalmente los datos del socio en el momento del registro
class SocioViewModel : ViewModel() {
    var nombre: String = ""
    var dni: String = ""
    var Email: String = ""
    var telefono: String = ""
    var categoria: String = ""
    var vencimiento: String = ""
    var monto: String = ""
    var metodoPago: String = ""
}