import java.rmi.*;
import java.io.Serializable;

public interface MonteCarloAgentInterface extends Remote {
    public void recibirToken(int numeroSimulaciones, long tiempoInicialLider, long tiempoLimite)throws RemoteException;
}