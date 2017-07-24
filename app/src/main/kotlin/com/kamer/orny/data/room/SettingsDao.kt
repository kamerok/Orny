package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.AppSettingsEntity
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.PageSettingsEntity
import io.reactivex.Flowable


@Dao
interface SettingsDao {

    @Query("SELECT * FROM page_settings LIMIT 1")
    fun getPageSettings(): Flowable<PageSettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPageSettings(settings: PageSettingsEntity)

    @Query("SELECT authors.id, authors.name, authors.position, authors.color FROM authors " +
            "JOIN app_settings ON authors.id = app_settings.default_author_id " +
            "WHERE app_settings.id = 0 " +
            "LIMIT 1")
    fun getDefaultAuthor(): Flowable<List<AuthorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAppSettings(settings: AppSettingsEntity)

}