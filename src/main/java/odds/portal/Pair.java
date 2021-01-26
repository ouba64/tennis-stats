package odds.portal;

/**
 * A Pair is a 2-tuple. 
 * 2 Pairs are equal when their components are equal.
 * @author Ouba
 *
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1,T2> {
	public T1 first;
	public T2 second;
	public Pair(){
		
	}
	public Pair(T1 t1, T2 t2) {
		this.first=t1;
		this.second=t2;
	}
	public String toString(){
		return "<"+first+" , "+second+">";
	}
	public T1 getFirst() {
		return first;
	}
	public void setFirst(T1 first) {
		this.first = first;
	}
	public T2 getSecond() {
		return second;
	}
	public void setSecond(T2 second) {
		this.second = second;
	}
	/**
	 * Two pairs p1 and p2 are equal if p1.first == p2.first
	 */
	@Override
	public boolean equals(Object obj) {
		boolean res = false;	
		if(obj==null){
			res = false;
		}
		else {
			if(obj instanceof Pair){
				boolean a1 = false;
				boolean a2 = false;
				@SuppressWarnings("unchecked")
				Pair<T1, T2> p2 = (Pair<T1, T2>)obj;
				if(first==null){
					a1 = p2.first == null;
				}
				else{
					a1 = first.equals(p2.first);
				}
				if(second == null){
					a2 = p2.second == null;
				}
				else{					
					a2 = second.equals(p2.second);
				}
				res = a1 && a2;
			}
		}
		return res;
	}
	
	@Override
	public int hashCode() {
		int h1 = 0;
		int h2 = 0;
		h1 = first == null ? 0 : first.hashCode();
		h2 = second == null ? 0 : second.hashCode();
		return h1+h2;
	}
}
