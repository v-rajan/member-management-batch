package com.vrajan.member.batch.registration;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.vrajan.member.model.Registration;

public class MergeDbRowMapper implements RowMapper<Registration> {

	@Override
	public Registration mapRow(ResultSet rs, int rowNum) throws SQLException {

		Registration registration = new Registration();
		registration.setId(null);
		registration.setEmployerNames(rs.getString("employer_names"));
		registration.setEmployerABN(rs.getString("employerabn"));
		registration.setTransactionIdentifier(rs.getString("transaction_identifier"));
		registration.setFundIdentifier(rs.getString("fund_identifier"));

		return registration;
	}

}
