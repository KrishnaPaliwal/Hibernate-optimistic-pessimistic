package com.lockingapproach;

import java.math.BigDecimal;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.SessionFactory;

public class BankingServiceMain {
	public static void main(String[] args) {

		final Logger logger = LogManager.getLogger(BankingServiceMain.class);

        SessionFactory sessionFactory = HibernateConfiguration.getSessionFactory();
	    BankingService bankingService = new BankingService(sessionFactory);

        DataInitializer dataInitializer = new DataInitializer();
        dataInitializer.initializeData();
        
	    String depositeAccountNumber = "123456";
	    BigDecimal depositAmount = new BigDecimal("100.00");
	    logger.info(depositAmount);
	    bankingService.depositFunds(depositeAccountNumber, depositAmount);

	    String withdrawAccountNumber = "123456";
	    BigDecimal withdrawAmount = new BigDecimal("50.00");

	    bankingService.withdrawFunds(withdrawAccountNumber, withdrawAmount);
	    System.out.println("finished");
	    bankingService.shutdown();
	}

}
