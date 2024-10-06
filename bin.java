// Java Program to Read and Write Binary Files
import java.io.*;

// Drver Class
public class bin {
      // Main Function
    public static void main(String[] args) 
    {
        try {
            
              // Writing to binary file
            OutputStream Stream = new FileOutputStream("dataClient.bin");
            Stream.write(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x6C, 0x6C, 0x6C, 0x6C, 0x6C, 0x6C, 0x6C, 0x6C, 0x6C,}); 
            
              // ASCII values for "Hello"
            Stream.close();
            
              // Reading from a binary file
            InputStream inputStream = new FileInputStream("dataClient.bin");
            
              int data;
          
            while ((data = inputStream.read()) != -1) {
                System.out.print((char) data); 
              // Convert byte to character
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}