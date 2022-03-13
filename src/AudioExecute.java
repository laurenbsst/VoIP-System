public class AudioExecute {
    public static void main (String[] args){

        AudioReceiver receiver = new AudioReceiver();
        AudioSender sender = new AudioSender();

        receiver.start();
        sender.start();

    }
}
