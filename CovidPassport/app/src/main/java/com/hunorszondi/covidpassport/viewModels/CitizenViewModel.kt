package com.hunorszondi.covidpassport.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hunorszondi.covidpassport.Session
import com.hunorszondi.covidpassport.api.ApiClient
import com.hunorszondi.covidpassport.api.ApiException
import com.hunorszondi.covidpassport.api.repository.CitizenRepository
import com.hunorszondi.covidpassport.model.apiModels.VaccineCertificateModel
import com.hunorszondi.covidpassport.model.apiModels.VaccineCertificateRequestModel
import com.hunorszondi.covidpassport.ui.citizen.VaccineFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Handles the logic and the data management for the citizen role
 */
class CitizenViewModel : ViewModel() {
    //----------------------------------------API request utilities-----------------------------------
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : CitizenRepository = CitizenRepository(ApiClient.apiService)

    //----------------------------------Certificate Manager Fragment related---------------------------------

    val vaccineCertificateList: MutableLiveData<MutableList<VaccineCertificateModel>> by lazy {
        MutableLiveData<MutableList<VaccineCertificateModel>>()
    }

    var currentCertificate: VaccineCertificateModel? = null

    //----------------------------------Vaccine Edit Fragment related------------------------------------

    var vaccineEditFragmentMode: String = VaccineFragment.Companion.MODES().INFO

    //-------------------------------------------METHODS-----------------------------------------

    //----------------------------Certificate Manager Fragment related methods-------------------------------

