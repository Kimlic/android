package com.kimlic.quorum;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class KimlicContractsContext extends Contract {
    private static final String BINARY = "0x60806040523480156200001157600080fd5b50604051602080620018a083398101604081815291516000805433600160a060020a031991821617909155600e8054909116600160a060020a03831617905560208083018190526015848401527f6163636f756e7453746f7261676541646170746572000000000000000000000060608085019190915284518085039091018152608090930193849052825191939182918401908083835b60208310620000ca5780518252601f199092019160209182019101620000a9565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600155818101819052600b828501527f6b696d6c6963546f6b656e0000000000000000000000000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620001705780518252601f1990920191602091820191016200014f565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600255818101819052601b828501527f766572696669636174696f6e436f6e7472616374466163746f727900000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620002165780518252601f199092019160209182019101620001f5565b51815160209384036101000a6000190180199092169116179052604080519290940182900382206003558181018190526011828501527f70726f766973696f6e696e6750726963650000000000000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620002bc5780518252601f1990920191602091820191016200029b565b51815160209384036101000a6000190180199092169116179052604080519290940182900382206004558181018190526015828501527f766572696669636174696f6e50726963654c69737400000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620003625780518252601f19909201916020918201910162000341565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600555818101819052601b828501527f70726f766973696f6e696e67436f6e7472616374466163746f727900000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620004085780518252601f199092019160209182019101620003e7565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600655818101819052601b828501527f636f6d6d756e697479546f6b656e57616c6c65744164647265737300000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620004ae5780518252601f1990920191602091820191016200048d565b51815160209384036101000a6000190180199092169116179052604080519290940182900382206007558181018190526011828501527f726577617264696e67436f6e74726163740000000000000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620005545780518252601f19909201916020918201910162000533565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600855818101819052600e828501527f6163636f756e7453746f726167650000000000000000000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620005fa5780518252601f199092019160209182019101620005d9565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600955818101819052601a828501527f72656c79696e67506172747953746f72616765416461707465720000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620006a05780518252601f1990920191602091820191016200067f565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600a558181018190526013828501527f72656c79696e67506172747953746f72616765000000000000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620007465780518252601f19909201916020918201910162000725565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600b55818101819052601e828501527f6174746573746174696f6e506172747953746f726167654164617074657200006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620007ec5780518252601f199092019160209182019101620007cb565b51815160209384036101000a600019018019909216911617905260408051929094018290038220600c558181018190526017828501527f6174746573746174696f6e506172747953746f726167650000000000000000006060808401919091528451808403909101815260809092019384905281519195509293508392850191508083835b60208310620008925780518252601f19909201916020918201910162000871565b5181516000196020949094036101000a939093019283169219169190911790526040519201829003909120600d5550505050610fcc80620008d46000396000f3006080604052600436106101745763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663070caeb08114610179578063072496531461019c57806309ccc1db146101bd5780630c943729146101ee57806314c6f21d146102035780631940d3991461021857806322d2dd341461022d578063293b0c4e14610242578063294cf9b0146102575780634b2c84641461026c5780634dac0a431461028d57806352380f61146102ae57806363badc6e146102cf578063715018a6146102e457806376b80b3a146102f95780638da5cb5b1461031a5780639a69a5b81461032f578063ac3f83da14610350578063b1b97f2d14610365578063b583127d14610386578063b819723a146103a7578063bc50fe24146103bc578063beb83712146103d1578063cba8e0d2146103e6578063e3c2fb7914610407578063e400fba114610428578063f1cba9df14610449578063f2fde38b1461046a578063f777550c1461048b575b600080fd5b34801561018557600080fd5b5061019a600160a060020a03600435166104a0565b005b3480156101a857600080fd5b5061019a600160a060020a036004351661052d565b3480156101c957600080fd5b506101d2610588565b60408051600160a060020a039092168252519081900360200190f35b3480156101fa57600080fd5b506101d261060c565b34801561020f57600080fd5b506101d261065f565b34801561022457600080fd5b506101d26106b2565b34801561023957600080fd5b506101d2610705565b34801561024e57600080fd5b506101d2610758565b34801561026357600080fd5b506101d26107ab565b34801561027857600080fd5b5061019a600160a060020a03600435166107fe565b34801561029957600080fd5b5061019a600160a060020a0360043516610870565b3480156102ba57600080fd5b5061019a600160a060020a03600435166108cb565b3480156102db57600080fd5b506101d261093d565b3480156102f057600080fd5b5061019a610990565b34801561030557600080fd5b5061019a600160a060020a03600435166109fc565b34801561032657600080fd5b506101d2610a57565b34801561033b57600080fd5b5061019a600160a060020a0360043516610a66565b34801561035c57600080fd5b506101d2610ad8565b34801561037157600080fd5b5061019a600160a060020a0360043516610b2b565b34801561039257600080fd5b5061019a600160a060020a0360043516610b86565b3480156103b357600080fd5b506101d2610bf8565b3480156103c857600080fd5b506101d2610c4b565b3480156103dd57600080fd5b506101d2610c9e565b3480156103f257600080fd5b5061019a600160a060020a0360043516610cf1565b34801561041357600080fd5b5061019a600160a060020a0360043516610d63565b34801561043457600080fd5b5061019a600160a060020a0360043516610dd5565b34801561045557600080fd5b5061019a600160a060020a0360043516610e47565b34801561047657600080fd5b5061019a600160a060020a0360043516610eb9565b34801561049757600080fd5b506101d2610f4d565b600054600160a060020a031633146104b757600080fd5b600e546001546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b505af1158015610526573d6000803e3d6000fd5b5050505050565b600e54600d546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600e546007546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b505af11580156105ef573d6000803e3d6000fd5b505050506040513d602081101561060557600080fd5b5051919050565b600e54600a546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e546001546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e54600c546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e546003546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e546009546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e54600b546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600054600160a060020a0316331461081557600080fd5b600e546008546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600e54600a546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a031633146108e257600080fd5b600e546002546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600e546002546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600054600160a060020a031633146109a757600080fd5b60008054604051600160a060020a03909116917ff8df31144d9c2f0f6b59d69b8b98abd5459d07f2742c4df920b25aae33c6482091a26000805473ffffffffffffffffffffffffffffffffffffffff19169055565b600e54600b546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a031681565b600054600160a060020a03163314610a7d57600080fd5b600e546003546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600e54600d546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e54600c546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a03163314610b9d57600080fd5b600e546009546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600e546006546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e546005546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600e546008546040805160e060020a6321f8a721028152600481019290925251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd5b600054600160a060020a03163314610d0857600080fd5b600e546007546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a03163314610d7a57600080fd5b600e546005546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a03163314610dec57600080fd5b600e54600480546040805160e060020a63ca446dd902815292830191909152600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a03163314610e5e57600080fd5b600e546006546040805160e060020a63ca446dd90281526004810192909252600160a060020a038481166024840152905192169163ca446dd99160448082019260009290919082900301818387803b15801561051257600080fd5b600054600160a060020a03163314610ed057600080fd5b600160a060020a0381161515610ee557600080fd5b60008054604051600160a060020a03808516939216917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a36000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600e54600480546040805160e060020a6321f8a7210281529283019190915251600092600160a060020a0316916321f8a72191602480830192602092919082900301818787803b1580156105db57600080fd00a165627a7a7230582038882367c306046d0e18a61716863167f22d624f89d143b7eaa469dec0d45cd50029";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_GETACCOUNTSTORAGEADAPTER = "getAccountStorageAdapter";

    public static final String FUNC_GETKIMLICTOKEN = "getKimlicToken";

    public static final String FUNC_GETVERIFICATIONCONTRACTFACTORY = "getVerificationContractFactory";

    public static final String FUNC_GETPROVISIONINGPRICELIST = "getProvisioningPriceList";

    public static final String FUNC_GETVERIFICATIONPRICELIST = "getVerificationPriceList";

    public static final String FUNC_GETPROVISIONINGCONTRACTFACTORY = "getProvisioningContractFactory";

    public static final String FUNC_GETCOMMUNITYTOKENWALLETADDRESS = "getCommunityTokenWalletAddress";

    public static final String FUNC_GETREWARDINGCONTRACT = "getRewardingContract";

    public static final String FUNC_GETACCOUNTSTORAGE = "getAccountStorage";

    public static final String FUNC_GETRELYINGPARTYSTORAGEADAPTER = "getRelyingPartyStorageAdapter";

    public static final String FUNC_GETRELYINGPARTYSTORAGE = "getRelyingPartyStorage";

    public static final String FUNC_GETATTESTATIONPARTYSTORAGEADAPTER = "getAttestationPartyStorageAdapter";

    public static final String FUNC_GETATTESTATIONPARTYSTORAGE = "getAttestationPartyStorage";

    public static final String FUNC_SETACCOUNTSTORAGEADAPTER = "setAccountStorageAdapter";

    public static final String FUNC_SETKIMLICTOKEN = "setKimlicToken";

    public static final String FUNC_SETVERIFICATIONCONTRACTFACTORY = "setVerificationContractFactory";

    public static final String FUNC_SETPROVISIONINGPRICELIST = "setProvisioningPriceList";

    public static final String FUNC_SETVERIFICATIONPRICELIST = "setVerificationPriceList";

    public static final String FUNC_SETPROVISIONINGCONTRACTFACTORY = "setProvisioningContractFactory";

    public static final String FUNC_SETCOMMUNITYTOKENWALLETADDRESS = "setCommunityTokenWalletAddress";

    public static final String FUNC_SETREWARDINGCONTRACT = "setRewardingContract";

    public static final String FUNC_SETACCOUNTSTORAGE = "setAccountStorage";

    public static final String FUNC_SETRELYINGPARTYSTORAGEADAPTER = "setRelyingPartyStorageAdapter";

    public static final String FUNC_SETRELYINGPARTYSTORAGE = "setRelyingPartyStorage";

    public static final String FUNC_SETATTESTATIONPARTYSTORAGEADAPTER = "setAttestationPartyStorageAdapter";

    public static final String FUNC_SETATTESTATIONPARTYSTORAGE = "setAttestationPartyStorage";

    public static final Event OWNERSHIPRENOUNCED_EVENT = new Event("OwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x79eb652ab7ba5b62c904f27768858061978ce4b5");
        _addresses.put("10", "0x9c61f536ccda8de54103ec4e72b2e35b23c6beef");
    }

    protected KimlicContractsContext(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected KimlicContractsContext(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<KimlicContractsContext> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String storageAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(storageAddress)));
        return deployRemoteCall(KimlicContractsContext.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<KimlicContractsContext> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String storageAddress) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(storageAddress)));
        return deployRemoteCall(KimlicContractsContext.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<OwnershipRenouncedEventResponse> getOwnershipRenouncedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, transactionReceipt);
        ArrayList<OwnershipRenouncedEventResponse> responses = new ArrayList<OwnershipRenouncedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipRenouncedEventResponse> ownershipRenouncedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipRenouncedEventResponse>() {
            @Override
            public OwnershipRenouncedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, log);
                OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipRenouncedEventResponse> ownershipRenouncedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPRENOUNCED_EVENT));
        return ownershipRenouncedEventObservable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventObservable(filter);
    }

    public RemoteCall<String> getAccountStorageAdapter() {
        final Function function = new Function(FUNC_GETACCOUNTSTORAGEADAPTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getKimlicToken() {
        final Function function = new Function(FUNC_GETKIMLICTOKEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getVerificationContractFactory() {
        final Function function = new Function(FUNC_GETVERIFICATIONCONTRACTFACTORY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getProvisioningPriceList() {
        final Function function = new Function(FUNC_GETPROVISIONINGPRICELIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getVerificationPriceList() {
        final Function function = new Function(FUNC_GETVERIFICATIONPRICELIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getProvisioningContractFactory() {
        final Function function = new Function(FUNC_GETPROVISIONINGCONTRACTFACTORY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getCommunityTokenWalletAddress() {
        final Function function = new Function(FUNC_GETCOMMUNITYTOKENWALLETADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getRewardingContract() {
        final Function function = new Function(FUNC_GETREWARDINGCONTRACT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getAccountStorage() {
        final Function function = new Function(FUNC_GETACCOUNTSTORAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getRelyingPartyStorageAdapter() {
        final Function function = new Function(FUNC_GETRELYINGPARTYSTORAGEADAPTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getRelyingPartyStorage() {
        final Function function = new Function(FUNC_GETRELYINGPARTYSTORAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getAttestationPartyStorageAdapter() {
        final Function function = new Function(FUNC_GETATTESTATIONPARTYSTORAGEADAPTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getAttestationPartyStorage() {
        final Function function = new Function(FUNC_GETATTESTATIONPARTYSTORAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> setAccountStorageAdapter(String accountStorageAdapterAddress) {
        final Function function = new Function(
                FUNC_SETACCOUNTSTORAGEADAPTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountStorageAdapterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setKimlicToken(String kimlicTokenAddress) {
        final Function function = new Function(
                FUNC_SETKIMLICTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(kimlicTokenAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setVerificationContractFactory(String verificationContractFactoryAddress) {
        final Function function = new Function(
                FUNC_SETVERIFICATIONCONTRACTFACTORY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(verificationContractFactoryAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setProvisioningPriceList(String provisioningPriceListAddress) {
        final Function function = new Function(
                FUNC_SETPROVISIONINGPRICELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(provisioningPriceListAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setVerificationPriceList(String verificationPriceListAddress) {
        final Function function = new Function(
                FUNC_SETVERIFICATIONPRICELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(verificationPriceListAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setProvisioningContractFactory(String provisioningContractFactoryAddress) {
        final Function function = new Function(
                FUNC_SETPROVISIONINGCONTRACTFACTORY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(provisioningContractFactoryAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setCommunityTokenWalletAddress(String communityTokenWalletAddressAddress) {
        final Function function = new Function(
                FUNC_SETCOMMUNITYTOKENWALLETADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(communityTokenWalletAddressAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setRewardingContract(String rewardingContractAddress) {
        final Function function = new Function(
                FUNC_SETREWARDINGCONTRACT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(rewardingContractAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setAccountStorage(String accountStorageAddress) {
        final Function function = new Function(
                FUNC_SETACCOUNTSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(accountStorageAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setRelyingPartyStorageAdapter(String relyingPartyStorageAdapterAddress) {
        final Function function = new Function(
                FUNC_SETRELYINGPARTYSTORAGEADAPTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(relyingPartyStorageAdapterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setRelyingPartyStorage(String relyingPartyStorageAddress) {
        final Function function = new Function(
                FUNC_SETRELYINGPARTYSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(relyingPartyStorageAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setAttestationPartyStorageAdapter(String attestationPartyStorageAdapterAddress) {
        final Function function = new Function(
                FUNC_SETATTESTATIONPARTYSTORAGEADAPTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(attestationPartyStorageAdapterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setAttestationPartyStorage(String attestationPartyStorageAddress) {
        final Function function = new Function(
                FUNC_SETATTESTATIONPARTYSTORAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(attestationPartyStorageAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static KimlicContractsContext load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new KimlicContractsContext(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static KimlicContractsContext load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new KimlicContractsContext(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class OwnershipRenouncedEventResponse {
        public Log log;

        public String previousOwner;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }
}
