import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable{
    private Socket userSocket;
    private String loggedUser;
    private ChatServer server;
    private boolean sessionFinished=false;

    public ClientHandler(ChatServer server, Socket userSocket) {
        super();
        this.server=server;
        this.userSocket = userSocket;
    }

    @Override
    public void run() {
        try(InputStream is=userSocket.getInputStream();
            OutputStream os=userSocket.getOutputStream();
            Scanner sc=new Scanner(is);
            PrintWriter pw=new PrintWriter(os, true)){
            pw.println("Jesteœ na serwerze chat. Podaj login.");
            pw.flush();
            String linia=sc.nextLine();
            if(!server.isRegistered(linia)){
                pw.println("Niepoprawna nazwa u¿ytkownika.");
                pw.flush();
            }else{
                if(server.isLoggedIn(linia)){
                    pw.println("Ten u¿ytkownik jest ju¿ zalogowany.");
                    pw.flush();
                }
                else{
                    pw.println("Jesteœ zalogowany !");
                    loggedUser=linia;
                    server.logInUser(loggedUser, userSocket);
                    printMonit(pw);
                    while(sc.hasNextLine() && !sessionFinished){
                        linia=sc.nextLine();
                        parseLine(linia,pw,sc);
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void printMonit(PrintWriter pw){
        pw.print(loggedUser+">");
        pw.flush();
    }

    private void parseLine(String line,PrintWriter pw, Scanner sc){
        StringTokenizer st=new StringTokenizer(line);
        String firstToken=st.nextToken();
        switch(firstToken){
            case "quit":
                sessionFinished=true;
                pw.println("Sesja zostanie zakoñczona");
                pw.flush();
                break;
            case "stop":
                pw.println("Serwer koñczy dzia³anie");
                pw.flush();
                System.exit(0);
            case "list":
                server.listUsers(pw);
                printMonit(pw);
                break;
            default:
                if(server.isLoggedIn(firstToken))
                {
                    if(firstToken.equals(loggedUser)){
                        pw.println("Nie mo¿esz wysy³aæ komunikatów do sieci");
                        pw.flush();
                    }
                    else{
                        String msg=line.substring(line.indexOf(firstToken)+firstToken.length()+1);
                        server.sendMessage(firstToken, msg);
                        printMonit(pw);
                    }
                }
                else{
                    pw.println("U¿ytkownik "+firstToken+" nie jest zalogowany");
                    pw.flush();
                }
                break;
        }
    }
}