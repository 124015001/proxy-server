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

