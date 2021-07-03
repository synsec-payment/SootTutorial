package dev.navids.soottutorial.android;

import dev.navids.soottutorial.visual.AndroidCallGraphFilter;
import dev.navids.soottutorial.visual.CallGraphFromInputScope;
import dev.navids.soottutorial.visual.Visualizer;
import dev.navids.soottutorial.visual.Serializer;
import soot.EntryPoints;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.Chain;

import java.io.File;
import java.util.*;

public class AndroidCallgraph {
    private final static String USER_HOME = System.getProperty("user.home");
    private static String androidJar = USER_HOME + "/Library/Android/sdk/platforms";
    static String androidDemoPath = System.getProperty("user.dir") + File.separator + "demo" + File.separator + "Android";
    //static String apkPath = androidDemoPath + File.separator + "/st_demo.apk";
    static String apkPathAliPay = "/home/silsil/Files/payment-app-analysis/apk/com.eg.android.AlipayGphone_382_apps.evozi.com.apk";
    static String apkPathSpay = "/home/silsil/Files/payment-app-analysis/apk/com.samsung.android.spay_253300100_apps.evozi.com.apk";
    static String apkPathVisa = "/home/silsil/Files/payment-app-analysis/apk/com.visa.app.cdet_20_apps.evozi.com.apk";
    static String apkPathGpay = "/home/silsil/Files/payment-app-analysis/apk/com.google.android.apps.nbu.paisa.user_2251167_google_pay_apps.evozi.com.apk";
    static String apkPathQq = "/home/silsil/Files/payment-app-analysis/apk/com.tencent.mobileqq_1380_apps.evozi.com.apk";
    static String apkPathUnionPay = "/home/silsil/Files/payment-app-analysis/apk/com.unionpay_2581_apps.evozi.com.apk";
    static String apkPathWechatPay = "/home/silsil/Files/payment-app-analysis/apk/com.unionpay_2581_apps.evozi.com.apk";
    static String apkPath = apkPathVisa;
    static String childMethodSignature = "<dev.navids.multicomp1.ClassChild: void childMethod()>";
    static String childBaseMethodSignature = "<dev.navids.multicomp1.ClassChild: void baseMethod()>";
    static String parentMethodSignature = "<dev.navids.multicomp1.ClassParent: void baseMethod()>";
    //static String unreachableMethodSignature = "<dev.navids.multicomp1.ClassParent: void unreachableMethod()>";
    static String unreachableMethodSignature = "";
    static String mainActivityEntryPointSignature = "<dummyMainClass: dev.navids.multicomp1.MainActivity dummyMainMethod_dev_navids_multicomp1_MainActivity(android.content.Intent)>";
    static String mainActivityClassName = "dev.navids.multicomp1.MainActivity";

