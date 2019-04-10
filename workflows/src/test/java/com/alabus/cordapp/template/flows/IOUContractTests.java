package com.alabus.cordapp.template.flows;


import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;

import org.junit.Test;

import com.alabus.cordapp.template.contract.IOUContract;
import com.alabus.cordapp.template.state.IOUState;
import com.google.common.collect.ImmutableList;

import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;

public class IOUContractTests {
    static private final MockServices ledgerServices = new MockServices(asList("com.alabus.cordapp.template.contract", "com.alabus.cordapp.template.flow"));
    static private final TestIdentity megaCorp = new TestIdentity(new CordaX500Name("MegaCorp", "London", "GB"));
    static private final TestIdentity miniCorp = new TestIdentity(new CordaX500Name("MiniCorp", "London", "GB"));
    static private final int iouValue = 1;

    @Test
    public void transactionMustIncludeCreateCommand() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.fails();
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveNoInputs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("No inputs should be consumed when issuing an IOU.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveOneOutput() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("Only one output state should be created.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void lenderMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(miniCorp.getPublicKey(), new IOUContract.Commands.Create());
                tx.failsWith("All of the participants must be signers.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void borrowerMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(megaCorp.getPublicKey(), new IOUContract.Commands.Create());
                tx.failsWith("All of the participants must be signers.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void lenderIsNotBorrower() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, megaCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("The lender and the borrower cannot be the same entity.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void cannotCreateNegativeValueIOUs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(-1, miniCorp.getParty(), megaCorp.getParty(), new UniqueIdentifier()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("The IOU's value must be non-negative.");
                return null;
            });
            return null;
        }));
    }
}