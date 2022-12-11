
/**
 * 
 * @author Clément Moreau
 *
 * @param <T> Generic Type
 */

public class Transition<T> {
	
	// Dans notre cas, T peut être un State ou un Set<State>.
	private T p;
	public T getP() { return p; }
	
	// Symbole lié à la transition
	private Symbol a;
	public Symbol getA() { return a; }
	
	// Contructeur
	public Transition(T _p, Symbol _a) {
		p = _p;
		a = _a;
	}
	
	public boolean equals(Object t) {	
		if (this == t)
			return true;
		if (t instanceof Transition) {
			Transition tr = (Transition) t;
			if (p != null ? !(p.equals(tr.p) && a.equals(tr.a)) : tr.p != null)
				return false;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + p + "," + a + ")";
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (p != null ? p.hashCode() : 0);
		hash = 31 * hash + (a != null ? a.hashCode() : 0);
		return hash;
	}

}
