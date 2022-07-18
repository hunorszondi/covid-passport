import ElrondAPI from './elrondAPI.mjs'
import { sendNotification } from '../services/pushNotificationService.mjs'

export default class ElrondService {
  constructor (certificateModel) {
    this.certificateModel = certificateModel
    this.signableData = {
      userName: certificateModel.userName,
      docName: certificateModel.docName,
      type: certificateModel.type,
      dateTime: certificateModel.dateTime,
      idCardNumber: certificateModel.idCardNumber,
      age: certificateModel.age,
      centerName: certificateModel.centerName,
      docSerialNr: certificateModel.docSerialNr
    }
  }

  async _onTransactionSuccess() {
    const hyperChainEntryPeriodicalCheck = setInterval(async () => {
      const transactionResult = await ElrondAPI.getTransactionByHash(this.txHash)
      console.log('TESTHUNI hyperblockHash: ', transactionResult.transaction.hyperblockHash)
      if (transactionResult.transaction.hyperblockHash) {
        clearInterval(hyperChainEntryPeriodicalCheck)

        const blockHash = transactionResult.transaction.hyperblockHash
        const blockResult = await ElrondAPI.getBlockByHash(blockHash)
        console.log('Hashed block: ', JSON.stringify(blockResult.hyperblock, null, 2))

        this.certificateModel.status = 'accepted'
        this.certificateModel.signature = blockHash
        await this.certificateModel.save()

        sendNotification(this.certificateModel.userName, 'Great news!', 'Your certificate has been validated')
      }
    }, 30 * 1000)
  }

  async _onTransactionFailed() {
    console.log('Hashing block failed with txHash: ', this.txHash)

    this.certificateModel.status = 'declined'
    this.certificateModel.signature = null
    await this.certificateModel.save()

    sendNotification(this.certificateModel.userName, 'Certification rejected', 'Your certificate couldn\'t been validated. Please review the provided data')
  }

  async _startPeriodicalCheck() {
    try {
      console.log('TESTHUNI periodicalCheck')
      const transactionStatusResult = await ElrondAPI.getTransactionStatusByHash(this.txHash)
      console.log('TESTHUNI Hashing block status: ', transactionStatusResult)
      switch (transactionStatusResult.status) {
        case 'pending':
        case 'received':
        case 'partially-executed':
          return 'pending'
        case 'success':
        case 'executed':
          this._onTransactionSuccess()
          clearInterval(this.periodicalCheck)
          break
        case 'fail':
        case 'not-executed':
        case 'invalid': // not enough balance
          this._onTransactionFailed()
          clearInterval(this.periodicalCheck)
          break
      }
    } catch (err) {
      console.log(`Hashing block failed - code:${err.code}, error: ${err.error}`)
      this._onTransactionFailed()
      clearInterval(this.periodicalCheck)
    }
  }

  async createBlock() {
    try {
      console.log('TESTHUNI createBlock')
      const transactionResult = await ElrondAPI.postTransaction(this.signableData)
      console.log('TESTHUNI postTransaction result: ', transactionResult)
      this.txHash = transactionResult.txHash

      this.certificateModel.status = 'blockchain verification'
      await this.certificateModel.save()

      this.periodicalCheck = setInterval(this._startPeriodicalCheck.bind(this), 30 * 1000);
      return 'Validation started'
    } catch (err) {
      return `Validation error: ${err.message}`
    }
  }

  async verifyBlock() {
    try {
      const transactionResult = await ElrondAPI.getTransactionByHash('12cfcfe46f7d12774ba895895e78dcab7a4360ffcf336f3a5416a821aa2bf001')
      console.log('TESTHUNI transactionResult: ', transactionResult.transaction.hyperblockHash)
      console.log('TESTHUNI verifyBlock')
      const blockResult = await ElrondAPI.getBlockByHash(this.certificateModel.signature)
      console.log('TESTHUNI verifyBlock blockResult: ', blockResult)
      if (!blockResult || !blockResult.hyperblock || !blockResult.hyperblock.transactions ||
        !blockResult.hyperblock.transactions.length || !blockResult.hyperblock.transactions[0].data) {
        throw new Error('Data missing')
      }
      return 'success'
    } catch (err) {
      console.log('TESTHUNI verifyBlock error: ', err)
      throw `Validation error: ${err.message}`
    }
  }
}

