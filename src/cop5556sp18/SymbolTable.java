package cop5556sp18;

import java.util.*;
import cop5556sp18.AST.*;

public class SymbolTable {
	int scope_current, scope_next;
	Stack<Integer> scope_stack = new Stack<Integer>();
	HashMap<String, ArrayList<ScopeAndDeclaration>> hashmap = new HashMap<String, ArrayList<ScopeAndDeclaration>>();

	public SymbolTable() {
		this.scope_current = 0;
		this.scope_next = 1;
		scope_stack.push(0);
	}

	@Override
	public String toString() {
		return this.toString();
	}

	public void enterScope() {
		scope_current = scope_next++;
		scope_stack.push(scope_current);
	}

	public void leaveScope() {
		scope_stack.pop();
		scope_current = scope_stack.peek();
	}

	public Declaration lookup(String identifier) {
		if (!hashmap.containsKey(identifier))
			return null;

		Declaration declaration = null;
		ArrayList<ScopeAndDeclaration> arraylist = hashmap.get(identifier);
		int size = arraylist.size() - 1;
		for (int i = size; i >= 0; i--) {
			int scope_temp = arraylist.get(i).getScope();
			if (scope_stack.contains(scope_temp)) {
				declaration = arraylist.get(i).getDec();
				break;
			}
		}
//		System.out.println(declaration.name);
//		System.out.print(ScopeAndDeclaration.getDec());
		return declaration; //Check declaration again before submitting
	}

	public boolean insert(String identifier, Declaration declaration) {
		ArrayList<ScopeAndDeclaration> arraylist = new ArrayList<ScopeAndDeclaration>();
		ScopeAndDeclaration obj = new ScopeAndDeclaration(scope_current, declaration);
		if (hashmap.containsKey(identifier)) {
			arraylist = hashmap.get(identifier);
			for (ScopeAndDeclaration sc : arraylist) {
				if (sc.getScope() == scope_current)
					return false;
			}
		}
		arraylist.add(obj);
		hashmap.put(identifier, arraylist);
		return true;
	}

	public class ScopeAndDeclaration {

		int scope;
		Declaration declaration;

		public ScopeAndDeclaration(int s, Declaration d) {
			this.scope = s;
			this.declaration = d;
		}

		public int getScope() {
			return scope;
		}

		public Declaration getDec() {
			return declaration;
		}
	}
}