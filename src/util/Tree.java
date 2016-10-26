package util;

import java.util.ArrayList;
import java.util.Stack;

import util.nodeGenerator.NodeGenerator;

public class Tree<E> implements Cloneable {
	private E value;
	private Tree<E> parent;
	private ArrayList<Tree<E>> children;
	private int height;
	private static final int defaultChildren = 2;
	
	/**
	 * Empty tree with no children.
	 * @param parent
	 */
	public Tree(){
		this.parent = null;
		this.children = new ArrayList<Tree<E>>(defaultChildren);
		this.height = 0;
	}
	
	/**
	 * Copy constructor
	 * @param copy Tree to copy from
	 */
	public Tree(Tree<E> copy) {
		this.value = copy.value;
		this.parent = null;
		this.height = copy.height;
		this.children = new ArrayList<Tree<E>>(copy.children.size());
		
		Stack<Tree<E>> s = new Stack<>();
		for (Tree<E> originalChild : copy.children) {
			s.push(originalChild);
			Tree<E> thisChild = new Tree<E>();
			thisChild.setParent(this);
			this.children.add(thisChild);
			s.push(thisChild);
		}
		
		while(!s.empty()) {
			Tree<E> thisOne = s.pop();
			Tree<E> original = s.pop();
			
			thisOne.value = original.value;
			thisOne.height = original.height;
			thisOne.children = new ArrayList<Tree<E>>(original.children.size());
			
			for (Tree<E> originalChild : original.children) {
				s.push(originalChild);
				Tree<E> thisOneChild = new Tree<E>();
				thisOneChild.setParent(thisOne);
				thisOne.children.add(thisOneChild);
				s.push(thisOneChild);
			}
		}
	}
	
	public E getValue(){return this.value;}
	public void setValue(E value){this.value = value;}
	
	public Tree<E> getParent(){return parent;}
	/**
	 * Add a parent to the current node. Does not check heights (do add/setChild instead).
	 * @param p
	 */
	public void setParent(Tree<E> p){parent = p;}
	
	public Tree<E> getLeftChild(){return this.children.get(0);}
	public void setLeftChild(Tree<E> left) {
		this.children.set(0, left);
		this.checkHeights(left);
	}
	
	public Tree<E> getRightChild(){return this.children.get(this.children.size() - 1);}
	public void setRightChild(Tree<E> right) {
		this.children.set(this.children.size() - 1, right);
		this.checkHeights(right);
	}
	
	public void addChild(Tree<E> child){
		this.children.add(child);
		this.checkHeights(child);
	}
	
	public Tree<E> getNChild(int n){return this.children.get(n);}
	public void setNChild(int n, Tree<E> child) {
		this.children.set(n, child);
		this.checkHeights(child);
	}
	
	public int getHeight(){return this.height;}
	
	public int getNodes() {
		Stack<Tree<E>> s = new Stack<>();
		int nodes = 1;
		s.push(this);
		
		while(!s.isEmpty()) {
			Tree<E> a = s.pop();
			
			for (Tree<E> child : a.children) {
				if(child != null) {
					s.push(child);
					nodes++;
				}
			}
		}
		
		return nodes;
	}
	
	public String preOrder() {
		return this.preOrder("    ");
	}
	
	private  String preOrder(String tabs){
		
		String content = tabs + this.value.toString();
		if (this.children.size() == 0)
			return content;
		else {
			tabs += "        ";
			for (Tree<E> child : this.children) {
				if(child != null)
					content += System.lineSeparator() + child.preOrder(tabs);
			}
		}
		
		return content;
	}
	
	public String preOrderIterative() {
		String content = new String("");
		Stack<Object> s = new Stack<>();
		int indent = 8;
		s.push(this);
		s.push(indent);
		
		while(!s.isEmpty()) {
			int n = (int) s.pop();
			@SuppressWarnings("unchecked")
			Tree<E> a = (Tree<E>) s.pop();
			content += new String(new char[n]).replace('\0', ' ') + a.value + System.lineSeparator();
			
			for (Tree<E> child : a.children) {
				if(child != null) {
					s.push(child);
					s.push(n + indent);
				}
			}
		}
		
		return content;
	}
	
	public String horizontalPreOrder(){
		String val = "";
		if(this.value == null)
			return val;
		
		val = this.value.toString();
		
		if(this.isLeaf())
			return val;
		else {
			String nextChildren = "( " + val;
			for (Tree<E> child : children) {
				if(child == null)
					nextChildren += " null";
				else
					nextChildren += " " + child.horizontalPreOrder();
			}
			return nextChildren + " )";
		}
	}
	
	public String toString() {
		return this.horizontalPreOrder();
	}
	
