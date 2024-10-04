import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private String filename;
    private int bytes;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public Server(int port) throws IOException { 
        try {
            socket = new ServerSocket(port);
            // Wait for connection and process it
            while (true) {
                try {
                    connection = socket.accept(); // Block for connection request

                    socketIn = new DataInputStream(connection.getInputStream()); // Read data from client
                    socketOut = new DataOutputStream(connection.getOutputStream()); // Write data to client

                    filename = socketIn.readUTF(); // Read filename from client

                    fileIn = new FileInputStream(filename);
                    socketOut.writeInt(1); //sends 1 to client error check if file does exist on server

                    // Write file contents to client
                    while (true) {
                        bytes = fileIn.read(buffer, 0, BUFFER_SIZE); // Read from file
                        if (bytes <= 0) break; // Check for end of file
                        socketOut.write(buffer); // Write bytes to socket
                    }

                } 
                catch (FileNotFoundException f) {
                    socketOut.writeInt(-1);
                    System.out.println("Client requested a file that does not exist");
                }
                catch (Exception ex) {
                    System.out.println("Error: " + ex);
                } 
                finally {
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
