package com.cokt.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.widget.TextView
import com.cokt.R
import com.cokt.secure.FingerprintAuth

class FingerprintDialog : DialogFragment() {

    companion object {
        private const val KEY_NAME = "com.cokt.secure.fingerprint.key1"
    }

    private val auth: FingerprintAuth = FingerprintAuth()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = activity?.layoutInflater?.inflate(R.layout.dialog_fingerprint, null)
        val txtCancel = view?.findViewById<TextView>(R.id.text_cancel)
        txtCancel?.setOnClickListener {
            dismiss()
        }
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        auth.cancel()
        super.onDismiss(dialog)
    }

    fun show(context: Context,
             manager: FragmentManager,
             cb: ((result: FingerprintManagerCompat.AuthenticationResult?) -> Unit)): Boolean {
        show(manager, "FingerprintDialog")
        return auth.request(context, KEY_NAME, cb)
    }

}