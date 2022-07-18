import mongoose from 'mongoose'
import config from '../config.mjs'

const Schema = mongoose.Schema
const mongoDB = config.dbConnection

const UserSchema = new Schema()

const CovidTestDetailsSchema = new Schema()
CovidTestDetailsSchema.add({
  userName: { type: String, required: true },
  docName: { type: String, required: true },
  type: { type: String, required: true },
  dateTime: { type: Number, required: true },
  docSerialNr: { type: String, required: true },
  signature: {type: String } // Blockchain id
})

const VaccineCertificateSchema = new Schema()
VaccineCertificateSchema.add({
  userName: { type: String, required: true },
  docName: { type: String, required: true },
  type: { type: String, required: true },
  dateTime: { type: Number, required: true },
  idCardNumber: { type: String, required: true },
  age: { type: Number, required: true },
  centerName: { type: String, required: true },
  docSerialNr: { type: String, required: true },
  idPhoto: { type: String },
  docPhoto: { type: String },
  status: { type: String, required: true, default: 'verifying' },
  signature: {type: String }, // Blockchain id
  validatorUserName: {type: String },
  validationDateTime : { type: Number }
})

UserSchema.add({
  userName: { type: String, unique: true, required: true },
  firstName: { type: String, required: true },
  lastName: { type: String, required: true },
  personalIdNumber: { type: String, required: true },
  idCardNumber: { type: String, required: true },
  address: { type: String, required: true },
  sex: { type: String/*, required: true*/ },
  birthdate: { type: Number/*, required: true*/ },
  password: { type: String }, // hash
  email: { type: String, required: true },
  photo: { type: String },
  permissions: { type: [String], required: true },
})

/**
 * Initializes the database connection. Call only once, when app starts
 * @returns {Promise} connection result
 */
export const initDb = async function () {
  // CONNECTION EVENTS
  // When successfully connected
  mongoose.connection.on('connected', function () {
    console.log('Mongoose default connection open to ' + mongoDB)
  })

  // If the connection throws an error
  mongoose.connection.on('error', function (err) {
    console.log('Mongoose default connection error: ' + err)
    mongoose.disconnect()
  })

  // When the connection is disconnected
  mongoose.connection.on('disconnected', function () {
    console.log('Mongoose default connection disconnected')
    setTimeout(() => {
      mongoose.connect(mongoDB)
    }, 5000)
  })

  try {
    mongoose.set('useNewUrlParser', true)
    await mongoose.connect(mongoDB)
  } catch (err) {
    err.dbConnection = mongoDB
    throw err
  }
}

export const UserModel = mongoose.model('UserModel', UserSchema, 'user_collection')
export const CovidTestDetailsModel = mongoose.model('CovidTestDetailsModel', CovidTestDetailsSchema, 'covid_test_collection')
export const VaccineCertificateModel = mongoose.model('VaccineCertificateModel', VaccineCertificateSchema, 'vaccine_certificate_collection')

