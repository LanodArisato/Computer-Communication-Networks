import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private final int BUFFER_SIZE = 4096;
    private Socket connection;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;
    private int bytes;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public Client(String host, int port, String filename,boolean write) {
        try {
            connection = new Socket(host, port);

            socketIn = new DataInputStream(connection.getInputStream()); // Read data from server
            socketOut = new DataOutputStream(connection.getOutputStream()); // Write data to server

            if (write == true) {

            }

            else {
                socketOut.writeUTF(filename); // Write filename to server

                if (socketIn.readInt() == -1) { //if server sends code for file not found
                    connection.close();
                    throw new Exception("File does not exist on server.");
                }

                OutputStream fileOut = new FileOutputStream(filename);

                // Read file contents from server
                while (true) {
                    bytes = socketIn.read(buffer, 0, BUFFER_SIZE); // Read from socket
                    if (bytes <= 0) break; // Check for end of file
                    //System.out.print(new String(buffer, StandardCharsets.UTF_8)); // Write to standard output
                    fileOut.write(buffer);
                }

                connection.close();
            }
        } catch (Exception ex) {
            System.out.println("[Client Error] " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            Client client = new Client(args[0], 5000, args[1], false);
        }
        else if (args.length == 3) {
            Client client = new Client(args[0], 5000, args[2], true);
        }
    }
}
