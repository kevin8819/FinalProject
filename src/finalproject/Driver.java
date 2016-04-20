package finalproject;

import java.sql.*;
import java.util.Scanner;

public class Driver
{
	public static void main(String[] args)
	{
		final String DRIVER = "org.sqlite.JDBC";
		final String CUSTOMER_DB_URL = "jdbc:sqlite:customerDB.db";
		final String USER_DB_URL = "jdbc:sqlite:userDB.db";
		Connection customerConn;
		Connection userConn;
		Scanner keyboard = new Scanner(System.in);
		int permission; //1=employee, 2=shipping, 3=manager
		StringBuilder currentUser = new StringBuilder("");
		
		try
		{
			Class.forName(DRIVER).newInstance();
			
			customerConn = DriverManager.getConnection(CUSTOMER_DB_URL);
			userConn = DriverManager.getConnection(USER_DB_URL);
			
			createCustomerDatabase(customerConn);
			
			do
			{
				permission = verifyUser(userConn, currentUser);
				if(permission == 0)
				{
					System.out.println("Invalid user name and/or password, try again.");
				}
			}while(permission == 0);

			int choice = 0;
			while(choice != 4)
			{
				printMenu(permission);
				choice = keyboard.nextInt();
				
				if(choice == 1)
				{
					viewMyData(userConn, currentUser);
				}
				else if(choice == 2 && permission >= 2)
				{
					viewCustomerData(customerConn);
				}
				else if(choice == 3 && permission >= 3)
				{
					modifyCustomerData(customerConn);
				}
				else if(choice == 4)
				{
					System.exit(0);
				}
				else
				{
					System.out.println("Invalid choice, try again.");
				}
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void createCustomerDatabase(Connection customerConn)
	{
		try
		{
			Statement stmt = customerConn.createStatement();
			
			stmt.execute("CREATE TABLE IF NOT EXISTS customer (" +
			"customer_id INT PRIMARY KEY, " +
			"first_name VARCHAR(50), " +
			"last_name VARCHAR(50), " +
			"phone VARCHAR(50), " +
			"email VARCHAR(50), " +
			"address VARCHAR(100)" + 
			")");
		}
		catch(Exception ex)
		{
			System.out.println("ERROR : " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static int verifyUser(Connection userConn, StringBuilder currentUser)
	{
		Scanner keyboard = new Scanner(System.in);
		String userName, password;
		int permission;
		
		System.out.println("Please login.");
		System.out.print("User: ");
		userName = keyboard.nextLine();
		System.out.print("Password: ");
		password = keyboard.nextLine();
		
		try
		{
			Statement stmt = userConn.createStatement();
			//SELECT user_name, password, permission FROM user WHERE user_name='userName' AND password='password'
			ResultSet result = stmt.executeQuery("SELECT user_name, password, permission FROM user WHERE user_name='" + userName + "' AND password='" + password + "'");
			permission = result.getInt("permission");
			String temp = result.getString("user_name");
			currentUser.append(temp);
			return permission;
		}
		catch(SQLException excep)
		{
			//System.out.println("ERROR: " + excep.getMessage());
			//excep.printStackTrace();
		}
		return 0;
	}
	
	public static void printMenu(int permission)
	{
		//1=employee, 2=shipping, 3=manager
		if(permission == 1)
		{
			System.out.println("1 - View my data");
			System.out.println("4 - Exit");
		}
		else if(permission == 2)
		{
			System.out.println("1 - View my data");
			System.out.println("2 - View customer data");
			System.out.println("4 - Exit");
		}
		else if(permission == 3)
		{
			System.out.println("1 - View my data");
			System.out.println("2 - View customer data");
			System.out.println("3 - Modify customer data");
			System.out.println("4 - Exit");
		}
	}
	
	public static void viewMyData(Connection userConn, StringBuilder currentUser)
	{
		try
		{
			Statement stmt = userConn.createStatement();
			//SELECT user_name, password, permission FROM user WHERE user_name='currentUser'
			ResultSet result = stmt.executeQuery("SELECT user_name, password, permission FROM user WHERE user_name='" + currentUser + "'");
			System.out.println("User name: " + result.getString("user_name") + ", pw: " + result.getString("password") + ", level: " + result.getInt("permission"));
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println();
	}
	
	public static void viewCustomerData(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			Statement stmt = customerConn.createStatement();
			int customerID;
			char choice;
			
			do
			{
				System.out.println("Enter a customer ID to view their information: ");
				customerID = Integer.parseInt(keyboard.nextLine());
				//SELECT customer_id, first_name, last_name, phone, email, address FROM customer WHERE customer_id=customerID
				ResultSet result = stmt.executeQuery("SELECT customer_id, first_name, last_name, phone, email, address FROM customer WHERE " +
					"customer_id=" + customerID);
			
				System.out.println("Customer ID: " + result.getInt("customer_id") +
					"\nFirst name: " + result.getString("first_name") +
					"\nLast name: " + result.getString("last_name") +
					"\nPhone: " + result.getString("phone") +
					"\nEmail: " + result.getString("email") +
					"\nAddress: " + result.getString("address"));
				System.out.println("View another customer? (y/n)");
				choice = keyboard.nextLine().charAt(0);
			}while(choice == 'Y' || choice == 'y');
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println();
	}
	
	public static void modifyCustomerData(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int choice = 0;

			while(choice != 4)
			{
				printModifyMenu();
				choice = Integer.parseInt(keyboard.nextLine());
				
				if(choice == 1)
				{
					addCustomer(customerConn);
				}
				else if(choice == 2)
				{
					editCustomer(customerConn);
				}
				else if(choice == 3)
				{
					removeCustomer(customerConn);
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void printModifyMenu()
	{
		System.out.println("What would you like to modify?");
		System.out.println("1 - Add customer");
		System.out.println("2 - Edit customer");
		System.out.println("3 - Remove customer");
		System.out.println("4 - Exit this menu");
	}
	
	public static void addCustomer(Connection customerConn)
	{
		try
		{
			int customerID;
			String firstName, lastName, phone, email, address;
			Scanner keyboard = new Scanner(System.in);
			
			System.out.println("Enter a customer ID: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			System.out.println("Enter a first name: ");
			firstName = keyboard.nextLine();
			System.out.println("Enter a last name: ");
			lastName = keyboard.nextLine();
			System.out.println("Enter a phone number: ");
			phone = keyboard.nextLine();
			System.out.println("Enter an email: ");
			email = keyboard.nextLine();
			System.out.println("Enter an address: ");
			address = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//INSERT INTO customer VALUES (customerID, 'firstName', 'lastName', 'phone', 'email', 'address')
			stmt.executeUpdate("INSERT INTO customer VALUES (" + customerID + ", '" + firstName + "', '" + lastName + "', '" + phone + "', '" + email + "', '" + address + "')");
		}
		catch(Exception ex)
		{
			System.out.println("CUSTOMER NOT ADDED");
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void editCustomer(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int choice = 0;
			
			printEditCustomerMenu();
			choice = Integer.parseInt(keyboard.nextLine());
			
			if(choice == 1)
			{
				editCustomerFirstName(customerConn);
			}
			else if(choice == 2)
			{
				editCustomerLastName(customerConn);
			}
			else if(choice == 3)
			{
				editCustomerPhone(customerConn);
			}
			else if(choice == 4)
			{
				editCustomerEmail(customerConn);
			}
			else if(choice == 5)
			{
				editCustomerAddress(customerConn);
			}
			else
			{
				System.out.println("Invalid choice, try again.");
			}
			
		}
		catch(Exception ex)		
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void printEditCustomerMenu()
	{
		System.out.println("Edit what?");
		System.out.println("1 - First name");
		System.out.println("2 - Last name");
		System.out.println("3 - Phone");
		System.out.println("4 - Email");
		System.out.println("5 - Address");
	}
	
	public static void editCustomerFirstName(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			String firstName;
			
			System.out.println("Enter the ID of the customer to edit: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			
			System.out.println("Enter a new first name: ");
			firstName = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//UPDATE customer SET first_name='firstName' WHERE customer_id=customerID
			stmt.executeUpdate("UPDATE customer SET first_name='" + firstName + "' WHERE customer_id=" + customerID);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void editCustomerLastName(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			String lastName;
			
			System.out.println("Enter the ID of the customer to edit: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			
			System.out.println("Enter a new last name: ");
			lastName = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//UPDATE customer SET last_name='lastName' WHERE customer_id=customerID
			stmt.executeUpdate("UPDATE customer SET last_name='" + lastName + "' WHERE customer_id=" + customerID);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void editCustomerPhone(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			String phone;
			
			System.out.println("Enter the ID of the customer to edit: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			
			System.out.println("Enter a new phone number: ");
			phone = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//UPDATE customer SET phone='phone' WHERE customer_id=customerID
			stmt.executeUpdate("UPDATE customer SET phone='" + phone + "' WHERE customer_id=" + customerID);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void editCustomerEmail(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			String email;
			
			System.out.println("Enter the ID of the customer to edit: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			
			System.out.println("Enter a new email: ");
			email = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//UPDATE customer SET email='email' WHERE customer_id=customerID
			stmt.executeUpdate("UPDATE customer SET email='" + email + "' WHERE customer_id=" + customerID);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void editCustomerAddress(Connection customerConn)
	{
		try
		{
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			String address;
			
			System.out.println("Enter the ID of the customer to edit: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			
			System.out.println("Enter a new address: ");
			address = keyboard.nextLine();
			
			Statement stmt = customerConn.createStatement();
			//UPDATE customer SET address='address' WHERE customer_id=customerID
			stmt.executeUpdate("UPDATE customer SET address='" + address + "' WHERE customer_id=" + customerID);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void removeCustomer(Connection customerConn)
	{
		try
		{
			Statement stmt = customerConn.createStatement();
			Scanner keyboard = new Scanner(System.in);
			int customerID;
			char choice;
			
			System.out.println("Enter the ID of the customer to delete: ");
			customerID = Integer.parseInt(keyboard.nextLine());
			System.out.println("Are you sure you want to delete user with ID: " + customerID + "? (y/n)");
			choice = keyboard.nextLine().charAt(0);
			
			if(choice == 'Y' || choice == 'y')
			{
				//DELETE FROM customer WHERE customer_id=customerID
				stmt.executeUpdate("DELETE FROM customer WHERE customer_id="+ customerID);
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}