package com.lockingapproach;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Account")
public class Account {

    @Version
    private int version;
    
    @Id
    @Column(name="accountNumber")
    private String accountNumber;
    
    @Column(name="balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
