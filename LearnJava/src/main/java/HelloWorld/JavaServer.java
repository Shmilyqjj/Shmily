package HelloWorld;

import py4j.GatewayServer;
public class JavaServer {

  public static void main(String[] args) {
    JavaClass javaClass = new JavaClass();    // app is now the gateway.entry_point
    GatewayServer server = new GatewayServer(javaClass);
    //GatewayServer server = new GatewayServer(app,25334); //Use other port -- 25333
    server.start(); //receive python requests
  }


}

class JavaClass{
  public int javaMethod(int first, int second) {
    return first + second;
  }
}