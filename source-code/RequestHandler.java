import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.imageio.ImageIO;

public class RequestHandler implements Runnable {
// Socket which is connected to client passed by Proxy server
Socket clientSocket;

// Read data client sends to the proxy
BufferedReader proxyToClient_br;

// Send data from proxy to client
BufferedWriter proxyToClient_bw;

// Thread - used to transmit data read from client to server when using HTTPS
// Reference to this is required so it can be closed once completed.
private Thread httpsClientToServer;

// Creates a RequestHandler object capable of servicing HTTP(S) GET requests
public RequestHandler(Socket clientSocket){
	this.clientSocket = clientSocket;
	try{
		this.clientSocket.setSoTimeout(50000);
		proxytoClientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		proxytoClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		} 
	catch (IOException err) {
		err.printStackTrace();
		}
	}

// Reads and examines the requestString and calls the appropriate method based on the  // request type. 
	public void run() {
		// Get Request from client
		String requestString;
		try{
			requestString = proxytoClient_br.readLine();
		} catch (IOException err) {
			err.printStackTrace();
			System.out.println("Error reading request from client");
			return;
		}

		// Parse out given URL
		System.out.println("Request Received " + requestString);
		// Get the Request type
		String request = requestString.substring(0,requestString.indexOf(' '));

		// Remove request type and space
		String urlString = requestString.substring(requestString.indexOf(' ')+1);

		// Remove everything past next space
		urlString = urlString.substring(0, urlString.indexOf(' '));

		// Prepend http:// if necessary to create correct URL
		if(!urlString.substring(0,4).equals("http")){
			String temp = "http://";
			urlString = temp + urlString;
		}

		// Check if site is blocked from blocked_sites file
		if(Proxy.isBlocked(urlString)){
			System.out.println("Blocked site requested :- " + urlString);
			blockedSiteRequested();
			return;
		}

		// Check request type
		if(request.equals("CONNECT")){
			System.out.println("HTTPS Request for :- " + urlString + "\n");
			handleHTTPSRequest(urlString);
		}
		else{
			// Check if cached copy exists
			File file;
			if((file = Proxy.getCachedPage(urlString)) != null){
				System.out.println("Cached Copy found for : " + urlString + "\n");
				send_CachedPageToClient(file);
			} else {
				System.out.println("HTTP GET for : " + urlString + "\n");
				send_NonCachedToClient(urlString);
			}
		}
	} 

	//  cachedFile - The file to be sent (can be image/text)
	private void send_CachedPageToClient(File cachedFile){
		// Read from File containing cached web page
		try{
			// If file is an image - write data to client using buffered image.
			String fileExtension = cachedFile.getName().substring(cachedFile.getName().lastIndexOf('.'));
			// Response to be sent to the server
			String response;
			if((fileExt.contains(".png")) || fileExt.contains(".jpg") ||
					fileExt.contains(".jpeg") || fileExt.contains(".gif")){
				// Read in image from storage
				BufferedImage image = ImageIO.read(cachedFile);
				if(image == null ){
					System.out.println("Image " + cachedFile.getName() + "was null");
					response = "http/1.0 404 error \n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClient_bw.write(response);
					proxyToClient_bw.flush();
				} else {
					response = "HTTP/1.0 200 OK\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClient_bw.write(response);
					proxyToClient_bw.flush();
					ImageIO.write(image, fileExt.substring(1), clientSocket.getOutputStream());
				}
			} 
			//  text based file requested
			else {
				BufferedReader cachedFile_BufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(cachedFile)));

				response = "HTTP/1.0 200 OK\n" +
						"Proxy-agent: ProxyServer/1.0\n" +
						"\r\n";
				proxyToClient_bw.write(response);
				proxyToClient_bw.flush();

				String line;
				while((line = cachedFile_BufferedReader.readLine()) != null){
					proxyToClient_bw.write(line);
				}
				proxyToClient_bw.flush();
				
				// Close all resources
				if(cachedFile_BufferedReader != null){
					cachedFile_BufferedReader.close();
				}	
			}


