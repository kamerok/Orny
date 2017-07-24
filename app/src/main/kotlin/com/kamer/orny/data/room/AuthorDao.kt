package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.AuthorEntity
import io.reactivex.Flowable


@Dao
interface AuthorDao {

    @Query("DELETE FROM authors")
    fun deleteAllAuthors()

    @Query("SELECT * FROM authors")
    fun getAllAuthors(): Flowable<List<AuthorEntity>>

    @Insert
    fun insertAll(expenses: List<AuthorEntity>)

}