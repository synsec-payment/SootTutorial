package dev.navids.soottutorial.visual;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import dev.navids.soottutorial.utility.ListUtility;
import dev.navids.soottutorial.utility.Logger;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CallGraphFromInputScope {
    static Hashtable<String, CallGraphNode> nodes = new Hashtable<String, CallGraphNode>();

	static Hashtable<String, HashSet<SootMethod>> fieldSetters = new Hashtable<String, HashSet<SootMethod>>();

	public static void init() {
		long st = System.currentTimeMillis();
		CallGraphNode tmp;
		Value tv;
		FieldRef fr;
		String str;
		for (SootClass sclas : Scene.v().getClasses()) {

			for (SootMethod smthd : sclas.getMethods()) {

				tmp = new CallGraphNode(smthd);
				nodes.put(smthd.toString(), tmp);
				if (smthd.isConcrete())
					smthd.retrieveActiveBody();
			}
		}
		Logger.printI("[CG time]:" + (System.currentTimeMillis() - st));
		for (SootClass sclas : Scene.v().getClasses()) {
			for (SootMethod smthd : ListUtility.clone(sclas.getMethods())) {

				if (!smthd.isConcrete())
					continue;
				Body body = smthd.retrieveActiveBody();
				if (body == null)
					continue;
				for (Unit unit : body.getUnits()) {
					if (unit instanceof Stmt) {
						if (((Stmt) unit).containsInvokeExpr()) {
							try {

								addCall(smthd, ((Stmt) unit).getInvokeExpr().getMethod());
							} catch (Exception e) {
								Logger.printW(e.getMessage());
							}
						}
						for (ValueBox var : ((Stmt) unit).getDefBoxes()) {
							tv = var.getValue();

							if (tv instanceof FieldRef) {
								fr = (FieldRef) tv;
								if (fr.getField().getDeclaringClass().isApplicationClass()) {
									str = fr.getField().toString();
									if (!fieldSetters.containsKey(str)) {
										fieldSetters.put(str, new HashSet<SootMethod>());
									}
									fieldSetters.get(str).add(smthd);
								}
							}
						}
					}
				}
			}
		}

		Logger.printI("[CG time]:" + (System.currentTimeMillis() - st));
	}

    public static CallGraph getCallGraphWithBFSFromStartingMethod(SootMethod startingMethod) {
        CallGraph callGraph = new CallGraph();

        long st = System.currentTimeMillis();
		CallGraphNode tmp;
        
		for (SootClass sclas : Scene.v().getApplicationClasses()) { // This line is modified

			for (SootMethod smthd : sclas.getMethods()) {

				tmp = new CallGraphNode(smthd);
				nodes.put(smthd.toString(), tmp);
				if (smthd.isConcrete())
					smthd.retrieveActiveBody();
			}
		}
		Logger.printI("[CG time]:" + (System.currentTimeMillis() - st));
        // Copied the above part from the init function

        Queue<SootMethod> q = new LinkedList<SootMethod>();
        q.add(startingMethod);
        SootMethod from, to;
		Edge edge;
        while(q.isEmpty() == false) {
            from = q.poll();

			if (from == null) {
				System.out.println("The starting method could not be found in this project.");
				continue;
			}

            if (!from.isConcrete())
                continue;
            Body body = from.retrieveActiveBody();
            if (body == null)
                continue;
            
            for (Unit unit: body.getUnits()) {
                if (unit instanceof Stmt) {
                    if (((Stmt) unit).containsInvokeExpr()) {
                        try {
                            to = ((Stmt) unit).getInvokeExpr().getMethod();
                            
							if (checkIfCallExist(from, to) == false) {
								edge = new Edge((MethodOrMethodContext) from, (Stmt) unit, (MethodOrMethodContext) to);
								addCall(from, to);
								callGraph.addEdge(edge);
                            	q.add(to);
							}
                            
                        } catch (Exception e) {
                            Logger.printW(e.getMessage());
                        }
                    }
                }
            }
        }
        Logger.printI("[CG time]:" + (System.currentTimeMillis() - st));
        return callGraph;
    }

	private static void addCall(SootMethod from, SootMethod to) {
		CallGraphNode fn, tn;
		fn = getNode(from);
		tn = getNode(to);


		if (fn == null || tn == null) {
			return;
		}

		fn.addCallTo(tn);
		tn.addCallBy(fn);

	}

	/*
	Since we are not creating a context-sensitive graph at this moment,
	we can check directly if they have edge between them (irrespective of their context/argument).
	*/
	private static boolean checkIfCallExist(SootMethod from, SootMethod to) {
		CallGraphNode fn, tn;
		fn = getNode(from);
		tn = getNode(to);


		if (fn == null || tn == null) {
			// Which means we don't need to add this call further in the call graph
			return true;
		}

		if (fn.getCallTo().contains(tn) == true && tn.getCallBy().contains(fn) == true) {
			// if (fn -> tn) exists in both fn and tn's set, there is an edge between them
			return true;
		} else {
			return false;
		}
	}

	public static CallGraphNode getNode(SootMethod from) {
		return getNode(from.toString());
	}

	public static CallGraphNode getNode(String from) {
		return nodes.get(from);
	}

	public static HashSet<SootMethod> getSetter(SootField sootField) {
		return fieldSetters.get(sootField.toString());
	}
}
