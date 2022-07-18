package com.hunorszondi.covidpassport.ui.citizen


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hunorszondi.covidpassport.R
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.model.apiModels.VaccineCertificateModel
import com.hunorszondi.covidpassport.ui.BaseFragment
import com.hunorszondi.covidpassport.ui.common.ImageViewerFragment
import com.hunorszondi.covidpassport.utils.*
import com.hunorszondi.covidpassport.viewModels.CitizenViewModel
import kotlinx.android.synthetic.main.fragment_vaccine.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * UI for Vaccine details
 */
class VaccineFragment : BaseFragment() {

    private val REQUEST_TAKE_PHOTO: Int = 100
    private val REQUEST_SELECT_IMAGE_FROM_GALLERY: Int = 101
    private val REQUEST_PERMISSION: Int = 200

    private var currentPhotoDestination: String? = null
    private var currentPhotoPath: String? = null
    private var originalPicture: Bitmap? = null
    private var idPhotoPath: String? = "NO_CHANGE"
    private var docPhotoPath: String? = "NO_CHANGE"
    private var functionToCallAfterPermission: (()->Unit)? = null

    companion object {
        fun newInstance() = VaccineFragment()
        data class MODES(
            val ADD: String = "Add",
            val EDIT: String = "Edit",
            val INFO: String = "Info"
        )
    }

    private lateinit var viewModel: CitizenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_vaccine, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(CitizenViewModel::class.java)

