/* eslint-disable no-undef */
import dotenv from 'dotenv'

dotenv.config()

/**
 * Configuration object which contains the environment variables
 * and the default value for them
 */
const config = {
    /**
     * Database configuration
     */
    dbUser: process.env.monogo_user || 'hunor',
    dbPassword: process.env.monogo_password || '8RrIfU8fpimiSN4P',
    dbHost: process.env.monogo_host || 'cluster0.cp8uj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority',

    /**
     * Auth JWT secret key
     */
    secret: 'a titkos kulcs', // anything

    /**
     * AWS S3 configuration
     */
    aws_bucket: process.env.aws_bucket || 'covid-passport-huni',
    aws_secretAccessKey: process.env.aws_secretAccessKey || 'yba0pCmTwGtrMItkp2LP2C7v53s7hFtggwBTJd2T',
    aws_accessKeyId: process.env.aws_accessKeyId || 'AKIAQBTSMTXKSZ4VT75P',
    aws_region: process.env.aws_region || 'eu-central-1',

    pusher_instance_id: process.env.pusher_instance_id || '79da0046-c72a-47f2-bf19-fc58d34d1047',
    pusher_secret_key: process.env.pusher_secret_key || '1E788B8F51B0F644E063A1737FDC3284EED997F3E4D8ACF0C5021BC618F533A8',

    Env: process.env.NODE_ENV || 'developer',
    LogLevel: process.env.LOG_LEVEL || 'info',

    /**
     * Elrond credentials
     */
    elrond_proxy: process.env.elrond_proxy || 'https://api.elrond.com',
    elrond_senderAddress: 'erd1n4hjvmmge77rpgmuwysmf4twpd766jh66hva24nlweyt7kmtkkpqz4mph5',
    elrond_sender_password: process.env.elrond_sender_password || 'Teknoske1997112!elrond',
    elrond_destinationAddress: 'erd15fp9azy0pxr9j8a7dhwed4hg2gk82k9g2x4m6cdsj3ghaduknaysat3ljc',
    elrond_destination_password: process.env.elrond_destination_password || 'no password',

    mainDir: process.cwd()
}

config.dbConnection = `mongodb+srv://${config.dbUser}:${config.dbPassword}@${config.dbHost}`
  
  export default config