    /**
     * Fetch certificate list for the logged in user, orders it, and maps to a displayable format
     *
     * @param force force to update list
     * @param callback communicate back to the caller environment
     */
    fun fetchCertificates(force: Boolean, callback: (Boolean, String) -> Unit) {

        if(!force && !vaccineCertificateList.value.isNullOrEmpty()) {
            vaccineCertificateList.postValue(vaccineCertificateList.value)
            callback(true, "Success")
            return
        }

        if(Session.instance.currentUser != null){
            scope.launch {
                try {
                    val certificateList = repository.getCertificates(Session.instance.currentUser!!.userName)
                    if(certificateList?.data != null) {

                        val safeList: MutableList<VaccineCertificateModel> =
                            certificateList.data
                                .filter { it != null }
                                .toMutableList()
                        safeList
                            .sortWith(compareByDescending<VaccineCertificateModel> { it.dateTime }
                            .thenBy {it.docName})
                        vaccineCertificateList.postValue(safeList)

                        callback(true, "Success")
                    } else {
                        callback(false, certificateList?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("CitizenViewModel", error.serverError?:"Unknown error with throw")
                    callback(false, error.serverError?:"Unknown error with throw")
                }
            }
        }

    }

    /**
     * Removes a certificate from certificates list
     *
     * @param position position of the certificate to be removed in the list
     * @param callback communicate back to the caller environment
     */
    fun removeCertificateByPosition(position: Int, callback: (Boolean, String) -> Unit) {
        val certificateId = vaccineCertificateList.value?.get(position)?.id!!
        if(Session.instance.currentUser != null){
            scope.launch {
                try {
                    val result = repository.removeCertificate(certificateId)
                    if(result?.data != null) {
                        val newCertificates = vaccineCertificateList.value
                        newCertificates!!.removeAt(position)
                        vaccineCertificateList.postValue(newCertificates)
                        callback(true, result.data)
                    } else {
                        callback(false, result?.error!!)
                    }
                } catch (error: ApiException) {
                    Log.d("CitizenViewModel", error.serverError?:"Unknown error with throw")
                    callback(false, error.serverError?:"Unknown error with throw")
                }
            }
        }
    }

    //----------------------------Vaccine Edit Fragment related methods----------------------------------

    /**
     * Adds certificate information in the backend
     *
     * @param docName Document name
     * @param type Vaccine type
     * @param dateTime Certificate date and time
     * @param centerName Vaccination center name
     * @param IDCardNumber Identification card number
     * @param age Users age
     * @param docSerialNr Document serial number
     * @param idPhoto Photo of id card or passport
     * @param docPhoto Photo of proof document
     * @param callback communicate back to the caller environment
     */
    fun addVaccineCertificate(docName: String,
                        type: String,
                        dateTime: Long?,
                        centerName: String,
                        IDCardNumber: String,
                        age: Int?,
                        docSerialNr: String,
                        idPhoto: String?,
                        docPhoto: String?,
                        callback: (Boolean, String)->Unit) {

        if(docName.isEmpty() || type.isEmpty() || dateTime == null || centerName.isEmpty()
            || IDCardNumber.isEmpty() || age == null || docSerialNr.isEmpty()) {
            callback(false, "Please fill every field")
            return
        }

        if (idPhoto == null || idPhoto == "NO_CHANGE" || docPhoto == null || docPhoto == "NO_CHANGE") {
            callback(false, "Please upload necessary documents")
            return
        }

        val currentUserName = Session.instance.currentUser!!.userName
        val updateModel = VaccineCertificateRequestModel(currentUserName, docName, type, dateTime, IDCardNumber, age, centerName, docSerialNr , idPhoto, docPhoto)

        scope.launch {
            try {
                val result = repository.upsertCertificate("add", currentUserName, updateModel)
                if(result?.data != null) {
                    fetchCertificates (true, fun (_: kotlin.Boolean, _: kotlin.String) {})
                    callback(true, "Certificate successfully added")
                } else {
                    callback(false, result?.error?:"Unknown error no throw")
                }
            } catch (error: ApiException) {
                Log.d("CitizenViewModel", error.serverError?:"Unknown error with throw")
                callback(false, error.serverError?:"Unknown error with throw")
            }
        }
    }

    /**
     * Updates certificate information in the backend
     *
     * @param docName Document name
     * @param type Vaccine type
     * @param dateTime Certificate date and time
     * @param centerName Vaccination center name
     * @param IDCardNumber Identification card number
     * @param age Users age
     * @param docSerialNr Document serial number
     * @param idPhoto Photo of id card or passport
     * @param docPhoto Photo of proof document
     * @param callback communicate back to the caller environment
     */
    fun updateVaccineCertificate(docName: String,
                                 type: String,
                                 dateTime: Long?,
                                 centerName: String,
                                 IDCardNumber: String,
                                 age: Int?,
                                 docSerialNr: String,
                                 idPhoto: String?,
                                 docPhoto: String?,
                                 callback: (Boolean, String)->Unit) {

        val certificate = this.currentCertificate!!

        if(docName.isEmpty() || type.isEmpty() || dateTime == null || centerName.isEmpty()
            || IDCardNumber.isEmpty() || age == null || docSerialNr.isEmpty()) {
            callback(false, "Please fill every field")
            return
        }

        if (idPhoto == null || docPhoto == null) {
            callback(false, "Please upload necessary documents")
            return
        }

        val updateModel = VaccineCertificateRequestModel()

        if(docName != certificate.docName) {
            updateModel.docName = docName
        }

        if(type != certificate.type) {
            updateModel.type = type
        }

        if(dateTime != certificate.dateTime) {
            updateModel.dateTime = dateTime
        }

        if(centerName != certificate.centerName) {
            updateModel.centerName = centerName
        }

        if(IDCardNumber != certificate.idCardNumber) {
            updateModel.idCardNumber = IDCardNumber
        }

        if(age != certificate.age) {
            updateModel.age = age
        }

        if(docSerialNr != certificate.docSerialNr) {
            updateModel.docSerialNr = docSerialNr
        }

        if(idPhoto != "NO_CHANGE" && idPhoto != certificate.idPhoto) {
            updateModel.idPhoto = idPhoto
        }

        if(docPhoto != "NO_CHANGE" && docPhoto != certificate.docPhoto) {
            updateModel.docPhoto = docPhoto
        }

        scope.launch {
            try {
                val result = repository.upsertCertificate("update", certificate.id, updateModel)
                if(result?.data != null) {
                    fetchCertificates (true, fun (_: kotlin.Boolean, _: kotlin.String) {})
                    callback(true, "Certificate successfully updated")
                } else {
                    callback(false, result?.error?:"Unknown error no throw")
                }
            } catch (error: ApiException) {
                Log.d("CitizenViewModel", error.serverError?:"Unknown error with throw")
                callback(false, error.serverError?:"Unknown error with throw")
            }
        }
    }
}