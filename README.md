This is my final project for course EEL4781 - Computer Communication Networks

This project is a simple file server that responds to client requests, and a client that requests the file(s) from the server.
It is written in Java using socket programming to enable communication between the client and server applications.

The Client is able to write received data bytes to a file with the name identified by the client’s user using "client <server-name> <file-name>"  .
Your client must write the bytes to the file in order and without introducing any other characters or
bytes. It must be able to successfully write a binary (not just text) file received from the server.
The client also has an option -w flag that allows it to write to the server using "client <server-name> [-w] <file-name>"

The server is able to both send and recieve files with the client. The server also is able to print a message specifying what file is being sent/recieved
and the IP address of the client.
