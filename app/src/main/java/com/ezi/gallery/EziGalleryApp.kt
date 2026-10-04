package com.ezi.gallery

import android.app.Application
import androidx.room.Room
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.ezi.gallery.core.preferences.AppPreferences
import com.ezi.gallery.data.local.AppDatabase
import com.ezi.gallery.data.mediastore.MediaStoreScanner
import com.ezi.gallery.data.repository.MediaRepository

class EziGalleryApp : Application(), ImageLoaderFactory {

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: AppPreferences
        private set

    lateinit var repository: MediaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ezi_gallery.db"
        ).fallbackToDestructiveMigration().build()

        preferences = AppPreferences(applicationContext)

        val scanner = MediaStoreScanner(applicationContext)
        repository = MediaRepository(applicationContext, scanner, database)
    }

    override fun newImageLoader(): ImageLoader {
        // Optimized for low-RAM and fast lazy grid scrolling
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    // Keep memory usage bounded to 25% of available heap to support 2-4GB devices
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024L * 1024L) // 100 MB disk thumbnail cache
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .crossfade(150)
            .build()
    }

    companion object {
        lateinit var instance: EziGalleryApp
            private set
    }
}
