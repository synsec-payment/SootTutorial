package dev.navids.soottutorial.visual;

import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.dot.DotGraph;
import soot.util.queue.QueueReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class Serializer {

    //Copied from https://github.com/takedatmh/CBIDAU_tsukuba/blob/b9ef8377fcd3fa10f29383c59676c81a4dc95966/src/dataflow/util/CGCreator.java
    //Searched for serializecallgraph in github

    public static List<Edge> EdgeListEdge= new ArrayList<Edge>();

    /**
     * Call Graph Creation
     *
     * @param graph
     * @param fileName
     */
    public static void serializeCallGraph(CallGraph graph, String fileName) {

        List<Edge> EdgeList = new ArrayList<Edge>();

        if (fileName == null) {
            fileName = soot.SourceLocator.v().getOutputDir();
            if (fileName.length() > 0) {
                fileName = fileName + java.io.File.separator;
            }
            fileName = fileName + "call-graph" + DotGraph.DOT_EXTENSION;
        }
        //System.out.println("file name " + fileName);
        DotGraph canvas = new DotGraph("Call_Graph");
        QueueReader<Edge> listener = graph.listener();

        int index = 0;
        while (listener.hasNext()) {
            Edge next = listener.next();
            MethodOrMethodContext src = next.getSrc();
            MethodOrMethodContext tgt = next.getTgt();
            String srcString = src.toString();
            String tgtString = tgt.toString();

            // Excepted java packages.
            if ((!srcString.startsWith("<java.")
                    && !srcString.startsWith("<sun.")
                    //&& !srcString.startsWith("<org.")
                    && !srcString.startsWith("<androidx.") //adding this line for simplicity now
                    && !srcString.startsWith("<android.") //adding this line for simplicity now
                    //&& !srcString.startsWith("<com.")
                    && !srcString.startsWith("<jdk.")
                    && !srcString.startsWith("<simple.")
                    && !srcString.startsWith("<javax."))
                    && (!tgtString.startsWith("<java.")
                    && !tgtString.startsWith("<sun.")
                    //&& !tgtString.startsWith("<org.")
                    && !tgtString.startsWith("<androidx.") //adding this line for simplicity now
                    && !tgtString.startsWith("<android.") //adding this line for simplicity now
                    //&& !tgtString.startsWith("<com.")
                    && !tgtString.startsWith("<jdk.")
                    && !srcString.startsWith("<simple.")
                    && !tgtString.startsWith("<javax."))) {

                // Drawing CG excepted designated java packages.
                canvas.drawNode(srcString);
                canvas.drawNode(tgtString);
                canvas.drawEdge(srcString, tgtString);

                //EdgeList
                EdgeList.add(index, next);
            }
        }
        //Write .dot file.
        canvas.plot(fileName);
        //Put edge List into class field to be refered by other methods.
        EdgeListEdge = EdgeList;

        ////Debug
        //for(Edge e : EdgeListEdge)
        //	System.out.println("ALL Edge : " + e.getSrc().method().getName() +" -> "+ e.getTgt().method().getName());

        return;
    }
}

