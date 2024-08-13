package com.tpt.takalobazaar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tpt.takalobazaar.models.Draft

@Dao
interface DraftDao {
    @Insert
    suspend fun insertDraft(draft: Draft): Long

    @Query("SELECT * FROM drafts")
    suspend fun getAllDrafts(): List<Draft>

    @Query("DELETE FROM drafts WHERE id = :id")
    suspend fun deleteDraft(id: Int)
}