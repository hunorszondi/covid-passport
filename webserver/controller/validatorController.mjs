import { makeSuccessResponse, makErrorResponse } from '../services/responseBuilder.mjs'
import { getNextCertificateService, saveValidationResponseService } from '../services/validatorService.mjs'

/**
 * Updates certificate validation state
 * 
 * @param {String} request.params.certificateId certificateId
 * @param {Boolean} request.body response
 * @param response 
 */
export const validationResponse = async (request, response) => {
  const certificateId = request.params.certificateId
  const validationResponse = request.body.data
  const validatorUserName = request.userData.userName
  try {
    await saveValidationResponseService(certificateId, validatorUserName, validationResponse)
    // send in response
    response.status(200)
    response.json(makeSuccessResponse('Success'))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse(err))
  }
}

/**
 * Returns the next certificated which wasn't validated yet
 *
 * @param response
 */
export const getNextCertificate = async (request, response) => {
  try {
    const certificate = await getNextCertificateService()
    // send in response
    response.status(200)
    response.json(makeSuccessResponse(certificate))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse(err))
  }
}
