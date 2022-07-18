import express from 'express'
import { getNextCertificate, validationResponse } from '../controller/validatorController.mjs'
import { permissionCheck } from '../middlewares/permissions.mjs'

const router = express.Router()

router.use(permissionCheck('validator'))

/**
 * Receive certificate validation response
 */
router.post('/validate/:certificateId', validationResponse)

/**
 * Get next certificate for validation
 */
router.get('/nextcertificate', getNextCertificate)

export default router