        initView()
    }

    /**
     * Loading data into UI elements
     */
    private fun initDataForEditCertificate(certificate: VaccineCertificateModel) {
        qrCodeButton.visibility = View.GONE
        initDateTimePicker()

        val sdf = DateUtil.getDateTimeFormat()
        val date = Date(certificate.dateTime)

        docNameInput.setText(certificate.docName)
        typeInput.setText(certificate.type)
        dateInput.setText(sdf.format(date))
        centerNameInput.setText(certificate.centerName)
        IDCardNumberInput.setText(certificate.idCardNumber)
        ageInput.setText(certificate.age.toString())
        docSerialNrInput.setText(certificate.docSerialNr)

        if (viewModel.currentCertificate?.idPhoto != null) {
            uploadIDButton.visibility = View.GONE
            idImageView.visibility = View.VISIBLE
            idImageHelpTextView.visibility = View.VISIBLE
            idImageDeleteImageView.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.idPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(idImageView)
            idImageDeleteImageView.setOnClickListener { deleteImage("id") }
        } else {
            uploadIDButton.visibility = View.VISIBLE
            idImageView.visibility = View.INVISIBLE
            idImageHelpTextView.visibility = View.INVISIBLE
            idImageDeleteImageView.visibility = View.GONE
        }

        if (viewModel.currentCertificate?.docPhoto != null) {
            uploadProofDocButton.visibility = View.GONE
            docImageView.visibility = View.VISIBLE
            docImageHelpTextView.visibility = View.VISIBLE
            docImageDeleteImageView.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.docPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(docImageView)
            docImageDeleteImageView.setOnClickListener { deleteImage("doc") }
        } else {
            uploadProofDocButton.visibility = View.VISIBLE
            docImageView.visibility = View.INVISIBLE
            docImageHelpTextView.visibility = View.INVISIBLE
            docImageDeleteImageView.visibility = View.GONE
        }
    }

    /**
     * Loading data into UI elements
     */
    private fun initDataForCertificateInfo(certificate: VaccineCertificateModel) {
        if (viewModel.currentCertificate?.signature != null) {
            qrCodeButton.visibility = View.VISIBLE
            qrCodeButton.setOnClickListener { showQRCode(viewModel.currentCertificate!!.signature.toString()) }
        } else {
            qrCodeButton.visibility = View.GONE
        }

        docNameInput.isFocusable = false
        typeInput.isFocusable = false
        dateInput.isFocusable = false
        centerNameInput.isFocusable = false
        IDCardNumberInput.isFocusable = false
        ageInput.isFocusable = false
        docSerialNrInput.isFocusable = false

        val sdf = DateUtil.getDateTimeFormat()
        val date = Date(certificate.dateTime)

        docNameInput.setText(certificate.docName)
        typeInput.setText(certificate.type)
        dateInput.setText(sdf.format(date))
        centerNameInput.setText(certificate.centerName)
        IDCardNumberInput.setText(certificate.idCardNumber)
        ageInput.setText(certificate.age.toString())
        docSerialNrInput.setText(certificate.docSerialNr)


        uploadIDButton.visibility = View.GONE
        uploadProofDocButton.visibility = View.GONE

        idImageView.visibility = View.VISIBLE
        idImageHelpTextView.visibility = View.VISIBLE
        docImageView.visibility = View.VISIBLE
        docImageHelpTextView.visibility = View.VISIBLE

        idImageDeleteImageView.visibility = View.GONE
        docImageDeleteImageView.visibility = View.GONE

        if (viewModel.currentCertificate?.idPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.idPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(idImageView)
            idImageView.setOnClickListener { openImage(viewModel.currentCertificate?.idPhoto!!) }
        }

        if (viewModel.currentCertificate?.docPhoto != null) {
            Glide.with(requireContext())
                .load(viewModel.currentCertificate?.docPhoto)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .fallback(R.drawable.ic_profile_placeholder)
                .into(docImageView)
            docImageView.setOnClickListener { openImage(viewModel.currentCertificate?.docPhoto!!) }
        }
    }

    /**
     * Loading data into UI elements
     */
    private fun initDataForNewCertificate() {
        qrCodeButton.visibility = View.GONE
        initDateTimePicker()

        val user = Session.instance.currentUser
        IDCardNumberInput.setText(user?.idCardNumber)
        //ageInput.setText(certificate.age.toString())

        uploadIDButton.visibility = View.VISIBLE
        uploadProofDocButton.visibility = View.VISIBLE

        idImageView.visibility = View.INVISIBLE
        idImageHelpTextView.visibility = View.GONE
        docImageView.visibility = View.INVISIBLE
        docImageHelpTextView.visibility = View.GONE

        idImageDeleteImageView.visibility = View.GONE
        docImageDeleteImageView.visibility = View.GONE
    }

    /**
     * Initializing View, defining listeners
     */
    private fun initView() {
        navigationHeader.setTitleVisibility(View.VISIBLE)
            .setTitle("${viewModel.vaccineEditFragmentMode} ${ResourceUtil.instance.getString(R.string.certificate)}")
            .setButtonVisibility(View.VISIBLE)
            .setButtonIcon(R.drawable.ic_arrow_back)
            .setButtonClickListener(View.OnClickListener { requireActivity().onBackPressed() })

        when (viewModel.vaccineEditFragmentMode) {
            MODES().ADD -> initDataForNewCertificate()
            MODES().EDIT -> initDataForEditCertificate(viewModel.currentCertificate!!)
            MODES().INFO ->  initDataForCertificateInfo(viewModel.currentCertificate!!)
        }

        uploadIDButton.setOnClickListener {
            currentPhotoDestination = "id"
            openImageSelector()
        }

        uploadProofDocButton.setOnClickListener {
            currentPhotoDestination = "doc"
            openImageSelector()
        }

        doneButton.setOnClickListener {
            val sdf = DateUtil.getDateTimeFormat()
            var timestamp: Long? = null
            var age: Int? = null
            try {
                timestamp = sdf.parse(dateInput.text.toString()).time
                age = ageInput.text.toString().toInt()
            } catch (e: Exception) { }

            when (viewModel.vaccineEditFragmentMode) {
                MODES().ADD -> {
                    showLoading()
                    viewModel.addVaccineCertificate(
                        docNameInput.text.toString(),
                        typeInput.text.toString(),
                        timestamp,
                        centerNameInput.text.toString(),
                        IDCardNumberInput.text.toString(),
                        age,
                        docSerialNrInput.text.toString(),
                        idPhotoPath,
                        docPhotoPath
                    ) { success, message ->
                        requireActivity().runOnUiThread {
                            cancelLoading()
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                requireActivity().onBackPressed()
                            }
                        }
                    }
                }
                MODES().EDIT -> {
                    showLoading()
                    viewModel.updateVaccineCertificate(
                        docNameInput.text.toString(),
                        typeInput.text.toString(),
                        timestamp,
                        centerNameInput.text.toString(),
                        IDCardNumberInput.text.toString(),
                        ageInput.text.toString().toInt(),
                        docSerialNrInput.text.toString(),
                        idPhotoPath,
                        docPhotoPath
                    ) { success, message ->
                        requireActivity().runOnUiThread {
                            cancelLoading()
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                requireActivity().onBackPressed()
                            }
                        }
                    }
                }
                MODES().INFO -> requireActivity().onBackPressed()
                else -> println("Unknown")
            }
        }
    }

    private fun initDateTimePicker() {
        val myCalendar = Calendar.getInstance()
        val dateListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, minute)
                dateInput.setText(DateUtil.getDateTimeFormat().format(myCalendar.time))
            }

            TimePickerDialog(context!!, timeListener,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show()
        }

        dateInput.setOnClickListener{
            DatePickerDialog(context!!, dateListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    /**
     * Opens the dialog where the user see the generated QR code
     */
    private fun showQRCode(signature: String) {
        val qrCode = QRUtil().generateQRCode(signature)
        if(qrCode == null) {
            Toast.makeText(context, "Error: QR code could not load", Toast.LENGTH_SHORT).show()
            return
        }
        val dialogBuilder = AlertDialog.Builder(context).create()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.qr_code_dialog, null)

        val qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView) as ImageView
        val button1 = dialogView.findViewById(R.id.exitButton) as Button
        button1.setOnClickListener { dialogBuilder.dismiss() }
        qrCodeImageView.setImageBitmap(qrCode)

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun deleteImage(type: String) {
        when (type) {
            "id" -> {
                idPhotoPath = null

                uploadIDButton.visibility = View.VISIBLE
                idImageView.visibility = View.INVISIBLE
                idImageHelpTextView.visibility = View.GONE
                idImageDeleteImageView.visibility = View.GONE

                idImageDeleteImageView.setOnClickListener {  }
            }
            "doc" -> {
                docPhotoPath = null

                uploadProofDocButton.visibility = View.VISIBLE
                docImageView.visibility = View.INVISIBLE
                docImageHelpTextView.visibility = View.GONE
                docImageDeleteImageView.visibility = View.GONE

                docImageDeleteImageView.setOnClickListener { }
            }
        }
        //TODO request to delete image+ thumbnail (modify update and add requests to support images)
    }

    private fun showPhoto() {
        when (currentPhotoDestination) {
            "id" -> {
                idPhotoPath = currentPhotoPath
                idImageView.setImageBitmap(originalPicture)

                uploadIDButton.visibility = View.GONE
                idImageView.visibility = View.VISIBLE
                idImageHelpTextView.visibility = View.VISIBLE
                idImageDeleteImageView.visibility = View.VISIBLE

                idImageDeleteImageView.setOnClickListener { deleteImage("id") }
            }
            "doc" -> {
                docPhotoPath = currentPhotoPath
                docImageView.setImageBitmap(originalPicture)

                uploadProofDocButton.visibility = View.GONE
                docImageView.visibility = View.VISIBLE
                docImageHelpTextView.visibility = View.VISIBLE
                docImageDeleteImageView.visibility = View.VISIBLE

                docImageDeleteImageView.setOnClickListener { deleteImage("doc") }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                /**
                 * After an image has been loaded from gallery, here will be handled the new data
                 */
                REQUEST_SELECT_IMAGE_FROM_GALLERY -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val imageUri = data!!.data
                        originalPicture = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUri
                        )
                        originalPicture = ImageRotationUtil()
                            .rotateImageIfRequired(
                                requireContext(),
                                originalPicture!!,
                                imageUri!!,
                                true
                            )
                        createImageFileFromGallery(originalPicture!!)
                        showPhoto()
                    } else {
                        Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                /**
                 * After an image has been captured with camera, here will be handled the new data
                 */
                REQUEST_TAKE_PHOTO -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val file = File(currentPhotoPath)
                        originalPicture = MediaStore.Images.Media
                            .getBitmap(requireContext().contentResolver, Uri.fromFile(file))
                        originalPicture = ImageRotationUtil()
                            .rotateImageIfRequired(
                                requireContext(),
                                originalPicture!!,
                                Uri.parse(currentPhotoPath),
                                false
                            )
                        showPhoto()
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d("ProfileFragment", ex.toString())
            Toast.makeText(context, "Image can not be loaded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val directory = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.toURI()
        )
        FileUtils.deleteDirectory(directory)
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
                    functionToCallAfterPermission?.invoke()
                }
            }
        }
    }

    /**
     * Opens the image source selector dialog
     */
    private fun openImageSelector() {
        val alertDialog = DialogFactory.makeMessage(requireContext(),
            ResourceUtil.instance.getString(R.string.select_image),
            ResourceUtil.instance.getString(R.string.choose_import_method),
            ResourceUtil.instance.getString(R.string.gallery),
            ResourceUtil.instance.getString(R.string.camera),
            DialogInterface.OnClickListener { _, _ -> selectImageFromGalleryWithCheck() },
            DialogInterface.OnClickListener { _, _ -> takePhotoWithCheck() })
        alertDialog.show()
    }

    /**
     * Opens the gallery to select an image with permission check
     */
    private fun selectImageFromGalleryWithCheck() {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            selectImageFromGallery()
        } else {
            makePermissionRequest(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                ::selectImageFromGallery
            )
        }
    }

    /**
     * Opens the gallery to select an image
     */
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                REQUEST_SELECT_IMAGE_FROM_GALLERY
            )
        }
    }

    /**
     * Opens the camera module to capture an image with permission check
     */
    private fun takePhotoWithCheck() {
        if(checkPermission(Manifest.permission.CAMERA)) {
            takePhoto()
        } else {
            makePermissionRequest(arrayOf(Manifest.permission.CAMERA), ::takePhoto)
        }
    }

    /**
     * Opens the camera module to capture an image
     */
    private fun takePhoto() {
        val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File = createImageFile("temp_image")
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.hunorszondi.android.fileprovider",
                photoFile
            )

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        }
    }

    /**
     * Creates an empty image file
     *
     * @param fileName base name of the file
     * @return empty image file
     */
    @Throws(IOException::class)
    private fun createImageFile(fileName: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = fileName + "_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        currentPhotoPath = image.absolutePath
        return image
    }

    /**
     * Creates an image file, if the source of the image is the gallery
     *
     * @param imageFromGallery bitmap image
     */
    private fun createImageFileFromGallery(imageFromGallery: Bitmap) {
        val file: File = createImageFile("image")

        val fileOut = FileOutputStream(file)
        imageFromGallery.compress(Bitmap.CompressFormat.JPEG, 80, fileOut)
        fileOut.flush()
        fileOut.close()
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
     * @param callbackSuccess function to call if permission is granted
     */
    private fun makePermissionRequest(
        permissionTypes: Array<String>,
        callbackSuccess: (() -> Unit)
    ) {
        functionToCallAfterPermission = callbackSuccess
        requestPermissions(permissionTypes, REQUEST_PERMISSION)
    }

    /**
     * Opens ImageViewerFragment
     *
     * @param imageUrl image to be shown in ImageViewerFragment
     */
    private fun openImage(imageUrl: String) {
        val bundle = bundleOf(ImageViewerFragment.IMAGE_URL_ARG_KEY to imageUrl)
        if(view != null){
            Navigation.findNavController(view!!).navigate(
                R.id.action_vaccineFragment_to_imageViewerFragment,
                bundle
            )
        }
    }
}
