import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KVPair;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker
 * @author Jafar Jarrar
 *
 */
public class AACMappings implements AACPage {
	
	private AssociativeArray<String, AACCategory> mappings;

	private AACCategory currentCategory;

	private AACCategory homeCategory;

	private String fileName;

	private File file;
	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		mappings = new AssociativeArray<String, AACCategory>();
		this.fileName = filename;
		this.file = new File(filename);
		this.homeCategory = new AACCategory("");
		try {
			this.mappings.set("", homeCategory);
		} catch (Exception e) {
			// Does nothing.
		} // try/catch
		readFile();
	} // AACMappings(String)

	/**
	 * Reads through the given file line by line and adds the new categories
	 * to the mappings AAC category.
	 */
	public void readFile() {
		try  {
			BufferedReader reader = new BufferedReader(new FileReader(this.file));
			String line = reader.readLine();
			while (line != null) {
				String[] divided = line.split(" ");
				String key = divided[0];
				String value = "";
				if (divided.length == 2) {
					value = divided[1];
				} else if (divided.length > 2) {
					for (int i = 1; i < divided.length - 1; i++) {
						value += divided[i] + " ";
					} // for
					value += divided[divided.length - 1];
				} // if

				if (key.charAt(0) == '>') {
					currentCategory.addItem(key.substring(1, key.length()), value);
				} else {
					try {
						currentCategory = new AACCategory(value);
						mappings.set(key, currentCategory);
						homeCategory.addItem(key, value);
					} catch (NullKeyException e) {
						// Does nothing.
					} // try/catch
				} // if
				line = reader.readLine();
			}
			this.currentCategory = homeCategory;
		} catch (Exception e) {
			// Does nothing.
		} // try/catch
	} // readFile()
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) {
		String text = "";
		if (currentCategory.equals(homeCategory)) {
			try {
				currentCategory = mappings.get(imageLoc);
			} catch (KeyNotFoundException e) {
				throw new NoSuchElementException();
			} // try/catch
		} else if (currentCategory.hasImage(imageLoc)){
			text = currentCategory.select(imageLoc);
		} else {
			throw new NoSuchElementException();
		} // if
		return text;
	} // select(String)
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		if (currentCategory.getSize() > 0) {
			return currentCategory.getImageLocs();
		} else {
			return new String[0];
		} // if
	} // getImageLocs()
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.currentCategory = this.homeCategory;
	} // reset()
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		try {
			PrintWriter pen = new PrintWriter(filename);

			Iterator<KVPair<String, AACCategory>> iterator = mappings.iterator();
			while (iterator.hasNext()) {
				KVPair<String, AACCategory> current = iterator.next();
				if (current.getVal().equals(homeCategory)) {
					continue;
				} else {
					pen.println(current.getKey() + " " + current.getVal().getCategory());
					Iterator<KVPair<String, String>> itemIterator = current.getVal().getItems().iterator();
					while (itemIterator.hasNext()) {
						KVPair<String, String> item = itemIterator.next();
						pen.println(">" + item.getKey() + " " + item.getVal());
					} // while
				} // if
			} // while
			pen.close();
		} catch (FileNotFoundException e) {
			// Does nothing.
		} // try/catch
	} // writeToFile(String)
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		currentCategory.addItem(imageLoc, text);
		if (currentCategory.equals(homeCategory)) {
			try {
				mappings.set(imageLoc, new AACCategory(text));
			} catch (NullKeyException e) {
				// Does nothing.
			} // try/catch
		} // if
	} // addItem(String, String)


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		if (currentCategory.equals(homeCategory)) {
			return "";
		} else {
			return currentCategory.getCategory();
		} // if
	} // getCategory()


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		Iterator<KVPair<String, AACCategory>> iterator = mappings.iterator();
		while (iterator.hasNext()) {
			KVPair<String, AACCategory> current = iterator.next();
			if (current.getVal().hasImage(imageLoc)) {
				return true;
			} // if
		} // while
		return false;
	} // hasImage(String)
} // class AACMappings
