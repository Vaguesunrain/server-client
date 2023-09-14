#include <iostream>
#include <string>
#include <QTcpSocket>
#include <QHostAddress>

using namespace std;

class Client
{
private:
    int port;
    QString ip;
    QTcpSocket* client_socket;
public:
    Client(int port, QString ip);
    ~Client();
    void t_send(QString msg);
    QString r_receive();
};

Client::Client(int port, QString ip)
{
    this->port = port;
    this->ip = ip;
    client_socket = new QTcpSocket();
    client_socket->connectToHost(QHostAddress(ip), port);
}

void Client::t_send(QString msg)
{
    client_socket->write(msg.toUtf8());
    client_socket->waitForBytesWritten();
}

QString Client::r_receive()
{
    client_socket->waitForReadyRead();
    QByteArray data = client_socket->readAll();
    return QString(data);
}

Client::~Client()
{
    client_socket->close();
    delete client_socket;
}

int main()
{
    int port = 1234;
    QString ip = "127.0.0.1";
    Client client(port, ip);
    client.t_send("Hello, server!");
    QString response = client.r_receive();
    cout << response.toStdString() << endl;

    return 0;
}
