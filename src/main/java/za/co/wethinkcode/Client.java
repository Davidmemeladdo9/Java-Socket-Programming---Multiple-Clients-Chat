package za.co.wethinkcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientsUsername;

    public Client(Socket socket,String clientsUsername){
        try{
            this.socket=socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientsUsername=clientsUsername;
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessages(){
        try{
            bufferedWriter.write(clientsUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(clientsUsername+" : "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter); 
        }
    }
    public void listenMesages(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                String groupchat;
                while(socket.isConnected()){
                    try{
                        groupchat = bufferedReader.readLine();
                        System.out.println(groupchat);
                    }catch(IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws UnknownHostException, IOException{

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your message for the group chat: ");

        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket,username);
        client.listenMesages();
        client.sendMessages();
        scanner.close();


    }

    
}
