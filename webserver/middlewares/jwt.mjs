import expressJwt from 'express-jwt'
import config from '../config.mjs'
import { getUserById } from '../services/userService.mjs'

const jwt = () => {
    const secret = config.secret;
    return expressJwt({ secret, isRevoked }).unless({
        path: [
            // public routes that don't require authentication
            '/users/authenticate',
            '/users/register',
            '/health',
        ]
    })
}

const isRevoked = async (req, payload, done) => {
    const user = await getUserById(payload.sub);

    // revoke token if user no longer exists
    if (!user) {
        return done(null, true)
    }
    // Extending request object
    req.userData = {
        userName: payload.name,
        permissions: payload.permissions
    }

    done()
}

export default jwt