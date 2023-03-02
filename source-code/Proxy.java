//import library class files
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//import socket library
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

//hashmap for blocked and cache file checking
import java.util.HashMap;
import java.util.Scanner;

/*
Proxy creates a Server Socket which will wait for connections on port 6969.
The Proxy creates a RequestHandler object on a new thread after accepting a connection and passing it the socket to be handled.
It allows the Proxy to continue accept further connections while others are being handled.
In order to prevent interfering with the acceptance of socket connections, the proxy class is also in charge of providing dynamic management of the proxy, i.e. (provided menu of blocking, cache, and exit) through the console. It is also operated on a separate thread.
This therefore allows the administrator to dynamically block websites in real time. 
Here, Proxy server is responsible for maintaining cached copies of the any websites that are requested by clients and this includes the HTML markup, images, css and js files associated with each webpage.
The HashMaps which hold cached items and blocked sites are serialized and written to a file and are loaded back in when the proxy is started once more, meaning that cached and blocked sites are maintained.
 */

public class Proxy implements Runnable{
	// Main method
	public static void main(String[] args) {
		// Create port for listening with port number 6969
		Proxy myProxy = new Proxy(6969);
		myProxy.listen();	
	}
	private ServerSocket serverSocket;

	// Semaphore for Proxy
	private volatile boolean runn = true;

	 // HashMap is used for searching cache content and blocked files names
	 // Key: url of page/image requested.
	 // Value: File in directory associated with this key.
	static HashMap<String, File> cache_file;
	static HashMap<String, String> block_sites;
	
	 // ArrayList of threads which are currently running and servicing requests.
	 // It  is required in order to join all threads on closing of server
	static ArrayList<Thread> servicingthreads;
  
  // Create the Proxy Server
	public Proxy(int port) {
		// Load hash map which contains previously cached sites and blocked Sites
		cache_file = new HashMap<>();
		block_sites = new HashMap<>();

		// Creating array list to hold servicing threads
		servicingthreads = new ArrayList<>();

		// Starting dynamic manager on a separate thread.
		new Thread(this).start();	// Starting run() method at bottom
		try{
			// Load cached sites from file
			File cached_sites = new File("cached_sites.txt");
			if(!cached_sites.exists()){
				System.out.println("Cached sites not found in directory. Creating new file");
				Cached_sites.createNewFile();
			} 
else {
FileInputStream fileinputStream = new FileInputStream(cached_sites);
ObjectInputStream objectInputStream = new ObjectinputStream(fileInputStream);
			cache_file = (HashMap<String,File>)objectinputStream.readObject();
			fileinputStream.close();
			objectinputStream.close();
			}

			// Load blocked sites from file
			File block_sitesTxtFile = new File("block_sites.txt");
			if(!block_sitesTxtFile.exists()){
				System.out.println("Blocked sites not found in directory. Creating new file");
				block_sitesTxtFile.createNewFile();
			} 
else {
		FileInputStream fileinputStream = new FileInputStream(block_sitesTxtFile);
		ObjectInputStream objectinputStream = new ObjectInputStream(fileinputStream);
		block_sites = (HashMap<String, String>)objectinputStream.readObject();
		fileinputStream.close();
		objectinputStream.close();
			}
		} catch (IOException err) {
			System.out.println("Error loading previously cached sites file");
			err.printStackTrace();
		} catch (ClassNotFoundException err) {
			System.out.println("No Class found while loading the previous cached sites file");
			err.printStackTrace();
		}

		try {
		// Create Server Socket for the Proxy 
		Server_socket = new ServerSocket(port);
		// Setting timeout
		System.out.println("Waiting for client on Port No." + server_socket.getLocalPort() + ": ..");
			runn = true;
		} 

		// Catching exceptions associated with opening socket
		catch (SocketException serr) {
			System.out.println("Socket Exception occurred while connecting to client");
			serr.printStackTrace();
		}
		catch (SocketTimeoutException sterr) {
			System.out.println("Timeout occured while connecting to client");
		} 
		catch (IOException ioerr) {
			System.out.println("IO exception, when connecting to client");
		}
	}

