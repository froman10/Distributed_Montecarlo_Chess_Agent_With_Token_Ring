import java.util.ArrayList;
import java.io.Serializable;
import java.util.Random;

public class Nodo implements Serializable
{
	int id;
	int idPadre;
	int victorias;
	int derrotas;
	Board tablero;
	int hijos;
	Move movimiento;
	Move[] movimientos;
	int cantidadDeMovimientos;

	//CONSTRUCTOR DE LA CLASE
	public Nodo(int id, Board tablero, int idPadre, Move movimiento)
	{
		this.id = id;
		this.tablero = tablero.clone();
		if(id!=1)//DADO MI MOVIENTO YO HAGO ESA JUGADA Y MODIFICO EL TABLERO EN BASE A ESE SE HACE LA SIMULACION
		{
			this.tablero.makeMove(movimiento);//HAGO EL MOVIEMIENTO Y EL TABLERO SE MODIFICA, YA QUE EL TABLERO ERA EL QUE TENIA EL NODO PADRE
		}
		this.idPadre = idPadre; //GUARDO EL ID DEL NODO PADRE
		this.victorias = 0;
		this.derrotas = 0;
		this.hijos = 0;
		this.movimiento = movimiento;//GUARDO EL MOVIMIENTO HECHO PARA DESPUES OBTENERLO SI ES EL MEJOR
		this.movimientos = this.tablero.getValidMoves();//GUARDO TODOS LOS MOVIMIENTOS POSIBLES PARA EL TABLERO DEL NODO
		this.cantidadDeMovimientos = this.movimientos.length;//CANTIDAD DE MOVIMIENTOS DISTINTOS, SE HARA UN NODO EN EL ARBOL POR MOVIMIENTO POSIBLE
	}

	//OBTENGO LA CANDIDAD DE POSIBLES JUGADAS QUE TENGO PARA EL TABLERO, LA CANTIDAD DE NODOS HIJOS QUE TENDRE
	//SERA IGUAL A LOS MOVIMIENTOS POSBLIES
	public int getCantidadDeMovimientos()
	{
		return cantidadDeMovimientos;
	}

	//OBTENGO TODOS LOS MOVIMIENTOS POSIBLES PARA ESTE TABLERO
	public Move[] getMovimientos()
	{
		return movimientos;
	}

	//AÑADO LAS VICOTRIAS OBTENIDOS SI MI HIJO OBTUBO VICTORIA SE AGREGAN AL NODO PADRE Y ASI SUCESIVAMENTE HASTA LLEGAR A RAIZ
	public void agregarVictorias(int victorias)
	{
		this.victorias+= victorias;
	}

	//AÑADO LAS DERROTAS OBTENIDOS SI MI HIJO OBTUBO DERROTA SE AGREGAN AL NODO PADRE Y ASI SUCESIVAMENTE HASTA LLEGAR A RAIZ
	public void agregarDerrotas(int derrotas)
	{
		this.derrotas+= derrotas;
	}

	//OBTENGO LAS VICTORIAS
	public int getVictorias()
	{
		return victorias;
	}

	//OBTENGO LAS DERROTAS
	public int getDerrotas()
	{
		return derrotas;
	}

	//MODIFICO LAS VICTORIAS
	public void setVictorias(int v)
	{
		this.victorias = v;
	}

	//MODIFICO LAS DERROTAS
	public void setDerrotas(int d)
	{
		this.derrotas = d;
	}

	//MODIFICO LA CANTIDAD DE HIJOS QUE TENGO
	public void setHijos(int hijos)
	{
		this.hijos = hijos;
	}

	//OBTENGO LA CANTIDAD DE HIJOS QUE TENGO
	public int getHijos()
	{
		return this.hijos;
	}

	//AUMENTO EL CONTADOR DE CANTIDAD DE HIJOS QUE TENGO
	public void agregarHijo()
	{
		this.hijos++;
	}

	//OBTENGO LE ID DEL NODO
	public int getId()
	{
		return this.id;
	}

	//OBTENGO EL ID DEL NODO PADRE
	public int getIdPadre()
	{
		return this.idPadre;
	}

	//OBTENGO EL TABLERO DEL NODO
	public Board getTablero()
	{
		return this.tablero;
	}

	//MODIFICO EL TABLERO DEL NODO
	public void setTablero(Board tablero)
	{
		this.tablero = tablero;
	}

	//OBTENGO EL MOVIMIENTO DEL TABLERO
	public Move getMovimiento()
	{
		return movimiento;
	}

	//MODIFICO EL MOVIMIENTO DEL TABLERO
	public void setMovimiento(Move movimiento)
	{
		this.movimiento = movimiento;
	}

	//OBTENGO UN MOVIMIENTO PARA GENERAR UN NODO HIJO CON UNA POSIBLE JUGADA, TAMBIEN EL MOVIMIENTO LO ELIMO
	//PARA ASI DESCARTAR ESTE POSIBLE MOVIMIENTO Y LOS NODOS NO TENGAN LA MISMA POSIBLE JUGADA
	public Move obtenerMovimiento()
	{
		try
		{
			Move move = movimientos[movimientos.length-1];//OBTENGO LA JUGADA QUE NECESITO
			Move[] nuevosMovimientos = new Move[movimientos.length-1];//CREO NUEVO ARREGLO
			for (int contador=0; contador < movimientos.length-1 ; contador++) 
			{
				nuevosMovimientos[contador] = movimientos[contador];//GUARDO TODAS LAS JUGADAS MENOS LA QUE SAQUE
			}
			this.movimientos = nuevosMovimientos;//MODIFICO EL ARREGLO DE MOVIMIENTOS
			return move;//RETORNO EL MOVIENTO QUE NECESITO
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			Random r=new Random();
			Move[] moves=tablero.getValidMoves();
			Move m = moves[r.nextInt(moves.length)];
			return m;
		}
	}

	//OBTENGO EL CUOCIENTE QUE DETERMINARA LA MEJOR JUGADA EN EL ARBOL
	public double obtenerCuociente()
	{
		return ((double)(victorias+1)/(derrotas+1));
	}
}
