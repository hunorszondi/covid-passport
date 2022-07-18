import express from 'express'
import {
    getAllCertificates,
    addCertificateToUser,
    updateCertificateById,
    removeCertificateById
} from '../controller/citizenController.mjs'
import { uploadDocumentImage } from '../services/imageS3Util.mjs'
import { permissionCheck } from '../middlewares/permissions.mjs'

const router = express.Router()

router.use(permissionCheck('citizen'))

/**
 * Get certificate list
 */
router.get('/certificates/:username', getAllCertificates)

/**
 * Add certificate to user
 */
router.post('/certificates/:username',
  uploadDocumentImage.fields([{ name: 'idPhoto', maxCount: 1 }, { name: 'docPhoto', maxCount: 1 }]),
  addCertificateToUser
)

/**
 * Update certificate of user
 */
router.put('/certificates/:certificateId',
  uploadDocumentImage.fields([{ name: 'idPhoto', maxCount: 1 }, { name: 'docPhoto', maxCount: 1 }]),
  updateCertificateById
)

/**
 * Remove certificate from user
 */
router.delete('/certificates/:certificateId', removeCertificateById)

export default router
