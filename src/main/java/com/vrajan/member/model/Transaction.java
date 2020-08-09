package com.vrajan.member.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@XmlRootElement(name = "Transaction")
public class Transaction {

	@Id
	private String transactionIdentifier;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction")
	private Set<Registration> registrations = new HashSet<Registration>(0);

	@XmlElement(name = "transactionIdentifier")
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

}
