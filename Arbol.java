import java.rmi.*;
import java.net.*;
import java.util.Random;
import java.util.ArrayList;

public class Arbol
{
	int cantidadNodos;
	Board tablero;
  ArrayList nodos;

	public Arbol(Board tablero) 
	{
		this.cantidadNodos = 1;
		this.tablero = tablero;
    this.nodos = new ArrayList<Nodo>();
    this.nodos.add(new Nodo(cantidadNodos, tablero, cantidadNodos-1, null));// primero id nodo, tablero, id del padre
	}

  //PRIMERO BUSCO AL NODO PADRE EN EL ARREGLO PARA ASI AUMENTAR EL CONTADOR DE CANTIDAD DE HIJOS QUE TIENE
  //UNA VEZ HECHO ESO SE PROCEDE A AGREGAR LAS VICTORIAS Y DERROTAS AL NODO SIMULADO, LUEGO DICHO NODO ES 
  //AGREGADO AL ARRGLO(ES DECIR AL MISMO ARBOL), LUEGO AGREGO LAS VICTORIAS Y DERROTAS DEL NODO AL NODO PADRE
  //Y LUEGO AL PADRE DEL PADRE Y ASI SUCESIVAMENTE HASTA LLEGAR AL NODO RAIZ
  public void armarArbol(int victorias, int derrotas, Nodo nodoAGuardar)
 	{
  	for(int contador = 0; contador < nodos.size() ; contador++) 
    {
      if(nodoAGuardar.getIdPadre() == ((Nodo) nodos.get(contador)).getId())
      {
        ((Nodo) nodos.get(contador)).agregarHijo();
        contador = nodos.size(); 
      } 
    }

    nodoAGuardar.agregarVictorias(victorias);
    nodoAGuardar.agregarDerrotas(derrotas);
    nodos.add(nodoAGuardar);

    int idPadre = nodoAGuardar.getIdPadre();
    for (int descontador = nodos.size()-1; descontador>=0 ; descontador--) 
    {
      if(((Nodo) nodos.get(descontador)).getId() == idPadre)
      {
        ((Nodo) nodos.get(descontador)).agregarVictorias(victorias);
        ((Nodo) nodos.get(descontador)).agregarDerrotas(derrotas);
        idPadre = ((Nodo) nodos.get(descontador)).getIdPadre();
      }
    }
 	}

	//metodo que hara la seleccion y entregara la jugada a un trabajador
	public Nodo obtenerJugada()
	{
		return expandir(seleccionNodo());
	}

	//metodo con el cual se guardaran las estadisticas de un trabajador
	public void guardarJugada(Nodo nodoAGuardar)
	{
    for(int contador = 0; contador < nodos.size() ; contador++) 
    {
      if(nodoAGuardar.getIdPadre() == ((Nodo) nodos.get(contador)).getId())
      {
        ((Nodo) nodos.get(contador)).agregarHijo();
        contador = nodos.size(); 
      } 
    }
    nodos.add(nodoAGuardar);
	}

  //CREO UN NUEVO NODO EL CUAL LUEGO SERA AÑADIDO AL ARBOL UNA VEZ QUE YA SE ALLA HECHO LA SIMULACION
	public Nodo expandir(Nodo nodoAExpandir)
  {
    cantidadNodos++;
    return new Nodo(cantidadNodos, nodoAExpandir.getTablero(), nodoAExpandir.getId(), nodoAExpandir.obtenerMovimiento());
  }

  //MUESTRO LOS DATOS DE NODO POR NODO PARA IR VIENDO COMO SE CONSTRUYE EL ARBOL, AYUDO PARA LA FASE DE DESARROLLO
 	public void mostrarDatos()
 	{
    for(int contador = 0; contador < nodos.size() ; contador++ ) 
    {
      Nodo nodoAux = ((Nodo) nodos.get(contador));
      System.out.println("\nId del nodo: " + nodoAux.getId());
      System.out.println("Id del nodo padre: " + nodoAux.getIdPadre());
      System.out.println("Victorias del nodo: " + nodoAux.getVictorias());
      System.out.println("Derrotas del nodo: " + nodoAux.getDerrotas());
      System.out.println("Tablero: \n" + nodoAux.getTablero());
      for(int contadorMovimientos = 0; contadorMovimientos < nodoAux.getMovimientos().length ; contadorMovimientos++ )
      {
        //System.out.println(nodoAux.getMovimientos()[contadorMovimientos]);
      }
      System.out.println("Cantidad hijos: " + nodoAux.getHijos());  
    }
 	}

  //HAGO UNA SELECCION DE UN NODO, PRIMERO EMPIEZO POR RAIZ Y VEO SI YA SE HICIERON TODOS LOS HIJOS POSIBLES LOS CUALES
  //SON IGUALES A LAS JUGADAS POSIBLES, SI AUN QUEDAN CREO UN NODO HIJO CON LA POSIBLE JUGADA NO HECHA. SI ES EL CASO QUE YA
  //SE HICIERON TODAS LAS JUGADAS POSIBLES, SIGO AVANZANDO POR LOS HIJOS Y ASI SUCESIVAMETE
 	public Nodo seleccionNodo()
 	{
    Nodo nodoElegido = null;
    for(int contador = 0 ; contador < nodos.size() ; contador++) 
    {
      if(((Nodo) nodos.get(contador)).getHijos() < ((Nodo) nodos.get(contador)).getCantidadDeMovimientos())
      {
        return (Nodo) nodos.get(contador);
      }
    }
  	return nodoElegido;
 	}

  //OBTENGO TODOS LOS NODOS HIJOS DEL NODO RAIZ, EN BASE A ESTOS NODOS CON DISTINTAS JUGADAS Y ESTADISTICAS PUEDO DETERMINAR LA MEJOR JUGADA
  public ArrayList obtenerPrimerNivel()
  {
    ArrayList nodosExportar = new ArrayList<Nodo>();
    for (int contador = 0 ; contador < nodos.size() ; contador++) 
    {
      if(((Nodo) nodos.get(contador)).getIdPadre() == 1)
      { 
        nodosExportar.add(nodos.get(contador));
      }  
    }
    return nodosExportar;
  }

  //OBTENGO LA MEJOR JUGADA POSIBLE PARA EL TABLERO ENTREGADO EVALUANDO EL COUCIENTE DE TODAS LAS SIMULACIONES
  public Nodo mejorNodo()
  {
    Nodo mejor = null;
    double couciente = 0;
    for(int contador = 0 ; contador < nodos.size() ; contador++) 
    {
      if(((Nodo) nodos.get(contador)).getIdPadre() == 1)
      {
        if(((Nodo) nodos.get(contador)).obtenerCuociente() > couciente)
        {
          mejor = ((Nodo) nodos.get(contador));
          couciente = ((Nodo) nodos.get(contador)).obtenerCuociente();
        }
      }
    }
    return mejor;
  } 
}