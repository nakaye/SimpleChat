import java.io.IOException;
import java.net.ServerSocket;

public class ServerDriver {

    public static void main(String[] args) throws IOException {
        int portNumber=8189;
        System.out.println("Łączenie z gniazdem "+portNumber+"...");
        try(ServerSocket ss=new ServerSocket(portNumber)){
            System.out.println("Połączono");
            ChatServer cs=new ChatServer(ss);
            cs.start();
        }

    }

}