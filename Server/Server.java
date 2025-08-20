import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private final int BUFFER_SIZE = 4096;
    private Socket connection;
    private ServerSocket socket;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private int bytes;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private String filename;

    public Server(int port) throws IOException { 
        try {
            socket = new ServerSocket(port);
            // Wait for connection and process it
            while (true) {
                try {
                    connection = socket.accept(); // Block for connection request

                    socketIn = new DataInputStream(connection.getInputStream()); // Read data from client
                    socketOut = new DataOutputStream(connection.getOutputStream()); // Write data to client

                    int intent = socketIn.readInt(); //receive intent

                    if (intent == 1)  //client wants to write to server
                    {
                        filename = socketIn.readUTF();
                        File exists = new File(filename);

                        if (exists.exists() == true) 
                        {
                            socketOut.writeInt(-2);
                            System.out.println("Client requested a file that already exists");
                        }
                        else 
                        {
                            socketOut.writeInt(0);
                            OutputStream fileOut = new FileOutputStream(filename);
                            System.out.println("Receiving " + filename + " from client."); //UX message

                            // Read file contents from client
                            while (true) {
                                bytes = socketIn.read(buffer, 0, BUFFER_SIZE); // Read from socket
                                if (bytes <= 0) break; // Check for end of file
                                //System.out.print(new String(buffer, StandardCharsets.UTF_8)); // Write to standard output
                                fileOut.write(buffer);
                            }
                            System.out.println("Received " + filename + " from client.");
                        }
                    }
                    else if (intent == 0) //Client wants to read from server
                    {
                        filename = socketIn.readUTF(); // Read filename from client

                        fileIn = new FileInputStream(filename);
                        socketOut.writeInt(0); //sends 1 to client error check if file does exist on server

                        System.out.println("Sending " + filename + " to client.");

                        // Write file contents to client
                        while (true)
                        {
                            bytes = fileIn.read(buffer, 0, BUFFER_SIZE); // Read from file
                            if (bytes <= 0) break; // Check for end of file
                            socketOut.write(buffer); // Write bytes to socket
                        }

                        System.out.println("Sent " + filename + " to client.");
                    }

                } 
                // catch (FileAlreadyExistsException x) 
                // {
                //     socketOut.write(-2);
                //     System.out.println("Client tried to write a file that already exists");
                // }
                catch (FileNotFoundException f) 
                {
                    socketOut.writeInt(-1);
                    System.out.println("Client requested a file that does not exist");
                }
                catch (Exception ex) 
                {
                    System.out.println("Error: " + ex);
                } 
                finally 
                {
                    // Clean up socket and file streams
                    if (connection != null) {
                        connection.close();
                    }

                    if (fileIn != null) {
                        fileIn.close();
                    }
                }
            }
        } catch (IOException i) {
            System.out.println("Error: " + i);

            //Client error message
            //socketOut.writeInt(-1); //-1 for file not found on server
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }
}

// class FileAlreadyExistsException extends Exception {
//     public FileAlreadyExistsException() {
//         super("File already exists on the server");
//     }
// }
