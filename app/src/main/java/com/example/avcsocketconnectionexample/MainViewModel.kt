package com.example.avcsocketconnectionexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.democall.composesample.data.chat.list.ChatListResponse
import com.app.democall.sdk.chat.api.AvcSdkChat
import com.app.democall.sdk.manager.api.AvcSdkManager
import com.app.democall.sdk.user.api.AvcSdkUser
import com.app.democall.socket.SocketManager
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val avcSdkManager: AvcSdkManager,
    private val avcSdkChat: AvcSdkChat,
    private val avcSocketManager: SocketManager,
    userManager: AvcSdkUser
) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, t ->
        Timber.e(t)
    }

    private val user = userManager
        .userFlow()
        .catch { Timber.i(it) }
        .onEach { _socketIsActive.tryEmit(it.firstName.isNotEmpty()) }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val _socketIsActive = MutableStateFlow(false)
    val socketIsActive = _socketIsActive

    private val _chatTitles = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val chatsTitle = _chatTitles.asSharedFlow()

    init {
        startSdk()

        observeChatList()
        avcSdkChat
            .chatEvents
            .onEach {
                println(it)
            }
            .launchIn(viewModelScope)

        user.launchIn(viewModelScope)
    }

    fun getChatList() {
        avcSdkChat.getChatList()
    }

    private fun observeChatList() {
        avcSocketManager.listenEvent(ChatListResponse, ChatListResponse::class.java) { response ->
            val chatsTitles = response.data.map { it.title }
            val chatsTitlesString = chatsTitles.joinToString { "Chat title: $it\n" }
            _chatTitles.tryEmit(chatsTitlesString)
        }
    }


    private fun startSdk() {
        viewModelScope.launch(handler) {
            avcSdkManager.start(
                token = TOKEN,
                langCode = "en",
                firebaseToken = "1",
                deviceType = "3"
            )
        }
    }

    companion object {
        private const val TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3Mjg0NjQ2ODgsIm5iZiI6MTcyODM3ODI4OCwidXNfaWQiOjI4NzUsInVzX3VzZXJuYW1lIjoibWlsb3NiYXJsb3YxIiwidXNfdXN0eXBfaWQiOjE4MCwidXNfc3RhdHVzIjoxLCJBQUwiOjEsIklBTCI6MSwibGFuZ19jb2RlIjoic2giLCJzZ2NvX2lkIjoxMDEyOCwiZGF0YSI6eyJpZCI6Mjg3NSwidXNlcm5hbWUiOiJtaWxvc2JhcmxvdjEiLCJyZW1lbWJlck1lIjpmYWxzZSwiQUFMIjoxLCJJQUwiOjEsInR5cGUiOjE4MCwic3RhdHVzIjoxLCJtZWQiOjAsImV4cGlyYXRpb24iOjg2NDAwLCJvbmNvIjowfX0.LIhOuubmcjSr56KbOMswUEMqscF-w8ANlRR5eADVer8"
    }
}