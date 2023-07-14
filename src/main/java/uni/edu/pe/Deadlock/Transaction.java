package uni.edu.pe.Deadlock;

import java.util.List;

public class Transaction {
	String name;
	List<String> operations;

	public Transaction(String name, List<String> operations) {
		this.name = name;
		this.operations = operations;
	}
}
