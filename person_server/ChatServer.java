
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import database.DatabaseOperation;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ChatServer {
    private String trans_ok;//1 is ok
    DatabaseOperation opt;
    private Selector selector;  // Selector用于管理多个通道的事件
    private ServerSocketChannel serverChannel;  // 服务器Socket通道，用于监听客户端连接
    private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 用于读取和发送数据的缓冲区
    private Map< SocketChannel, String>  clientMap = new HashMap<>();
    public  void chat_entry() throws IOException {
        ChatServer server = new ChatServer();
        server.startServer();
    }

    public void startServer() throws IOException {
        selector = Selector.open();  // 创建一个Selector
        serverChannel = ServerSocketChannel.open();  // 创建服务器Socket通道
        serverChannel.configureBlocking(false);  // 设置为非阻塞模式
        serverChannel.bind(new InetSocketAddress(2019));  // 绑定服务器地址和端口
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);  // 将通道注册到Selector，关注接受连接事件

        System.out.println("Chat Server started...");
        // 创建定时执行线程池
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
		try{
			super_broadcast();}
	        catch(IOException e){
		    e.printStackTrace();
		}
	},0, 3, TimeUnit.SECONDS);
      
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
     //   clientMap.put(clientChannel,clientChannel.get);
        //System.out.println("New client connected: " + clientChannel.getRemoteAddress());
        
    }

    private void handleRead(SelectionKey key) throws IOException {

    	SocketChannel clientChannel = (SocketChannel) key.channel();
    	buffer.clear();
    	int bytesRead;

        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
        // 处理客户端关闭连接引发的异常
  //      String clientIP = clientIPMap.get(clientChannel);
            key.cancel();
            clientChannel.close();
	    clientMap.remove(clientChannel);//remove closed client from map
            //System.out.println("Client disconnected remove ok");
            return;
        }
        if (bytesRead == -1) {
    // 客户端关闭连接
            key.cancel();
            clientChannel.close();
            clientMap.remove(clientChannel); // remove closed client from map
            //System.out.println("Client disconnected remove ok");
            return;
        }
        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        String message = new String(data);
        if(message.isEmpty()){return;}
        //System.out.println("Received from " + clientChannel.getRemoteAddress() + ": " + message);
	// System.out.println("Received  "  + ": " + message.length());
	//System.out.println("Received  "  + ": " + message.substring(0,3));
        if(message.substring(0,3).equals("000")){
    // 广播消息给所有其他客户端
            broadcastMessage(message.substring(3), clientChannel);}
	    else if (message.substring(0,3).equals("xxx")){
	        clientMap.put(clientChannel,message.substring(3));//set index and channel reflaxed
          //  System.out.println("index is"+message.substring(3));
	}
	
        else{
	        //select to trans
	        trans_ok = sendMessage(message.substring(3),findSocketChannelByString(clientMap, message.substring(0,3)));
	        sendMessage(trans_ok,clientChannel);//return flag of failed or success
	    }
    }

    

    private void broadcastMessage(String message, SocketChannel senderChannel) throws IOException {
        Set<SelectionKey> keys = selector.keys();  
        for (SelectionKey key : keys) {

            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != senderChannel) {
                SocketChannel clientChannel = (SocketChannel) channel;
                if (key.isValid()) {  // 检查SelectionKey是否仍然有效
                    ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
                    while (messageBuffer.hasRemaining()) {
                         try{clientChannel.write(messageBuffer);}
                                // 将消息发送给指定客户端
                        catch(IOException e){

                        }

                    }
                }
            }
        }
    }


     private void super_broadcast() throws IOException {
	    
        Set<SelectionKey> keys = selector.keys();
	String message="ooo"+concatenateValues(clientMap);
	//  System.out.println("super_broadcast"+message);
       	SocketChannel clientChannel;
        for (SelectionKey key : keys) {
	 

            Channel channel = key.channel();

	     if (channel instanceof SocketChannel){
            clientChannel = (SocketChannel) channel;
	  
                if (key.isValid()&&(clientChannel.isConnected())) {  // 检查SelectionKey是否仍然有效
		    
                    ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
                    while (messageBuffer.hasRemaining()) {
                         try{clientChannel.write(messageBuffer);}
                                // 将消息发送给指定客户端
                        catch(IOException e){

                        }

                    }
                }
	     }
        }
	

    }


    private String sendMessage(String message, SocketChannel receiveChannel) throws IOException {
        Set<SelectionKey> keys = selector.keys();
    
        for (SelectionKey key : keys) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel == receiveChannel) {
                SocketChannel clientChannel = (SocketChannel) channel;
                if (key.isValid() && clientChannel.isOpen()) {  // 检查SelectionKey和客户端通道是否仍然有效
                    ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
                    while (messageBuffer.hasRemaining()) {
                        try{clientChannel.write(messageBuffer);
			    return "1";
			}
		               	// 将消息发送给指定客户端
		        catch(IOException e){
			    return "0";
			}
                    }
                }
            }
        }
	return "0";
    }


   
    private String concatenateValues(Map<SocketChannel, String> hashMap) {
        StringBuilder result = new StringBuilder();

        for (String value : hashMap.values()) {
            result.append(value);
        }

        return result.toString();
    }

    private  SocketChannel findSocketChannelByString(Map<SocketChannel, String> map, String targetString) {
        for (Map.Entry<SocketChannel, String> entry : map.entrySet()) {
            if (entry.getValue().equals(targetString)) {
                return entry.getKey();  // Return the matching SocketChannel
            }
        }
        return null;  // Return null if no match is found
    }
}

