/**
 * File: 	NonCirculinearShape2DException.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 1 nov. 09
 */
package math.geom2d.exceptions;


/**
 * @author dlegland
 *
 */
public class NonCirculinearShape2DException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Object object;
	
	public NonCirculinearShape2DException(Object obj) {
		this.object = obj;
	}
	
	public Object getObject() {
		return object;
	}
}
