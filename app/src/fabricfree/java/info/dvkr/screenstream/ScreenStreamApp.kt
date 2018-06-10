package info.dvkr.screenstream

import android.app.Application
import android.util.Log
import info.dvkr.screenstream.di.koinModule
import org.koin.Koin
import org.koin.android.ext.android.startKoin
import org.koin.log.Logger
import timber.log.Timber


class ScreenStreamApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Set up Timber
        Timber.plant(CrashReportingTree())
        Timber.w("[${Thread.currentThread().name}] onCreate: Start")

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
            Timber.e(throwable, "Uncaught throwable in thread ${thread.name}")
            defaultHandler.uncaughtException(thread, throwable)
        }

        // Set up DI
        startKoin(this, listOf(koinModule))
        Koin.logger = object : Logger {
            override fun debug(msg: String) = Timber.d(msg)
            override fun err(msg: String) = Timber.e(msg)
            override fun log(msg: String) = Timber.i(msg)
        }

        Timber.w("[${Thread.currentThread().name}] onCreate: End")
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) return
        }
    }
}