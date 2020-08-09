package com.vrajan.member.batch.registration;

import org.springframework.batch.item.ItemProcessor;

import com.vrajan.member.model.Registration;
import com.vrajan.member.model.Transaction;

public class XmlItemProcessor
		implements ItemProcessor<Registration, Registration> {

	public Registration process(Registration registration) throws Exception {

		Registration registrationEntity = new Registration();
		registrationEntity.setEmployerABN(registration.getEmployerABN());

		Transaction transaction = new Transaction();
		transaction.setTransactionIdentifier(registration.getTransactionIdentifier());

		registrationEntity.setTransaction(transaction);
		registrationEntity.setTransactionIdentifier(registration.getTransactionIdentifier());
		registrationEntity.setEmployerNames(registration.getEmployerNames());
		registrationEntity.setEmployerABN(registration.getEmployerABN());
		registrationEntity.setFundIdentifier(registration.getFundIdentifier());

		return registrationEntity;
	}
}
