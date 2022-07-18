import express from 'express'
import { authenticateSignature } from '../controller/authenticatorController.mjs'
import { permissionCheck } from '../middlewares/permissions.mjs'

const router = express.Router()

router.use(permissionCheck('authenticator'))

/**
 * Get certificate list
 */
router.get('/verifysignature/:signature', authenticateSignature)

export default router
