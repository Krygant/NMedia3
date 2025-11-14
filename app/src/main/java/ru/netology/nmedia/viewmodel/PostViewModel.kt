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
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty(), loading = false)
            }

            override fun onError(throwable: Throwable) {
                if (throwable is RuntimeException && throwable.message.toString().contains("Server returned an error")) {
                    val serverCode = throwable.message?.substringAfterLast(": ")?.substringBefore("-")
                        ?.trim()?.toIntOrNull()
                    if (serverCode in 500..599) {
                        // Ошибка типа 5xx - внутренняя ошибка сервера
                        _data.value = FeedModel(networkError = true, loading = false)
                    } else {
                        // Другие типы ошибок
                        _data.value = FeedModel(error = true, loading = false)
                    }
                } else {
                    // Общая ошибка сети или сервиса
                    _data.value = FeedModel(networkError = true, loading = false)
                }
            }
        })
    }

    fun getById(id: Long) {
        repository.getById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(post: Post) {
                // Если запрос успешен, устанавливаем полученный пост в live data
                _data.value = FeedModel(posts = listOf(post))
            }

            override fun onError(throwable: Throwable) {
                // Если произошла ошибка, сообщаем об ошибке
                _data.value = FeedModel(error = true)
            }
        })
    }

    // сохраняем пост и добавляем его в список
    fun save() {
        edited.value?.let { currentEdited ->
            repository.save(currentEdited, object : PostRepository.Callback<Post> {
                override fun onSuccess(savedPost: Post) {
                    // получаем текущий список постов
                    val updatedPosts = (_data.value?.posts.orEmpty() + savedPost)

                    // обновляем данные
                    _data.value = FeedModel(posts = updatedPosts, error = false, loading = false)

                    // очищаем редактируемое поле
                    edited.value = empty

                    // уведомляем экран о создании поста
                    _postCreated.value = Unit
                }

                override fun onError(throwable: Throwable) {
                    // обработка ошибок при сохранении поста
                    _data.value = FeedModel(error = true)
                }
            })
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

    fun likeById(id: Long) {
        val currentPost = _data.value?.posts?.find { it.id == id }
        if (currentPost == null) {
            return
        }

        if (currentPost.likedByMe) {
            // Пользователь хочет снять лайк
            repository.dislikeById(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(updatedPost: Post) {
                    updatePostInList(updatedPost)
                }

                override fun onError(throwable: Throwable) {
                    handleError(throwable)
                }
            })
        } else {
            // Пользователь хочет поставить лайк
            repository.likeById(id, object : PostRepository.Callback<Post> {
                override fun onSuccess(updatedPost: Post) {
                    updatePostInList(updatedPost)
                }

                override fun onError(throwable: Throwable) {
                    handleError(throwable)
                }
            })
        }
    }

    // Обновляет посты в списке ViewModel
    private fun updatePostInList(updatedPost: Post) {
        val newPosts = _data.value?.posts.orEmpty().map {
            if (it.id == updatedPost.id) updatedPost else it
        }
        _data.value = FeedModel(posts = newPosts, error = false, loading = false)
    }

    // Обработка ошибок
    private fun handleError(throwable: Throwable) {
        _data.value = FeedModel(error = true)
    }

    // удаляем пост
    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(result: Unit) {
                // удаляем пост из списка
                val updatedPosts = _data.value?.posts.orEmpty().filter { it.id != id }
                _data.value = FeedModel(posts = updatedPosts, error = false, loading = false)
            }

            override fun onError(throwable: Throwable) {
                _data.value = FeedModel(error = true)
            }
        })
    }
}

