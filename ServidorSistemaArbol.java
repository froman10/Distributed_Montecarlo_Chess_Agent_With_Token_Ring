import java.net.*;
import java.rmi.*;

public class ServidorSistemaArbol{
   public static void main(String[] args) {
      try {
         InterfazSistemaArbol sistemaArbol = new SistemaArbol();
         Naming.rebind("sistemaArbol", sistemaArbol);
         
         System.out.println("Server is running.");
      }
      catch (RemoteException rex) {
         System.out.println("Server exception.main(): " + rex);
      }
      catch (MalformedURLException ex) {
         System.out.println("MalformedURLException " + ex);
      }
   }
}