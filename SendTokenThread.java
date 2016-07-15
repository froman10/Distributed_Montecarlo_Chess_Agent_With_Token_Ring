import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.server.UnicastRemoteObject;

class SendTokenThread extends Thread{

	String ipSgteTrabajador = "";
	long tiempoLimite; //Tiempo límite en milisegundos
	int numeroSimulaciones;//Número de simulaciones por trabajador.
	long tiempoInicialLider;


	SendTokenThread(String ipSgteTrabajador, int numeroSimulaciones, long tiempoLimite, long tiempoInicialLider){

		this.ipSgteTrabajador = ipSgteTrabajador;
		this.numeroSimulaciones = numeroSimulaciones;
		this.tiempoLimite = tiempoLimite;
		this.tiempoInicialLider = tiempoInicialLider;

	}

	@Override
	public void run() {
		try{
			Object o;
	        MonteCarloAgentInterface monteCarloAgent;
    		o = Naming.lookup("rmi://"+ipSgteTrabajador+"/monteCarloAgent");
    		monteCarloAgent = (MonteCarloAgentInterface) o;
			monteCarloAgent.recibirToken(numeroSimulaciones, tiempoInicialLider, tiempoLimite); 
        }
		catch (MalformedURLException ex) {
			System.err.println("URL de RMI invalida");
		}
		catch (RemoteException ex) {
			System.err.println("Objeto remoto genero excepcion " + ex);
		}
		catch (NotBoundException ex) {
			System.err.println("No se encuentra objeto remoto en el servidor");
		}
	}

}