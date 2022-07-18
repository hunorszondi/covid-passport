import createError from 'http-errors'
import express from 'express'
import cors from 'cors'
import logger from 'morgan'
import citizenRouter from './routes/citizen.mjs'
import authenticatorRouter from './routes/authenticator.mjs'
import validatorRouter from './routes/validator.mjs'
import adminRouter from './routes/admin.mjs'
import userRouter from './routes/users.mjs'
import generalRouter from './routes/general.mjs'
import jwt from './middlewares/jwt.mjs'
import { makErrorResponse } from './services/responseBuilder.mjs'

let app = express()

app.use(logger('dev'))
app.use(express.urlencoded({ extended: false }))
app.use(express.json())
app.use(cors())

app.use(jwt())

app.use('/users', userRouter)
app.use('/citizen', citizenRouter)
app.use('/authenticator', authenticatorRouter)
app.use('/validator', validatorRouter)
app.use('/admin', adminRouter)
app.use('/', generalRouter)

// catch 404 and forward to error handler
app.use((req, res, next) => {
  next(createError(404))
})

// error handler
const errorHandler = (err, req, res) => {
  if (typeof (err) === 'string') {
    return res.status(400).json(makErrorResponse(err))
  }

  if (err.name === 'ValidationError') {
    // mongoose validation error
    return res.status(400).json(makErrorResponse(err.message))
  }

  if (err.name === 'UnauthorizedError') {
    // jwt authentication error
    return res.status(401).json(makErrorResponse('Invalid Token'))
  }

  if (err.name === 'PermissionError') {
    // jwt authentication error
    return res.status(403).json(makErrorResponse('Forbidden: no permission to access this endpoint'))
  }

  // default to 500 server error
  return res.status(500).json(makErrorResponse(err.message))
}

app.use(errorHandler)

export default app
