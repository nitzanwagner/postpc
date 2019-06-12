package com.example.niwagner.postit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.*
import com.google.ar.sceneform.math.Vector3
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity() {

    private val RC_PERMISSIONS = 0x123

    private var hasFinishedLoading = false
    private var installRequested: Boolean = false
    private lateinit var arSceneView: ArSceneView
    private lateinit var mTextTitle : String
    private lateinit var mTextContent : String
    private lateinit var addButton: FloatingActionButton
    private var loadingMessageSnackbar: Snackbar? = null
    private var gestureDetector: GestureDetector? = null
    private lateinit var postItRenderable: ViewRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            return
        }

        setContentView(R.layout.activity_main)
        arSceneView = findViewById(R.id.ar_scene_view)

        setNewPostItButton()
        setListeners()

        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS)
    }

    private fun setNewPostItButton() {
        addButton = findViewById(R.id.fab)
        addButton.setOnClickListener{
            val f = NoteFragment::class.java
            val fragment = f.getConstructor().newInstance()
            fragment.setCallback(object: NoteFragment.OnNewPostItListener {
                override fun onPost(title: String, content: String) {
                    createPostItRenderable()
                    mTextTitle = title
                    mTextContent = content
                    supportFragmentManager.popBackStack()
                }

                override fun onClose() {
                    supportFragmentManager.popBackStack()
                }
            })

            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                //.setCustomAnimations(R.anim.fragment_note, R.anim.fragment_fade_out)
                .replace(R.id.container, fragment, "TAG").commit()
        }
    }

    private fun setListeners() {
        // Set up a tap gesture detector.
        gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    onSingleTap(e)
                    return true
                }

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }
            })

        // Set a touch listener on the Scene to listen for taps.
        arSceneView.scene.setOnTouchListener { hitTestResult: HitTestResult, event: MotionEvent ->
            return@setOnTouchListener gestureDetector!!.onTouchEvent(event)
        }

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView.scene.addOnUpdateListener {
            if (loadingMessageSnackbar == null) {
                return@addOnUpdateListener
            }

            val frame = arSceneView.arFrame ?: return@addOnUpdateListener

            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@addOnUpdateListener
            }

            for (plane in frame.getUpdatedTrackables(Plane::class.java)) {
                if (plane.trackingState == TrackingState.TRACKING) {
                    hideLoadingMessage()
                }
            }
        }
    }

    private fun createPostItRenderable() {
        // Build a renderable from a 2D View.
        val postItStage = ViewRenderable.builder().setView(this, R.layout.post_it)
            .build()

        postItStage.thenAccept {
            it.isShadowReceiver = false
            it.isShadowCaster = false
        }

        CompletableFuture.allOf(postItStage)
            .handle<Any> { _, throwable ->

                if (throwable != null) {
                    DemoUtils.displayError(this, "Unable to load renderable", throwable)
                }

                try {
                    postItRenderable = postItStage.get()
                    hasFinishedLoading = true

                } catch (ex: InterruptedException) {
                    DemoUtils.displayError(this, "Unable to load renderable", ex)
                } catch (ex: ExecutionException) {
                    DemoUtils.displayError(this, "Unable to load renderable", ex)
                }
                null
            }
    }

    override fun onResume() {
        super.onResume()

        if (arSceneView.session == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                val session = DemoUtils.createArSession(this, installRequested)
                if (session == null) {
                    installRequested = DemoUtils.hasCameraPermission(this)
                    return
                } else {
                    arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                DemoUtils.handleSessionException(this, e)
            }

        }

        try {
            arSceneView.resume()
        } catch (ex: CameraNotAvailableException) {
            DemoUtils.displayError(this, "Unable to get camera", ex)
            finish()
            return
        }

        if (arSceneView.session != null) {
            showLoadingMessage()
        }
    }

    public override fun onPause() {
        super.onPause()
        arSceneView.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        arSceneView.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, results: IntArray
    ) {
        if (!DemoUtils.hasCameraPermission(this)) {
            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                DemoUtils.launchPermissionSettings(this)
            } else {
                Toast.makeText(
                    this, "Camera permission is needed to run this application", Toast.LENGTH_LONG
                )
                    .show()
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Standard Android full-screen functionality.
            window
                .decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onSingleTap(tap: MotionEvent) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            return
        }

        val frame = arSceneView.arFrame
        if (frame != null) {
            tryPlacePostIt(tap, frame)
        }
    }

    private fun tryPlacePostIt(tap: MotionEvent, frame: Frame) {
        if (tap != null && frame.camera.trackingState == TrackingState.TRACKING) {
            for (hit in frame.hitTest(tap)) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    // Create the Anchor.
                    val anchor = hit.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arSceneView.scene)
                    val postIt = createPostIt()
                    anchorNode.addChild(postIt)
                }
            }
        }
    }

    private fun createPostIt(): Node? {
        val postItView = postItRenderable.view
        val title = postItView.findViewById(R.id.title) as TextView
        val content = postItView.findViewById(R.id.content) as TextView
        title.text = mTextTitle
        content.text = mTextContent

        val base = Node()
        val postIt = Node()
        postIt.setParent(base)
        postIt.renderable = postItRenderable
        postIt.localPosition = Vector3(0.0f, 0.5f, 0.0f)

        return base
    }

    private fun showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar!!.isShownOrQueued) {
            return
        }

        loadingMessageSnackbar = Snackbar.make(
            this@MainActivity.findViewById(android.R.id.content) as View,
            R.string.plane_finding,
            Snackbar.LENGTH_INDEFINITE
        )
        loadingMessageSnackbar!!.view.setBackgroundColor(-0x40cdcdce)
        loadingMessageSnackbar!!.show()
    }

    private fun hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return
        }

        loadingMessageSnackbar!!.dismiss()
        loadingMessageSnackbar = null
    }
}
