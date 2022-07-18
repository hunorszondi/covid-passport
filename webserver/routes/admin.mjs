import express from 'express'
import { deleteUser, getByUserName, updateUserPermissions } from '../controller/userController.mjs'
import { permissionCheck } from '../middlewares/permissions.mjs'

const router = express.Router()

router.use(permissionCheck('admin'))

/**
 * Get user by username
 */
router.get('/users/:username', getByUserName)

/**
 * Update user permissions by userName
 */
router.put('/users/:username/permissions', updateUserPermissions)

/**
 * Delete user by username
 */
router.delete('/users/:username', deleteUser)

export default router
