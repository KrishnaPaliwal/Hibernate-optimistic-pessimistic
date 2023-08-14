package com.lockingapproach;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;

public class DataInitializer {

    public void initializeData() {
        SessionFactory sessionFactory = HibernateConfiguration.getSessionFactory();

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Create five customers
            Customer customer1 = new Customer();
            customer1.setName("John Doe");

            Customer customer2 = new Customer();
            customer2.setName("Jane Smith");

            Customer customer3 = new Customer();
            customer3.setName("Michael Johnson");

            Customer customer4 = new Customer();
            customer4.setName("Emily Davis");

            Customer customer5 = new Customer();
            customer5.setName("William Wilson");

            session.save(customer1);
            session.save(customer2);
            session.save(customer3);
            session.save(customer4);
            session.save(customer5);

            // Create accounts and associate them with customers
            Account account1 = createAccount("123456", BigDecimal.valueOf(1000), customer1);
            Account account2 = createAccount("789012", BigDecimal.valueOf(500), customer2);
            Account account3 = createAccount("345678", BigDecimal.valueOf(2000), customer3);
            Account account4 = createAccount("901234", BigDecimal.valueOf(1500), customer4);
            Account account5 = createAccount("567890", BigDecimal.valueOf(300), customer5);

            // Create second account for a few customers
            Account account6 = createAccount("111111", BigDecimal.valueOf(800), customer1);
            Account account7 = createAccount("222222", BigDecimal.valueOf(700), customer3);

            session.save(account1);
            session.save(account2);
            session.save(account3);
            session.save(account4);
            session.save(account5);
            session.save(account6);
            session.save(account7);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Account createAccount(String accountNumber, BigDecimal balance, Customer customer) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCustomer(customer);
        return account;
    }
}
