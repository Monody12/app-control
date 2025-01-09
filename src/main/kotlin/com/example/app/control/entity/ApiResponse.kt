package com.example.app.control.entity

data class ApiResponse<T>(
    var data: T? = null,
    var code: Int = 0,
    var msg: String = ""
) {

}