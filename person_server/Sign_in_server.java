import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import database.DatabaseOperation;
public class Sign_in_server {
    private Selector selector;  // Selector用于管理多个通道的事件
    private ServerSocketChannel serverChannel;  // 服务器Socket通道，用于监听客户端连接
    private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 用于读取和发送数据的缓冲区
    DatabaseOperation operation=new DatabaseOperation();

    public  void sign_entry() throws IOException {
        Sign_in_server server = new Sign_in_server();
        server.startServer();

      //  operation.connect_to_sql();
    }

    public void startServer() throws IOException {
        selector = Selector.open();  // 创建一个Selector
        serverChannel = ServerSocketChannel.open();  // 创建服务器Socket通道
        serverChannel.configureBlocking(false);  // 设置为非阻塞模式
        serverChannel.bind(new InetSocketAddress(2020));  // 绑定服务器地址和端口
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);  // 将通道注册到Selector，关注接受连接事件

        //System.out.println("log in Server started...");

        while (true) {
            int readyChannels = selector.select();  // 阻塞，等待就绪事件

            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();  // 获取就绪通道的集合
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    handleAccept(key);  // 处理接受连接事件
                } else if (key.isReadable()) {
                    handleRead(key);  // 处理读事件
                }
                keyIterator.remove();  // 从集合中移除已处理的通道
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = server.accept();  // 接受客户端连接
        clientChannel.configureBlocking(false);  // 设置客户端通道为非阻塞模式
        clientChannel.register(selector, SelectionKey.OP_READ);  // 将客户端通道注册到Selector，关注读事件
        //System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
       
        SocketChannel clientChannel = (SocketChannel) key.channel();
        buffer.clear();
        int bytesRead ;
	    try {
        bytesRead = clientChannel.read(buffer);
    } catch (IOException e) {
        // 处理客户端关闭连接引发的异常
       // String clientIP = clientIPMap.get(clientChannel);
        key.cancel();
        clientChannel.close();
        //System.out.println("Client disconnected: " + clientIP);
        return;
    }
        if (bytesRead == -1) {
    // 客户端关闭连接
            key.cancel();
            clientChannel.close();
       //     clientMap.remove(clientChannel); // remove closed client from map
            //System.out.println("Client disconnected remove ok");
            return;
        }
        buffer.flip();  // 切换为读模式
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        String message = new String(data);
//这里处理注册信息
//      
         
        try {      
        operation.connect_to_sql();
        System.out.println(message);
        String[] parts = message.split(",");//split  username and pasword
        operation.Log_in_insert(parts[0],parts[1]);		
        returnMessage("Register ok", clientChannel);
	operation.close_connect_sql();}
	catch (ClassNotFoundException e){
	    e.printStackTrace();
	}
        
    }
    


    private void returnMessage(String re_message, SocketChannel senderChannel) throws IOException {
        Set<SelectionKey> keys = selector.keys();
    
        for (SelectionKey key : keys) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel == senderChannel) {
                SocketChannel clientChannel = (SocketChannel) channel;
                if (key.isValid() && clientChannel.isOpen()) {  // 检查SelectionKey和客户端通道是否仍然有效
                    ByteBuffer messageBuffer = ByteBuffer.wrap(re_message.getBytes());
                    while (messageBuffer.hasRemaining()) {
                        clientChannel.write(messageBuffer);  
                    }
                }
            }
        }
    }
    
    
}

