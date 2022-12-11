import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

public class AFNDe extends NFA {
	// création d'un nouveau symbole epsilone
	public static Symbol eps = new Symbol ("e");

	public AFNDe(Set<State> _states, Set<Symbol> _alphabet, HashSet<State> _start, Set<State> _ends,
			Map<Transition<State>, HashSet<State>> _delta) {
		super(_states, _alphabet, _start, _ends, _delta);
		// ajout du symbole epsilone à l'alphabet
		_alphabet.add(eps);
	}

	public AFNDe(String path) {
		super(path);
		// ajout du symbole epsilone à l'alphabet
		getAlphabet().add(eps);
	}

	// permet de récupérer les états liés grâce à un epsilone à l'état A en paramètre 
	public Set<State> epsilonClause(Set<State> A){
		HashSet<State> result = new HashSet<State>();
		Iterator<State> i = A.iterator(); 
		while(i.hasNext()) {
			Transition t = new Transition(i.next(), eps);
			if(this.applyDelta(t) != null) {
				result.addAll(applyDelta(t));
			}
		}
		return result;
	}

	// méthode qui accepte ou non un mot
	public boolean accept(String x) {
		// on récupère les états de départ : current est devenu un HashSet pour gérer plusieurs états de départ
		HashSet<State> current = getStart() ;
		boolean result = false ;

		// pour chaque lettre du mot on effectue la transition 
		for( char d : x.toCharArray()) {
			// on ajoute à current les états pouvant être atteint par un epsilon
			current.addAll(this.epsilonClause(current));
			Iterator<State> i = current.iterator(); 
			HashSet<State> retour = new HashSet<State>() ;
			while (i.hasNext()) {
				Transition t = new Transition(i.next(), new Symbol(""+d));
				if(this.applyDelta(t) != null) {
					retour.addAll(this.getDelta().get(t));
				}
			}
			current.clear();
			current = (HashSet<State>) retour.clone();
			// on vide current et on le remplace par les états de retour
			//pour que les états précédents ne restent pas dans current
		}

		// on applique l'epsilone transition aux états d'arrivée
		current.addAll(this.epsilonClause(current));

		// si l'état d'arrivée est null on renvoie faux
		if(current == null)return result ;

		// sinon on compare les états d'arrivée du mot avec les états finaux (en string)
		else {
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

			// comparaison
			while(j.hasNext()) {
				Iterator<String> k = end.iterator(); 
				if(j.next().equals(k.next())) return true;
			}
		}
		// on renvoie le résultat
		return result ;

	}


	public DFA toDFA() {
		// nouveaux états finaux
		Set<State> newends = new HashSet<State>();

		// avoir l'évolution des états avec evo et evo2
		Map<State, HashSet<State>> evo   = new HashMap<>();
		Map<HashSet<State>,State > evo2   = new HashMap<>();

		// le delta qui récupèrera les transitions
		Map<Transition<State>, HashSet<State>> newdelta = new HashMap<>();

		// le delta qui les fera correspondre avec les nouveaux états
		Map<Transition<State>, State> newdelta2 = new HashMap<>();

		// on crée l'alphabet qui servira de paramètre en enlevant l'epsilon
		Set<Symbol> newalphabet = new HashSet<Symbol>();
		Iterator<Symbol> j2 = this.getAlphabet().iterator();	
		while (j2.hasNext()) {
			newalphabet.add(j2.next());
		}
		newalphabet.remove(eps);

		int n = 1 ; // entier qui donnera le chiffre des nouveaux états ex: S1 , S2...
		int nb = 1; // entier qui permettra de récupérer les nouveaux états ex: get(new State(S1)...)

		// on récupère l'alphabet et on le transforme en un mot (en String) pour tester chaque symbole avec les états que nous récupèrerons
		Iterator<Symbol> j = newalphabet.iterator();	
		String x = "";
		while(j.hasNext()) {
			x += j.next();
		}

		// on nomme le premier nouvel état qui sera le start
		State s = new State("S1");

		// les états de départ pour l'itération sur lequel on applique l'epsilon transition
		HashSet<State> start = this.getStart();
		start.addAll(this.epsilonClause(start));

		HashSet<State> current = this.getStart() ;
		current.addAll(this.epsilonClause(current));

		// on met le premier nouvel état dans les deux evo
		evo.put(s,(HashSet<State>) current.clone());
		evo2.put((HashSet<State>) current.clone(), s);

		// l'itérateur 'ite' va permettre d'avoir toutes les transitions pour chacun des nouveaux états
		boolean ite = true;

		// on regarde si dans les états de départ il y en a qui sont finaux
		// si c'est le cas, le nouvel état S1 sera mis dans newends
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
				HashSet<State> retour = new HashSet<State>();
				while (i.hasNext()) {
					Transition t = new Transition(i.next(), new Symbol(""+d));

					// quand l'image n'est pas null on récupère les images dans retour
					if(this.applyDelta(t) != null) { 
						retour.addAll(this.getDelta().get(t));

						// on applique l'epsilon transition aux images
						retour.addAll((HashSet<State>) this.epsilonClause(retour)); 
						String symb = "" + d ;

						// on met ensuite dans new delta la transition correspondant aux nouveaux états plus les images renvoyées
						newdelta.put(new Transition(evo2.get(current),new Symbol(symb)), retour);
					}
				}

				// si les images renvoyées ne correspondent à aucun nouvel état on en crée donc un
				if(!evo.containsValue(retour) && !retour.isEmpty() ) {
					// création d'un état
					n += 1 ;
					State s2 = new State("S"+n);

					// on met le nouvel état avec l'ensemble d'états avec qui il correspond dans nos evo
					evo.put(s2, (HashSet<State>) retour.clone());
					evo2.put((HashSet<State>) retour.clone(), s2);

					// on regarde s'il y a des états finaux parmis les états qui constitue le nouvel état
					// si c'est le cas le nouvel état S1 sera mis dans newends
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
			current = evo.get(new State("S"+nb));

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

		// on remplit newdelta2 qui servira de paramètre grâce à new delta qui nous donnera les transitions 
		// avec les nouveaux états et les images correspondant que nous changeront en mettant les nouveaux grâce à evo2
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
		return 	super.toString();
	}


}


