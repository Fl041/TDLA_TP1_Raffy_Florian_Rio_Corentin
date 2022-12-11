import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		//Attention : ne pas utiliser le accept() et le toDFA() en même temps sinon la deuxième fonction ne fonctionnera pas 


		// Tests pour DFA1
		DFA dfa1 = new DFA("DFA1.json");
		System.out.println(dfa1.toString());
		System.out.println(dfa1.accept("aba"));

		/*		
		// Tests pour DFA3
		DFA dfa3 = new DFA("DFA3.json");
		System.out.println(dfa3.toString());
		System.out.println(dfa3.accept("aba"));
		 */

		/*
		// Tests pour NFA1
		NFA nfa1 = new NFA("NFA1.json");
		System.out.println(nfa1.toString());
		System.out.println(nfa1.accept("ab"));
		System.out.println(nfa1.toDFA());
		 */

		/*
		// Tests pour NFA3
		NFA nfa3 = new NFA("NFA3.json");
		System.out.println(nfa3.toString());
		System.out.println(nfa3.accept("ab"));
		System.out.println(nfa3.toDFA());
		 */

		/*
		// Tests pour NFA1e
		AFNDe nfa1e = new AFNDe("NFA1e.json");
		System.out.println(nfa1e.accept("100"));
		System.out.println(nfa1e.toDFA());
		 */
	}
}

