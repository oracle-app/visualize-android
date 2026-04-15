package com.oracle.visualize

import android.app.Application
import com.oracle.visualize.di.AppModule

/**
 * Custom Application class for dependency injection setup.
 */
class VisualizeApplication : Application() {
    
    // Manual Dependency Injection container
    lateinit var appModule: AppModule

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}
