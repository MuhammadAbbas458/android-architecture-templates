package com.example.mvvmclean.presentation.state

/** Emitted by the screens through a single onAction callback. */
sealed class UserAction {

    data class CreateNote(val title: String, val content: String) : UserAction()

    data class DeleteNote(val id: Long) : UserAction()
}
