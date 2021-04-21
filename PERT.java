/* Starter code for PERT algorithm (LP2)
 * @author rbk
 */

// change package to your netid
package pxc190029;

import idsa.Graph;
import idsa.Graph.*;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PERT extends Graph.GraphAlgorithm<PERT.PERTVertex> {
    int numCrital; // record num of v in critical path
    public static class PERTVertex implements Factory {
        int es,ls,ef,lf,d,slack;
        boolean critical;
        public PERTVertex(Vertex u) {
            int es=0,ls=0,ef=0,lf=0,d=0,slack=0;
            boolean critical = false;
        }

        public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    /**
     * set source and target edges,make the graph suitable for pert algorithm
     * @param g: a given general graph
     */
    public PERT(Graph g) {
        super(g, new PERTVertex(null));
        for(Vertex v :g){
            if(v.getName()-1!=0){
                g.addEdge(0,v.getName()-1,1);
            }
        }
        for(Vertex v :g){
            if(v.getIndex()!= g.size()-1){
                g.addEdge(v.getIndex(),g.size()-1,2);
            }
        }
    }

    public void setDuration(Vertex u, int d) {
        get(u).d = d;
    }


    public boolean pert() {
        Boolean isdag = false;
        List<Vertex> topologicalList = DFS.topologicalOrder1(g);
        if(topologicalList==null) return true;
        for (Vertex u : g){
            PERTVertex u_ = get(u);
            u_.es =0;
        }
        for (Vertex u : topologicalList){
            PERTVertex u_ = get(u);
            u_.ef = u_.es+u_.d;
            for(Edge e :g.outEdges(u)){
                PERTVertex v_ = get(e.otherEnd(u));
                if(v_.es<u_.ef) v_.es = u_.ef;
            }
        }
        PERTVertex sink = get(g.getVertex(g.size()));
        int sink_finish_time = sink.ef;
        for (Vertex u : g) {
            PERTVertex u_ = get(u);
            u_.lf =sink_finish_time;
        }
        reverseList(topologicalList);
        for(Vertex u : topologicalList){
            PERTVertex u_ = get(u);
            u_.ls = u_.lf- u_.d;
            u_.slack = u_.lf-u_.ef;
            for (Edge e :g.inEdges(u)) {
                PERTVertex v_ = get(e.otherEnd(u));
                if(v_.lf>u_.ls) v_.lf = u_.ls;
            }
        }
        for(Vertex u :g){
            if (get(u).slack==0) {get(u).critical=true;
                numCrital++;}
        }
        return isdag;
    }
    public <T> void reverseList(List<T> list)
    {
        if (list == null || list.size() < 2)
            return;
        T value = list.remove(0);
        reverseList(list);
        list.add(value);
    }


    public int ec(Vertex u) {
        return get(u).ef;
    }

    public int lc(Vertex u) {
        return get(u).lf;
    }

    public int slack(Vertex u) {
        return get(u).slack;
    }

    public int criticalPath() {
        PERTVertex sink = get(g.getVertex(g.size()-1));
        return sink.lf;
    }

    public boolean critical(Vertex u) {
        PERTVertex v = get(u);
        return v.critical;
    }

    public int numCritical() {
        return numCrital;
    }

    // setDuration(u, duration[u.getIndex()]);
    public static void allsetDuration(PERT p,int[] duration){
        for(Vertex v : p.g){
            int d = duration[v.getIndex()];
            p.setDuration(v,d);
        }
    }
    public static PERT pert(Graph g, int[] duration) {
        PERT p = new PERT(g);
        p.numCrital=0;
        allsetDuration(p,duration);
        return null;
    }

    public static void main(String[] args) throws Exception {
        String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";

        // for the testing cases: just uncomment and change filepath

//        String filePath = "C:/Users/utdstudent/Downloads/lp2 test cases/lp2-pert-scc test cases/lp2-pert5.txt";
//        String content = "";
//        content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
//        graph=content.split("_____________________________________")[0];


        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
        Graph g = Graph.readDirectedGraph(in);
        g.printGraph(false);

        PERT p = new PERT(g);
        for(Vertex u: g) {
            p.setDuration(u, in.nextInt());
        }
        // Run PERT algorithm.  Returns null if g is not a DAG
        if(p.pert()) {
            System.out.println("Invalid graph: not a DAG");
        } else {
            System.out.println("Number of critical vertices: " + p.numCritical());
            System.out.println("u\tEC\tLC\tSlack\tCritical");
            for(Vertex u: g) {
                System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
            }
//			System.out.println("number of critical nodes "+p.numCrital);
        }
    }
}
