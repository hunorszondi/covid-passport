export const makeSuccessResponse = (successData) => {
  return {
    data: successData,
    error: null
  }
}

export const makErrorResponse = (errorData) => {
  return {
    data: null,
    error: errorData
  }
}