			// Close Down Resources
			if(proxyToClient_bw != null){
				proxyToClient_bw.close();
			}

		} catch (IOException err) {
			System.out.println("Error Sending Cached file to client");
			err.printStackTrace();
		}
	}

	 // urlString - URL of file requested
	private void send_NonCachedToClient(String urlString){
		try{
			// Compute a logical file name as per schema, which allows the files on stored disk to resemble that of the URL it was taken from
			int fileExtIndex = urlString.lastIndexOf(".");
			String fileExt;

			// Get the type of file
			fileExt = urlString.substring(fileExtIndex, urlString.length());

			// Get initial file name
			String fileName = urlString.substring(0,fileExtIndex);


			// Trimming http://www. as we don’t need it in filename
			fileName = fileName.substring(fileName.indexOf('.')+1);

			// Remove any illegal characters from file name
			fileName = fileName.replace("/", "__");
			fileName = fileName.replace('.','_');
			
			// Trailing / result in index.html of that directory being fetched
			if(fileExt.contains("/")){
				fileExt = fileExt.replace("/", "__");
				fileExt = fileExt.replace('.','_');
				fileExt += ".html";
			}
			fileName = fileName + fileExt;

			// Attempt to create File to cache
			boolean caching = true;
			File fileToCache = null;
			BufferedWriter fileToCache_bw = null;
			try{
				// Create File to cache 
				fileToCache = new File("cached/" + fileName);

				if(!fileToCache.exists()){
					fileToCache.createNewFile();
				}

				fileToCache_bw = new BufferedWriter(new FileWriter(fileToCache));
			}
			catch (IOException err){
				System.out.println("Couldn't cache:- " + fileName);
				caching = false;
				err.printStackTrace();
			} catch (NullPointerException err) {
				System.out.println("NPE opening file…");
			}

			// Check if file is an image
			if((fileExt.contains(".png")) || fileExt.contains(".jpg") ||
					fileExt.contains(".jpeg") || fileExt.contains(".gif")){
				// Create the URL
				URL remoteURL = new URL(urlString);
				BufferedImage image = ImageIO.read(remoteURL);
				if(image != null) {
					// Cache the image to disk
					ImageIO.write(image, fileExtension.substring(1), fileToCache);
					// Send response code to client
					String line = "http/1.0 200 works\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClient_bw.write(line);
					proxyToClient_bw.flush();
					// Send image data
					ImageIO.write(image, fileExt.substring(1), clientSocket.getOutputStream());
				// else No image received from remote server
				} else {
					System.out.println("Sending 404 to client as image wasn't received from server"+ fileName);
					String error = "HTTP/1.0 404 NOT FOUND\n" +
							"Proxy-agent: ProxyServer/1.0\n" +
							"\r\n";
					proxyToClient_bw.write(error);
					proxyToClient_bw.flush();
					return;
				}} 
			// File is a text file
			else {			
				// Create the URL
				URL remoteURL = new URL(urlString);
				// Creating a connection to remote server
				HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
				proxyToServerCon.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");
				proxyToServerCon.setRequestProperty("Content-Language", "en-US");  
				proxyToServerCon.setUseCaches(false);
				proxyToServerCon.setDoOutput(true)
				// Create Buffered Reader from remote Server
				BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));
				
				// Send success code to client…200
				String line = "http/1.0 200 works\n" +
						"Proxy-agent: ProxyServer/1.0\n" +
						"\r\n";
				proxyToClient_bw.write(line);
		
				// Read from input stream between proxy & remote server
				while((line = proxyToServer_br.readLine()) != null){
					// Send on data to client
					proxyToClient_bw.write(line);

					// Write to our cached copy of the file
					if(caching){
						fileToCache_bw.write(line);
					}
				}
				
				// Ensure all data is sent.
				proxyToClient_bw.flush();
				// Closing Down all Resources
				if(proxyToServer_br != null){
					proxyToServer_br.close();
				}
			}

			if(caching){
				// Ensure data written and add to our cached hash maps
				fileToCache_bw.flush();
				Proxy.addCachedPage(urlString, fileToCache);
			}
			// Closing down all resources
			if(fileToCache_bw != null){
				fileToCache_bw.close();
			}
			if(proxyToClient_bw != null){
				proxyToClient_bw.close();
			}
		} 

		catch (Exception err){
			err.printStackTrace();
		}
	}

	// Handles HTTPS requests between client and remote server
	// urlString - file to be transmitted over HTTPS
	private void handleHTTPSRequest(String urlString){ 
		String url = urlString.substring(7);
		String pieces[] = url.split(":");
		url = pieces[0];
		int port  = Integer.valueOf(pieces[1]);
		try{
			// Only first line of HTTPS request has been read at this point (CONNECT *)
			// Read (and throw away) the rest of the initial data on the stream
			for(int i=0;i<5;i++){
				proxyToClient_br.readLine();
			}

			// Get actual IP associated with this URL through DNS
			InetAddress address = InetAddress.getByName(url);
			// Open a socket to the remote server 
			Socket proxyToServerSocket = new Socket(address, port);
			proxyToServerSocket.setSoTimeout(50000);

			// Send Connection established to the client
			String line = "HTTP/1.0 200 Connection established\r\n" +
					"Proxy-Agent: ProxyServer/1.0\r\n" +
					"\r\n";
			proxyToClient_bw.write(line);
			proxyToClient_bw.flush();
			
			// Client and Remote will both start sending data to proxy
			// Proxy has to asynchronously read data from each party and send it to the other one.
			//Create a Buffered Writer betwen proxy and remote
			BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));

			// Create Buffered Reader from proxy and remote
			BufferedReader proxyToServer_br = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));
			// Create a new thread to listen to client and transmit to server
			ClientToServerHttpsTransmit clientToServerHttps = new ClientToServerHttpsTransmit(clientSocket.getInputStream(), proxyToServerSocket.getOutputStream());
			httpsClientToServer = new Thread(clientToServerHttps);
			httpsClientToServer.start();
			
			// Listen to remote server and relay to client
			try {
				byte[] buffer = new byte[4096];
int read;
				do {
					read= = proxyToServerSocket.getInputStream().read(buffer);
					if (read > 0) {
						clientSocket.getOutputStream().write(buffer, 0, read);
						if (proxyToServerSocket.getInputStream().available() < 1) {
							clientSocket.getOutputStream().flush();
						}
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException err) {
			}
			catch (IOException err) {
				err.printStackTrace();
			}

			// Closing all Resources
			if(proxyToServerSocket != null){
				proxyToServerSocket.close();
			}

			if(proxyToServer_br != null){
				proxyToServer_br.close();
			}

			if(proxyToServer_bw != null){
				proxyToServer_bw.close();
			}
			if(proxyToClient_bw != null){
				proxyToClient_bw.close();
			}
		} catch (SocketTimeoutException e) {
			String line = "HTTP/1.0 504 Timeout Occured after 50s\n" +
					"User-Agent: ProxyServer/1.0\n" +
					"\r\n";
			try{
				proxyToClient_bw.write(line);
				proxyToClient_bw.flush();
			} catch (IOException ioerr) {
				ioerr.printStackTrace();
			}
		} 
		catch (Exception err){
			System.out.println("Error on HTTPS : " + urlString );
			err.printStackTrace();
		}
	}
	// Listen to data from client and transmits it to server.
	// This is done on a separate thread as must be done 
	// asynchronously to reading data from server and transmitting 
	// that data to the client. 
	class ClientToServerHttpsTransmit implements Runnable{
		
		InputStream proxyToClient_ipstream;
		OutputStream proxyToServer_opstream;
		// Creates Object to Listen to Client and Transmit that data to the server
		// proxyToClient_ipstream - Stream that proxy uses to receive data from client
		// proxyToServer_opstream - Stream that proxy uses to transmit data to remote server
		public ClientToServerHttpsTransmit(InputStream proxyToClientIS, OutputStream proxyToServerOS) {
			this.proxyToClient_ipstream = proxyToClient_ipstream;
			this.proxyToServer_opstream = proxyToServer_opstream;
		}
		public void run(){
			try {
				// Read byte by byte from client and send directly to server
				byte[] buffer = new byte[4096];
				int read;
				do {
					read = proxyToClient_ipstream.read(buffer);
					if (read > 0) {
						proxyToServer_opstream.write(buffer, 0, read);
						if (proxyToClient_ipstream.available() < 1) {
							proxyToServer_opstream.flush();
						}
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException sterr) {
			}
			catch (IOException err) {
				System.out.println("Proxy to client HTTPS read timed out…");
				err.printStackTrace();
			}
		}
	}
	// If a user requests a page that is blocked by the proxy.
	// Sends an - access forbidden message back to the client as response
	private void blockedSiteRequested(){
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			String line = "http/1.0 403 Access Denied \n" +
					"User-Agent: ProxyServer/1.0\n" +
					"\r\n";
			bufferedWriter.write(line);
			bufferedWriter.flush();
		} catch (IOException err) {
			err.printStackTrace();
		}
	}
}
