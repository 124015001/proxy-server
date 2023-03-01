<h1>Implementation of HTTP Web Proxy Server</h1>
<br>
<div align="center">
    <h2>1. ABSTRACT</h2>
</div>
<br>
* The internet is used by billions of individuals in this developing world. Major problems occur everyday web pages, like security concerns, Response time and restricting sensitive content to the users.
<br><br>
* Because so many people are using the internet at once, there is a rise in the intensity of traffic on the link that carries data between servers and clients. As a result, the average response time, or the amount of time it takes from the browser to request an object to receiving it, increases and will likely be in the minutes range, which is undesirable to some users. A Proxy Server can be used to implement Caching, reducing the amount of traffic and the delay, to solve this problem. resulting in a quicker response time. Through the use of CDN and the localization of much of the traffic, web caches are becoming an increasingly significant part of the internet.
<br><br>
* Using a proxy server, one can also implement parental control (blocking sensitive websites).
<br><br>
<strong>KEYWORDS:</strong> Blocking websites, Caching, Traffic Intensity, Response Time, Delay.
<br><br>
<div align="center">
    <h2>2. INTRODUCTION</h2>
</div>
<h3>2.1	PROXY SERVER</h3>
<br>
- Proxy servers are typically employed as a gateway between a user and the internet. Implemented between online visitors' browsers and the websites they visit. A computer uses an IP address to connect to the internet. A proxy server is comparable to an internet-connected machine with a unique IP address. As a result, it aids in preventing online intruders from accessing a private network.
<br><br>
- Network Address Translation, often known as NAT, is frequently combined with proxies to mask the client's IP address from the distant server.

