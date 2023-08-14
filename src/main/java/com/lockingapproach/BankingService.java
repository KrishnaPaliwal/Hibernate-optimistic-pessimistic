package com.lockingapproach;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankingService {

	private static final Logger logger = LogManager.getLogger(BankingService.class);

	private final SessionFactory sessionFactory;
	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	public BankingService(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void depositFunds(String accountNumber, BigDecimal amount) {
		executorService.execute(() -> {
			try (Session session = sessionFactory.openSession()) {
				Transaction tx = session.beginTransaction();

				try {
					@SuppressWarnings("deprecation")
					Account account = (Account) session.createCriteria(Account.class)
							.add(Restrictions.eq("accountNumber", accountNumber))
							.setLockMode(LockMode.OPTIMISTIC_FORCE_INCREMENT).uniqueResult();

					account.setBalance(account.getBalance().add(amount));
					session.update(account);

					tx.commit();
				} catch (Exception e) {
					if (tx != null) {
						tx.rollback();
					}
					logger.error("Error during deposit operation", e);
				}
			} catch (Exception e) {
				logger.error("Error opening or closing session", e);
			}
		});
	}

	public void withdrawFunds(String accountNumber, BigDecimal amount) {
		executorService.execute(() -> {
			try (Session session = sessionFactory.openSession()) {
				Transaction tx = session.beginTransaction();

				try {
					@SuppressWarnings("deprecation")
					Account account = (Account) session.createCriteria(Account.class)
							.add(Restrictions.eq("accountNumber", accountNumber))
							.setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).uniqueResult();
					if (account.getBalance().compareTo(amount) >= 0) {
						account.setBalance(account.getBalance().subtract(amount));
						session.update(account);

						tx.commit();
					} else {
						tx.rollback();
						logger.warn("Insufficient funds for withdrawal");
					}
				} catch (Exception e) {
					if (tx != null) {
						tx.rollback();
					}
					logger.error("Error during withdrawal operation", e);
				}
			}
		});
	}

	public void shutdown() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Error shutting down executor service", e);
		}
		sessionFactory.close();
	}
}
