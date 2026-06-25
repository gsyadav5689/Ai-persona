package com.example.data

import kotlinx.coroutines.flow.Flow

class AvatarRepository(private val dao: AvatarDao) {
    val allGenerations: Flow<List<AvatarGeneration>> = dao.getAllGenerations()
    val allCustomFaces: Flow<List<CustomFace>> = dao.getAllCustomFaces()

    suspend fun insertGeneration(generation: AvatarGeneration) = dao.insertGeneration(generation)
    suspend fun deleteGeneration(id: Int) = dao.deleteGeneration(id)
    suspend fun updateFavorite(id: Int, isFavorite: Boolean) = dao.updateFavorite(id, isFavorite)

    suspend fun insertCustomFace(face: CustomFace) = dao.insertCustomFace(face)
    suspend fun deleteCustomFace(id: Int) = dao.deleteCustomFace(id)
}
