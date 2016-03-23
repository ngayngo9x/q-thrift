namespace java com.pheu.thrift.example

service TestThriftService {
    string echo(1:string message);
    void echo1(1:string message);
}