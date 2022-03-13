import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class AudioReceiver implements Runnable {
    static DatagramSocket receiving_socket;
    static AudioPlayer player;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run(){
        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try{
            receiving_socket = new DatagramSocket(PORT);
            player = new AudioPlayer();
        } catch (SocketException | LineUnavailableException e){
            System.out.println("ERROR: AudioReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;

        while (running){
            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[516];
                DatagramPacket encryptedPacket = new DatagramPacket(buffer, 0, 516);
                receiving_socket.receive(encryptedPacket);
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

                int key = 150;

                System.out.println(byteBuffer.getInt());
                byte[] arr = byteBuffer.array();

                ByteBuffer unwrapDecrypt = ByteBuffer.allocate(buffer.length);

                ByteBuffer cipherText = ByteBuffer.wrap(arr);
                for(int j = 0; j < buffer.length/4; j++) {
                    int fourByte = cipherText.getInt();
                    fourByte = fourByte ^ key; // XOR decrypt
                    unwrapDecrypt.putInt(fourByte);
                }
                byte[] decryptedBlock = unwrapDecrypt.array();

//                System.out.println(buffer[0]);
                player.playBlock(decryptedBlock);

            } catch (IOException e){
                System.out.println("ERROR: AudioReceiver: Some random IO error occurred!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}
