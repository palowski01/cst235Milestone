package edu.gcu.bootcamp.java.william.palowski.bankingapplication;

import java.math.BigDecimal;
// Interface for the withdraw method that is used throughout the banking application

public interface Withdraw {
	void doWithdraw(BigDecimal amount, Account account);

}