    public static void main(String[] args){

        if(System.getenv().containsKey("ANDROID_HOME"))
            androidJar = System.getenv("ANDROID_HOME")+ File.separator+"platforms";
        // Parse arguments
        InfoflowConfiguration.CallgraphAlgorithm cgAlgorithm = InfoflowConfiguration.CallgraphAlgorithm.SPARK;
        if (args.length > 0 && args[0].equals("CHA"))
            cgAlgorithm = InfoflowConfiguration.CallgraphAlgorithm.CHA;
        boolean drawGraph = false;
        if (args.length > 1 && args[1].equals("draw"))
            drawGraph = true;
        // Setup FlowDroid
        final InfoflowAndroidConfiguration config = AndroidUtil.getFlowDroidConfig(apkPath, androidJar, cgAlgorithm);
        SetupApplication app = new SetupApplication(config);
        // Create the Callgraph without executing taint analysis
        app.constructCallgraph();// This line is necessary. getEntryPoints or methodsOfApplicationClasses won't work without it.
        //CallGraph callGraph = Scene.v().getCallGraph();
        int classIndex = 0;
        // Print some general information of the generated callgraph. Note that although usually the nodes in callgraph
        // are assumed to be methods, the edges in Soot's callgraph is from Unit to SootMethod.
        // AndroidCallGraphFilter androidCallGraphFilter = new AndroidCallGraphFilter(AndroidUtil.getPackageName(apkPath));
        // for(SootClass sootClass: androidCallGraphFilter.getValidClasses()){
        //     System.out.println(String.format("Class %d: %s", ++classIndex, sootClass.getName()));
        //     for(SootMethod sootMethod : sootClass.getMethods()){
        //         int incomingEdge = 0;
        //         for(Iterator<Edge> it = callGraph.edgesInto(sootMethod); it.hasNext();incomingEdge++,it.next());
        //         int outgoingEdge = 0;
        //         for(Iterator<Edge> it = callGraph.edgesOutOf(sootMethod); it.hasNext();outgoingEdge++,it.next());
        //         System.out.println(String.format("\tMethod %s, #IncomeEdges: %d, #OutgoingEdges: %d", sootMethod.getName(), incomingEdge, outgoingEdge));
        //     }
        // }
        System.out.println("-----------");

        // List<SootMethod>entryPoints = Scene.v().getEntryPoints();
        // for (SootMethod entryPoint: entryPoints) {
        //     System.out.println(entryPoint.getSignature());
        // }
        // This block of code returns only <dummyMainClass: void dummyMainMethod(java.lang.String[])>
        // for visa app
        System.out.println("-----------");

        SootMethod processCommandApduMethod = getSpecificMethodFromName("processCommandApdu");
        // This block reveals the processCommandApdu method
        System.out.println("-----------");

        Chain<SootClass> chain = Scene.v().getApplicationClasses();

        System.out.println("-----------");

        System.out.println("call graph creation is started.");
        CallGraph callGraph = CallGraphFromInputScope.getCallGraphWithBFSFromStartingMethod(processCommandApduMethod);
        System.out.println("call graph is created.");
        Serializer.serializeCallGraph(callGraph, "output.dot");
        // Draw a subset of call graph
    //    if (drawGraph) {
    //        Visualizer.v().addCallGraph(callGraph,
    //                androidCallGraphFilter,
    //                new Visualizer.AndroidNodeAttributeConfig(true));
    //        Visualizer.v().draw();
    //    }

        System.out.println("-----------");

        // Retrieve some methods to demonstrate reachability in callgraph
        // SootMethod childMethod = Scene.v().getMethod(childMethodSignature);
        // SootMethod parentMethod = Scene.v().getMethod(parentMethodSignature);
        //SootMethod unreachableMehthod = Scene.v().getMethod(unreachableMethodSignature);
        //Scene.v().getmethod
        // SootMethod mainActivityEntryMethod = Scene.v().getMethod(mainActivityEntryPointSignature);

        // A better way to find MainActivity's entry method (generated by FlowDroid)
        // for(SootMethod sootMethod : app.getDummyMainMethod().getDeclaringClass().getMethods()) {
        //     if (sootMethod.getReturnType().toString().equals(mainActivityClassName)) {
        //         System.out.println("MainActivity's entrypoint is " + sootMethod.getName()
        //                 );//+ " and it's equal to mainActivityEntryMethod: " + sootMethod.equals(mainActivityEntryMethod));
        //     }
        // }
        // Perform BFS from the main entrypoint to see if "unreachableMehthod" is reachable at all or not
        // Map<SootMethod, SootMethod> reachableParentMapFromEntryPoint = getAllReachableMethods(app.getDummyMainMethod());
        // if(reachableParentMapFromEntryPoint.containsKey(unreachableMehthod))
        //     System.out.println("unreachableMehthod is reachable, a possible path from the entry point: " + getPossiblePath(reachableParentMapFromEntryPoint, unreachableMehthod));
        // else
        //     System.out.println("unreachableMehthod is not reachable from the entrypoint.");
        // // Perform BFS to get all reachable methods from MainActivity's entry point
        // Map<SootMethod, SootMethod> reachableParentMapFromMainActivity = getAllReachableMethods(mainActivityEntryMethod);
        // if(reachableParentMapFromMainActivity.containsKey(childMethod))
        //     System.out.println("childMethod is reachable from MainActivity, a possible path: " + getPossiblePath(reachableParentMapFromMainActivity, childMethod));
        // else
        //     System.out.println("childMethod is not reachable from MainActivity.");
        // if(reachableParentMapFromMainActivity.containsKey(parentMethod))
        //     System.out.println("parentMethod is reachable from MainActivity, a possible path: " + getPossiblePath(reachableParentMapFromMainActivity, parentMethod));
        // else
        //     System.out.println("parentMethod is not reachable from MainActivity.");


        // Draw a subset of call graph
//        if (drawGraph) {
//            Visualizer.v().addCallGraph(callGraph,
//                    androidCallGraphFilter,
//                    new Visualizer.AndroidNodeAttributeConfig(true));
//            Visualizer.v().draw();
//        }
        // if (drawGraph) {
        //     Serializer.serializeCallGraph(callGraph, "output.dot");
        // }
    }

    private static SootMethod getSpecificMethodFromName(String string) {
        // Getting all methods from applicationClasses
        List<SootMethod>applicationMethods = EntryPoints.v().methodsOfApplicationClasses();
        //List<SootMethod>applicationMethods = Scene.v().getApplicationClasses();
        for (SootMethod applicationMethod: applicationMethods) {
            //System.out.println(applicationMethod.getSignature());
            //System.out.println(applicationMethod.getName());
            if (applicationMethod.getName().equals("processCommandApdu")) {
                System.out.println("Found apdu method.");
                System.out.println(applicationMethod.getDeclaringClass());
                System.out.println(applicationMethod.getSignature());
                System.out.println(applicationMethod.getSubSignature());
                return applicationMethod;
            }
        }
        return null;
    }

    // A Breadth-First Search algorithm to get all reachable methods from initialMethod in the callgraph
    // The output is a map from reachable methods to their parents
    public static Map<SootMethod, SootMethod> getAllReachableMethods(SootMethod initialMethod){
        CallGraph callgraph = Scene.v().getCallGraph();
        List<SootMethod> queue = new ArrayList<>();
        queue.add(initialMethod);
        Map<SootMethod, SootMethod> parentMap = new HashMap<>();
        parentMap.put(initialMethod, null);
        for(int i=0; i< queue.size(); i++){
            SootMethod method = queue.get(i);
            for (Iterator<Edge> it = callgraph.edgesOutOf(method); it.hasNext(); ) {
                Edge edge = it.next();
                SootMethod childMethod = edge.tgt();
                if(parentMap.containsKey(childMethod))
                    continue;
                parentMap.put(childMethod, method);
                queue.add(childMethod);
            }
        }
        return parentMap;
    }

    public static String getPossiblePath(Map<SootMethod, SootMethod> reachableParentMap, SootMethod it) {
        String possiblePath = null;
        while(it != null){
            String itName = it.getDeclaringClass().getShortName()+"."+it.getName();
            if(possiblePath == null)
                possiblePath = itName;
            else
                possiblePath = itName + " -> " + possiblePath;
            it = reachableParentMap.get(it);
        } return possiblePath;
    }

}
