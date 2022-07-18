import express from 'express'
import {
  authenticate,
  deleteUser,
  getAll,
  getById,
  getByUserName, getUserProfilePicture,
  register,
  update
} from '../controller/userController.mjs'
import { uploadProfilePicture } from '../services/imageS3Util.mjs'

const router = express.Router()

/**
 * Authenticate user
 */
router.post('/authenticate', authenticate)

/**
 * Register user
 */
router.post('/register', uploadProfilePicture.single('file'), register)

/**
 * Get all users
 */
router.get('/', getAll)

/**
 * Get user by username
 */
router.get('/:username', getByUserName)

/**
 * Get user by id
 */
router.get('/id/:id', getById)

/**
 * Update user information
 */
router.put('/id/:id', uploadProfilePicture.single('file'), update)

/**
 * Delete user by username
 */
router.delete('/:username', deleteUser)

/**
 * Delete user
 */
router.delete('/', deleteUser)

/**
 * Get user profile picture
 */
router.get('/profilepicture/:username', getUserProfilePicture)

export default router