package com.claude.jdbcexample;

import com.claude.jdbcexample.hello.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class JdbcexampleApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(JdbcexampleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JdbcexampleApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;
	//JDBC template instance has access to methods that allows to use methods to connect to a database.

	@Override
	public void run(String... strings) throws Exception {
		log.info("Creating tables");

		//execute method parses texts as SQL code.
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" +
				"id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
	// Split up the array of whole names into an array of first/last names

		//method here takes an array of strings and for each element separate them into two variables based on where the space is located
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

	// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

	// Uses JdbcTemplate's batchUpdate operation to bulk load data
		//adds firstName lastName rows columns
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
		/**
		 * For single insert statements, JdbcTemplate’s `insert method is good.
		 * But for multiple inserts, it’s better to use batchUpdate.
		 */

		log.info("Querying for customer records where first_name = 'Josh':");


		//retrieves data from all columns where a String Object, "Josh" is stored in the firstName var
		jdbcTemplate.query(
				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
		).forEach(customer -> log.info(customer.toString()));
	}
}

