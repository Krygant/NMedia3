package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.save(post, object : PostRepository.SaveCallback {
                override fun onSuccess(savedPost: Post?) {
                    if (savedPost != null) {
                        _postCreated.postValue(Unit)
                    }
                }

                override fun onError(e: Exception) {
                    // обработка ошибки
                }
            })
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.RemoveCallback {
            override fun onSuccess() {
                val currentPosts = _data.value?.posts.orEmpty()
                val indexOfRemovedPost = currentPosts.indexOfFirst { it.id == id }

                if (indexOfRemovedPost >= 0) {
                    // Локальное обновление списка после успешного удаления
                    val updatedPosts = currentPosts.toMutableList().apply { removeAt(indexOfRemovedPost) }
                    _data.postValue(_data.value?.copy(posts = updatedPosts))
                }
            }

            override fun onError(e: Exception) {
                // Обработка ошибки удаления
            }
        })
    }

    fun toggleLikeById(id: Long) {
        val currentPosts = _data.value?.posts.orEmpty()
        val postToUpdate = currentPosts.find { it.id == id } ?: return
        val updatedPost = repository.toggleLikeById(id, !postToUpdate.likedByMe, object :
            PostRepository.ToggleLikeCallback {
            override fun onSuccess(updatedPost: Post?) {
                if (updatedPost != null) {
                    val newPosts = currentPosts.map { if (it.id == id) updatedPost else it }
                    _data.postValue(_data.value?.copy(posts = newPosts))
                }
            }

            override fun onError(exception: Exception) {
                // обработка ошибок
            }
        })
    }
}

