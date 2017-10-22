package socket;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Handler implements Runnable {

    Functions functions = new Functions();
    MIME mime = new MIME();
    HashMap<String, String> type = mime.getMime();
    String contentType = null;
    private Socket client = null;

    public Handler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        if (client!= null) try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String header = reader.readLine();
            System.out.println("客户端发送的请求信息：>>>>>>>>>>>>>");
            System.out.println(header);

            String resource = header.split(" ")[1];
            System.out.println("客户端发送的请求信息结束：<<<<<<<<<<");
            System.out.println("resource: " + resource);
            System.out.println();
            String suffix = null;
            if (resource.equals("/")) {
                resource = "/index.html";
            }
            String[] names = resource.split("\\.");
            suffix = names[names.length - 1];
            contentType = type.get(suffix);

            ////////
            String path = "/opt/" + resource;
            File file = new File(path);
            if (file.exists()) {
                if (suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg")) {
                    functions.readImg(file, client, contentType);
                } else {
                    functions.readFile(file, client, contentType);
                }
            } else {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("HTTP/1.0 404 NotFound");
                out.println("Content-Type:text/html;charset=UTF-8");
                out.println();
                out.println("对不起，您寻求的资源在本服务器上不存在！");
                out.close();
                functions.closeSocket(client);
            }

        } catch (IOException e) {
            System.out.println("HTTP服务器错误： " + e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8000;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket client = null;
        while (true) {
            client = serverSocket.accept();
            System.out.println(client + "链接到HTTP服务器");
            Handler handler = new Handler(client);
            new Thread(handler).start();
        }
    }
}
