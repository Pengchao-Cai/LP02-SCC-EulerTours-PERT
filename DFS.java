/** Starter code for SP5
 *  @author rbk
 */

// change to your netid
package pxc190029;

import idsa.Graph;
import idsa.Graph.*;

import java.io.File;
import java.util.*;

/**
 * Topological Order for DAG
 *
 * Author: Jie Su
 * Author: PengChao Cai
 */
public class DFS extends Graph.GraphAlgorithm<DFS.DFSVertex> {

    private List<List<Integer>> scc; // List of strongly connected components
    private int numOfSCC;

    public static class DFSVertex implements Graph.Factory {
        int cno;
        status vstatus;
        LinkedList<Vertex> adj; // use parallel array to store adjEdges of each DFSVertex
        public DFSVertex(Vertex u) {
            vstatus = status.NEW;
            if (u != null) {
                this.adj = new LinkedList<>();
            }
        }
        public DFSVertex make(Vertex u) { return new DFSVertex(u); }

        public enum status{
            NEW,
            ACTIVE,
            FINISHED;
        }
    }

    /**
     * constructor of DFS.
     * @param g
     */
    public DFS(Graph g) {
        super(g, new DFSVertex(null));
        this.scc = new ArrayList<>();
    }

    public static DFS depthFirstSearch(Graph g) {
        return null;
    }

    // Member function to find topological order

    /**
     * Get the topologicalOrder of DAG. If not DAG, return null.
     *
     * @return list of vertexes in topological order
     */
    public List<Vertex> topologicalOrder1() {
        LinkedList<Vertex> topologlist = new LinkedList<>();
        for(Vertex v : g) get(v).vstatus = DFSVertex.status.NEW;
        for(Vertex v : g)
            if (get(v).vstatus == DFSVertex.status.NEW) {
                if (!isDAG(v, topologlist)) return null;
            }
        return topologlist;
    }

    /**
     * identify a Graph is DAG or not. Put the vertexes in topological order
     * in the list topologlist.
     *
     * @param u
     * @param topologlist
     * @return true or false
     */
    public boolean isDAG(Vertex u, LinkedList<Vertex> topologlist){
        get(u).vstatus = DFSVertex.status.ACTIVE;
        for(Edge edge : g.outEdges(u)){
            Vertex w = edge.otherEnd(u);
            if(get(w).vstatus == DFSVertex.status.ACTIVE){
                return false;
            }else if(get(w).vstatus == DFSVertex.status.NEW){
                if(!isDAG(w, topologlist)){
                    return false;
                }
            }
        }
        get(u).vstatus = DFSVertex.status.FINISHED;
        topologlist.addFirst(u);
        return true;
    }

    // Find the number of connected components of the graph g by running dfs.
    // Enter the component number of each vertex u in u.cno.
    // Note that the graph g is available as a class field via GraphAlgorithm.
    public int connectedComponents() {
        return numOfSCC;
    }

    // After running the connected components algorithm, the component no of each vertex can be queried.
    public int cno(Vertex u) {
        return get(u).cno;
    }

