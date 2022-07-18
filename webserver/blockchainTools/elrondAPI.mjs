import fetch from 'node-fetch'
import ElrondSigner from './elrondSigner.mjs'
import config from '../config.mjs'

const elrondAccessPoint = config.elrond_proxy

const getNetworkConfig = async () => {
  const response = await fetch(`${elrondAccessPoint}/network/config`, {
    method: 'get',
    headers: { 'Content-Type': 'application/json' }
  });
  if(response.status === 200){
    const resultData = await response.json();
    return resultData.data
  }
  throw response
}

const getSenderAddressNonce = async () => {
  const response = await fetch(`${elrondAccessPoint}/address/${config.elrond_senderAddress}`, {
    method: 'get',
    headers: { 'Content-Type': 'application/json' }
  });
  if(response.status === 200){
    const resultData = await response.json();
    return resultData.data.account.nonce
  }
  throw response
}

const postTransaction = async (data) => {
  try {
    const jsonData = JSON.stringify(data)
    const networkConfig = await getNetworkConfig()
    console.log('TESTHUNI postTransaction 1 networkConfig: ', networkConfig)
    const senderAddressNonce = await getSenderAddressNonce()
    const estimateGasUnits = await estimateCostOfTransaction(jsonData, networkConfig.config.erd_chain_id)

    let transaction = {
      nonce: senderAddressNonce,
      value: '0',
      receiver: config.elrond_destinationAddress,
      sender: config.elrond_senderAddress,
      gasPrice: networkConfig.config.erd_min_gas_price + 1,
      gasLimit: estimateGasUnits.txGasUnits,
      data: jsonData,
      chainID: networkConfig.config.erd_chain_id,
      version: 1
    };

    const jsonTransaction = ElrondSigner.signTransaction(transaction)

    const response = await fetch(`${elrondAccessPoint}/transaction/send`, { // TODO: TESTHUNI  change to ${elrondAccessPoint}/transaction/send
      method: 'post',
      body: jsonTransaction,
      headers: { 'Content-Type': 'application/json' },
    });


    const resultData = await response.json();
    console.log('TESTHUNI postTransaction 5 resultData: ', resultData)
    if(response.status === 200){
      if (resultData.code === 'successful'){
        return resultData.data
      } else {
        throw resultData.data
      }
    } else {
      throw resultData
    }
  } catch (err) {
    console.error(err)
    throw err
  }
}

const estimateCostOfTransaction = async (jsonData, chainID) => {
  const data = {
    value: "0",
    sender: config.elrond_senderAddress,
    receiver: config.elrond_destinationAddress,
    data: Buffer.from(jsonData).toString('base64'),
    chainID,
    version: 1
  }

  const response = await fetch(`${elrondAccessPoint}/transaction/cost`, {
    method: 'post',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  if(response.status === 200){
    const resultData = await response.json();
    return resultData.data
  }
  throw response
}

const getTransactionByHash = async (txHash) => {
  try {
    const response = await fetch(`${elrondAccessPoint}/transaction/${txHash}`, {
      method: 'get',
      headers: { 'Content-Type': 'application/json' }
    });
    if(response.status === 200){
      const resultData = await response.json();
      return resultData.data
    }
    throw response
  } catch (err) {
    console.error(err)
    throw err
  }
}

const getTransactionStatusByHash = async (txHash) => {
  try {
    const response = await fetch(`${elrondAccessPoint}/transaction/${txHash}/status`, {
      method: 'get',
      headers: { 'Content-Type': 'application/json' }
    });
    const resultData = await response.json();
    console.log('TESTHUNI postTransaction 5 resultData: ', resultData)
    if(response.status === 200){
      if (resultData.code === 'successful'){
        return resultData.data
      } else {
        throw resultData.data
      }
    } else {
      throw resultData
    }
  } catch (err) {
    console.error(err)
    throw err
  }
}

const getBlockByHash = async (hash) => {
  try {
    const response = await fetch(`${elrondAccessPoint}/hyperblock/by-hash/${hash}`, {
      method: 'get',
      headers: { 'Content-Type': 'application/json' }
    });
    if(response.status === 200){
      const resultData = await response.json();
      return resultData.data
    }
    throw response
  } catch (err) {
    console.error(err)
    throw err
  }
}

export default {
  postTransaction,
  getTransactionByHash,
  getTransactionStatusByHash,
  getBlockByHash
}