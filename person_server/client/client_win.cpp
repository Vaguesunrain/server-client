#include<iostream>
#include<string>
#include<winsock2.h>

using namespace std;
class Client
{
private:
    int port;
    string ip;
    SOCKET client_socket;
    
public:
    Client(int port,string ip);
    ~Client();
    void t_send(string msg);
    string r_receive();
};

//new 
Client::Client(int port,string ip)
{
    this->port=port;
    this->ip=ip;
    WSADATA wsaData;
    WORD DllVersion = MAKEWORD(2, 2);
    if (WSAStartup(DllVersion, &wsaData) != 0)
    {
        cout << "Winsock startup failed.\n";
        return;
    }
client_socket = socket(AF_INET, SOCK_STREAM, 0);
}


void Client::t_send(string msg){
    send(client_socket, msg.c_str(), msg.size(), 0);
}

string Client::r_receive(){
    char buffer[1024];
    int bytes_received;
    bytes_received = recv(client_socket, buffer, 1024, 0);
    return buffer;
}


Client::~Client()
{
    closesocket(client_socket);
    WSACleanup();
}

