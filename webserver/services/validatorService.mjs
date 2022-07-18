/* eslint-disable no-unused-vars */
import { UserModel, VaccineCertificateModel } from '../models/database.mjs'
import { validateCertificate } from '../blockchainTools/signatureMock.mjs'
import ElrondService from '../blockchainTools/elrondService.mjs'

/**
 * Updates certificate validation state
 * @param certificateId
 * @param validatorUserName
 * @param validationResponse
 */
export const saveValidationResponseService = async (certificateId, validatorUserName, validationResponse) => {
  const certificateModel = await VaccineCertificateModel.findById(certificateId).exec()
  if (!certificateModel) {
    throw `Certificate doesn't exists`
  }
  certificateModel.validatorUserName = validatorUserName
  certificateModel.validationDateTime = new Date().getTime()

  // With mock
  // if (validationResponse) {
  //   certificateModel.status = 'accepted'
  //   certificateModel.signature = validateCertificate({
  //     userName: certificateModel.userName,
  //     docName: certificateModel.docName,
  //     type: certificateModel.type,
  //     dateTime: certificateModel.dateTime,
  //     idCardNumber: certificateModel.idCardNumber,
  //     age: certificateModel.age,
  //     centerName: certificateModel.centerName,
  //     docSerialNr: certificateModel.docSerialNr
  //   })
  // } else {
  //   certificateModel.status = 'declined'
  //   certificateModel.signature = null
  // }
  // return certificateModel.save()

  // With blockchain
  if (validationResponse) {
    const elrondService = new ElrondService(certificateModel)
    return elrondService.createBlock()
    } else {
      certificateModel.status = 'declined'
      certificateModel.signature = null
      return certificateModel.save()
    }
}

/**
 * Returns the next certificated which wasn't validated yet
 *
 * @returns {object} verified certificate
 */
export const getNextCertificateService = async () => {
  const certificateModel = await VaccineCertificateModel.findOne({ status: 'verifying' }).exec()
  if (!certificateModel) {
    throw ''
  }
  const userModel = await UserModel.findOne({ userName: certificateModel.userName }).exec()
  if (!userModel) {
    throw 'Not existing userName in certificate'
  }
  return {
    firstName: userModel.firstName,
    lastName: userModel.lastName,
    email: userModel.email,
    personalIdNumber: userModel.personalIdNumber,
    idCardNumber: userModel.idCardNumber,
    address: userModel.address,
    photo: userModel.photo,
    certificateId: certificateModel._id,
    type: certificateModel.type,
    dateTime: certificateModel.dateTime,
    age: certificateModel.age,
    centerName: certificateModel.centerName,
    docSerialNr: certificateModel.docSerialNr,
    idPhoto: certificateModel.idPhoto,
    docPhoto: certificateModel.docPhoto
  }
}
