package uni.edu.pe.CarteroChino;
/*
algoritmo del Problema del Cartero Chino utilizando el algoritmo de camino euleriano
Este programa resuelve el Problema del Cartero Chino utilizando el algoritmo de camino euleriano. 
El grafo se representa mediante una matriz de adyacencia. 
Puedes modificar la matriz grafo en el método main para ajustarlo a tus necesidades. 
El programa imprimirá la ruta óptima que el cartero debe seguir para recorrer todos los caminos al menos una vez.
*/
import java.util.ArrayList;
import java.util.List;

public class CarteroChinoCaminoEureliano {

	private static final int INF = Integer.MAX_VALUE / 2;

	public static void main(String[] args) {
		//La dinstancia entre los nodos no nectados sera de INF
		int[][] grafo = {
				{0,		10,	INF,	30,		INF},
				{10,	0, 	20, 	5,		10},
				{INF,	20, 0, 		INF,	15},
				{30,	5,	INF,	0,		20},
				{INF,	10, 15,		20,		0}
		};

		List<Integer> rutaOptima = encontrarRutaOptima(grafo);
		System.out.println("Ruta óptima: " + rutaOptima);
	}

	public static List<Integer> encontrarRutaOptima(int[][] grafo) {
		int n = grafo.length;
		int[][] matrizRecorrido = new int[n][n];

		// Construir la matriz de recorrido
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				matrizRecorrido[i][j] = -1;
				if(grafo[i][j] != 0 && grafo[i][j] != INF){
					matrizRecorrido[i][j] = 1;
				}
			}
		}

		List<Integer> verticesImpares = obtenerVerticesImpares(grafo);

		// Completar las distancias entre los vértices impares
		for (int k = 0; k < verticesImpares.size(); k++) {

			for (int i = 0; i < verticesImpares.size(); i++) {

				for (int j = 0; j < verticesImpares.size(); j++) {
					
					int origen = verticesImpares.get(i);
					int destino = verticesImpares.get(j);

					if (grafo[origen][destino] != 0 && grafo[origen][destino] != INF && matrizRecorrido[origen][destino] == -1) {
						
						int intermedio = verticesImpares.get(k);
						int distancia = grafo[origen][intermedio] + grafo[intermedio][destino];

						// revisa si es que la distancia de la arista creada es menor a la distancia original, si es asi crea la conexcion
						if (distancia < grafo[origen][destino]) {
							grafo[origen][destino] = distancia;
							matrizRecorrido[origen][destino] = 1;
						}
					}

				}
			}
		}

		// Encontrar la ruta óptima
		List<Integer> rutaOptima = new ArrayList<>();
		int inicio = verticesImpares.get(0);
		euleriano(grafo, matrizRecorrido, inicio, rutaOptima);

		return rutaOptima;
	}


	//Encuentra el camino euleriano de manera recursiva recorriendo todos los nodos
	private static void euleriano(int[][] grafo, int[][] matrizRecorrido, int actual, List<Integer> rutaOptima) {
		int n = grafo.length;
		for (int i = 0; i < n; i++) {
			if (grafo[actual][i] != 0 && grafo[actual][i] != INF) {
				grafo[actual][i] = 0;
				grafo[i][actual] = 0;
				euleriano(grafo, matrizRecorrido, i, rutaOptima);
			}
		}
		rutaOptima.add(actual);
	}

	//Consigue los vertices impares del grafo (en el caso presentado solo serian los vertices 4 y 3)
	private static List<Integer> obtenerVerticesImpares(int[][] grafo) {
		List<Integer> verticesImpares = new ArrayList<>();
		for (int i = 0; i < grafo.length; i++) {
			int grado = 0;
			for (int j = 0; j < grafo.length; j++) {
				if (grafo[i][j] != 0 && grafo[i][j] != INF) {
					grado++;
				}
			}
			if (grado % 2 != 0) {
				verticesImpares.add(i);
			}
		}
		return verticesImpares;
	}

	/*
	 * los cambios hechos en este codigo van principalmente en aplicar con mayor 
	 * frecuencia el uso de INF en la definicion del grafo para que tenga mejor utilidad
	 * asi como eliminar un bucle For inecesario que existia en la generacion de conexciones
	 * por el resto de casos, el programa crea las aristas necesarias para crear el grafo euleriano
	 * asi como tambien hace el recorrido satisfactoriamente
	 */
}