	 // Listens to given port number and accepts new socket connections. 
	 // Creates a new thread to handle the request, passes it the socket connection and continues listening.
	public void listen(){
		while(runn){
			try {
			// server_socket.accpet() - Blocks until a connection is made
			Socket socket = server_socket.accept();
				
			// Creating new Thread and passing it to the Runnable RequestHandler
			Thread thread = new Thread(new RequestHandler(socket));
				
			// Key reference to each thread so they can be joined later if necessary
			servicethreads.add(thread);
			thread.start();	
			} catch (SocketException err) {
	
// Socket exception is triggered to shut down the proxy 
			System.out.println("Server closed.");
			} catch (IOException err) {
				e.printStackTrace();
			}
		}
	}

	 // Saves the blocked and cached sites to a file so they can be re loaded at a later time.
	 // Also joins all of the RequestHandler threads currently servicing requests.
	private void closeServer(){
		System.out.println("\nClosing Server...");
		runn = false;
		try{
  
      FileOutputStream fileoutputStream = new FileOutputStream("cached_sites.txt");
      ObjectOutputStream objectoutputStream = new ObjectOutputStream(fileoutputStream);

	    objectoutputStream.writeObject(cache_file);
      objectoutputStream.close();
	    fileoutputStream.close();
	    System.out.println("Cached Sites written successfully");

	    FileOutputStream fileoutputStream2 = new FileOutputStream("block_sites.txt");
	    ObjectOutputStream objectoutputStream2 = new ObjectOutputStream(fileoutputStream2);
	    objectoutputStream2.writeObject(block_sites);
	    objectoutputStream2.close();
	    fileoutputStream2.close();
	    System.out.println("Blocked Site list saved..");
		try{
		  // Close all servicing threads
		  for(Thread thread : servicingthreads){
		  if(thread.isAlive()){
			System.out.print("Waiting on "+  thread.getId()+" to close….");
			thread.join();
			System.out.println("Closed.");
			}
		}
	} catch (InterruptedException err) {
				err.printStackTrace();
			}
	} catch (IOException err) {
				err.printStackTrace();
			}
		try{
			System.out.println("Connection terminating.");
			server_socket.close();
			} 
catch (Exception err) {
			System.out.println("Exception closing proxy's server socket");
			err.printStackTrace();
			}
		}

		 // Looks for File in cache
		public static File getCachedPage(String url){
			return cache_file.get(url);
		}

		 // Adds a new page to the cache
		 // urlString - URL of webpage to cache
		//fileToCache - File Object pointing to File put in cache
		public static void addCachedPage(String urlString, File fileToCache){
			cache_file.put(urlString, fileToCache);
		}

		 // Check if a URL is blocked by the proxy
		// url - URL to check  
// returns true if URL is blocked, else false
		public static boolean isBlocked (String url){
			if(block_sites.get(url) != null){
				return true;
			} else {
				return false;
			}
		}

		 // blocked : Lists currently blocked sites
		 // cached : Lists currently cached sites
		 // close : Close the proxy server
		 // Character: Adds it to the list of blocked sites
		public void run() {
		Scanner scanner = new Scanner(System.in);
		String cmd;
		while(runn){
			System.out.println("\n\t\tPROXY SERVER MENU:\n\nEnter new site to block, or type:\n\"blocked\" -- to see blocked sites,\n\"cached\" -- to see cached sites,\n\"close\" -- to close server.")
			cmd = scanner.nextLine();
			if(cmd.toLowerCase().equals("blocked")){
				System.out.println("\nCurrently Blocked Sites: ");
				for(String key : block_sites.keySet()){
					System.out.println(key);
					}
				System.out.println();
				} 
			else if(cmd.toLowerCase().equals("cached")){
				System.out.println("\nCurrently Cached Sites: ");
				for(String key : cache_file.keySet()){
					System.out.println(key);
					}
				System.out.println();
				}
			else if(cmd.equals("close")){
				runn= false;
				closeServer();
				}
			else {
				block_sites.put(cmd, cmd);
				System.out.println("\n" + cmd + " blocked successfully!! \n");
				}
			}
			scanner.close(); 
}
 }

  
