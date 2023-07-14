package uni.edu.pe.Deadlock;
/*
Este programa implementa el algoritmo de detección de puntos muertos en transacciones utilizando un grafo de espera. 
Permite ejecutar comandos de lectura (read), escritura (write) y finalización (end) de transacciones. 
Cuando se detecta un punto muerto, interrumpe la transacción más reciente y coloca sus pasos al final de la entrada.
El programa utiliza un mecanismo de bloqueo binario para implementar el grafo de espera.
Cuando una transacción accede a un registro, se pone un candado en ese registro 
y ninguna otra transacción puede acceder al mismo registro hasta que la transacción actual termine.
*/
import java.util.*;

class DeadlockDetection {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Transaction> transactions = new ArrayList<>();

		//Lista de recursos que ya se estan usando y no pueden usarse en otra operacion
        Set<String> lockedResources = new HashSet<>();
        Map<String, Set<String>> waitGraph = new HashMap<>();

        String input;

		//Entrada de transacciones al flujo del grafo
        while (!(input = scanner.nextLine()).equals("end")) {
            String[] tokens = input.split(",");
            String transactionName = tokens[0];
            String operation = tokens[1];

            if (operation.equals("end")) {
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    transactions.remove(currentTransaction);
                    releaseResources(currentTransaction, lockedResources, waitGraph);
                }
            } else if (operation.equals("read")) {
                String resource = tokens[2];
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    if (lockedResources.contains(resource)) {
                        // Wait for the resource to be released
                        addEdge(waitGraph, transactionName, findTransactionByResource(lockedResources, resource));
                        currentTransaction.operations.add(input);
                    } else {
                        // Acquire the resource
                        lockedResources.add(resource);
                        currentTransaction.operations.add(input);
                    }
                }
            } else if (operation.equals("write")) {
                String resource = tokens[2];
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    if (lockedResources.contains(resource)) {
                        // Wait for the resource to be released
                        addEdge(waitGraph, transactionName, findTransactionByResource(lockedResources, resource));
                        currentTransaction.operations.add(input);
                    } else {
                        // Acquire the resource
                        lockedResources.add(resource);
                        currentTransaction.operations.add(input);
                    }
                }
            } else {
                System.out.println("Invalid operation: " + operation);
            }
        }

        // Detect deadlock
        List<String> deadlockCycle = findDeadlockCycle(waitGraph);
        if (deadlockCycle.isEmpty()) {
            System.out.println("No deadlock detected");
        } else {
			//En caso de la existenia de un ciclo se interrumpe la ultima transaccion agregada
            System.out.println("Deadlock detected. Transaction " + deadlockCycle.get(deadlockCycle.size() - 1) + " interrupted.");
        }

		scanner.close();
    }

	//buscar la transacion por el nombre
    private static Transaction findTransactionByName(List<Transaction> transactions, String name) {
        for (Transaction transaction : transactions) {
            if (transaction.name.equals(name)) {
                return transaction;
            }
        }
        return null;
    }

	//Buscar la transaccion por el recurso que esta usando
    private static Transaction findTransactionByResource(Set<String> lockedResources, String resource) {
        for (String transactionName : lockedResources) {
            if (transactionName.startsWith("T") && transactionName.substring(1).equals(resource)) {
                return new Transaction(transactionName, new ArrayList<String>());
            }
        }
        return null;
    }

	//agrega un nuevo vertice al grafo usando el origen, las conexciones y el destino de la transaccion
    private static void addEdge(Map<String, Set<String>> waitGraph, String source, Transaction destination) {
        if (!waitGraph.containsKey(source)) {
            waitGraph.put(source, new HashSet<>());
        }
        if (destination != null) {
            waitGraph.get(source).add(destination.name);
        }
    }
	//liberar recursos de cierta transaccion asi como eliminar la transaccion del grafo de espera
    private static void releaseResources(Transaction transaction, Set<String> lockedResources, Map<String, Set<String>> waitGraph) {
        for (String operation : transaction.operations) {
            String[] tokens = operation.split(",");
            String resource = tokens[2];
            lockedResources.remove(resource);
            removeEdge(waitGraph, transaction.name, findTransactionByResource(lockedResources, resource));
        }
    }

    private static void removeEdge(Map<String, Set<String>> waitGraph, String source, Transaction destination) {
        if (waitGraph.containsKey(source) && destination != null) {
            waitGraph.get(source).remove(destination.name);
        }
    }

	//Pasar la informacion para la busquedda de un ciclo
    private static List<String> findDeadlockCycle(Map<String, Set<String>> waitGraph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        List<String> cycle = new ArrayList<>();

        for (String transaction : waitGraph.keySet()) {
            if (isCyclic(transaction, visited, recursionStack, waitGraph, cycle)) {
                break;
            }
        }

        return cycle;
    }

	//Buscar si existe algun ciclo en las transacciones que eviten el flujo natural de estas (Deadlock)
    private static boolean isCyclic(String transaction, Set<String> visited, Set<String> recursionStack, Map<String, Set<String>> waitGraph, List<String> cycle) {
        if (recursionStack.contains(transaction)) {
            cycle.add(transaction);
            return true;
        }

        if (!visited.contains(transaction)) {
            visited.add(transaction);
            recursionStack.add(transaction);

            if (waitGraph.containsKey(transaction)) {
                for (String neighbor : waitGraph.get(transaction)) {
                    if (isCyclic(neighbor, visited, recursionStack, waitGraph, cycle)) {
                        cycle.add(transaction);
                        return true;
                    }
                }
            }
        }

        recursionStack.remove(transaction);
        return false;
    }

    /*
    En el caso del codigo presentado, no requere cambios dentro de la logica
    dado que se considera que el grafo esta bien implementado asi como su deteccion del ciclo
    faltaria agregar un poco de orden y encapsular apropiadamente las clases para un mejor uso
    (me falto tiempo para encapsular las clases :( )
     */
}


