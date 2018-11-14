package edu.gcu.bootcamp.java.william.palowski.bankingapplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Banking application created by 
 * William Palowski
 * Eric Stoll
 * Christina Herman
 * 
 */
public class Bank {
    /*
     * We create the scanner object to be used for all inputs in the bank application.
     * We only need to create one SessionFactory since we are only using one database for the application.
     * We open the session at the beginning to be used through out the application.
     * All numbers will be in BigDecimal format so that all numbers are only two decimal places.
     */
	static Scanner scanner = new Scanner(System.in);
	static SessionFactory factory = new Configuration().configure().buildSessionFactory();
	static private BigDecimal bd;
	
	private String name; //used to set name of bank
	static Bank bank = new Bank();
	
	public static void main(String[] args) {
	//	bank.addEmployee();  Only used to load the employees.
		bank.startMenu();
	}
	
	/**
	 * Only used to create all employees of the bank.  There are only three for 
	 * this application.
	 */
	
	public void addEmployee() {
		Employee emp = new Employee();
		emp.setEmp_Id(1);
		emp.setF_Name("Bill");
		emp.setL_Name("Palowski");
		emp.setUsername("palowski01");
		emp.setPassword(EncryptPassword.encrypt("william73"));
		
		Employee emp1 = new Employee();
		emp1.setEmp_Id(2);
		emp1.setF_Name("Eric");
		emp1.setL_Name("Stoll");
		emp1.setUsername("EStoll");
		emp1.setPassword(EncryptPassword.encrypt("Java42"));
		
		Employee emp2 = new Employee();
		emp2.setEmp_Id(3);
		emp2.setF_Name("Christina");
		emp2.setL_Name("Herman");
		emp2.setUsername("KikiHerm");
		emp2.setPassword(EncryptPassword.encrypt("!@#KikiHerm"));
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(emp);
		session.save(emp1);
		session.save(emp2);
		session.getTransaction().commit();	
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Start menu is used for login of the customer or administrator (the employees are the admins)
	 */
	public void startMenu() {
		
		String option;
		int o;
		do {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println();
			System.out.println("          WELCOME TO GCU BANK");
			System.out.println();
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("Pick an option: ");
			System.out.println("------------------------");
			System.out.println(" 1: : Customer sign in");
			System.out.println(" 2: : Admin sign in");
			System.out.println(" 3: : Exit");
			System.out.println("------------------------");
			option = scanner.nextLine();
			o = Integer.parseInt(option);
			switch(o) {
			case 1: bank.checkCustomer();
			break;
			case 2: bank.checkAdmin();
			break;
			case 3: bank.displayExitScreen();
			}
		}while(o != 3);
	}
	
/**
 * checkAdmin will check the employee table in the database to 
 * see if they are allowed access to the banking application.
 * This is the admin login screen.
*/
	public void checkAdmin() {
		System.out.println("Enter user name: ");
		String userName = scanner.nextLine();
		System.out.println("Enter password: ");
		String password = scanner.nextLine();
		Employee emp = null;
		Session session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Employee> criteria = cb.createQuery(Employee.class);
		Root<Employee> groot = criteria.from(Employee.class);
		criteria.select(groot);
		criteria.where(cb.and(cb.equal(groot.get("username"),userName),cb.equal(groot.get("password"),EncryptPassword.encrypt(password))));
		try {
		emp = session.createQuery(criteria).getSingleResult();
		bank.adminMenu();
		}
		catch (NoResultException e) {
		    System.out.println("Wrong username and password");
		    bank.checkAdmin();
		}	
		
	}
	
/**
 * The customer menu is accessed after customer login.
 * @param customer
 */
	public void customerMenu(Customer customer) {
		String option;
		do {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("               MAIN MENU");
			System.out.println("                 "+ "GCU BANK");
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("Pick an option: ");
			System.out.println("-----------------------");
			System.out.println(" 1: : Deposit to Checking");
			System.out.println(" 2: : Deposit to Savings");
			System.out.println(" 3: : Withdraw from checking");
			System.out.println(" 4: : Withdraw from Savings");			
			System.out.println(" 5: : Get balance");
			System.out.println(" 6: : Make Loan Payment");
			System.out.println(" 7: : Get monthly statement");
			System.out.println("------------------------");
			System.out.println(" 9: : Logout");
			option = scanner.nextLine();
			bank.actionMenu(Integer.parseInt(option), customer);
		} while (Integer.parseInt(option) != 9);
	}
/**
 * This is the customer login screen.  It is used to verify customers that have already 
 * been created.	
 */
	public void checkCustomer() {
		
		System.out.println("Enter user name: ");
		String userName = scanner.nextLine();
		System.out.println("Enter password: ");
		String password = scanner.nextLine();
		Customer customer = null;
		Session session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
		Root<Customer> groot = criteria.from(Customer.class);
		criteria.select(groot);
		
		//This criteria string is used to check the customer table for the username and password combination.
		//"user_name" is the name of the column and userName is the one entered above
		//"password" is the name of the column and password is the password entered above.
		//The password is encrypted before it is checked.
		
		criteria.where(cb.and(cb.equal(groot.get("user_name"),userName),cb.equal(groot.get("password"),EncryptPassword.encrypt(password))));
		try {
		customer = session.createQuery(criteria).getSingleResult();
		bank.customerMenu(customer);
		}
		catch (NoResultException e) {
		    System.out.println("Wrong username or password");
		    bank.checkCustomer();
		}	
		
	}

	/**
	 * The actionMeun takes the option picked from the customerMenu and
	 * carrys out the appropriate action.
	 * @param option
	 * @param customer
	 */
	private void actionMenu(int option, Customer customer) {
		List<Account> accountList = null;
		
		//Empty objects used to access the doWithdraw and doDeposit methods within each class
		//Checking, Saving, and Loan
		
		Checking checking = new Checking();
		Saving saving = new Saving();
		Loan loan = new Loan();
		Account acct;
		int id = customer.getId();
		Session session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = cb.createQuery(Account.class);
		Root<Account> groot = criteria.from(Account.class);
		criteria.select(groot);
		
		//Gets the customer ID and grabs all of the accounts under that ID
		//and puts them in an ArrayList called accountList.
		
		criteria.where(cb.equal(groot.get("cust_id"),id));
		accountList = session.createQuery(criteria).getResultList();
		
				switch(option) {
				//Deposits into checking
				case 1: criteria.where(cb.and(cb.equal(groot.get("cust_id"),id),cb.equal(groot.get("account_type"),"checking")));
					try {
						acct = session.createQuery(criteria).getSingleResult();
						System.out.println("How much would you like to deposit: ");
						String amount = scanner.nextLine();
						bd = new BigDecimal(amount);
						checking.doDeposit(bd, acct);
					}
					catch (NoResultException e) {
						System.out.println("You do not have a checking account");
						bank.customerMenu(customer);
					}
					break;
				//Deposits into savings
				case 2:  criteria.where(cb.and(cb.equal(groot.get("cust_id"),id),cb.equal(groot.get("account_type"),"saving")));
					try {
						acct = session.createQuery(criteria).getSingleResult();
						System.out.println("How much would you like to deposit: ");
						String amount = scanner.nextLine();
						bd = new BigDecimal(amount);
						saving.doDeposit(bd, acct);
					}
					catch (NoResultException e) {
						System.out.println("You do not have a savings account");
						bank.customerMenu(customer);
					}
					break;
				//Withdraws from chekcing	
				case 3:  criteria.where(cb.and(cb.equal(groot.get("cust_id"),id),cb.equal(groot.get("account_type"),"checking")));
					try {
						acct = session.createQuery(criteria).getSingleResult();
						System.out.println("How much would you like to withdraw: ");
						String amount = scanner.nextLine();
						bd = new BigDecimal(amount);
						checking.doWithdraw(bd, acct);
					}
					catch (NoResultException e) {
						System.out.println("You do not have a checking account");
						bank.customerMenu(customer);
					}
					break;
				//Withdraws from savings
				case 4:  criteria.where(cb.and(cb.equal(groot.get("cust_id"),id),cb.equal(groot.get("account_type"),"saving")));
					try {
						acct = session.createQuery(criteria).getSingleResult();
						System.out.println("How much would you like to withdraw: ");
						String amount = scanner.nextLine();
						bd = new BigDecimal(amount);
						saving.doWithdraw(bd, acct);
					}
					catch (NoResultException e) {
						System.out.println("You do not have a savings account");
						bank.customerMenu(customer);
					}
					break;
				//Prints out all balances for all accounts for a particular customer
				case 5: for (Account a : accountList) {
					System.out.println("You account balance for " + a.getAccount_num() + " is " + a.getAccount_balance());
				}
					break;
				//Makes a payment on the Loan
				case 6:  criteria.where(cb.and(cb.equal(groot.get("cust_id"),id),cb.equal(groot.get("account_type"),"loan")));
					try {
						acct = session.createQuery(criteria).getSingleResult();
						System.out.println("How much would you like to make a payment: ");
						String amount = scanner.nextLine();
						bd = new BigDecimal(amount);
						loan.doWithdraw(bd, acct);
					}
					catch (NoResultException e) {
						System.out.println("You do not have a loan");
						bank.customerMenu(customer);
					}
					break;
				//Generates the statement for all accounts for a particular customer	
				case 7: criteria.where(cb.equal(groot.get("cust_id"),customer.getId()));
						try {
							accountList = session.createQuery(criteria).getResultList();
							bank.doEndOfMonth(accountList);
						}
						catch (NoResultException e) {
							System.out.println("Customer does not exist");
							bank.customerMenu(customer);
						}
					break;
				//Logs out of the customer menu 
				case 9: System.out.println("User has logged out");
					break;
				default: System.out.println("Wrong Entry");
				}		
	}
	
	/**
	 * The admin menu is accessed by only the employees of the bank.
	 * It allows to pull all customers in the bank.
	 * It allows for the creation of customers
	 * It can get all customer transactions by customer ID number.
	 */
	private void adminMenu() {
		String option;
		do {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("              ADMIN MENU");
			System.out.println("                 "+ "GCU BANK");
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("Pick an option: ");
			System.out.println("-----------------------");
			System.out.println(" 1: : Customer List");
			System.out.println(" 2: : Create new Customer");
			System.out.println(" 3: : Get customer statement");
			System.out.println("------------------------");
			System.out.println(" 9: : Logout");
			option = scanner.nextLine();
			bank.adminActionMenu(Integer.parseInt(option));
		} while (Integer.parseInt(option) != 9);
	}
	
	/**
	 * takes the option from the admin menu and accesses the different areas
	 * for the administrators
	 * 
	 * @param option
	 */
	public void adminActionMenu(int option) {
		
		switch(option) {
		//creates the customer list for the bank
		case 1: createCustomerList();
			break;
		//allows for the creation of customers
		case 2: createCustomer();
			break;
		//gets all customer transactions by customer ID number
		case 3: System.out.println("Enter id for customer statement: "); 
				String customerId = scanner.nextLine();
				Session session = factory.openSession();
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<Account> criteria = cb.createQuery(Account.class);
				Root<Account> groot = criteria.from(Account.class);
				criteria.select(groot);
				criteria.where(cb.equal(groot.get("cust_id"),Integer.parseInt(customerId)));
				try {
				List<Account> acct = session.createQuery(criteria).getResultList();
				bank.doEndOfMonth(acct);
				}
				catch (NoResultException e) {
					System.out.println("Customer does not exist");
					bank.adminMenu();
				}
			break;
		//logs out of admin menu
		case 9: System.out.println("user has logged out");		
			break;
		default: System.out.println("Wrong Entry");
		}
	}
	
	/**
	 * Generates the customer list for the bank
	 */
	public void createCustomerList() {
		
		List<Customer> allUsers = null;
        
		//pulls all customers from the customer table
        try {
            Session session = factory.openSession(); 
            CriteriaQuery<Customer> criteriaQuery = session.getCriteriaBuilder().createQuery(Customer.class);
            criteriaQuery.from(Customer.class);
            allUsers = session.createQuery(criteriaQuery).getResultList();
        }
        catch(Exception e) {
        
        }
        for(Customer h: allUsers) {
            System.out.println(h);
        }
		
	}

/**
 * Creates the customers for the bank.
 */
	public void createCustomer() {
		Customer user1 = new Customer();

		System.out.print("Enter first name: ");
		user1.setFirst_name(scanner.nextLine());
		System.out.print("Enter last name: ");
		user1.setLast_name(scanner.nextLine());
		System.out.print("Pick login name: ");
		user1.setUser_name(scanner.nextLine());
		System.out.print("Pick password: ");
		user1.setPassword(EncryptPassword.encrypt(scanner.nextLine())); //encrypts password before it is stored into the table
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(user1);
		session.getTransaction().commit();
		
		//customers must create an account if they are going to belong to the bank
		bank.createAccount(user1.getId());
		
	}

	/**
	 * creates the accounts for the customer ID that is passed in.
	 * @param cust_id
	 */
	public void createAccount(int cust_id) {
		Account account = new Account();
		BankTransaction trans = new BankTransaction();
		account.setCust_id(cust_id);
		String userInput = "Y";
		Random rnd = new Random();
		String accountNum;
		String phrase = "";
		int n;
	
		//Gives them the option of creating a checking, savings, or loan accounts.  They must create at least one.
		do {
			System.out.print("What type of account would you like to create? 1 for Saving, "
					+ "2 for Checking, 3 for Loan: ");
			String option = scanner.nextLine();
			int o = Integer.parseInt(option);
			switch(o) {
			//creates a savings account with a random account number.
			case 1: account.setAccount_type("saving");
					n = 100000 + rnd.nextInt(900000);
					accountNum = cust_id +"-SAV" + n; //appends the random number with the customer ID followed by -SAV for savings
					account.setAccount_num(accountNum);
					phrase = "Enter the amount you would like to deposit: ";
					break; 
			//creates a checking account with a random account number.
			case 2: account.setAccount_type("checking");
					n = 100000 + rnd.nextInt(900000);
					accountNum = cust_id +"-CHK" + n; //appends the random number with the customer ID followed by -CHK for checking
					account.setAccount_num(accountNum);
					phrase = "Enter the amount you would like to deposit: ";
					break; 
			//creates a loan account with a random account number.
			case 3: account.setAccount_type("loan");
					n = 100000 + rnd.nextInt(900000);
					accountNum = cust_id +"-LOA" + n; //appends the random number with teh customer ID followed by -LOA for loan
					account.setAccount_num(accountNum);
					phrase = "Enter the amount for the loan: ";
					break; 
			default: System.out.println("You have entered an incorrect number... ");
			
			}	
		
		System.out.println(phrase);
		bd = new BigDecimal(scanner.nextLine());
		account.setAccount_balance(bd);
		trans = bank.createTransaction(account, bd, "initial deposit", bd); //creates a transaction for initial deposit into any account
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(account);
		session.save(trans);
		session.getTransaction().commit();
		
		System.out.println("Would you like to add an account? Y or N");
		userInput = scanner.nextLine().toUpperCase();
		
		}while(userInput.equals("Y"));
	}
	
	/**
	 * Creates a transaction object and passes it back to where it was called to be stored in the transaction table
	 * @param account
	 * @param amount
	 * @param option
	 * @param balance
	 * @return
	 */
	public BankTransaction createTransaction(Account account, BigDecimal amount, String option, BigDecimal balance) {
        BankTransaction trans = new BankTransaction();
        
        trans.setAccount_num(account.getAccount_num());
        trans.setAmount_trans(amount);
        trans.setTrans_type(option);
        trans.setBalance(balance);
        trans.setDate(LocalDate.now());
        
        return trans;
    }
	/**
	 * Calculates the statements for a customer either from the admin menu or from the customer number
	 * @param acct
	 */
	public void doEndOfMonth(List<Account> acct) {
		
		//Pulls the individual account objects from the account list that is passed in.
		Account checking = acct.stream().filter(x -> "checking".equals(x.getAccount_type())).findAny().orElse(null);
		Account saving = acct.stream().filter(x -> "saving".equals(x.getAccount_type())).findAny().orElse(null);
		Account loan = acct.stream().filter(x -> "loan".equals(x.getAccount_type())).findAny().orElse(null);
		
		//runs a check on the checking object to make sure they have a checking account.
		//if there is a checking account it will print out all transactions from the checking account
		if (checking != null) {
			Session session = factory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<BankTransaction> criteria = cb.createQuery(BankTransaction.class);
			Root<BankTransaction> groot = criteria.from(BankTransaction.class);
			criteria.select(groot);
			criteria.where(cb.equal(groot.get("account_num"), checking.getAccount_num()));
			List<BankTransaction> checkingTransactions = session.createQuery(criteria).getResultList();
			for(BankTransaction trans : checkingTransactions) {
				System.out.println(trans);
			}
		}
		
		//runs a check on the saving object to make sure they have a savings account.
		//if there is a savings account it will print out all transactions from the savings account
		if (saving != null) {
			Saving.savingEndOfMonth(saving);
			Session session = factory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<BankTransaction> criteria = cb.createQuery(BankTransaction.class);
			Root<BankTransaction> groot = criteria.from(BankTransaction.class);
			criteria.select(groot);
			criteria.where(cb.equal(groot.get("account_num"), saving.getAccount_num()));
			List<BankTransaction> savingTransactions = session.createQuery(criteria).getResultList();
			for(BankTransaction trans : savingTransactions) {
				System.out.println(trans);
			}
		}
		
		//runs a check on the loan object to make sure they have a loan account.
		//if there is a loan account it will print out all transactions from the loan account
		if (loan != null) {
			Loan.loanEndOfMonth(loan);
			Session session = factory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<BankTransaction> criteria = cb.createQuery(BankTransaction.class);
			Root<BankTransaction> groot = criteria.from(BankTransaction.class);
			criteria.select(groot);
			criteria.where(cb.equal(groot.get("account_num"), loan.getAccount_num()));
			List<BankTransaction> loanTransactions = session.createQuery(criteria).getResultList();
			for(BankTransaction trans : loanTransactions) {
				System.out.println(trans);
			}
		}
		
	}
	// displays exit screen from application
	public void displayExitScreen() {
        bank.setName("GCU Credit Union");
        System.out.println("Thank you for banking with " + bank.getName());
    }
}
