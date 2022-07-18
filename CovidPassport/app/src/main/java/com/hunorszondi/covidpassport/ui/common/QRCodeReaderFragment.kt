package com.hunorszondi.covidpassport.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.hunorszondi.covidpassport.R
import com.google.common.util.concurrent.ListenableFuture
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.utils.QRCodeReader
import kotlinx.android.synthetic.main.fragment_qr_code_reader.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * UI for QRCodeReader
 */
class QRCodeReaderFragment : BaseFragment() {

    private val REQUEST_PERMISSION: Int = 200

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService

    companion object {
        fun newInstance() = QRCodeReaderFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_qr_code_reader, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        closeButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if(checkPermission(Manifest.permission.CAMERA)) {
            initView()
        } else {
            makePermissionRequest(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun onQRCodeResult(result: String) {
        setNavigationResult(result)
        requireActivity().onBackPressed()
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.createSurfaceProvider(null))

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, QRCodeReader {
            onQRCodeResult(it)
        })

        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show()
                } else {
                    initView()
                }
            }
        }
    }

    /**
     * Checks permission
     *
     * @param permissionType permission type String
     * @return boolean
     */
    private fun checkPermission(permissionType: String): Boolean {
        val permission = ContextCompat.checkSelfPermission(requireContext(), permissionType)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Makes permission request
     *
     * @param permissionTypes permission types Array of Strings
     */
    private fun makePermissionRequest(permissionTypes: Array<String>) {
        requestPermissions(permissionTypes, REQUEST_PERMISSION)
    }
}
