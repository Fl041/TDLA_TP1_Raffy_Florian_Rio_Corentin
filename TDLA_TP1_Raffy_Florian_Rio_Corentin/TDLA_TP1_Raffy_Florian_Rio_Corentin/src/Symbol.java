
/**
 * @author Clément Moreau
 */

public class Symbol {

	private String symbol;
	public String getSymbol() { return symbol; }

	public Symbol(String _symbol) {
		symbol = _symbol;
	}

	@Override
	public String toString() {
		return "" + symbol;
	}

	@Override
	public boolean equals(Object o) {	
		if (this == o)
			return true;
		if (o instanceof Symbol) {
			Symbol _o = (Symbol) o;
			return symbol.equals(_o.symbol);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 9;
		hash = 31 * hash + (symbol != null ? symbol.hashCode() : 0);
		return hash;
	}
	

}
