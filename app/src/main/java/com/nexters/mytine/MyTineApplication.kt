package com.nexters.mytine

import android.app.Application
import com.facebook.stetho.Stetho
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
internal class MyTineApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        EmojiManager.install(IosEmojiProvider())
    }
}
