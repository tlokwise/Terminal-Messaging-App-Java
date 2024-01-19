import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(3000);
        System.out.println("Server is running!!!!");
        while(!server.isClosed()){
            Socket socket = server.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandler.t.start();
        }
    }
}

class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientList = new ArrayList<>();
    Socket socket;
    Thread t;
    DataOutputStream dout;
    DataInputStream din;
    public ClientHandler(Socket socket){
        t = new Thread(this, "Client Handler");
        this.socket = socket;
        if(clientList.size() == 2){
            this.closeEverything(socket, dout, din);
            return;
        }
        clientList.add(this);

    }


    @Override
    public void run() {
        try {
            dout = new DataOutputStream(socket.getOutputStream());
            din = new DataInputStream(socket.getInputStream());

            while(socket.isConnected()){
                String incomingMessage = din.readUTF();
                System.out.println(incomingMessage);

                forwardMessage(incomingMessage);
            }

        } catch (IOException e) {
            closeEverything(socket, dout, din);
        }
    }

    public void forwardMessage(String messageToSend){
        for(ClientHandler client : clientList){
            try{
                if(client != this){
                    client.dout.writeUTF(messageToSend);
                    client.dout.flush();
                }
            } catch(IOException e){
                closeEverything(socket, dout, din);
            }
        }


    }

    public void closeEverything(Socket socket, DataOutputStream dout, DataInputStream din){
        try{
            if(socket != null){
                socket.close();
            }
            if(dout != null){
                dout.close();
            }
            if(din != null){
                din.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
