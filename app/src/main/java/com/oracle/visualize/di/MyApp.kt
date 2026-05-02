package com.oracle.visualize.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The [Application] class for the app, annotated with [HiltAndroidApp] to trigger
 * Hilt's code generation.
 */
@HiltAndroidApp
class MyApp: Application(){
}