import {
  getAllCertificatesOfUser,
  addCertificateToUserService,
  removeCertificateService,
  updateCertificateByIdService
} from '../services/citizenService.mjs'
import { makeSuccessResponse, makErrorResponse } from '../services/responseBuilder.mjs'

/**
 * Returns all certificates of a user
 * 
 * @param {String} request.params.username username 
 * @param response 
 */
export const getAllCertificates = async (request, response) => {
  const userName = request.params.username
  try {
    const certificates = await getAllCertificatesOfUser(userName)
    // send in response
    response.status(200)
    response.json(makeSuccessResponse(certificates))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse('A database error occurred. More details: ' + JSON.stringify(err)))
  }
}

/**
 * Adds a new certificate to a user
 * 
 * @param {String} request.params.username username to add the new certificate to
 * @param {Object} request.body new certificate
 * @param {Object} request.files uploaded files
 * @param response 
 */
export const addCertificateToUser = async (request, response) => {
  const userName = request.params.username
  const certificate = request.body
  try {
    // Extract photo locations
    if (request.files) {
      if (request.files.idPhoto && request.files.idPhoto.length && request.files.idPhoto[0] && request.files.idPhoto[0].location) {
        certificate.idPhoto = request.files.idPhoto[0].location
      }
      if (request.files.docPhoto && request.files.docPhoto.length && request.files.docPhoto[0] && request.files.docPhoto[0].location) {
        certificate.docPhoto = request.files.docPhoto[0].location
      }
    }

    const certificateModel = await addCertificateToUserService(userName, certificate)

    // send in response
    response.status(200)
    response.json(makeSuccessResponse(certificateModel))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse(JSON.stringify(err)))
  }
}

/**
 * Updates a certificate with given id
 *
 * @param {String} request.params.certificateId username to add the new certificate to
 * @param {Object} request.body certificate
 * @param {Object} request.files uploaded files
 * @param response
 */
export const updateCertificateById = async (request, response) => {
  const certificateId = request.params.certificateId
  const certificate = request.body
  try {
    // Extract photo locations
    if (request.files) {
      if (request.files.idPhoto && request.files.idPhoto.length && request.files.idPhoto[0] && request.files.idPhoto[0].location) {
        certificate.idPhoto = request.files.idPhoto[0].location
      }
      if (request.files.docPhoto && request.files.docPhoto.length && request.files.docPhoto[0] && request.files.docPhoto[0].location) {
        certificate.docPhoto = request.files.docPhoto[0].location
      }
    }

    const certificateModel = await updateCertificateByIdService(certificateId, certificate)

    // send in response
    response.status(200)
    response.json(makeSuccessResponse(certificateModel))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse(JSON.stringify(err)))
  }
}

/**
 * Removes a certificate with given id
 * 
 * @param {String} request.params.certificateId certificate id
 * @param response 
 */
export const removeCertificateById = async (request, response) => {
  const certificateId = request.params.certificateId
  try {
    await removeCertificateService(certificateId)

    // send in response
    response.status(200)
    response.json(makeSuccessResponse('Certificate successfully removed'))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse('A database error occurred. More details: ' + JSON.stringify(err)))
  }
}
