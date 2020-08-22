/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("\nMAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	/* example query
	      try{
         	String query = "SELECT * FROM Catalog WHERE cost < ";
         	System.out.print("\tEnter cost: $");
        	String input = in.readLine();
         	query += input;

         	int rowCount = esql.executeQuery(query);
        	System.out.println ("total row(s): " + rowCount);
      } catch(Exception e){
         	System.err.println (e.getMessage());
      }
	*/

	public static void AddCustomer(MechanicShop esql){//1
		try {
			String custQuery = "INSERT INTO customer (id, fname, lname, phone, address) VALUES (";
			
			// get the id of the last customer in database
			String idQuery = "SELECT MAX(id) FROM Customer;";
			List<List<String>> maxIdList = esql.executeQueryAndReturnResult(idQuery); 
			String maxId = (maxIdList.get(0).get(0));
			

			int userId = Integer.parseInt(maxId) + 1;
			String userIdString = String.valueOf(userId);
			custQuery += "'" + userIdString + "'";
	

			System.out.print("Enter first name: ");
			String input = in.readLine();
			custQuery += ", " + "'" + input + "'";

			System.out.print("Enter last name: ");
			input = in.readLine();
			custQuery += ", " + "'" + input + "'";

			System.out.print("Enter phone: ");
			input = in.readLine();
			custQuery += ", " + "'" + input + "'";

			System.out.print("Enter address: ");
			input = in.readLine();
			custQuery += ", " + "'" + input + "'" + ");";

			esql.executeUpdate(custQuery);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void AddMechanic(MechanicShop esql){//2

		try {
			String mechQuery = "INSERT INTO mechanic (id, fname, lname, experience) VALUES(";
			
			// get the id of the last mech in database
			String idQuery = "SELECT MAX(id) FROM mechanic;";
			List<List<String>> maxIdList = esql.executeQueryAndReturnResult(idQuery); 
			String maxId = (maxIdList.get(0).get(0));
			

			int userId = Integer.parseInt(maxId) + 1;
			String userIdString = String.valueOf(userId);
			mechQuery += "'" + userIdString + "'";

			System.out.print("Enter first name: ");
			String input = in.readLine();
			mechQuery += ", " + "'" + input + "'";

			System.out.print("Enter last name: ");
			input = in.readLine();
			mechQuery += ", " + "'" + input + "'";

			System.out.print("Enter years of experience: ");
			input = in.readLine();
			mechQuery += ", " + "'" + input + "'" + ");";
		
			esql.executeUpdate(mechQuery);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void AddCar(MechanicShop esql){//3
		try {
			String carQuery = "INSERT INTO car (vin, make, model, year) VALUES(";

			System.out.print("Enter VIN: ");
			String input = in.readLine();
			String vin = input;

			String queryForMatchingVIN = "SELECT * FROM car WHERE vin=" + "'" + input + "'" + ";";
			if(esql.executeQueryAndPrintResult(queryForMatchingVIN) >= 1) {
				System.out.println("VIN already exists in database");
				return;
			}
			carQuery += "'" + input + "'";

			System.out.print("Enter make: ");
			input = in.readLine();
			carQuery += ", " + "'" + input + "'";

			System.out.print("Enter model: ");
			input = in.readLine();
			carQuery += ", " + "'" + input + "'";

			System.out.print("Enter year (Format XXXX): ");
			input = in.readLine();
			carQuery += ", " + "'" + input + "'" + ");";
			
			esql.executeUpdate(carQuery);

			makeOwner(esql, vin);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void makeOwner(MechanicShop esql, String vin) {
		try {
			String lastName;
			boolean cont = true;
			do {
				System.out.print("Enter car owner's last name: ");
				lastName = in.readLine();
				System.out.print(lastName + "; is this correct? (Y/N): ");
				String yn = in.readLine();

				if(yn.equals("Y") || yn.equals("y")) {
					cont = false;
				}
			} while(cont);

			String queryLastName = "SELECT * FROM customer WHERE lname='" + lastName + "';";
			int rows = esql.executeQuery(queryLastName);
			if(rows < 1) {
				System.out.println("Last name not in database");
				System.out.print("Create new customer? (Y/N): ");
				String yn = in.readLine();

				if(yn.equals("Y") || yn.equals("y")) {
					AddCustomer(esql);
					System.out.println("\n\ncreating...\n");
				}
				else {
					System.out.println("Ok, returning to main menu");
					return;
				}
			}
			
			rows = esql.executeQueryAndPrintResult(queryLastName);
			String chosenId;
			System.out.print("Enter the ID of your customer: ");
			chosenId = in.readLine();	

			String carQuery = "INSERT INTO owns (ownership_id, customer_id, car_vin) VALUES(";

			String idQuery = "SELECT MAX(ownership_id) FROM owns;";
			String userIdString = MaxID(esql, idQuery);

			carQuery += "'" + userIdString + "'";
			carQuery += ", '" + chosenId + "'";
			carQuery += ", '" + vin + "');";

			esql.executeUpdate(carQuery);

			System.out.println("Car " + vin + " added for customer with ID " + chosenId);

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void InsertServiceRequest(MechanicShop esql){//4
		try{
			// get last name
			String lastName;
			boolean cont = true;
			do {
				System.out.print("Enter a last name: ");
				lastName = in.readLine();
				System.out.print(lastName + "; is this correct? (Y/N): ");
				String yn = in.readLine();

				if(yn.equals("Y") || yn.equals("y")) {
					cont = false;
				}
			} while(cont);

			String queryLastName = "SELECT * FROM customer WHERE lname='" + lastName + "';";
			int rows = esql.executeQuery(queryLastName);
			if(rows < 1) {
				System.out.println("Last name not in database");
				System.out.print("Create new customer? (Y/N): ");
				String yn = in.readLine();

				if(yn.equals("Y") || yn.equals("y")) {
					AddCustomer(esql);
					System.out.println("\n\ncreating...\n");
				}
				else {
					System.out.println("Ok, returning to main menu");
					return;
				}
					
			}
			
			rows = esql.executeQueryAndPrintResult(queryLastName);
			String chosenId;
			System.out.print("Enter the correct ID of your customer: ");
			chosenId = in.readLine();	

			System.out.println("\nListing customer's cars (vin):");
			String custCarQuery = "SELECT o.car_vin, c.make, c.model FROM owns o, car c WHERE o.customer_id='" + chosenId + "' AND o.car_vin=c.vin;";

			esql.executeQueryAndPrintResult(custCarQuery);
			System.out.print("Create new car? (Y/N)");

			String yn  = in.readLine();
			if(yn.equals("Y") || yn.equals("y")) {
				AddCar(esql);
				System.out.println("\n\ncreating...\n");
			}

			System.out.print("Enter the VIN of the problem car: ");
			String carVin = in.readLine();
			System.out.print("Enter the problem: ");
			String complain = in.readLine();
			System.out.print("Enter the date: ");
			String date = in.readLine();
			System.out.print("enter odometer reading: ");
			String odometer = in.readLine();

			String insertQuery = "INSERT INTO service_request (rid, customer_id, car_vin, date, odometer, complain) VALUES (";
			
			// get the id of the last sr in database
			String idQuery = "SELECT MAX(rid) FROM service_request;";
			String userIdString = MaxID(esql, idQuery);
			insertQuery += "'" + userIdString + "'";
			insertQuery += ", '" + chosenId + "'";
			insertQuery += ", '" + carVin + "'";
			insertQuery += ", '" + date + "'";
			insertQuery += ", '" + odometer + "'";
			insertQuery += ", '" + complain + "');";

			esql.executeUpdate(insertQuery);

			System.out.println("\n\nService request opened\n");

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
	try {	
		String query = "INSERT INTO closed_request (wid, rid, mid, date, comment, bill) VALUES (";
		String maxID = "SELECT MAX(wid) FROM closed_request;";
		String userIdString = MaxID(esql, maxID);	
		query += "'" + userIdString + "'"; 
		System.out.print("Enter service request number");
		String input = in.readLine();
		String queryForServiceExists = "SELECT * FROM service_request WHERE rid=" + "'" + input + "'" + ";";
		if(esql.executeQuery(queryForServiceExists) < 1) {
			System.out.println("Service Request doesn't exists");
			return;
		}
		query += ", " + "'" + input + "'";
		System.out.print("Enter mechanic Id: ");
		input = in.readLine();
		String queryForMechanicId = "SELECT * FROM mechanic WHERE id=" + "'" + input + "'" + ";";
		if(esql.executeQuery(queryForMechanicId) < 1) {
			System.out.println("Mechanic ID doesn't exists");
			return;
		}
		query += ", " + "'" + input + "'";
		System.out.print("Enter the date: ");
		input = in.readLine();
		query += ", " + "'" + input + "'";

		System.out.print("Enter a comment: ");
		input = in.readLine();
		query += ", " + "'" + input + "'";
				
		System.out.print("Enter a bill amount: ");
		input = in.readLine();
		query += ", " + "'" + input + "');";
		
		
		esql.executeUpdate(query);
	} catch (Exception e) {
		System.err.println(e.getMessage());		
		}			  	
		//TODO
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		try {
			String query = "SELECT DISTINCT cu.id, cr.date, cr.comment, cr.bill FROM closed_request cr, customer cu, service_request sr ";
			query += "WHERE cr.bill < 100 AND cr.rid = sr.rid AND sr.customer_id = cu.id;";

			int row = esql.executeQueryAndPrintResult(query);
			if(row <= 0) {
				System.out.println("No results returned\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		try {
			String query = "SELECT cu.fname, cu.lname FROM customer cu ";
			query += "GROUP BY cu.id HAVING ( SELECT COUNT(*) FROM owns o ";
			query += "WHERE o.customer_id = cu.id ) > 20;";	
		
			int row = esql.executeQueryAndPrintResult(query);
			if(row <= 0) {
				System.out.println("No results returned\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try{
			String query = "SELECT c.make, c.model, c.year FROM car c, service_request sr ";
			query += "WHERE c.vin = sr.car_vin AND sr.odometer < 50000 AND c.year < 1995;";	
				
			int row = esql.executeQueryAndPrintResult(query);
			if(row <= 0) {
				System.out.println("No results returned\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		//
		try{
			String query = "SELECT c.make, c.model, COUNT(sr.*) FROM car c, service_request sr ";
			query += "WHERE sr.car_vin = c.vin ";	
			query += "GROUP BY c.vin ";	
			query += "ORDER BY COUNT(sr.*) DESC ";	
			query += "LIMIT ";	
			
			System.out.print("Enter K: ");
			String kStr = in.readLine();

			query += kStr + ";";
			
			int row = esql.executeQueryAndPrintResult(query);
			if(row <= 0) {
				System.out.println("No results returned\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//9
		//
		try{
			String query = "SELECT cu.fname, cu.lname, cr.bill FROM customer cu, closed_request cr, service_request sr ";
			query += "WHERE cr.rid = sr.rid AND sr.customer_id = cu.id ";	
			query += "ORDER BY cr.bill DESC;";	
			
			int row = esql.executeQueryAndPrintResult(query);
			if(row <= 0) {
				System.out.println("No results returned\n");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static String MaxID(MechanicShop esql, String idQuery) {
		try {
			List<List<String>> maxIdList = esql.executeQueryAndReturnResult(idQuery); 
			String maxId = (maxIdList.get(0).get(0));
			

			int userId = Integer.parseInt(maxId) + 1;
			String userIdString = String.valueOf(userId);

			return userIdString;
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}

		return "";
	}
	
}
