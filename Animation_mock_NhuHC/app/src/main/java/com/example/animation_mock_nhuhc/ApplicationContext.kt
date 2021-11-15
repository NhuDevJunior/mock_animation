package com.example.animation_mock_nhuhc

import android.app.Application

class ApplicationContext : Application() {

    companion object {
        private lateinit var appContext: ApplicationContext
        fun getContext() = appContext
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}