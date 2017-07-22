package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.model.DbPageSettings
import io.reactivex.Flowable


@Dao
interface SettingsDao {

    @Query("SELECT * FROM page_settings LIMIT 1")
    fun getPageSettings(): Flowable<DbPageSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPageSettings(settings: DbPageSettings)

}