![image](https://user-images.githubusercontent.com/74541810/222122862-f8a52c84-b6e9-4a80-a888-959ead28a7aa.png)
<br>
<h4>2.1.1 Application Level and Circuit Level</h4>
<p>Application Level Proxy: A proxy that only allows access to a certain type of material, such as an HTTP Web Proxy or an FTP Proxy. They are committed to completing a particular task. Various applications can be supported by Circuit Level Proxies.</p>
<h4>2.1.2 Forward and Reverse Proxies</h4>
<p>Forward Proxies are proxies located near the client which conceals details of the client from the server and the remote server will not know the true identity of the requesting client whereas Reverse proxies, on the other hand, are proxies that are located close to the server but conceal information about the server from clients. This prevents the remote server from knowing the genuine identity of the client making the request.</p>

<div align="center">
    <img src="https://user-images.githubusercontent.com/74541810/222123770-fb60d8b6-c3d1-41ee-9cf2-a41cfca5adc3.png">
</div>

<h4>2.1.3 Other Proxies</h4>
<P>These are the anonymous proxy servers that are accessible online and help to conceal users' identity.</p>
<h3>2.2	PROXY FUNCTIONS</h3>
<br>
<h4>2.2.1 Web Caching</h4>
<p>A Web Caching Proxy Server keeps copies of previously accessed requested objects in this storage, which is part of the server's own disc storage. All HTTP queries can be set up in the user's web browser to go through the Web Proxy first. The procedures that follow in order to complete the transmission take place once these requests are directed to the web proxy server:</p>

  1. The browser connects over TCP to the Web cache and issues an HTTP request to the Web cache for the object.
  
  2. Web caching checks to see if a duplicate of the object is locally saved. If a copy is present, the Web cache sends the item to the client browser as part of an HTTP response message.
  
  3. The proxy initiates a TCP connection to the origin server if the web caching proxy does not already hold the object. The Web cache then uses the proxy-to-server TCP connection to send an HTTP request for the object. The origin server delivers the object along with an HTTP response to the Web cache after receiving this request.
  
  4. After receiving the item, the web caching proxy makes a copy and stores it locally before sending a copy and an HTTP response message to the client browser via the already established TCP connection between cache and client.
  
<p>Multiple clients using the proxy maximises the proxy cache's benefits, allowing other clients to access pages that have been cached by one client while experiencing dramatically reduced response times.</p>
<div align="center">
    <img src="https://user-images.githubusercontent.com/74541810/222128117-babca122-2e72-4e3e-b211-71dbe1ca3d54.png">
</div>

<h4>2.2.2 Access Controls</h4>
<p>Web proxies are highly helpful in Private Networks, such as those found in Institutions, to prohibit users from accessing specific websites or even for parental control because they can give Access Control. A web proxy can block websites on a client's request if it is given a list of websites to ban. When a client request is examined, the proxy checks the received destination URL address against the list of prohibited websites and refuses access if they match.</p>
<h4>2.2.3 Address Translation</h4>
<p>When used in conjunction with NAT, web proxies hide the client's IP address by changing it to an external IP and making requests on their behalf.</p>
<h3>2.3 HTTPS Requests</h3>
<p>SSL or TLS are used for HTTPS connections. In this case, the client and server exchange encrypted data. To ensure secure transactions, this is widely employed in the financial sector and is currently present practically everywhere.</p>
<p>However, this creates a problem for proxy servers because they have access to all the information or services that clients have requested to the server and there is no secure channel of communication between the two. Since these proxy servers cache all the recently accessed web pages and their traffic, there is a higher risk of a Man In The Middle Attack.</p>
<p>However, these proxy servers will be unable to block or cache traffic of a certain kind known as HTTPS traffic.</p>
<p>Here, the proxy creates a secure connection between the client and the server instead of caching the objects. Implementation of this uses HTTP CONNECT Tunneling.</p>
<div align="center">
    <img src="https://user-images.githubusercontent.com/74541810/222129475-5a53d4a0-b246-4524-a21c-594d8be955c5.png">
</div>
<h4>2.3.1 Setting up and Running Proxy Server</h4>
<p>A web page can be requested from the browser after the Proxy server programme has been launched from a cmd prompt. Utilizing the IP address and port number, the requests are routed to the proxy server (to which the proxy server programme is listening to). </p>
<p>The localhost can be changed to the IP address of the machine on which the proxy server code is executing if you want to utilise the proxy server with a browser and proxy on separate computers. Additionally, the port number must match the one that the proxy server is listening on.</p>
<p>IP Address used here: localhost</p>
<p>Port number: 6969</p>
<div align="center">
    <h2>3. WORKING</h2>
</div>
<p>Traffic from the client is transmitted to the proxy server, which then requests the requested data from the distant server on the client's behalf. The required data is subsequently forwarded to the client via the proxy server. This is helpful if the administrator wants to specify how the user should interpret the data. Clients won't be able to access particular websites, for instance, if the proxy server blocks them dynamically. The client can specify the website url to block, and access to that website will subsequently be limited. In the case of parental control, this aids in limiting the content that users or kids can see.</p>
<div align="center">
    <img src="https://user-images.githubusercontent.com/74541810/222130437-b8ff0f05-abef-41c9-b8d6-1130cd467f9a.png">
</div>
<h3>3.1 The Implementation</h3>
<p>Implemented using java its TCP socket libraries. Here, Firefox was used to set up all of its traffic to the specified port (6969) and IP address(localhost), which were then used in the proxy configuration.</p>
<p>Main components of implementation :- </p>

  1. Proxy class 
  
  2. RequestHandler class
  
<h4>Proxy Class</h4>
<p>In order to receive incoming socket connections from the client, the Proxy Class produces a Server Socket. As the server must serve numerous clients at once, the implementation ought to be multithreaded. As a result, the Proxy accepts the socket connection as it happens and starts a new thread to handle the request (RequestHandler). Multiple clients can have their requests served simultaneously thanks to the server's ability to accept new socket connections before the request has been entirely processed.</p>
<p>Additionally, blocking and caching functions are implemented in this case using a proxy server. It caches websites requested by clients and blocks specific websites listed in the block sites file in the directory.</p>
<p>It is suggested to save references to currently banned and cached websites in a data structure with a constant order search time because response time is crucial for the proxy server (Hashmap). If the file is not in the cache, there is very little overhead, and if the file is located in the cache and saved in a directory, there is an improvement in performance.</p>
<h4>The RequestHandler Class</h4>
<p>It services the requests that come through to the proxy.</p>

  1. HTTP GET requests. 
  
  2. HTTP GET requests for files contained in the cache. 
  
  3. HTTPS CONNECT Requests.
  
