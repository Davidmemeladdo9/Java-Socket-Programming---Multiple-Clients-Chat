package za.co.wethinkcode;

import java.net.Socket;
import java.util.ArrayList;
import java.io.*;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clienthandlers= new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientsUsername;

    public ClientHandler(Socket socket) {
        try{
            this.socket=socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientsUsername = bufferedReader.readLine();
            clienthandlers.add(this);
            broadCastMessage("Server:"+clientsUsername+" has entered the group chat!");
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
        
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    public void broadCastMessage(String messageToSend){
        for (ClientHandler clientHandler: clienthandlers){
            try{
                if (clientHandler.clientsUsername.equals(clientsUsername)){

                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){
        clienthandlers.remove(this);
        broadCastMessage("Server: " +clientsUsername+" has left the chat");

    }


    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader !=null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.setStackTrace(null);
        }
    }

    
}
