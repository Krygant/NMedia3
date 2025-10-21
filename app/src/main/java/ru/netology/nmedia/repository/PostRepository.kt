package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun save(post: Post, callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(savedPost: Post?)
        fun onError(e: Exception)
    }

    fun removeById(id: Long, callback: RemoveCallback)

    interface RemoveCallback {
        fun onSuccess()
        fun onError(e: Exception)
    }

    fun toggleLikeById(id: Long, addLike: Boolean, callback: ToggleLikeCallback)
    interface ToggleLikeCallback {
        fun onSuccess(updatedPost: Post?)
        fun onError(exception: Exception)
    }

    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
}

