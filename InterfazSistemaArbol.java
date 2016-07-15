import java.rmi.*;
import java.io.Serializable;
import java.util.ArrayList;

public interface InterfazSistemaArbol extends Remote {
    public void recibirTablero(Board tablero)throws RemoteException;
    public Nodo obtenerJugada()throws RemoteException;
    public void guardarJugada(Nodo nodo)throws RemoteException;
    public ArrayList<Nodo> obtenerPrimerNivel()throws RemoteException;
}