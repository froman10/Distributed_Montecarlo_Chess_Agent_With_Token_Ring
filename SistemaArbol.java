import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SistemaArbol extends UnicastRemoteObject implements InterfazSistemaArbol {
   
	Board tableroRaiz; //Tablero raíz.
	Arbol arbol; //Arbol actual.


	public SistemaArbol() throws RemoteException{
		super();
	}
	public SistemaArbol (String[] parameters) throws RemoteException {
	}

	/*
	Método que recibe el tablero raíz y lo guarda.
	*/
	public void recibirTablero(Board tablero){
		this.tableroRaiz = tablero;
		this.arbol = new Arbol(tablero);
	}

	/*
	Método que retorna un nodo sobre el que se hará la simulación.
	*/
	public Nodo obtenerJugada(){
		Nodo nodo = this.arbol.obtenerJugada(); //Obtenemos el nodo desde el árbol.
		return nodo;
	}

	/*
	Método que recibe un nodo con los resultados de la simulación y que debe ser 
	guardado en el árbol.
	*/
	public void guardarJugada(Nodo nodo){
		this.arbol.guardarJugada(nodo); //Le pedimos al árbol que guarde el nodo.
	}

	/*
	Método que retorna los nodos pertenecientes al primer nivel del árbol.
	*/
	public ArrayList<Nodo> obtenerPrimerNivel(){
		return this.arbol.obtenerPrimerNivel();
	}

		
}

