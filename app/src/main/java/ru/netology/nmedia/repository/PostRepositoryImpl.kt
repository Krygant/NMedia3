package ru.netology.nmedia.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.lang.RuntimeException

class PostRepositoryImpl : PostRepository {
    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }
            }

            override fun onFailure(call: Call<List<Post>>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }

    override fun getById(
        id: Long,
        callback: PostRepository.Callback<Post>
    ) {
        PostsApi.retrofitService.getById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }

                val post = response.body()
                if (post != null) {
                    callback.onSuccess(post)
                } else {
                    callback.onError(RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }


    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<Post>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }

                callback.onSuccess(Unit)
            }

            override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }

                val updatedPost = response.body()
                if (updatedPost != null) {
                    callback.onSuccess(updatedPost)
                } else {
                    callback.onError(RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }

    override fun dislikeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when {
                    !response.isSuccessful -> { // Если ответ не успешный
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("PostsApi", "Error fetching posts: ${response.code()} - $errorMessage")
                        callback.onError(RuntimeException("Server returned an error: ${response.code()} - $errorMessage"))
                    }
                    else -> {
                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                }

                val updatedPost = response.body()
                if (updatedPost != null) {
                    callback.onSuccess(updatedPost)
                } else {
                    callback.onError(RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, throwable: Throwable) {
                Log.e("PostsApi", "Failed to fetch posts", throwable)
                callback.onError(throwable)
            }
        })
    }

}

