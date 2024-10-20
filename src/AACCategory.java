

import java.util.NoSuchElementException;

import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

/**
 * Represents the mappings for a single category of items that should
 * be displayed.
 * 
 * @author Catie Baker & Jafar Jarrar
 *
 */
public class AACCategory implements AACPage {

	private String name;

	private AssociativeArray<String, String> items;

	/**
	 * Creates a new empty category with the given name
	 * @param name the name of the category
	 */
	public AACCategory(String name) {
		this.name = name;
		this.items = new AssociativeArray<String, String>();
	} // AACCategory(String)
	
	/**
	 * Adds the image location, text pairing to the category
	 * @param imageLoc the location of the image
	 * @param text the text that image should speak
	 */
	public void addItem(String imageLoc, String text) {
		try {
			this.items.set(imageLoc, text);
		} catch (NullKeyException e) {
			// Does nothing.
		} // try/catch
	} // addItem(String, String)

	/**
	 * Returns an array of all the images in the category
	 * @return the array of image locations; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		return new String[] {""}; // STUB
	} // getImageLocs()

	/**
	 * Returns the name of the category
	 * @return the name of the category
	 */
	public String getCategory() {
		return this.name;
	} // getCategory()

	/**
	 * Returns the text associated with the given image in this category
	 * @param imageLoc the location of the image
	 * @return the text associated with the image
	 * @throws NoSuchElementException if the image provided is not in the current
	 * 		   category
	 */
	public String select(String imageLoc) {
		try {
			return this.items.get(imageLoc);
		} catch (KeyNotFoundException e) {
			throw new NoSuchElementException();
		} // try/catch
	} // select(String)

	/**
	 * Determines if the provided images is stored in the category
	 * @param imageLoc the location of the category
	 * @return true if it is in the category, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		try {
			return (this.items.find(imageLoc) > -1);
		} catch (KeyNotFoundException e) {
			return false;
		} // try/catch
	} // hasImage(String)
} // class AACCategory
