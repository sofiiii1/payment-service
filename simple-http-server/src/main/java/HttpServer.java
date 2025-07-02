import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080");

        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            while(!reader.ready()) {
            }

            while(reader.ready()) {
                System.out.println(reader.readLine());
            }

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println();
            writer.println("<h1>Hello from server</h1>");
            writer.println("<p>It works</p>");
            writer.flush();
            socket.close();
        }
    }

}
