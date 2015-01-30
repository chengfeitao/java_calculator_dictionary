
// import lists and other data structures from the Java standard library
import java.util.*;

// a type for arithmetic expressions
     double eval(); 	                       // Problem 1a
     List<Sopn> toRPN(); 	               // Problem 1c
     Pair<List<Sopn>,Integer> toRPNopt();    // Problem 1d
}

class Num implements AExp {
	double a;
	Num(double num) {a=num;}
	public double eval() {return a;}
	public List<Sopn> toRPN() {
		List<Sopn> l = new LinkedList<Sopn>();
		l.add(new Push(a));
		return l;
	}
	public Pair<List<Sopn>,Integer> toRPNopt() {
		List<Sopn> l2 = this.toRPN();
		Pair<List<Sopn>,Integer> p = new Pair<List<Sopn>,Integer>(l2,1);
		return p;
	}
}

class BinOp implements AExp {
	AExp e1, e2;
	Op op;
	BinOp(AExp a1, Op o, AExp a2) {e1=a1;op=o;e2=a2;}
	public double eval() {return op.calculate(e1.eval(),e2.eval());}
	public List<Sopn> toRPN() {
		List<Sopn> l = new LinkedList<Sopn>();
		l.addAll(e1.toRPN());
		l.addAll(e2.toRPN());
		l.add(new Calculate(op));
		return l;
	}
	public Pair<List<Sopn>,Integer> toRPNopt() {
		List<Sopn> l = new LinkedList<Sopn>();
		List<Sopn> lLeft = e1.toRPNopt().fst();
		List<Sopn> lRight = e2.toRPNopt().fst();
		int iLeft = e1.toRPNopt().snd();
		int iRight = e2.toRPNopt().snd();

		if (iLeft < iRight) {
			l.addAll(lRight);
			l.addAll(lLeft);
			l.add(new Swap());
			l.add(new Calculate(op));
			return new Pair<List<Sopn>,Integer> (l,iRight);
		}
		else {
			l.addAll(lLeft);
			l.addAll(lRight);
			l.add(new Calculate(op));
			if (iLeft > iRight)
				return new Pair<List<Sopn>,Integer> (l,iLeft);
			else 
				return new Pair<List<Sopn>,Integer> (l,iLeft+1);
		}
	}
}

// a representation of four arithmetic operators
enum Op {
    PLUS { public double calculate(double a1, double a2) { return a1 + a2; } },
    MINUS { public double calculate(double a1, double a2) { return a1 - a2; } },
    TIMES { public double calculate(double a1, double a2) { return a1 * a2; } },
    DIVIDE { public double calculate(double a1, double a2) { return a1 / a2; } };

    abstract double calculate(double a1, double a2);
}

// a type for stack operations
interface Sopn {
	void eval (Stack<Double> s);
}

class Push implements Sopn {
	double value;
	Push(double num) {this.value=num;}
	public void eval (Stack<Double> a) {a.push(this.value);}
	public String toString() {return "Push "+value;}
}

class Calculate implements Sopn {
	Op op;
	Calculate(Op o) {this.op=o;}
	public void eval (Stack<Double> a) {
		double e1 = a.pop();
		double e2 = a.pop();
		a.push(this.op.calculate(e2,e1));
	}
	public String toString() {return "Calculate "+op;}
}

class Swap implements Sopn {
	Swap() {}
	public void eval (Stack<Double> a) {
		double e1 = a.pop();
		double e2 = a.pop();
		a.push(e1);
		a.push(e2);
	}
	public String toString() {return "Swap";}
}

// an RPN expression is essentially a wrapper around a list of stack operations
class RPNExp {
    protected List<Sopn> instrs;

    public RPNExp(List<Sopn> instrs) { this.instrs = instrs; }

    public double eval() {
	Stack<Double> s = new Stack<Double>();
	for (Sopn i : instrs)
		i.eval(s);
	return s.pop();
	}  // Problem 1b
}

class Pair<A,B> {
    protected A fst;
    protected B snd;

    Pair(A fst, B snd) { this.fst = fst; this.snd = snd; }

    A fst() { return fst; }
    B snd() { return snd; }
}


