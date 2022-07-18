import fs from 'fs'
import core from '@elrondnetwork/elrond-core-js'
import config from '../config.mjs'

//let keyFileJson = fs.readFileSync("./blockChainCredentials/sender/erd1n4hjvmmge77rpgmuwysmf4twpd766jh66hva24nlweyt7kmtkkpqz4mph5.json", { encoding: "utf8" }).trim();
let keyFileJson = `{"version":4,"id":"2e6eb8e7-0d0c-45fa-8602-fed90b784a72","address":"9d6f266f68cfbc30a37c7121b4d56e0b7dad4afad5d9d5567f7648bf5b6bb582","bech32":"erd1n4hjvmmge77rpgmuwysmf4twpd766jh66hva24nlweyt7kmtkkpqz4mph5","crypto":{"ciphertext":"84a55f86e0bdb4815cdd898a31235142646ef9a4cfa674bf89323a8430f36766bd30e6d46fef7f0ee0ca550cceae4362266abf149ffbc194381d7513800c02e8","cipherparams":{"iv":"2921ff82933249d6e59794ffc591422e"},"cipher":"aes-128-ctr","kdf":"scrypt","kdfparams":{"dklen":32,"salt":"73e322a0446e7c1458dc8908f26cfecb0085f0285f5fbdfb3fc73bdb42aa118a","n":4096,"r":8,"p":1},"mac":"d41b693529b30536df141a6e228d83c742eca7770fd6e186aa61432f1cd913e5"}}`;

let keyFileObject = JSON.parse(keyFileJson);

const signTransaction = (transactionData) => {
  let account = new core.account();
  account.loadFromKeyFile(keyFileObject, config.elrond_sender_password);

  let transaction = new core.transaction({
    nonce: transactionData.nonce,
    from: transactionData.sender,
    to: transactionData.receiver,
    value: transactionData.value,
    gasPrice: transactionData.gasPrice,
    gasLimit: transactionData.gasLimit,
    data: transactionData.data,
    chainID: transactionData.chainID,
    version: transactionData.version
  });

  let serializedTransaction = transaction.prepareForSigning();
  transaction.signature = account.sign(serializedTransaction);
  let signedTransaction = transaction.prepareForNode();
  return JSON.stringify(signedTransaction, null, 4);
}

export default {
  signTransaction
}
