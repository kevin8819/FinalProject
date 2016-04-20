package finalproject;

import java.sql.*;
import java.util.Scanner;

public class UserCreator
{
	public static void main(String[] args)
	{
		final String DRIVER = "org.sqlite.JDBC";
		final String DB_URL = "jdbc:sqlite:userDB.db";
		Connection conn;
		
		try
		{
			Class.forName(DRIVER).newInstance();
			
			conn = DriverManager.getConnection(DB_URL);
			
			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE IF NOT EXISTS user (user_name VARCHAR(50) PRIMARY KEY, password VARCHAR(50), permission INT)");
			
			Scanner keyboard = new Scanner(System.in);
			
			int choice = 0;
			while(choice != 6)
			{
				printMenu();
				choice = keyboard.nextInt();
				
				if(choice == 1)
				{
					addUser(conn);
				}
				else if(choice == 2)
				{
					changePassword(conn);
				}
				else if(choice == 3)
				{
					changePermission(conn);
				}
				else if(choice == 4)
				{
					deleteUser(conn);
				}
				else if(choice == 5)
				{
					showUsers(conn);
				}
				else if(choice == 6)
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
	
	public static void printMenu()
	{
		System.out.println("Select an action: ");
		System.out.println("1 - Add user");
		System.out.println("2 - Change user password");
		System.out.println("3 - Change user permission");
		System.out.println("4 - Delete user");
		System.out.println("5 - Show users");
		System.out.println("6 - Exit");
	}
	
	public static void addUser(Connection conn)
	{
		char choice;
		try
		{
			Statement stmt = conn.createStatement();
			Scanner keyboard = new Scanner(System.in);
			String userName, password;
			int permission;
			
			do
			{
				System.out.println("Enter a user name: ");
				userName = keyboard.nextLine();
				System.out.println("Enter a password: ");
				password = keyboard.nextLine();
				System.out.println("Enter a permission level (1=employee, 2=shipping, 3=manager): ");
				permission = Integer.parseInt(keyboard.nextLine());
				
				try
				{
					//INSERT INTO user VALUES ('userName', 'password', permission)
					stmt.executeUpdate("INSERT INTO user VALUES ('" + userName + "', '" + password + "', " + permission + ")");
				}
				catch(SQLException excep)
				{
					System.out.println("ERROR: user name already exists, user NOT added");
				}
				
				System.out.println("Add another user? (y/n) ");
				choice = keyboard.nextLine().charAt(0);
				
			}while(choice == 'Y' || choice == 'y');
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void changePassword(Connection conn)
	{
		try
		{
			Statement stmt = conn.createStatement();
			Scanner keyboard = new Scanner(System.in);
			String userName;
			String password;
			
			System.out.println("Enter a user name to change the password of: ");
			userName = keyboard.nextLine();
			
			System.out.println("Enter a new password: ");
			password =  keyboard.nextLine();
			
			try
			{	//UPDATE user SET password='password' WHERE user_name='userName'
				stmt.executeUpdate("UPDATE user SET password='" + password + "' WHERE user_name='" + userName + "'");
			}
			catch(SQLException excep)
			{
				System.out.println(excep.getMessage());
				excep.printStackTrace();
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void changePermission(Connection conn)
	{
		try
		{
			Statement stmt = conn.createStatement();
			Scanner keyboard = new Scanner(System.in);
			String userName;
			int permission;
			
			System.out.println("Enter a user name to change the permission of: ");
			userName = keyboard.nextLine();
			
			System.out.println("Enter a new permission level (1=employee, 2=shipping, 3=manager): ");
			permission = Integer.parseInt(keyboard.nextLine());
			
			try
			{
				//UPDATE user SET permission=permission WHERE user_name='userName'
				stmt.executeUpdate("UPDATE user SET permission=" + permission + " WHERE user_name='" + userName + "'");
			}
			catch(SQLException excep)
			{
				System.out.println("ERROR: " + excep.getMessage());
				excep.printStackTrace();
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void deleteUser(Connection conn)
	{
		char choice;
		try
		{
			Statement stmt = conn.createStatement();
			Scanner keyboard = new Scanner(System.in);
			String userName;
			
			do
			{
				System.out.println("Enter a user name to delete: ");
				userName = keyboard.nextLine();
				System.out.println("Are you sure you want to delete user: " + userName + "? (y/n)");
				choice = keyboard.nextLine().charAt(0);
				
				if(choice == 'Y' || choice == 'y')
				{
					try
					{
						//DELETE FROM user WHERE user_name='userName'
						stmt.executeUpdate("DELETE FROM user WHERE user_name='" + userName + "'");
					}
					catch(SQLException excep)
					{
						System.out.println(excep.getMessage());
						excep.printStackTrace();
					}
				}
				
				System.out.println("Delete another user? (y/n)");
				choice = keyboard.nextLine().charAt(0);
				
			}while(choice == 'Y' || choice == 'y');
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void showUsers(Connection conn)
	{
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("SELECT user_name, password, permission FROM user");
			
			while(result.next())
			{
				System.out.println(result.getString("user_name") + ", pw: " + result.getString("password") + ", level: " + result.getInt("permission"));
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println();
	}
}