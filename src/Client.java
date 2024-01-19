import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 3000);
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        DataInputStream din = new DataInputStream(socket.getInputStream());
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        //this thread waits for incoming messages
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try{
                        String incomingMessage = din.readUTF();
                        System.out.println(incomingMessage);
                    } catch(IOException e){
                        closeEverything(socket, dout, din);
                    }
                }
            }
        }).start();

        //this loop will send messages
        while(socket.isConnected()){
            try{
                String messageToSend = scanner.nextLine();
                dout.writeUTF(username + ": " + messageToSend);
                dout.flush();
            }catch(IOException e){
                closeEverything(socket, dout, din);
            }
        }
    }

    public static void closeEverything(Socket socket, DataOutputStream dout, DataInputStream din){
        try{
            if(socket != null){
                socket.close();
            }
            if(din != null){
                din.close();
            }
            if(dout != null){
                dout.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
