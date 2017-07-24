package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.DbAppSettings
import com.kamer.orny.data.room.entity.DbPageSettings
import io.reactivex.Flowable


@Dao
interface SettingsDao {

    @Query("SELECT * FROM page_settings LIMIT 1")
    fun getPageSettings(): Flowable<DbPageSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPageSettings(settings: DbPageSettings)

    @Query("SELECT * FROM app_settings LIMIT 1")
    fun getAppSettings(): Flowable<DbAppSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAppSettings(settings: DbAppSettings)

}