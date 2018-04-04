package tasklisteners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;

import ast.ASTAuxiliarStorage;
import database.DatabaseFachade;
import database.nodes.NodeTypes;
import database.relations.PartialRelation;
import database.relations.RelationTypes;
import utils.JavacInfo;
import utils.Pair;
import visitors.ASTTypesVisitor;
import visitors.PDGVisitor;

public class GetMembersAndClassesFromStructures implements TaskListener {
	private static final boolean DEBUG = false;
	private final JavacTask task;
	private final GraphDatabaseService graphDb;
	private Map<CompilationUnitTree, Integer> classCounter = new HashMap<CompilationUnitTree, Integer>();
	// private Set<CompilationUnitTree> unitsInTheSameFile = new
	// HashSet<CompilationUnitTree>();
	private boolean started = false;
	private boolean firstClass = true;

	private int counter = 0;

	private Transaction transaction;
	private Pair<PartialRelation<RelationTypes>, Object> argument;
	private CompilationUnitTree cu;
	private PDGVisitor pdgUtils = new PDGVisitor();

	private ASTAuxiliarStorage ast = new ASTAuxiliarStorage();
	public static List<ClassTree> classes = new ArrayList<ClassTree>();

	public static List<Tree> members = new ArrayList<Tree>();

	public GetMembersAndClassesFromStructures(JavacTask task, GraphDatabaseService graphDb) {
		this.task = task;
		this.graphDb = graphDb;
		System.out.println(task.getClass());
	}

	@Override
	public void finished(TaskEvent arg0) {
		if (DEBUG)
			System.out.println("FINISHING " + arg0.getKind());
		CompilationUnitTree u = arg0.getCompilationUnit();
		if (arg0.getKind() == Kind.PARSE)
			classCounter.put(u, u.getTypeDecls().size());
		else if (arg0.getKind() == Kind.ANALYZE) {

			started = true;
			classCounter.put(u, classCounter.get(u) - 1);

			if (firstClass) {
				firstClass = false;
				firstScan(u, u.getTypeDecls().get(counter++));
			} else
				scan(u.getTypeDecls().get(counter++), false);

			if (classCounter.get(u) == 0) {
				classCounter.remove(u);
				firstClass = true;
				counter = 0;

				// Tomamos como transacci�n cada archivo
				try {
					transaction.success();
				} finally {
					transaction.close();
				}
			}

		}
		if (DEBUG)
			System.out.println("FINISHED " + arg0.getKind());
	}

	private void firstScan(CompilationUnitTree u, Tree typeDeclaration) {

		JavacInfo.setJavacInfo(new JavacInfo(u, task));
		DatabaseFachade.setDB(graphDb);

		String fileName = u.getSourceFile().toUri().toString();

		transaction = DatabaseFachade.beginTx();
		Node compilationUnitNode = DatabaseFachade.createSkeletonNode(u, NodeTypes.COMPILATION_UNIT);
		compilationUnitNode.setProperty("fileName", fileName);

		argument = Pair.createPair(compilationUnitNode, RelationTypes.CU_PACKAGE_DEC);
		cu = u;
		scan(typeDeclaration, true);

	}

	private void scan(Tree typeDeclaration, boolean first) {

		if (DEBUG) {
			System.err.println("-*-*-*-*-*-*-* NEW TYPE DECLARATION AND VISITOR-*-*-*-*-*-*-*");
			System.err.println(cu.getSourceFile().getName());
			System.out.println("Final State:\n");

			System.out.println(typeDeclaration);
		}
		new ASTTypesVisitor(typeDeclaration, first, pdgUtils, ast).scan(cu, argument);
	}

	@Override
	public void started(TaskEvent arg0) {
		if (DEBUG)
			System.out.println("STARTING " + arg0.getKind());
		if (arg0.getKind() == Kind.GENERATE && started) {

			if (classCounter.size() == 0) {
				shutdownDatabase();
				started = false;
			}
		}
		if (DEBUG)
			System.out.println("STARTED " + arg0.getKind());

	}

	public void shutdownDatabase() {
		if (DEBUG)
			System.out.println("SHUTDOWN THE DATABASE");
		// System.out.println("CLASSES");
		// classes.forEach(System.out::println);
		// System.out.println("MEMBERS");
		// members.forEach(System.out::println);
		graphDb.shutdown();
		if (DEBUG)
			System.out.println("SHUTDOWN THE DATABASE ENDED");

	}

}