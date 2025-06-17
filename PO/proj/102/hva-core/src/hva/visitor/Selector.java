package hva.visitor;

public interface Selector<T> {
  
	/** 
	 * @param t the entity
	 * @return true if selected, false otherwise.
	 */
	default boolean ok(T t) {
    return true;
	}
	
}
