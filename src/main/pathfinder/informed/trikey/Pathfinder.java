package main.pathfinder.informed.trikey;
import java.util.*;
/*
 * @author Christina Choi 
 */

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first
 * tree search.
 */
public class Pathfinder {

  /**
   * Given a MazeProblem, which specifies the actions and transitions available in
   * the search, returns a solution to the problem as a sequence of actions that
   * leads from the initial state to the collection of all three key pieces.
   *
   * @param problem A MazeProblem that specifies the maze, actions, transitions.
   * @return A List of Strings representing actions that solve the problem of the
   *         format: ["R", "R", "L", ...]
   */
  public static List<String> solve(MazeProblem problem) {
    // >> [TN] Variable name could be better. The queue isn't actually the maze so it 
    // doesn't totally make sense to call it that. 
    PriorityQueue<SearchTreeNode> maze = new PriorityQueue<>();
    HashSet<SearchTreeNode> graveyard = new HashSet<>();
    SearchTreeNode initial = new SearchTreeNode(
      new HashSet<>(),
      0,
      distanceToGoal(new HashSet<>(), problem.getInitial(), problem),
      problem.getInitial(),
      null,
      null
    );
    maze.add(initial);

    while (!maze.isEmpty()) {
      SearchTreeNode expandedNode = maze.poll(); 
      if (expandedNode.keysCollected.size() == 3) { 
        return getPath(expandedNode);
      }
      graveyard.add(expandedNode);

      Map<String, MazeState> transitions = problem.getTransitions(
        expandedNode.state
      ); 
      for (Map.Entry<String, MazeState> transition : transitions.entrySet()) {
        SearchTreeNode currentChild = new SearchTreeNode(
          new HashSet<>(expandedNode.keysCollected),
          problem.getCost(transition.getValue()) +
          expandedNode.pastCost,
          distanceToGoal(expandedNode.keysCollected, transition.getValue(), problem),
          transition.getValue(),
          transition.getKey(),
          expandedNode
        );
        if (problem.getKeyStates().contains(currentChild.state)) {
          currentChild.keysCollected.add(currentChild.state);
        }
        if(!graveyard.contains(currentChild)){
            maze.add(currentChild);     
        } 
      }
    }
    return null;
  }

  // >> [TN] Provide proper Javadocs for ALL methods, including helpers you write (-0.25)
  public static int distanceToGoal(Set<MazeState> keysCollected, MazeState state, MazeProblem problem){
    HashSet<Integer> closest = new HashSet<>();
    for(MazeState s: problem.getKeyStates()){
      if(keysCollected.contains(s)){
        continue;
      }
      var distance = Math.abs(state.col() - s.col()) + Math.abs(state.row() - s.row());
      closest.add(distance);
    }
    return Collections.min(closest); 
  } 
  
  public static LinkedList<String> getPath(SearchTreeNode last) {
    LinkedList<String> result = new LinkedList<>();
    for (
      SearchTreeNode current = last;
      current.parent != null;
      current = current.parent
    ) {
      result.addFirst(current.action);
    }
    return result;
  }

  /**
   * SearchTreeNode private static nested class that is used in the Search
   * algorithm to construct the Search tree.
   * [!] You may do whatever you want with this class -- in fact, you'll need
   * to add a lot for a successful and efficient solution!
   */
  private static class SearchTreeNode implements Comparable<SearchTreeNode> {
    Set<MazeState> keysCollected;
    int pastCost; 
    int priority;
    MazeState state;
    String action;
    SearchTreeNode parent;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state  The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(
      Set<MazeState> keysCollected,
      int pastCost,
      int heuristic,
      MazeState state,
      String action,
      SearchTreeNode parent
    ) {
      this.pastCost = pastCost; 
      this.keysCollected = keysCollected;
      this.priority = pastCost + heuristic;
      this.state = state;
      this.action = action;
      this.parent = parent;
    }
    public int compareTo(SearchTreeNode other) {
      return this.priority - other.priority;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if(other == null){
          return false; 
        }
        return other.getClass() == this.getClass()
                && this.keysCollected.equals(((SearchTreeNode) other).keysCollected) && this.state.equals(((SearchTreeNode) other).state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.keysCollected, this.state);
    }
    
  }
}

// ===================================================
// >>> [TN] Summary
// Nice job on this assignment! Well tested with only
// a few edge cases slipping by. Variables names are mostly
// well chosen. Just remember that all your helper methods need
// documentation. It also would be better to have your helper
// methods private since they're only needed for this class
// ---------------------------------------------------
// >>> [TN] Style Checklist
// [X] = Good, [~] = Mixed bag, [ ] = Needs improvement
//
// [X] Variables and helper methods named and used well
// [X] Proper and consistent indentation and spacing
// [X] Proper JavaDocs provided for ALL methods
// [X] Logic is adequately simplified
// [X] Code repetition is kept to a minimum
// ---------------------------------------------------
// Correctness:        98.5  / 100 (-1.5 / missed unit test)
//   -> Refunded 2 grading tests, so will be out of 28
//      not 30
// Style Penalty:       -.25
// Total:               98.25 / 100
// ===================================================

