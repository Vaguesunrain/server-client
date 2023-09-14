import java.io.IOException;

public class Server_total {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        Log_in_server server2=new Log_in_server();
        Sign_in_server server3=new Sign_in_server();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.chat_entry();
                } catch (IOException e) {
                    System.out.println("server1 error");
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server2.log_entry();
                } catch (IOException e) {
                    System.out.println("server2 error");
                    e.printStackTrace();
                }
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server3.sign_entry();
                } catch (IOException e) {
                    System.out.println("server3 error");
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }

}