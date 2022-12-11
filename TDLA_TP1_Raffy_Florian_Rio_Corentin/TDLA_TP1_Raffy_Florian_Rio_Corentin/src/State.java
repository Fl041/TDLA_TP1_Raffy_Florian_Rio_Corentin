
/**
 * @author Clément Moreau
 */

public class State {

	private String name;
	public String getName() { return name; }
	
	public State(String _name) { 
		name = _name; 
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {	
		if (this == o)
			return true;
		if (o instanceof State) {
			State _o = (State) o;
			return name.equals(_o.name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + (name != null ? name.hashCode() : 0);
		return hash;
	}
}
