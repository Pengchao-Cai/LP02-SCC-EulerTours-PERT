/** Starter code for LP2
 *  @author rbk ver 1.0
 *  @author SA ver 1.1
 */

// change to your netid
package pxc190029;

import idsa.Graph;
import idsa.Graph.*;
import idsa.Graph.Timer;

import java.io.File;
import java.util.*;


public class Euler extends GraphAlgorithm<Euler.EulerVertex> {
    static int VERBOSE = 1;
    Vertex start;
    List<Vertex> tour;
    EulerVertex pointer; // global pointer to visit all the edges and stitch sub-tours at the same time

	// You need this if you want to store something at each node
    static class EulerVertex implements Factory {
        EulerVertex next;
        Vertex self; // mapping of vertex
        LinkedList<Vertex> adj; // use parallel array to store adjEdges of each DFSVertex
        EulerVertex(Vertex u) {
	        if(u != null) {
                this.next = null;
                this.self= u;
                this.adj = new LinkedList<>();
	        }
        }

	    public EulerVertex make(Vertex u) { return new EulerVertex(u); }

    }

	public Euler(Graph g, Vertex start) {
        super(g, new EulerVertex(null));
        this.start = start;
        this.pointer = new EulerVertex(start);
        tour = new LinkedList<>();
    }

    /* Test if the graph is Eulerian.
     * If the graph is not Eulerian, it prints the message:
     * "Graph is not Eulerian" and one reason why, such as
     * "inDegree = 5, outDegree = 3 at Vertex 37" or
     * "Graph is not strongly connected"
     */
    public boolean isEulerian() {
        DFS d = new DFS(this.g);
        d = d.stronglyConnectedComponents(g);

        if (d.connectedComponents() > 1) {
            System.out.println("Graph is not Eulerian! Graph is not strongly connected!");
            return false;
        }

        for (Vertex v : this.g) {
            if (v.inDegree() != v.outDegree()) {
                System.out.println("At Vertex " + v.getName());
                System.out.println("InDegree " + v.inDegree() + "OutDegree" + v.outDegree());
                return false;
            }
        }
        return true;

	}


    public List<Vertex> findEulerTour() {
        if(!isEulerian()) { return new LinkedList<Vertex>(); }

        // init  outEdges for all EulerVertex in parallel array
        for (Vertex cur : this.g) {
            EulerVertex curEuVer = get(cur);
            g.outEdges(cur).forEach(x -> curEuVer.adj.add(x.toVertex()));
        }

        EulerVertex eulerStart =  this.pointer;
        // when the pointer reaches null, stitch of sub-tours completes.
        while (pointer != null ) {
            tour.add(pointer.self);
            EulerVertex tmp = pointer.next;
            EulerVertex tail = findAndStitch(pointer); // connect during finding sub-tour
            tail.next = tmp;
            pointer = pointer.next;
        }
        return tour;
    }

    EulerVertex findAndStitch(EulerVertex cur) {
        while (get(cur.self).adj.size() > 0) {
            // each time remove one edge from current adj out going edges
            // which are stored in parallel array
            Vertex to = get(cur.self).adj.removeFirst();
            cur.next = new EulerVertex(to);
            cur = cur.next;
        }
        return cur;
    }
    
    public static void main(String[] args) throws Exception {
//        String path = "C:/Users/utdstudent/Downloads/lp2 test cases/lp2 Euler  test cases/lp2 test cases/test4-cycles.txt";
//        Scanner in = new Scanner(new File(path));
        Scanner in;
        String string = "8 9  1 2 0  2 3 0  3 4 0  4 2 0  4 5 0  5 6 0  6 7 0  7 5 0  7 8 0";
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);
	    int start = 1;
//        if(args.length > 1) {
//	    start = Integer.parseInt(args[1]);
//		}
//		// output can be suppressed by passing 0 as third argument
//		if(args.length > 2) {
//            VERBOSE = Integer.parseInt(args[2]);
//        }
        Graph g = Graph.readDirectedGraph(in);
	    Vertex startVertex = g.getVertex(start);
        Timer timer = new Timer();

	    Euler euler = new Euler(g, startVertex);
	    List<Vertex> tour = euler.findEulerTour();
            System.out.println(tour);

        timer.end();
        if(VERBOSE > 0) {
	    System.out.println("Output:");
            // print the tour as sequence of vertices (e.g., 3,4,6,5,2,5,1,3)
	    System.out.println();
        }
        System.out.println(timer);

	
    }

    public void setVerbose(int ver) {
	VERBOSE = ver;
    }
}
