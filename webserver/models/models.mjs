export const PermissionTypes = [
  'admin', // Handle accounts and system
  'citizen', // Normal user
  'authenticator', // Authorities
  'validator' // Validates certificates
]

export const CertificateTypes = [
  'vaccine', // Vaccine certificate
  'test', // Official Covid test certificate
]

export const CertificateStatuses = [
  'verifying',
  'blockchain verification',
  'accepted',
  'declined'
]
