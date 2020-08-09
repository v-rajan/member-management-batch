package com.vrajan.member.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "registration")
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonRootName(value = "Registration")
@XmlRootElement(name = "Registration")
public class Registration {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	private String transactionIdentifier;

	private String employerNames;

	private String employerABN;

	private String fundIdentifier;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TRANSACTION_FK", nullable = false)
	private Transaction transaction;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name = "transactionIdentifier")
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	@XmlElement(name = "employerNames")
	public String getEmployerNames() {
		return employerNames;
	}

	public void setEmployerNames(String employerNames) {
		this.employerNames = employerNames;
	}

	@XmlElement(name = "employerABN")
	public String getEmployerABN() {
		return employerABN;
	}

	public void setEmployerABN(String employerABN) {
		this.employerABN = employerABN;
	}

	@XmlElement(name = "fundIdentifier")
	public String getFundIdentifier() {
		return fundIdentifier;
	}

	public void setFundIdentifier(String fundIdentifier) {
		this.fundIdentifier = fundIdentifier;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

}
