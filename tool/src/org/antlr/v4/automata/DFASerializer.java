package org.antlr.v4.automata;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A DFA walker that knows how to dump them to serialized strings. */
public class DFASerializer {
	Grammar g;
	DFAState start;

	public DFASerializer(Grammar g, DFAState start) {
		this.g = g;
		this.start = start;
	}

	public String toString() {
		if ( start==null ) return null;
		Set<Integer> marked = new HashSet<Integer>();

		List<DFAState> work = new ArrayList<DFAState>();
		work.add(start);

		StringBuilder buf = new StringBuilder();
		DFAState s = null;

		while ( work.size()>0 ) {
			s = work.remove(0);
			if ( marked.contains(s.stateNumber) ) continue;
			marked.add(s.stateNumber);
			int n = s.getNumberOfEdges();
			//System.out.println("visit "+getStateString(s)+"; edges="+n);
			for (int i=0; i<n; i++) {
				buf.append(getStateString(s));
				Edge t = s.edge(i);
				work.add( t.target );				
				buf.append("-"+t.toString(g)+"->"+ getStateString(t.target)+'\n');
			}
		}
		String output = buf.toString();
		return Utils.sortLinesInString(output);
	}

	String getStateString(DFAState s) {
		int n = s.stateNumber;
		String stateStr = "s"+n;
		if ( s.isAcceptState ) {
			if ( s instanceof LexerState ) {
				stateStr = ":s"+n+"=>";
				StringBuilder buf = new StringBuilder();
				for (Rule r : ((LexerState)s).matchesRules) {
					buf.append(" "+r.name);
				}
				stateStr += buf.toString();
			}
			else {
				stateStr = ":s"+n+"=>"+s.getUniquelyPredictedAlt();
			}
		}
		return stateStr;
	}
}