	public Tree<E> getNode(int n) {
		Stack<Tree<E>> s = new Stack<>();
		Tree<E> node = null;
		int counter = 0;
		boolean finish = false;
		s.push(this);
		
		while(!finish && !s.isEmpty()) {
			Tree<E> a = s.pop();
			if(counter == n) {
				node = a;
				finish = true;
			}
			
			for (int i = 0; !finish && i < a.children.size(); i++) {
				Tree<E> child = a.children.get(i);
				if(child != null) {
					s.push(child);
					counter++;
					if(counter == n) {
						node = child;
						finish = true;
					}
				}
			}
		}
		
		return node;
	}
	
	/**
	 * Exchange a child for another but does not change its parent old reference.
	 * @param current
	 * @param exchanged
	 */
	public void exchangeChild(Tree<E> current, Tree<E> exchanged) {
		boolean finished = false;
		
		for (int i = 0; !finished && i < this.children.size(); i++) {
			Tree<E> a = this.children.get(i);
			if(a == current) {
				this.children.set(i, exchanged);
				this.checkHeights(exchanged);
				
				finished = true;
			}
		}
	}

	public boolean isLeaf() {
		boolean leaf = true;
		
		for (int i = 0; leaf && i < this.children.size(); i++) {
			leaf = leaf && (this.children.get(i) == null);
		}
		
		return leaf;
	}
	
	public Tree<E> clone() {
		return new Tree<E>(this);
	}
	
	public Tree<E> getRandomLeaf() {
		Tree<E> node = this;
		
		while(!node.isLeaf())
			node = node.children.get(RandGenerator.getInstance().nextInt(node.children.size()));
		
		return node;
	}
	
	/**
	 * Return a random inner node. Returns null if the tree is a leaf.
	 * @return node or null
	 */
	public Tree<E> getRandomInnerNode() {
		Tree<E> node = this;
		boolean end = false;
		RandGenerator random = RandGenerator.getInstance();
		
		while(!end) {
			if(node.isLeaf()) {
				node = node.parent;
				end = true;
			}
			else {
				if(random.nextDouble() < 0.5) {
					node = node.children.get(random.nextInt(node.children.size()));
				} else
					end = true;
			}
		}
		
		return node;
	}
	
	/**
	 * Returns random node, it can be both inner or leaf
	 * @return node of the tree
	 */
	public Tree<E> getRandomNode() {
		Tree<E> node = this;
		boolean end = false;
		RandGenerator random = RandGenerator.getInstance();
		
		while(!end) {
			if(node.isLeaf()) {
				end = true;
			}
			else {
				if(random.nextDouble() < 0.4) {
					node = node.children.get(random.nextInt(node.children.size()));
				} else
					end = true;
			}
		}
		
		return node;
	}
	
	public void dropChilds() {
		this.children = new ArrayList<>(defaultChildren);
	}
	
	private void checkHeights(Tree<E> newNode) {
		if(newNode.height + 1 > this.height) {
			this.height = newNode.height + 1;
			adjustUpperHeights(this);
		}
	}

	private void adjustUpperHeights(Tree<E> node) {
		Tree<E> current = node;
		Tree<E> parent = node.parent;
		
		while (parent != null) {
			if (current.height + 1 > parent.height) {
				parent.height = current.height + 1;
				current = parent;
				parent = current.parent;
			}
			else
				parent = null;
		}
	}
	
	public void replaceBelowDepth(int n, NodeGenerator<E> generator) {
		Stack<Tree<E>> s = new Stack<>();
		Stack<Integer> depth = new Stack<>();
		s.push(this);
		depth.push(0);
		
		while(!s.isEmpty() && !depth.isEmpty()) {
			Tree<E> a = s.pop();
			int currentDepth = depth.pop();
			
			for (int i = 0; i < a.children.size(); i++) {
				Tree<E> child = a.children.get(i);
				if(child != null) {
					if(currentDepth > n - 3) {
						Tree<E> terminal = generator.next();
						a.setNChild(i, terminal);
						child.setParent(null);
						terminal.setParent(a);
					}
					else {
						s.push(child);
						depth.push(currentDepth + 1);
					}
				}
			}
		}
	}
	
	/* recursive clone
	public Tree<E> clone() {
		ArrayList<Tree<E>> retChildren = new ArrayList<Tree<E>>(this.children.size());
		
		for (int i = 0; i < this.children.size(); i++) {
			if(this.children.get(i) != null)
				retChildren.add(this.children.get(i).clone());
		}
		Tree<E> ret = new Tree<E>(retChildren, this.value, this.parent);
		
		return ret;
	} */
	
}