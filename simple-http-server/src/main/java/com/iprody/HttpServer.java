package com.iprody;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (!reader.ready()) {

            };

            String line = reader.readLine();

            if (line == null || line.isEmpty()) return;

            String[] lines = line.split(" ");

            if (lines.length < 2) return;
            String uri = lines[1];

            String uriFilePath = uri.split("\\?")[0];

            if(uriFilePath.equals("/index.html")){
                InputStream inputStream = HttpServer.class.getClassLoader().getResourceAsStream("static" + uriFilePath);
                if(inputStream == null){
                    send404(writer);
                }else{
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    byte[] fileData = bufferedInputStream.readAllBytes();
                    String contentType = URLConnection.guessContentTypeFromName(uriFilePath);
                    if(contentType != null){
                        contentType = "text";
                    }
                    writer.println("HTTP/1.1 200 OK");
                    writer.println("Content-Type: " + contentType + "; charset=utf-8");
                    writer.println();
                    writer.flush();

                    out.write(fileData);
                    out.flush();
                }
            }else{
                send404(writer);
            }
            socket.close();
        }
    }

    static void send404(PrintWriter writer){
        writer.println("HTTP/1.1 404 Not Found");
        writer.println("Content-Type: text/html; charset=utf-8");
        writer.println();
        writer.print("<h1>404 Not Found</h1>");
        writer.flush();
    }

}
