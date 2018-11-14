package edu.gcu.bootcamp.java.william.palowski.bankingapplication;

import java.math.BigDecimal;
//This is the deposit method that handles all of the deposits for the banking application.

public interface Deposit {
	void doDeposit(BigDecimal amount, Account account);

}