    // Find topological oder of a DAG using DFS. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder1(Graph g) {
        DFS d = new DFS(g);
        return d.topologicalOrder1();
    }

    // Find topological oder of a DAG using the second algorithm. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder2(Graph g) {
        return null;
    }



    public void setNumberOfSCC(int numOfSCC) {
        this.numOfSCC = numOfSCC;
    }
    public void setSccSet(List<List<Integer>> sccSet) {
        this.scc = sccSet;
    }

    /**
     * When an instance of DFS calls this function, it shall return itself but with
     * fields changed.
     * @param g
     * @return an instance of DFS itself.
     */
    public DFS stronglyConnectedComponents(Graph g) {
        // overallTimerStack to store all vertices in the order of their complete time
        // vertex on the top of the stack should be the first vertex in the sink component of reversed graph
        Deque<Integer> overallTimerStack = new ArrayDeque<>();
        boolean[] visi = new boolean[g.size() + 1];

        // initiate  outEdges for all DFSVertex in parallel array
        for (Vertex cur : this.g) {
            DFSVertex curEuVer = get(cur);
            g.outEdges(cur).forEach(x -> curEuVer.adj.add(x.toVertex()));
        }

        for (int i = 1; i <= g.size(); i++)
            {
                if (!visi[i])
                {
                    // An explicit stack to complete current dfs to avoid any stackoverflow error.
                    Deque<Integer> explicitStack = new ArrayDeque<>();
                    fillOrder(g.getVertex(i),  explicitStack, visi, overallTimerStack);
                }
            }

        g.reverseGraph();

        // initiate again outEdges for all DFSVertex in parallel array
        for (Vertex cur : this.g) {
            DFSVertex curEuVer = get(cur);
            g.outEdges(cur).forEach(x -> curEuVer.adj.add(x.fromVertex()));
        }

        Arrays.fill(visi, false);

        int increasingCno = 1; // mark the cno of each DFSvertex
        while (!overallTimerStack.isEmpty())
        {
            List<Integer> set = new ArrayList<>();
            int v = overallTimerStack.pop();

            Deque<Integer> explicitStack = new ArrayDeque<>();
            if (!visi[v]) DFSUtil(g.getVertex(v),  set, increasingCno, visi, explicitStack);
            if(set.size()>0){

                increasingCno++;
                scc.add(set);
                numOfSCC++;
            }
        }
        this.setSccSet(scc);
        this.setNumberOfSCC(numOfSCC);
        g.reverseGraph();
        return this;
    }
    // utility function to find all the scc
    private void DFSUtil(Vertex u, List<Integer> sccSet, int increasingCno, boolean visi[], Deque<Integer> explicitStack) {
        explicitStack.push(u.getName());
        visi[u.getName()] = true;
        while (!explicitStack.isEmpty()) {
            Vertex cur = g.getVertex(explicitStack.peek());
            if (get(cur).adj.size() > 0) {
                int next = get(cur).adj.removeFirst().getName();
                if (!visi[next]) {
                    explicitStack.push(next);
                    visi[next] = true;
                }
            }
            else {
                int curVer = explicitStack.pop();
                get(g.getVertex(curVer)).cno = increasingCno;
                sccSet.add(curVer);
            }
        }
    }

    // to get all the vertices and stored in overallTimerStack
    private void fillOrder(Vertex v, Deque<Integer> explicitStack, boolean visi[], Deque<Integer> overallTimerStack) {
             explicitStack.push(v.getName());
             visi[v.getName()] = true;
            while (!explicitStack.isEmpty()) {
                Vertex cur = g.getVertex(explicitStack.peek());
                if (get(cur).adj.size() > 0) {
                    int next = get(cur).adj.removeFirst().getName();
                    if (!visi[next]) {
                        explicitStack.push(next);
                        visi[next] = true;
                    }
                }
                else overallTimerStack.push(explicitStack.pop());
            }
    }


    public static void main(String[] args) throws Exception {
//        String path = "C:/Users/utdstudent/Downloads/lp2 test cases/lp2 Euler  test cases/lp2 test cases/test4-cycles.txt";
//        Scanner in = new Scanner(new File(path));
        String string = "8 9  1 2 0  2 3 0  3 4 0  4 2 0  4 5 0  5 6 0  6 7 0  7 5 0  7 8 0";
        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);
        // Read graph from input
        Graph g = Graph.readDirectedGraph(in);
        DFS d = new DFS(g);
        d = d.stronglyConnectedComponents(g);
        System.out.println("Number of strongly connected components: "+d.connectedComponents());
        System.out.println("Components are:");
        d.scc.forEach(
                set -> {
                    System.out.print(set);
                });


//        System.out.println("Topo result: ");
//        List<Vertex> topoRes = DFS_zhu.topologicalOrder1(g);
//        if(topoRes == null) System.out.println("this is not DAG!");
//        else {System.out.println(topoRes);}
    }
}