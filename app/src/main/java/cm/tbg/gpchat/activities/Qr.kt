/*
 * Created by Boris Gautier on 10/$today.mounth/2021
 */

/*package cm.tbg.gpchat.activities

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cm.tbg.gpchat.R
import cm.tbg.gpchat.utils.QrResult
import cm.tbg.gpchat.utils.QrResultG
import cm.tbg.gpchat.global.AppBack
import cm.tbg.gpchat.global.Global
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.makeramen.roundedimageview.RoundedImageView
import it.auron.library.mecard.MeCard
import it.auron.library.mecard.MeCardParser
import net.glxn.qrgen.android.QRCode
import java.lang.Boolean
import java.util.*



class Qr : AppCompatActivity() {
    var qr: RoundedImageView? = null
    var mAuth: FirebaseAuth? = null
    private var mCodeScanner: CodeScanner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        Global.currentactivity = this
        qr = findViewById(R.id.qr)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.currentUser != null) generateQr()
        Dexter.withActivity(this@Qr)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        //scan
                        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
                        mCodeScanner = CodeScanner(this@Qr, scannerView)
                        mCodeScanner!!.setDecodeCallback(object : DecodeCallback {
                            override fun onDecoded(result: Result) {
                                runOnUiThread { parseQr(result.getText()) }
                            }
                        })
                        scannerView.setOnClickListener(View.OnClickListener { mCodeScanner!!.startPreview() })
                        mCodeScanner!!.startPreview()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    private fun generateQr() {
        val meCard = MeCard()
        meCard.setName(Global.nameLocal)
        meCard.setAddress(mAuth!!.currentUser!!.uid)
        meCard.setUrl(Global.avaLocal)
        meCard.setNote(Global.statueLocal)
        meCard.addTelephone(Global.phoneLocal)
        meCard.setOrg(java.lang.String.valueOf(Global.myscreen))
        val meCardContent: String = meCard.buildString()
        qr!!.setImageBitmap(QRCode.from(meCardContent).withSize(250, 250).bitmap())
    }

    private fun parseQr(meCardString: String) {
        try {
            val meCard: MeCard = MeCardParser.parse(meCardString)
            Log.wtf("oooo", meCard.getAddress().toString() + "")
            Log.wtf("oooo", meCard.getName().toString() + "")
            Log.wtf("oooo", meCard.getUrl().toString() + "")
            if (meCard.getAddress() != null) {
                if (meCard.getAddress().contains("groups-")) {
                    if (meCard.getAddress() != null && meCard.getName() != null && meCard.getUrl() != null) {
                        val cdd = QrResultG(this@Qr, meCard.getUrl(), meCard.getName(), meCard.getAddress())
                        cdd.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        cdd.getWindow()!!.getAttributes().windowAnimations = R.style.CustomDialogAnimation
                        cdd.show()
                    } else {
                        Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (meCard.getAddress() != null && meCard.getName() != null && meCard.getOrg() != null && meCard.getTelephones() != null && meCard.getUrl() != null) {
                        val cdd = QrResult(this@Qr, meCard.getUrl(), meCard.getName(), meCard.getAddress(), meCard.getTelephones().get(0), Boolean.parseBoolean(meCard.getOrg()))
                        cdd.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation
                        cdd.show()
                    } else {
                        Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show()
            }
        } catch (e: NullPointerException) {
            Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val mData = FirebaseDatabase.getInstance().getReference(Global.USERS)
        Global.currentactivity = this
        val myApp: AppBack = this.application as AppBack
        if (myApp.wasInBackground) {
            //init data
            val map: MutableMap<String, Any> = HashMap()
            map[Global.Online] = true
            if (mAuth!!.currentUser != null) mData.child(mAuth!!.currentUser!!.uid).updateChildren(map)
            Global.local_on = true
            //lock screen
            (application as AppBack).lockscreen((application as AppBack).shared().getBoolean("lock", false))
        }
        myApp.stopActivityTransitionTimer()
    }

    override fun onPause() {
        (this.application as AppBack).startActivityTransitionTimer()
        Global.currentactivity = null
        super.onPause()
    }

    override fun onDestroy() {
        try {
            if (mCodeScanner != null) mCodeScanner.releaseResources()
        } catch (e: NullPointerException) {
        }
        super.onDestroy()
    }
}*/