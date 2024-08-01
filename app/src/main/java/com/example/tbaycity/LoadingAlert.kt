package com.example.tbaycity

import android.app.AlertDialog
import android.app.Activity
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintSet

class LoadingAlert(private val activity: Activity) {
    private lateinit var dialog: AlertDialog

    fun startAlertDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_layout, null))
        builder.setCancelable(true)
        dialog = builder.create()
        dialog.show()
    }

    fun dismissAlertDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
