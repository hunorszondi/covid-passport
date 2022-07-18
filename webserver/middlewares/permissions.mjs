export const permissionCheck = (permissionType) => {
  return (req, _, next) => {
    if (!req.userData.permissions || !req.userData.permissions.includes(permissionType)) {
      throw {
        name: 'PermissionError',
        message: 'Forbidden'
      }
    }
    next()
  }
}