package socket;

import java.io.*;
import java.net.Socket;

public class Functions {

    public void closeSocket(Socket socket) {
        try {
            socket.close();
            System.out.println(socket + "离开了HTTP服务器");
            System.out.println();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readImg(File file,Socket client,String contentType){
        PrintStream out = null;
        FileInputStream fileInputStream = null;
        try {
            out = new PrintStream(client.getOutputStream(),true);
            fileInputStream = new FileInputStream(file);
            byte[] data = new byte[fileInputStream.available()];
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type:" + contentType + ";charset=UTF-8");
            out.println("Content-Length:" + file.length());
            out.println();
            fileInputStream.read(data);
            out.write(data);
            fileInputStream.close();
        } catch (IOException e) {
            out.println("HTTP/1.0 500");
            out.println();
            out.flush();
        }finally {
            out.close();
            closeSocket(client);
        }
    }

    public void readFile(File file,Socket client,String contentType){
        PrintWriter out = null;
        try {
            out = new PrintWriter(client.getOutputStream(),true);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String string = null;
            out.print("HTTP/1.0 200 OK");
            out.print("Content-Type:" + contentType + ";charset=UTF-8");
            out.println();
            while ((string = bufferedReader.readLine()) != null){
                out.println(string);
            }
        } catch (IOException e) {
            out.print("HTTP/1.0 500");
            out.println();
            out.flush();
        }finally {
            out.close();
            closeSocket(client);
        }
    }
}
