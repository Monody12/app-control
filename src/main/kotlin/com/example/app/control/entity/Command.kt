package com.example.app.control.entity

data class Command<T>(
    val type : String,
    val cmd : T
)
