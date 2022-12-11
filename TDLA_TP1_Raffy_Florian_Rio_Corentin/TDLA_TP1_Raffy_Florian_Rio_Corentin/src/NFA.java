import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NFA extends FSM  {

	// plusieurs états de départ
	private HashSet<State> start = new HashSet<State>();
	public HashSet<State> getStart() { return start; }

	// Fonction de Transititon 
	private Map<Transition<State>, HashSet<State>> delta;
	public Map<Transition<State>, HashSet<State>> getDelta() { return delta; }

	//constructeur avec paramètres
	public NFA(Set<State> _states,
			Set<Symbol> _alphabet,
			HashSet<State> _start,
			Set<State> _ends,
			Map<Transition<State>, HashSet<State>> _delta) {
		super(_states, _alphabet, _ends);
		start = _start;
		delta = _delta;
	}

	// constructeur avec chemin
	public NFA(String path) {
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
		JSONArray ja = (JSONArray) jo.get("starts");
		// on met dans un HashSet tout les états de départ
		start = new HashSet<State>();
		for(Object o : ja) {
			start.add(new State((String) o));
			System.err.print(this.getStates().contains(start) ? "" : "État initial "+ start +" ∉ Q\n");

		}
		ja = (JSONArray) jo.get("delta");

		delta = new HashMap<Transition<State>, HashSet<State>>();		

		// on met dans un Hashmap tous les états de transition
		for(Object _o : ja) {

			JSONObject jo2 = (JSONObject) _o;

			State q = new State((String) jo2.get("state"));			
			System.err.print(this.getStates().contains(q) ? "" : "État " + q.toString() +" ⊄ Q\n");


			Symbol a = new Symbol((String) jo2.get("symbol"));			
			System.err.print(this.getAlphabet().contains(a) ? "" : "Symbole " + a.toString() +" ∉ ∑\n");		

			Transition<State> t = new Transition<State>(q, a);
			// modification de p qui récupère les images pour chaques transitions, il est passé d'un State à un HashSet<State>
			HashSet<State> p = new HashSet<State>();
			JSONArray ji = (JSONArray) jo2.get("images");
			for(Object o : ji) {
				p.add(new State((String)o));			
				System.err.print(this.getStates().contains(p) ? "" : "Image " + p.toString() +" ⊄ Q\n");	        	

			}

			delta.put(t,p);
		}

	}

	// Application de la fonction de transition
	public HashSet<State> applyDelta(Transition<State> t) {

		return delta.get(t);

	}

	// fonction accept
	public boolean accept(String x) {
		HashSet<State> current = start ;
		// on récupère les états de départ , current est devenu un HashSet pour gérer les états de départ
		// on multiple et par la suite pour les images pouvant être multiple après chaque transition
		boolean result = false ;

		//pour chaque lettre du mot on effectue la transition 
		for( char d : x.toCharArray()) {
			Iterator<State> i = current.iterator(); 
			HashSet<State> retour = new HashSet<State>() ;
			while (i.hasNext()) {
				Transition t = new Transition(i.next(), new Symbol(""+d));
				if(this.applyDelta(t) != null) {
					retour.addAll(this.delta.get(t));// les images sont mies dans retour

				}
			}
			// on vide current et on le remplace par les états de retour 
			// pour que les états précédents ne restent pas dans current
			current.clear();
			current = (HashSet<State>) retour.clone();
		}
		
		// si on arrive à un état d'arrivée qui est null alors on renvoie faux
		if(current == null)return result;
		
		//sinon on compare les états d'arrivée du mot avec les états finaux (que l'on transforme en string) 
		else {
			//transformation
			HashSet<String> end = new HashSet<String>();
			Iterator<State> l = this.getEnds().iterator(); 
			
			while (l.hasNext()) {
				end.add(l.next().toString().replace("[", "").replace("]", ""));
			} 
			HashSet<String> resultat = new HashSet<String>();
			Iterator<State> i = current.iterator(); 
			
			while (i.hasNext()) {
				resultat.add(i.next().toString());
			} 
			Iterator<String> j = resultat.iterator(); 
			
			//comparaison
			while(j.hasNext()) {
				Iterator<String> k = end.iterator(); 
				if(j.next().equals(k.next())) return true;
			}
		}
		
		//on renvoie le résultat
		return result ;
	}

	public DFA toDFA() {
		// nouveaux états finaux
		Set<State> newends = new HashSet<State>();
		
		// on récupère l'évolution des états avec evo et evo2
		Map<State, HashSet<State>> evo   = new HashMap<>();
		Map<HashSet<State>,State > evo2   = new HashMap<>();
		
		// le delta qui récupèrera les transitions
		Map<Transition<State>, HashSet<State>> newdelta = new HashMap<>();
		
		// le delta qui les fera correspondre avec les nouveaux états
		Map<Transition<State>, State> newdelta2 = new HashMap<>();
		
		int n = 1 ;//entier qui donnera le chiffre des nouveaux états ex: S1 , S2...
		int nb = 1;//entier qui permettra de récupérer les nouveaux états ex: get(new State(S1)...)
		
		// on récupère l'alphabet et on le transforme en un mot en String pour tester chaque symbole avec les états créés
		Iterator<Symbol> j = this.getAlphabet().iterator();	
		String x = "";
		while(j.hasNext()) {
			x += j.next();
		}
		
		// on nomme le premier nouvel état qui sera le start
		State s = new State("S1");
		
		// on le met dans les deux evo
		evo.put(s, this.start);
		evo2.put(this.start, s);
		
		//l'itérateur 'ite' qui permettra d'avoir toutes les transitions pour chacun des nouveaux états
		boolean ite = true;
		
		// les états de départ pour l'itération
		HashSet<State> current = this.start;
		
		// on regarde s'il y a des états finaux dans les états de départ
		// si c'est le cas le nouvel état S1 sera mit dans newends
		HashSet<String> end = new HashSet<String>();
		Iterator<State> l = this.getEnds().iterator(); 
		
		while (l.hasNext()) {
			end.add(l.next().toString().replace("[", "").replace("]", ""));
		} 
		HashSet<String> resultat = new HashSet<String>();
		Iterator<State> q = current.iterator(); 
		
		while (q.hasNext()) {
			resultat.add(q.next().toString());
		} 
		Iterator<String> z = resultat.iterator(); 
		
		while(z.hasNext()) {
			Iterator<String> k = end.iterator(); 
			if(z.next().equals(k.next())) newends.add(s);
		}

		// on effectue toutes les transitions possible	
		while(ite) {
			
			// on teste tous les symboles de l'alphabet
			for( char d : x.toCharArray()) {
				Iterator<State> i = current.iterator(); 
				HashSet<State> retour = new HashSet<State>() ;
				while (i.hasNext()) {
					Transition t = new Transition(i.next(), new Symbol(""+d));
					
					// quand l'image n'est pas null on récupère les images dans retour
					if(this.applyDelta(t) != null) {
						retour.addAll(this.delta.get(t));
						String symb = "" + d ;
						
						// on met ensuite dans new delta la transition correspondant aux nouveaux états plus les images renvoyées
						newdelta.put(new Transition(evo2.get(current),new Symbol(symb)), retour);

					}
				}
				
				// si les images renvoyées ne correspondent à aucun nouvel états on en crée donc un
				if(!evo.containsValue(retour) && !retour.isEmpty() ) {
					// création d'un nouvel état
					n += 1 ;
					State s2 = new State("S"+n);
					
					// on met le nouvel état avec l'ensemble d'états avec qui il correspond dans nos evo
					evo.put(s2, (HashSet<State>) retour.clone());
					evo2.put((HashSet<State>) retour.clone(), s2);
					
					// on regarde s'il y a des états finaux dans les états qui constitue le nouvel état
					// si c'est le cas le nouvel état S1 sera mit dans newends
					if(retour.contains(this.getEnds())) newends.add(s2);

					HashSet<String> end2 = new HashSet<String>();
					Iterator<State> l2 = this.getEnds().iterator(); 
					
					while (l2.hasNext()) {
						end2.add(l2.next().toString().replace("[", "").replace("]", ""));
					} 
					HashSet<String> resultat2 = new HashSet<String>();
					Iterator<State> q2 = retour.iterator(); 
					
					while (q2.hasNext()) {
						resultat.add(q2.next().toString());
					} 
					Iterator<String> z2 = resultat.iterator(); 
					
					while(z2.hasNext()) {
						Iterator<String> k2 = end2.iterator(); 
						if(z2.next().equals(k2.next())) newends.add(s2);
					}

				}
			}
			
			// current prend ensuite la valeur d'un des nouveaux états jusqu'à ce qu'il n'y en ait plus
			nb += 1;
			current = evo.get(new State("S"+nb)) ;
			
			// s'il n'y en a plus alors on arrête
			if(current == null) {
				ite = false ;
			}
		}	
		
		//on crée l'état de départ qui servira de paramètre 
		State newstart = new State("S1");
		
		// on crée l'ensemble des états qui servira de paramètre grâce à evo
		Set<State> newstates = new HashSet<State>();
		newstates.addAll(evo.keySet());
		
		// on crée l'alphabet qui servira de paramètre
		Set<Symbol> newalphabet = this.getAlphabet();
		
		// on remplit newdelta2 qui servira de paramètre grâce à new delta qui nous donnera les transitions 
		//avec les nouveaux états et les images correspondant que nous changeront en mettant les nouveaux grâce à evo2
		for (Entry<Transition<State>, HashSet<State>> entry : newdelta.entrySet()) {
			Set<State> value = entry.getValue();
			Transition<State> entre = entry.getKey();
			newdelta2.put(entre,evo2.get(value));
		}
		
		// on crée le DFA à partir de tous les paramètres précédents
		return new DFA(newstates,newalphabet,newstart,newends,newdelta2); 
	}




	@Override
	public String toString() {
		return 	super.toString() + 
				"s = " + start.toString() + "\n" +
				"ẟ = " + delta.toString().replaceAll("(\\{)|(\\})", "")
				.replace("],", "]")
				.replace("(", " \n  (");
	}


}
