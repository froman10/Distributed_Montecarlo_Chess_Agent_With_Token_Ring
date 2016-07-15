import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.rmi.server.UnicastRemoteObject;

/*
Esta clase se encarga de simular los juegos, asi obtener victorias y derrotas.
*/
class SimulatorThread extends Thread{
	int numeroSimulaciones;
	boolean trabajando = false; //Permite identificar si el thread ya termino su tarea.
	int victorias = 1;
	int derrotas = 1;
	Nodo nodo;

	//Entregamos el nodo que contiene el tablero y el movimiento sobre el cual simular,
	//y el número de repeticiones.
	SimulatorThread(Nodo nodo, int numeroSimulaciones){

		this.nodo = nodo;
		this.numeroSimulaciones = numeroSimulaciones;

	}
	SimulatorThread(){
	}
	public Nodo getNodo(){
		return this.nodo;
	}
	public boolean isTrabajando(){
		return this.trabajando;
	}
	public void reiniciarNodo(){
		this.nodo = null;
	}
	@Override
	public void run() {
		this.trabajando = true; //Marcamos como trabajando
		int value = 0;
		//Realizamos la simulación n veces.
		for(int i = 0; i < this.numeroSimulaciones; i++){
			//Ejecutamos la smulacion con el tablero y movimiento entregados por el nodo.
			value = play(nodo.getTablero(), nodo.getMovimiento());
			if(value == 1){//Si resulta 1, aumentamos las victorias.
				this.victorias++;
			}
			else if(value == -1){//Si resulta -1 aumentamos las derrotas
				this.derrotas ++;
			}
		}
		//Guardamos victorias y derrotas.
		nodo.setVictorias(this.victorias);
		nodo.setDerrotas(this.derrotas);
		this.trabajando = false;//Marcamos como no trabajando.
	}

	/*
	Este método recibe un tablero y un movimiento para el cual simular.
	Como resultado retorna:
	0 si es empate
	1 si es victoria
	-1 si es derrota
	*/
	private int play(Board board, Move m){
		try{
		    Boolean firstMove = true;
		    Board b = board.clone();
		    Random r=new Random();
			while(true){
			  if(!b.isStalemate() && !b.isCheckMate()){//queda por jugar
			      if(!firstMove){
			        Move[] moves=b.getValidMoves();
			        m = moves[r.nextInt(moves.length)];
			      }
			      firstMove = false;
			      b.makeMove(m);
				}
				else{
			  		break;
				} 
			}
			if(b.isStalemate()){
				return 0;
			}
			else if(b.isCheckMate() && board.turn == b.turn){
				return -1;
			}
			return 1;
		}
		catch(ArrayIndexOutOfBoundsException ex){
			return 0;
		}
		catch(IllegalArgumentException ex){
			return 0;
		}

    }
}