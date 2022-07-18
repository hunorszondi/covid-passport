import sharp from 'sharp'
import multer from 'multer'
import multerS3 from 'multer-s3'
import aws from 'aws-sdk'
import config from '../config.mjs'

aws.config.update({
    secretAccessKey: config.aws_secretAccessKey,
    accessKeyId: config.aws_accessKeyId,
    region: config.aws_region
})

const s3 = new aws.S3()
const bucket = config.aws_bucket
const acl = 'public-read'
const documentImagePath = 'imageData/documentImages/'
const profilePicturePath = 'imageData/profileImages/'

/**
 * Document images storage definition
 */
export const uploadDocumentImage = multer({
    storage: multerS3({
      s3: s3,
      bucket,
      acl,
      contentType: multerS3.AUTO_CONTENT_TYPE,
      metadata: function (req, file, cb) {
        cb(null, {fieldName: file.fieldname});
      },
      key: function (req, file, cb) {
        cb(null, `${documentImagePath}${req.params.certificateId}_${Date.now()}_${file.originalname}`)
      }
    })
  })

/**
 * Profile pictures storage definition
 */
export const uploadProfilePicture = multer({
    storage: multerS3({
      s3: s3,
      bucket,
      acl,
      contentType: multerS3.AUTO_CONTENT_TYPE,
      metadata: function (req, file, cb) {
        cb(null, {fieldName: file.fieldname});
      },
      key: function (req, file, cb) {
        cb(null, `${profilePicturePath}${Date.now()}_${file.originalname}`)
      }
    })
  })

/**
 * Delete files from S3
 * 
 * @param {String[]} fileNames list of filenames to be deleted
 */
const deleteFilesFromS3 = async (fileNames) => {
  const objects = fileNames.map((name) => { return { Key: name } })

  const params = {
    Bucket: bucket, 
    Delete: { // required
        Objects: objects,
    },
  }
  
  try {
    await s3.deleteObjects(params).promise()
  } catch (err) {
    console.log(err, err.stack)
  }
}

/**
 * Get file from S3
 * 
 * @param {String} fileName filename to get
 * @returns {S3.Body} object or nothing
 */
const getFileFromS3 = async (fileName) => {
  const params = {
    Bucket: bucket, 
    Key: fileName,
  }
  
  try {
    const s3Object = await s3.getObject(params).promise()
    return s3Object.Body
  } catch (err) {
    console.log(err, err.stack)
  }
}

/**
 * Upload file to S3
 * @param {S3.Body} file file to be uploaded
 * @param {String} fileName name of the file
 */
const putFileToS3 = async (file, fileName) => {
  const params = {
    Bucket: bucket, 
    Key: fileName,
    Body: file
  }
  
  try {
    await s3.putObject(params).promise()
  } catch (err) {
    console.log(err, err.stack)
  }
}

/**
 * Delete document image from storage
 * 
 * @param {String} url of the file
 */
export const deleteDocumentImage = async (url) => {
  return deleteFilesFromS3([`${documentImagePath}${url.substring(url.lastIndexOf('/')+1)}`])
}

/**
 * Delete profile picture from storage
 * 
 * @param {String} url of the file
 */
export const deleteProfilePicture = async (url) => {
    return deleteFilesFromS3([`${profilePicturePath}${url.substring(url.lastIndexOf('/')+1)}`])
}

/**
 * Get profile picture from storage
 * 
 * @param {String} url of the file
 */
export const getProfilePicture = async (url) => {
  return getFileFromS3(`${profilePicturePath}${url.substring(url.lastIndexOf('/')+1)}`)
}

/**
 * Reduces the given image size to its 60%
 * 
 * @param {String} imagePath path to load the image from
 */
export const reduceImageSize = async (imagePath) => {
  const image = await getFileFromS3(imagePath)
  const newImage = await sharp(image)
      .resize(512, 512)
      .jpeg({ quality: 60, force: false })
      .png({ compressionLevel: 9, force: false })
      .withMetadata()
      .toBuffer()
  return putFileToS3(newImage, imagePath)
}