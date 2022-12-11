import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author ClÃƒÂ©ment Moreau
 */

// Automate fini dÃƒÂ©terministe
public class DFA extends FSM {	

	// Unique ÃƒÂ©tat de dÃƒÂ©part
	private State start;
	public State getStart() { return start; }

	// Fonction de Transititon 
	private Map<Transition<State>, State> delta;
	public Map<Transition<State>, State> getDelta() { return delta; }

	// Constructeur par dÃƒÂ©faut
	public DFA(Set<State> _states,
			Set<Symbol> _alphabet,
			State _start,
			Set<State> _ends,
			Map<Transition<State>, State> _delta) {

		super(_states, _alphabet, _ends);
		start = _start;
		delta = _delta;
	}

	// Constructeur par fichier
	public DFA(String path) {

		super(path);

		Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		JSONObject jo = (JSONObject) obj;

		start = new State((String) jo.get("start"));
		System.err.print(this.getStates().contains(start) ? "" : "Ãƒâ€°tat initial "+ start +" Ã¢Ë†â€° Q\n");

		JSONArray ja = (JSONArray) jo.get("delta");

		delta = new HashMap<Transition<State>, State>();		

		for(Object _o : ja) {

			JSONObject _jo = (JSONObject) _o;

			State q = new State((String) _jo.get("state"));			
			System.err.print(this.getStates().contains(q) ? "" : "Ãƒâ€°tat " + q.toString() +" Ã¢Å â€ž Q\n");

			Symbol a = new Symbol((String) _jo.get("symbol"));			
			System.err.print(this.getAlphabet().contains(a) ? "" : "Symbole " + a.toString() +" Ã¢Ë†â€° Ã¢Ë†â€˜\n");		

			Transition<State> t = new Transition<State>(q, a);

			State p = new State((String) _jo.get("image"));			
			System.err.print(this.getStates().contains(p) ? "" : "Image " + p.toString() +" Ã¢Å â€ž Q\n");	        	

			delta.put(t,p);
		}

	}

	// Application de la fonction de transition
	public State applyDelta(Transition<State> t) {

		return delta.get(t);

	}


	// méthode qui accepte un mot ou non
	public boolean accept(String x) {
		// on récupère l'état de départ
		State current = start; 
		boolean result = false;

		//pour chaque lettre du mot on effectue la transition
		for( char d : x.toCharArray()) { 
			Transition t = new Transition(current, new Symbol(""+d));
			current = this.delta.get(t);
		}

		// si on arrive à un état d'arrivée null alors on renvoie faux
		if (current == null) return false ;

		// sinon on compare l'état d'arrivée du mot avec les états finaux (qui ont été transformés en string)
		else {
			//transformation
			String c= ""+current;
			HashSet<String> end = new HashSet<String>();
			Iterator<State> l = this.getEnds().iterator(); 
			while (l.hasNext()) {
				end.add(l.next().toString().replace("[", "").replace("]", ""));
			}

			// comparaison
			if(end.contains(c))return true ;
			// on renvoie le résultat
			return result;
		}
	}



	@Override
	public String toString() {
		return 	super.toString() + 
				"s = " + start.toString() + "\n" +
				"ẟ = \n" + delta.toString().replaceAll("(\\{)|(\\})", "")
				.replace(", ", "\n")
				.replace("(", "   (");
	}




}
