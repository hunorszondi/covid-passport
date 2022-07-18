import { makeSuccessResponse, makErrorResponse } from '../services/responseBuilder.mjs'
import { authenticateSignatureService } from '../services/authenticatorService.mjs'

/**
 * Returns verified certificate
 * 
 * @param {String} request.params.signature signature
 * @param response 
 */
export const authenticateSignature = async (request, response) => {
  const signature = request.params.signature
  try {
    const verifiedCertificate = await authenticateSignatureService(signature)
    // send in response
    response.status(200)
    response.json(makeSuccessResponse(verifiedCertificate))
  } catch (err) {
    response.status(500)
    response.json(makErrorResponse(JSON.stringify(err)))
  }
}
