import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private ServerSocket ss;
    private ArrayList<String> users;
    private Map<String,Socket> loggedUsers;
    public ChatServer(ServerSocket ss) {
        super();
        this.ss = ss;
        users = new ArrayList<>();
        users.add("a");
        users.add("b");
        users.add("c");
        loggedUsers = new HashMap<>();
    }

    public boolean isRegistered(String user){
        return users.contains(user);
    }

    public boolean isLoggedIn(String user){
        return loggedUsers.containsKey(user);
    }

    public void logInUser(String user, Socket socket){
        loggedUsers.put(user, socket);
    }

    public void logOutUser(String user){
        loggedUsers.remove(user);
    }

    public void listUsers(PrintWriter pw){
        loggedUsers.keySet().forEach(pw::println);
    }

    public void sendMessage(String user, String msg){
        Socket s=loggedUsers.get(user);
        try {
            OutputStream os=s.getOutputStream();
            PrintWriter pw=new PrintWriter(os,true);
            pw.println(msg);
            pw.flush();
            pw.print(user+">");
            pw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() throws IOException{
        while(true){
            Socket s=ss.accept();
            ClientHandler ch=new ClientHandler(this,s);
            Thread t=new Thread(ch);
            t.start();
        }
    }
}