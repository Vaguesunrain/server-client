/**
 * test
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// it is used for android multiple thread to use a tcp client
public class client {
    //this class contained TCP connect,close, data read ,data trans method
    Socket socket;
    OutputStream outputStream;
    InputStream inputStream;
    String ip;
    int port;
    

    public  void client_connect(String ip, int port) {
        
        try {
            socket = new Socket(ip, port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(10000);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void client_close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void client_send(String data) {
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String client_read() {
        try {
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            return new String(buffer, 0, len);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
