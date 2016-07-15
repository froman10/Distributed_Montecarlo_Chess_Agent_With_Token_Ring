import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MonteCarloAgent extends UnicastRemoteObject implements MonteCarloAgentInterface {
   
	String ipSgteTrabajador = ""; //ip del siguiente trabajador.
	String ipServidorArbol = "";//ip del servidor donde esta el arbol.
	Boolean esLider = false; //Bandera utilizada para saber si es el trabajador lider.
	SimulatorThread simulador;




	public MonteCarloAgent() throws RemoteException{
		super();
	}

	public MonteCarloAgent (String[] parametros) throws RemoteException {
		super();
		this.ipSgteTrabajador = parametros[0];
		this.ipServidorArbol = parametros[1];
		simulador = new SimulatorThread();
		if(parametros.length > 2){//Si es el trabajador es lider, comienza la ejecución.
			System.out.println("Soy lider");
			this.esLider = true;
			//Tiempo límite en milisegundos
			long tiempoLimite = minutosAMilisegundos(Integer.parseInt(parametros[2]));
			//Número de simulaciones por trabajador.
			int numeroSimulaciones = Integer.parseInt(parametros[3]);
			long tiempoInicialLider = System.currentTimeMillis();
			Board tablero = leerTablero(parametros[4]);//Leo el tablero desde el archivo.


			try{
		        InterfazSistemaArbol sistemaArbol;
	    		Object o = Naming.lookup("rmi://"+ipServidorArbol+"/sistemaArbol");
	    		sistemaArbol = (InterfazSistemaArbol) o;
	    		sistemaArbol.recibirTablero(tablero);//Le entrego el tablero raíz al árbol.
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
			//Realiza la primera llamada a si mismo.
			recibirToken(numeroSimulaciones, tiempoInicialLider, tiempoLimite); 
			
		}
		else{
			System.out.println("No soy lider");
		}
	}
	/*
	Método que le permite al trabajador saber que tiene el token.
	Recibe el numero de simulaciones por jugada, el tiempo inicial del lider y el tiempo limite.
	*/
	public void recibirToken(int numeroSimulaciones, long tiempoInicialLider, long tiempoLimite){
		try{	

			System.out.println("Recibi el token");
			//Inicio el enlace.
			InterfazSistemaArbol sistemaArbol;
    		Object o = Naming.lookup("rmi://"+ipServidorArbol+"/sistemaArbol");
    		sistemaArbol = (InterfazSistemaArbol) o;

    		MonteCarloAgentInterface monteCarloAgent;
    		Object ob = Naming.lookup("rmi://"+ipSgteTrabajador+"/monteCarloAgent");
    		monteCarloAgent = (MonteCarloAgentInterface) ob;    		

    		//Si aun no se pasa el tiempo limite
			if((System.currentTimeMillis()-tiempoInicialLider) < tiempoLimite){ 
				System.out.println("Aun no pasa el tiempo: "+(System.currentTimeMillis()-tiempoInicialLider));

				if(this.simulador.getNodo() == null){//si aun no se asigna una jugada al simulador.
					System.out.println("Voy a simular");
					Nodo nodo = sistemaArbol.obtenerJugada();//Obtenemos el nodo desde el arbol.

					simulador = new SimulatorThread(nodo, numeroSimulaciones);
					simulador.start();
				}
				else if(!simulador.isTrabajando()){//Si el simulador ya termino de trabajar.
					System.out.println("Enviare el resultado al arbol.");
					//Le enviamos al arbol el nodo con las estadísticas
					sistemaArbol.guardarJugada(simulador.getNodo());
					simulador.reiniciarNodo();//Vaciamos el nodo.
				}
				
				System.out.println("Paso el token a: "+this.ipSgteTrabajador);
				SendTokenThread stt = new SendTokenThread(ipSgteTrabajador,  numeroSimulaciones, tiempoLimite, tiempoInicialLider);
				stt.start();
				//monteCarloAgent.recibirToken(numeroSimulaciones, tiempoInicialLider, tiempoLimite); 
				
			}
			else{//Si se pasa el tiempo límite
				System.out.println("El tiempo paso");
				if(this.esLider){//Si es el trabajador lider
					System.out.println("Si soy lider obtengo el primer nivel");
		    		//Obtengo los nodos del primer nivel del arbol.
		    		ArrayList<Nodo> nodos = sistemaArbol.obtenerPrimerNivel();

		    		Nodo mejorNodo = nodos.get(0);//Momentaneamente el primer nodo tiene la mejor jugada.

		    		for(Nodo nodo:nodos){//Recorro todos los nodos

		    			//Comparo los cuocientes de cada nodo con el "momentaneamente" mejor, y lo guardo si es mejor.
		    			if(nodo.obtenerCuociente() > mejorNodo.obtenerCuociente()){
		    				mejorNodo = nodo;
		    			}
		    			imprimirNodo(nodo);//Imprimo la info de un nodo.
		    		}
		    		System.out.println("El nodo elegido es");
		    		imprimirNodo(mejorNodo);
					
				}
				else{
					System.out.println("No soy lider, paso el token");
					simulador = new SimulatorThread();
					SendTokenThread stt = new SendTokenThread(ipSgteTrabajador,  numeroSimulaciones, tiempoLimite, tiempoInicialLider);
					stt.start();

				}
			}
			System.out.println("Me salgo");

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
	private void imprimirNodo(Nodo nodo){
		System.out.println("Movimiento: " + nodo.getMovimiento() + " Ganadas: "
			+nodo.getVictorias()+" Perdidas: "+nodo.getDerrotas()+" Cuociente: "+nodo.obtenerCuociente());
	}

	private Board leerTablero(String archivoTablero){
		int[][] board = new int[8][8];
		Board b = new Board();
		try{
			BufferedReader input =   new BufferedReader(new FileReader(archivoTablero));
			for (int i=0; i<8; i++) {
				String line=input.readLine();
				String[] pieces=line.split("\\s");
				for (int j=0; j<8; j++) {
					board[i][j]=Integer.parseInt(pieces[j]);
				}
			}
			String turn=input.readLine();
			b.fromArray(board);
			if (turn.equals("N")) b.setTurn(b.TURNBLACK);
			else b.setTurn(b.TURNWHITE);
			b.setShortCastle(b.TURNWHITE,false);
			b.setLongCastle(b.TURNWHITE,false);
			b.setShortCastle(b.TURNBLACK,false);
			b.setLongCastle(b.TURNBLACK,false);
		
			String st=input.readLine();
			while (st!=null) {
				if (st.equals("EnroqueC_B")) b.setShortCastle(b.TURNWHITE,true);
				if (st.equals("EnroqueL_B")) b.setLongCastle(b.TURNWHITE,true);
				if (st.equals("EnroqueC_N")) b.setShortCastle(b.TURNBLACK,true);
				if (st.equals("EnroqueL_N")) b.setLongCastle(b.TURNBLACK,true);
				st=input.readLine();
			}
		} catch (Exception e) {}
		return b;
	}
	private long minutosAMilisegundos(int minutos){
		int milisegundos = minutos*60*1000;
		return (long)milisegundos;
	}
		
}

