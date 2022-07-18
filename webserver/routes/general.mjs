import express from 'express'
import { makeSuccessResponse } from '../services/responseBuilder.mjs'

const router = express.Router()

/**
 * Health check
 */
router.get('/health', (request, response) => {
  response.json(makeSuccessResponse('Health check: OK'))
})

export default router