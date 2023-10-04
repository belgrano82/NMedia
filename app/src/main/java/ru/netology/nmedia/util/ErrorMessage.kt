package ru.netology.nmedia.util

import android.app.AlertDialog
import android.content.Context

    fun showErrorMessage(context: Context, errorMessage: String) {
        AlertDialog.Builder(context)
            .setTitle("Ошибка")
            .setMessage(errorMessage)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

