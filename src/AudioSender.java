import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class AudioSender implements Runnable {
    static DatagramSocket4 sending_socket;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run(){
        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine
            //clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: AudioSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try{
            sending_socket = new DatagramSocket4();
        } catch (SocketException e){
            System.out.println("ERROR: AudioSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Get a handle to the Standard Input (console) so we can read user input

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;
        int counter = 0;

        AudioRecorder recorder = null;
        try {
            recorder = new AudioRecorder();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        while (running){
            try{
                //Read in a string from the standard input
                //String str = in.readLine();
                //Convert it to an array of bytes
                byte[] buffer = recorder.getBlock();
                ByteBuffer unwrapEncrypt = ByteBuffer.allocate(buffer.length);
                counter++;

                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);//.putInt(counter);

                int key = 150;

                for( int j = 0; j < buffer.length/4; j++) {
                    int fourByte = byteBuffer.getInt();
                    fourByte = fourByte ^ key; // XOR operation with key
                    unwrapEncrypt.putInt(fourByte);
                }
                byte[] encryptedBlock = unwrapEncrypt.array();

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket encryptedPacket = new DatagramPacket(encryptedBlock, encryptedBlock.length, clientIP, PORT);

                //Send it
                sending_socket.send(encryptedPacket);

                //The user can type EXIT to quit
                //if (str.equals("EXIT")){
                //    running=false;
                // }

            } catch (IOException e){
                System.out.println("ERROR: AudioSender: Some random IO error occurred!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}
