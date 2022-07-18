/* eslint-disable no-unused-vars */
import { UserModel, VaccineCertificateModel } from '../models/database.mjs'
import ElrondService from '../blockchainTools/elrondService.mjs'

/**
 * Authenticate signature
 * 
 * @param {String} signature signature
 * @returns {object} verified certificate
 */
export const authenticateSignatureService = async (signature) => {
  const certificateModel = await VaccineCertificateModel.findOne({ signature }).exec()
  if (!certificateModel) {
    throw 'No certificate found'
  }
  const userModel = await UserModel.findOne({ userName: certificateModel.userName }).exec()

  const elrondService = new ElrondService(certificateModel)
  const blockChainValidationResult = await elrondService.verifyBlock()

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
    status: certificateModel.status,
    blockChainValidation: blockChainValidationResult,
    idPhoto: certificateModel.idPhoto,
    docPhoto: certificateModel.docPhoto,
    validatorUserName: certificateModel.validatorUserName,
    validationDateTime: certificateModel.validationDateTime
  }
}
