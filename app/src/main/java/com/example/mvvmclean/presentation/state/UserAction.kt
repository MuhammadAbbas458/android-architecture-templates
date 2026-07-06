package com.example.mvvmclean.presentation.state

/**
 * Everything the user can do on the notes screens. Screens emit these to the
 * ViewModel through a single `onAction` callback instead of one lambda per
 * interaction.
 */
sealed class UserAction {

    data class CreateNote(val title: String, val content: String) : UserAction()

    data class DeleteNote(val id: Long) : UserAction()
}
