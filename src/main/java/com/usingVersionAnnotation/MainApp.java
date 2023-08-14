package com.usingVersionAnnotation;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) throws InterruptedException {

    	LOGGER.setLevel(Level.INFO);

        // Create a Hibernate session factory
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        try {
            // Create a new book
            Book book = new Book();
            book.setTitle("Introduction to Hibernate");

            // Save the book to the database
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();
                session.persist(book);
                tx.commit();
                LOGGER.info("Book saved: " + book);
            }

            // Simulate concurrent modification
            new Thread(() -> {
                try (Session session = sessionFactory.openSession()) {
                    Transaction tx = session.beginTransaction();

                    // Load the book and modify its title
                    Book loadedBook = session.get(Book.class, book.getId());
                    loadedBook.setTitle("Hibernate in Depth");
                    //Thread.sleep(2);
                    tx.commit();
                    LOGGER.info("Concurrent modification committed: " + loadedBook);
                } catch (Exception e) {
					e.printStackTrace();
				}
            }).start();

            // Main thread updates the book
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();

                // Load the book and modify its title
                Book loadedBook = session.get(Book.class, book.getId());
                loadedBook.setTitle("Advanced Hibernate");
               // Thread.sleep(2);
                // Commit the changes
                tx.commit();
                LOGGER.info("Main thread modification committed: " + loadedBook);
            }
        } catch (Exception e) {
            LOGGER.severe("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the session factory
            sessionFactory.close();
        }
    }
}
