/* eslint-disable no-unused-vars */
import { UserModel, VaccineCertificateModel } from '../models/database.mjs'
import { CertificateStatuses } from '../models/models.mjs'
import { deleteDocumentImage } from './imageS3Util.mjs'

const validateStatus = (status) => {
  return CertificateStatuses.includes(status)
}

/**
 * Get all certificates of a user
 * 
 * @param {String} userName username to get the list from
 * @returns {VaccineCertificateModel[]} certificate list
 */
export const getAllCertificatesOfUser = async (userName) => {
  return VaccineCertificateModel.find({ userName }).exec()
}

/**
 * Add [certificate] to [currentUserName] certificates
 * 
 * @param {String} currentUserName current username
 * @param {object} certificate
 * @param {String} certificate.userName
 * @param {String} certificate.docName
 * @param {String} certificate.type
 * @param {Number} certificate.dateTime
 * @param {String} certificate.idCardNumber
 * @param {Number} certificate.age
 * @param {String} certificate.centerName
 * @param {String} certificate.docSerialNr
 * @param {String} certificate.status
 * @param {String} certificate.idPhoto
 * @param {String} certificate.docPhoto
 * @returns {Certificate[]} certificate item
 */
export const addCertificateToUserService = async (currentUserName, certificate) => {
  console.log(`certificate.userName:  ${certificate.userName}`)
  console.log(`certificate.docName:  ${certificate.docName}`)
  console.log(`certificate.type:  ${certificate.type}`)
  console.log(`certificate.dateTime:  ${certificate.dateTime}`)
  console.log(`certificate.idCardNumber:  ${certificate.idCardNumber}`)
  console.log(`certificate.age:  ${certificate.age}`)
  console.log(`certificate.centerName:  ${certificate.centerName}`)
  console.log(`certificate.docSerialNr:  ${certificate.docSerialNr}`)
  console.log(`certificate.status:  ${certificate.status}`)
  if (!certificate.userName ||
      !certificate.docName ||
      !certificate.type ||
      !certificate.dateTime ||
      !certificate.idCardNumber ||
      !certificate.age ||
      !certificate.centerName ||
      !certificate.docSerialNr) {
    throw 'Missing certificate data'
  }

  if (!certificate.idPhoto || !certificate.docPhoto) {
    throw 'Missing certificate documents'
  }

  const userModel = await UserModel.findOne({ userName: currentUserName }).exec()
  if (!userModel) {
    throw `User doesn't exists`
  }

  try {
    certificate.dateTime = Number.parseInt(certificate.dateTime, 10)
    if(isNaN((new Date(certificate.dateTime)).getTime())) {
      throw ''
    }
  } catch (err) {
    throw 'Wrong dateTime'
  }

  try {
    certificate.age = Number.parseInt(certificate.age, 10)
  } catch (err) {
    throw 'Wrong age'
  }

  const certificateModel = new VaccineCertificateModel(certificate)
  return certificateModel.save()
}

/**
 * Update [certificate] with [certificateId]
 *
 * @param {String} certificateId certificateId
 * @param {object} certificate
 * @param {String} certificate.userName
 * @param {String} certificate.docName
 * @param {String} certificate.type
 * @param {Number} certificate.dateTime
 * @param {String} certificate.idCardNumber
 * @param {Number} certificate.age
 * @param {String} certificate.centerName
 * @param {String} certificate.docSerialNr
 * @param {String} certificate.status
 * @param {String} certificate.idPhoto
 * @param {String} certificate.docPhoto
 * @returns {Certificate[]} certificate item
 */
export const updateCertificateByIdService = async (certificateId, certificate) => {

  const certificateModel = await VaccineCertificateModel.findById(certificateId).exec()
  if (!certificateModel) {
    throw `Certificate doesn't exists`
  }
  if (certificate.docName) {
    certificateModel.docName = certificate.docName
  }

  if (certificate.type) {
    certificateModel.type = certificate.type
  }

  if (certificate.dateTime) {
    try {
      certificate.dateTime = Number.parseInt(certificate.dateTime, 10)
      if(isNaN((new Date(certificate.dateTime)).getTime())) {
        throw ''
      }
      certificateModel.dateTime = certificate.dateTime
    } catch (err) {
      throw 'Wrong dateTime'
    }
  }

  if (certificate.idCardNumber) {
    certificateModel.idCardNumber = certificate.idCardNumber
  }

  if (certificate.age) {
    try {
      certificate.age = Number.parseInt(certificate.age, 10)
      certificateModel.age =certificate.age
    } catch (err) {
      throw 'Wrong age'
    }
  }

  if (certificate.centerName) {
    certificateModel.centerName = certificate.centerName
  }

  if (certificate.docSerialNr) {
    certificateModel.docSerialNr = certificate.docSerialNr
  }

  if (certificate.idPhoto) {
    if (certificateModel.idPhoto) {
      await deleteDocumentImage(certificateModel.idPhoto)
    }
    certificateModel.idPhoto = certificate.idPhoto
  }

  if (certificate.docPhoto) {
    if (certificateModel.docPhoto) {
      await deleteDocumentImage(certificateModel.docPhoto)
    }
    certificateModel.docPhoto = certificate.docPhoto
  }

  certificateModel.status = 'verifying'
  certificateModel.signature = null
  certificateModel.validatorUserName = null
  certificateModel.validationDateTime = null

  return certificateModel.save()
}

/**
 * Remove certificate with [certificateId]
 * 
 * @param {String} certificateId certificate id
 */
export const removeCertificateService = async (certificateId) => {
  const certificateModel = await VaccineCertificateModel.findById(certificateId).exec()
  if (certificateModel.idPhoto) {
    await deleteDocumentImage(certificateModel.idPhoto)
  }
  if (certificateModel.docPhoto) {
    await deleteDocumentImage(certificateModel.docPhoto)
  }
  return VaccineCertificateModel.findByIdAndDelete(certificateId)
}

export const deleteUserCertificates = async (userName) => {
  const promises = []

  const certificates = await VaccineCertificateModel.find({ userName }).exec();
  for(let certificate of certificates) {
    promises.push(removeCertificateService(certificate._id))
  }

  return Promise.all(promises)
}
