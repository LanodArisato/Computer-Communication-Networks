import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private static final int BUFFER_SIZE = 4096;
    private Socket connection;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;
    private FileInputStream fileIn;
    private int bytes;
    private byte[] buffer = new byte[BUFFER_SIZE];

    public Client(int startByte, int endByte, String host, int port, boolean write, String filename) {
        try {
            connection = new Socket(host, port);
            socketIn = new DataInputStream(connection.getInputStream()); // Read data from server
            socketOut = new DataOutputStream(connection.getOutputStream()); // Write data to server

            if (write == true) {
                socketOut.writeInt(1); //send intent to write
                writeTo(filename);
            }
            else {
                socketOut.writeInt(0); //send intent to read
                socketOut.writeUTF(filename); // Write filename to server
                int readStatus = socketIn.readInt();

                if (readStatus == -1) { //if server sends code for file not found
                    connection.close();
                    throw new Exception("File does not exist on server.");
                }

                OutputStream fileOut = new FileOutputStream(filename);

                // Read file contents from server
                while (readStatus >= 0) {
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
  
    private void writeTo(String filename) {
        try 
        {
            fileIn = new FileInputStream(filename);
            socketOut.writeUTF(filename);
            int existStatus = socketIn.readInt();

            if(existStatus == 0) //if server sends all clear code
            {
                System.out.println("Writing " + filename + " to server.");

                    while (true) {
                        bytes = fileIn.read(buffer, 0, BUFFER_SIZE); // Read from file
                        if (bytes <= 0) break; // Check for end of file
                        socketOut.write(buffer); // Write bytes to socket
                    }
                connection.close();
            }
            else if (existStatus == -2) //if server sends alrdy exists code
            {
                System.out.println("File already exists on server.");
                connection.close();
            }
        }
        catch (Exception ex) {

        }
    }

    public static void main(String[] args) {
        if (args.length == 2) { //client server-name file-name
            Client client = new Client(0, BUFFER_SIZE, args[0], 5000,false, args[1]);
        }
        else if (args.length == 3) { //client server-name -w file-name
            Client client = new Client(0, BUFFER_SIZE, args[0], 5000,true, args[2]);
        }
        else if (args.length == 4) { //client -s startBlock server-name file-name || client -e LastBlock server-name file-name || client server-name -p port file-name
            if (args[0].compareTo("-s") == 0) { //client -s startBlock server-name file-name
                Client client = new Client(Integer.parseInt(args[1]), BUFFER_SIZE, args[2], 5000,false, args[3]);
            }
            else if(args[0].compareTo("-e") == 0) { //client -e LastBlock server-name file-name
                Client client = new Client(0, Integer.parseInt(args[1]), args[2], 5000,false, args[3]);
            }
            else if(args[1].compareTo("-p") == 0) { //client server-name -p port file-name
                Client client = new Client(0, BUFFER_SIZE, args[0], Integer.parseInt(args[2]),false, args[3]);
            }
            //Client client = new Client(args[0], 5000, args[2], true);
        }
        else if (args.length == 5) { //client [-s StartBlock] server-name [-w] file-name || client [-e LastBlock] server-name [-w] file-name || client server-name [-p port] [-w] file-name
            if (args[0].compareTo("-s") == 0) { //client -s startBlock server-name -w file-name
                Client client = new Client(Integer.parseInt(args[1]), BUFFER_SIZE, args[2], 5000,true, args[4]);
            }
            else if(args[0].compareTo("-e") == 0) { //client -e LastBlock server-name -w file-name
                Client client = new Client(0, Integer.parseInt(args[1]), args[2], 5000,true, args[4]);
            }
            else if(args[1].compareTo("-p") == 0) { //client server-name -p port -w file-name
                Client client = new Client(0, BUFFER_SIZE, args[0], Integer.parseInt(args[2]),true, args[4]);
            }
             // Client client = new Client(args[0], 5000, args[2], true);
        }
        else if (args.length == 6) { //client [-s StartBlock] [-e LastBlock] server-name file-name || client [-s StartBlock] server-name [-p port] file-name || client [-e LastBlock] server-name [-p port] file-name
            if (args[0].compareTo("-s") == 0) { 
                if (args[2].compareTo("-e") == 0) { //client -s startBlock -e endBlock server-name file-name
                    Client client = new Client(Integer.parseInt(args[1]), Integer.parseInt(args[3]), args[4], 5000,false, args[5]);
                }
                else if(args[3].compareTo("-p") == 0) { //client -s startBlock server-name -p port file-name
                    Client client = new Client(Integer.parseInt(args[1]), BUFFER_SIZE, args[2], Integer.parseInt(args[4]),false, args[5]);
                }
            }
            else if(args[0].compareTo("-e") == 0) { //client -e LastBlock server-name -p port file-name
                Client client = new Client(0, Integer.parseInt(args[1]), args[2], Integer.parseInt(args[4]),false, args[5]);
            }
        }
        else if (args.length == 7) { //client [-s StartBlock] [-e LastBlock] server-name -w file-name || client [-s StartBlock] server-name [-p port] -w file-name || client [-e LastBlock] server-name [-p port] -w file-name
            if (args[0].compareTo("-s") == 0) { 
                if (args[2].compareTo("-e") == 0) { //client -s startBlock -e endBlock server-name -w file-name
                    Client client = new Client(Integer.parseInt(args[1]), Integer.parseInt(args[3]), args[4], 5000,true, args[6]);
                }
                else if(args[3].compareTo("-p") == 0) { //client -s startBlock server-name -p port -w file-name
                    Client client = new Client(Integer.parseInt(args[1]), BUFFER_SIZE, args[2], Integer.parseInt(args[4]),true, args[6]);
                }
            }
            else if(args[0].compareTo("-e") == 0) { //client -e LastBlock server-name -p port -w file-name
                Client client = new Client(0, Integer.parseInt(args[1]), args[2], Integer.parseInt(args[4]),true, args[6]);
            }
        }
        else if (args.length == 8) { //client [-s StartBlock] [-e LastBlock] server-name [-p port] file-name
            Client client = new Client(Integer.parseInt(args[1]), Integer.parseInt(args[3]), args[4], Integer.parseInt(args[6]),false, args[7]);
        }
        else if (args.length == 9) { //client [-s StartBlock] [-e LastBlock] server-name [-p port] [-w] file-name
            Client client = new Client(Integer.parseInt(args[1]), Integer.parseInt(args[3]), args[4], Integer.parseInt(args[6]),true, args[8]);
        }
    }
}

