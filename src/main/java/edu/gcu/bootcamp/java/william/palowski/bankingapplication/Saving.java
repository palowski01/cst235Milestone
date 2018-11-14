package edu.gcu.bootcamp.java.william.palowski.bankingapplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Saving extends Account implements Withdraw, Deposit{
	private static BigDecimal serviceFee = new BigDecimal(25.00); 
	private static BigDecimal INTERESTRATESAVE =new BigDecimal(.03);
	private static BigDecimal MINIMUMBAL = new BigDecimal(200.00); 
	private static BigDecimal interestPerMonth = new BigDecimal(.0025);

	static SessionFactory factory = new Configuration().configure().buildSessionFactory();
	static BankTransaction trans = new BankTransaction();
	Bank bank = new Bank();

	public BigDecimal getMinimumBal() {
		return MINIMUMBAL;
	}

	public BigDecimal getInterestRateSave() {
		return INTERESTRATESAVE;
	}

	public void setInterestRateSave(BigDecimal interestRate) {
		this.INTERESTRATESAVE = interestRate;
	}
	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}
	
	/**
	 * Implements the doDeposit interface to create deposit transactions in savings
	 * and deposits money to the saving account for the customer
	 */
	public void doDeposit(BigDecimal amount, Account account) {
		BigDecimal result = amount.add(account.getAccount_balance());
		account.setAccount_balance(result);
		trans = bank.createTransaction(account, amount, "Deposit", result);
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(trans);
		session.saveOrUpdate(account);
		session.getTransaction().commit();
	}
	
/**
 * Implements the doWithdraw interface to create withdraw transactions in savings
 * and withdraws money from the saving account for the customer
 */
	public void doWithdraw(BigDecimal amount, Account account) {
		BigDecimal result = account.getAccount_balance().subtract(amount);
		
		account.setAccount_balance(result);
		trans = bank.createTransaction(account, amount, "Withdraw", result);
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(trans);
		session.saveOrUpdate(account);
		session.getTransaction().commit();
		
		Transaction tx =session.beginTransaction();
		CriteriaBuilder bob = session.getCriteriaBuilder();
		CriteriaUpdate<Account> criteria = bob.createCriteriaUpdate(Account.class);
		Root<Account> groot = criteria.from(Account.class);
		criteria.set(groot.get("account_balance"), account.getAccount_balance());
		criteria.where(bob.equal(groot.get("account_num"), account.getAccount_num()));
		session.createQuery(criteria).executeUpdate();
		tx.commit();
	
		
	}
	/**
	 * Does end of month transactions for the savigs account and checks if it is below the minimum balance
	 * of 200 dollars.  If it is below the minimum balance it applies a service fee and creates a transaction
	 * for the fee being added.
	 * @param account
	 */
	public static void savingEndOfMonth(Account account) {
	if (account.getAccount_balance().compareTo(MINIMUMBAL) > 200) {
		account.setAccount_balance(account.getAccount_balance().add(serviceFee));
		trans.setAccount_num(account.getAccount_num());
		trans.setAmount_trans(serviceFee);
		trans.setDate(LocalDate.now());
		trans.setTrans_type("Service Fee");
		trans.setBalance(account.getAccount_balance());
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(trans);
		session.saveOrUpdate(account);
		session.getTransaction().commit();
		
		       
	 }else {
			 BigDecimal interest = account.getAccount_balance().multiply(interestPerMonth);
			 account.setAccount_balance(interest.add(account.getAccount_balance()));
			 
			trans.setAccount_num(account.getAccount_num());
			trans.setAmount_trans(interest);
			trans.setDate(LocalDate.now());
			trans.setTrans_type("Interest added");
			trans.setBalance(account.getAccount_balance());
			
			Session session = factory.openSession();
			session.beginTransaction();
			session.save(trans);
			session.saveOrUpdate(account);
			session.getTransaction().commit();
		}
	}

}