class CalcTest {
    public static void main(String[] args) {
	    // a test for Problem 1a
	 AExp aexp =
	     new BinOp(new BinOp(new Num(1.0), Op.PLUS, new Num(2.0)),
	 	      Op.TIMES,
	 	      new Num(3.5));
	 System.out.println("aexp evaluates to " + aexp.eval()); // aexp evaluates to 9.0

	// a test for Problem 1b
	 List<Sopn> instrs = new LinkedList<Sopn>();
	 instrs.add(new Push(2.0));
	 instrs.add(new Push(2.5));
	 instrs.add(new Calculate(Op.PLUS));
	 instrs.add(new Push(3.0));
	 instrs.add(new Calculate(Op.DIVIDE));
	 RPNExp rpnexp = new RPNExp(instrs);
	 System.out.println("rpnexp evaluates to " + rpnexp.eval());  // rpnexp evaluates to 9.0

	// a test for Problem 1c
	 System.out.println("aexp converts to " + aexp.toRPN());

	// a test for Problem 1d
	 AExp aexp2 =
	     new BinOp(new Num(1.0), Op.MINUS, new BinOp(new Num(2.0), Op.PLUS, new Num(3.0)));
	 System.out.println("aexp2 optimally converts to " + aexp2.toRPNopt().fst()+aexp2.toRPNopt().snd());
    }
}


interface Dict<K,V> {
    void put(K k, V v);
    V get(K k) throws NotFoundException;
}

class NotFoundException extends Exception {}



class DictImpl2<K,V> implements Dict<K,V> {
    protected Node<K,V> root;

    DictImpl2() {this.root = new Empty<K,V>(); }

    public void put(K k2, V v2) {this.root = new Entry<K,V>(k2,v2,this.root);}

    public V get(K k) throws NotFoundException {
	try {return this.root.get(k);}
	catch (NotFoundException e) {throw e;}
	}
}

interface Node<K,V> {

	V get(K k) throws NotFoundException;
}

class Empty<K,V> implements Node<K,V> {
	Empty() {}
	public V get(K k) throws NotFoundException {throw new NotFoundException();}

}

class Entry<K,V> implements Node<K,V> {
    protected K k;
    protected V v;
    protected Node<K,V> next;

    Entry(K k, V v, Node<K,V> next) {
	this.k = k;
	this.v = v;
	this.next = next;
    }
	
	public V get(K k2) throws NotFoundException {
		if (this.k.equals(k2))
			return this.v;
		else
		{
			try {return this.next.get(k2);}
			catch (NotFoundException e) {throw e;}
		}
	}

}


interface DictFun<A,R> {
    R invoke(A a) throws NotFoundException;
}

class DictImpl3<K,V> implements Dict<K,V> {
    protected DictFun<K,V> dFun;

    DictImpl3() { 
	dFun = new DictFun<K,V>() {
		public V invoke(K a) throws NotFoundException {throw new NotFoundException();}
	};
    }

    public void put(K k, V v) { 
	final K k2 = k;
	final V v2 = v;
	final DictFun<K,V> existdFun = dFun;
	dFun = new DictFun<K,V>() {
		public V invoke(K a) throws NotFoundException {
			if (a.equals(k2))
				return v2;
			else {
				try {return existdFun.invoke(a);}
				catch (NotFoundException e) {throw e;}
			}
		}
	};
    }

    public V get(K k) throws NotFoundException { 
	try {return dFun.invoke(k);}
	catch (NotFoundException e) {throw e;}
    }
}


class DictTest {
    public static void main(String[] args) {

	    // a test for Problem 2a
	 Dict<String,Integer> dict1 = new DictImpl2<String,Integer>();
	 dict1.put("hello", 23);
	 dict1.put("bye", 45);
	 try {
	     System.out.println("bye maps to " + dict1.get("bye")); // prints 45
	     System.out.println("hello maps to " + dict1.get("hello"));  // throws an exception
	 } catch(NotFoundException e) {
	     System.out.println("not found!");  // prints "not found!"
	 }

	// a test for Problem 2b
	 Dict<String,Integer> dict2 = new DictImpl3<String,Integer>();



	 try {
	     System.out.println("bye maps to " + dict2.get("bye"));  // prints 45
	     System.out.println("hi maps to " + dict2.get("hi"));   // throws an exception
	 } catch(NotFoundException e) {
	     System.out.println("not found!");  // prints "not found!"
	 }
    }
}

