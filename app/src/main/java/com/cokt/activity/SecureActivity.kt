package com.cokt.activity

import android.os.Bundle
import com.cokt.R
import com.cokt.dialog.FingerprintDialog
import com.cokt.tool.CoktToast
import kotlinx.android.synthetic.main.activity_secure.*

class SecureActivity : BaseActivity() {

    private var fingerprintDialog: FingerprintDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secure)
        btn_req_fingerprint.setOnClickListener {
            if (fingerprintDialog?.isAdded == true) {
                fingerprintDialog?.dismiss()
            }
            fingerprintDialog = FingerprintDialog()
            fingerprintDialog?.show(applicationContext, supportFragmentManager, {
                if (it != null) {
                    fingerprintDialog?.dismiss()
                    CoktToast.toast(R.string.fingerprint_auth_success)
                } else {
                    CoktToast.toast(R.string.fingerprint_auth_failed)
                }
            })
        }
    }

    override fun onDestroy() {
        fingerprintDialog?.dismiss()
        super.onDestroy()
    }

}