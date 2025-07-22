package com.litmus7;

class Employee{
	public static void main(String args[]) {
		EmployeeReader er = new EmployeeReader();
		// Call readEmployees and get the overall import response
		Response finalResponse = er.readEmployees("employees.csv");
		// Print the overall summary to the console
		System.out.println("\n" + finalResponse);
	}
}
