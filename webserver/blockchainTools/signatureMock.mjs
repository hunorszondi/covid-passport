import crypto from 'crypto'
export const validateCertificate = (objectToHash) => {
  return crypto.createHash('sha512').update(JSON.stringify(objectToHash)).digest('hex')
}