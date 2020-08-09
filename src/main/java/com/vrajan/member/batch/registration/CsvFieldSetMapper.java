package com.vrajan.member.batch.registration;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.vrajan.member.model.Registration;
import com.vrajan.member.model.Transaction;

public class CsvFieldSetMapper implements FieldSetMapper<Registration> {

	@Override
	public Registration mapFieldSet(FieldSet fieldSet) throws BindException {

		Transaction transaction = new Transaction();
		transaction.setTransactionIdentifier(fieldSet.readString("transactionIdentifier"));

		Registration registration = new Registration();
		registration.setTransaction(transaction);
		registration.setTransactionIdentifier(fieldSet.readString("transactionIdentifier"));
		registration.setEmployerNames(fieldSet.readString("employerNames"));
		registration.setEmployerABN(fieldSet.readString("employerABN"));
		registration.setFundIdentifier(fieldSet.readString("fundIdentifier"));

		return registration;
	}
}
