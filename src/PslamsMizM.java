import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader; 

import javax.swing.JOptionPane;

public class PslamsMizM
{

	/**
	 * Returned by read of BufferReader to signify that the end of file has been reached
	 */
	public static final int EOF = -1;

	/**
	 * Binary Search Method
	 * 
	 * @param pairArray
	 * 				pair array to be searched			
	 * 
	 * @param left
	 * 			the lower bound of the search area
	 * 
	 * @param right
	 * 			the upper bound of the search area
	 * 
	 * @param psalmNumber
	 * 				psalm number to search for
	 * 
	 * @return the psalm found or null
	 */
	public static Pair<Integer, String> doBinarySearch(Pair<Integer, String>[] pairArray, int left, int right, int psalmNumber)
	{
		if (left > right)
			return null;

		int middle = (left + right) / 2;

		if (pairArray[middle].first == psalmNumber)
			return pairArray[middle];

		if (pairArray[middle].first > psalmNumber)
		{
			return doBinarySearch(pairArray, left, middle - 1, psalmNumber);
		}
		else
		{
			return doBinarySearch(pairArray, middle + 1, right, psalmNumber);
		}
	}

	/**
	 * Reads a file and fill the buffer object with it's contents
	 * 
	 * 
	 * @param filename
	 *            the filename of the file to read
	 * 
	 * @param memory
	 *            a buffer instance to be filled with the contents of the file
	 * 
	 * @return the status of the function 0 if succeeded else it failed
	 */
	public static int readFile(String filename, Buffer memory)
	{
		File file = new File(filename);
		BufferedReader reader = null;
		
		if (!file.exists())
			return 1;

		if (!file.canRead())
			return 2;

		// allocate a buffer if it's not already allocated
		if (memory.buffer == null)
			memory.allocate((int) file.length());

		try
		{
			reader = new BufferedReader(new FileReader(file));
			int nCharsRead = reader.read(memory.buffer);

			if (nCharsRead == EOF)
			{
				reader.close();
				return 3;
			}

			// a partial read occurred
			if (nCharsRead != memory.buffer.length)
			{
				reader.close();
				return 4;
			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 *  Extracts the Psalms from a file to a Pair array
	 * 
	 * @param file
	 * 			a buffer valid buffer object full of a file's contents	
	 * 
	 * @return all the psalms in a pair array
	 */
	public static Pair<Integer, String>[] extractPsalmsFromFile(Buffer file)
	{
		int index = 0;
		int lines = 0;
		int pairs = 0;
		IntStringPair[] psalms = null;
		String data = null;
		StringReader sReader = null;
		BufferedReader reader = null;
		String line = null;
		int nLine = 1;
		boolean isSentence = false;
		int lineId = 0;
		String phrase = null;
		int i = 0;
		
		if (file == null)
			return null;
		
		if (file.buffer == null)
			return null;
		
		for (; index < file.buffer.length; ++index)
		{
			if (file.buffer[index] == '\n')
			{
				++lines;

				if (lines % 2 == 0)
					++pairs;
			}
		}

		psalms = new IntStringPair[pairs + 1];
		data = String.copyValueOf(file.buffer);
		sReader = new StringReader(data);
		reader = new BufferedReader(sReader);

		try
		{
			while (true)
			{
				line = reader.readLine();
				
				// the end of file has been reached
				if (line == null)
					break;
					
				if (isSentence)
				{
					phrase = line;
				}
				else
				{
					lineId = Integer.parseInt(line);
				}

				if (nLine % 2 == 0 && nLine > 0)
				{
					psalms[i++] = new IntStringPair(lineId, phrase);
				}
				isSentence = !isSentence;
				++nLine;
			} 

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return psalms;
	}

	/**
	 * Creates a input style message box dialog
	 * 
	 * @see createMessageBox if no user input is desired
	 * @see createOptionMessageBox if the user input is a "yes no" type or similar
	 * 
	 * @param text
	 *            a message
	 * @param caption
	 *            a caption; title
	 * @param selectionValues
	 *            values to be selected can be null
	 * @param defaultSelection
	 *            default value to be selected can be null
	 * @param type
	 *            type of message box (info, warning, question, etc...)
	 * @param <T>
	 *            template for selections
	 * 
	 * @return the JOptionPane.showInputDialog return value casted to string
	 */
	public static <T> String createInputMessageBox(String text, String caption, T[] selectionValues, T defaultSelection, int type)
	{
		return (String) JOptionPane.showInputDialog(null, text, caption, type, null, selectionValues, defaultSelection);
	}

	/**
	 * Creates a message box
	 * 
	 * @see createInputMessageBox if you want to obtain user input from the user
	 * 
	 * @see createOptionMessageBox if the user input is a "yes no" type or similar
	 * 
	 * @param text
	 *            a message
	 * @param caption
	 *            a caption (the title)
	 * @param type
	 *            type of message box (info, warning, question, etc...)
	 */
	public static void createMessageBox(String text, String caption, int type)
	{
		JOptionPane.showMessageDialog(null, text, caption, type);
	}

	/**
	 * Get the desired psalm number from the end user
	 * 
	 * @return the desired psalm number or -1 if the user desires to exit
	 */
	public static int getPsalmNumber()
	{
		int psalmNumber = 0;
		boolean bInputSucceeded = false;
		String number = null;
		
		do
		{
			try
			{
				number = createInputMessageBox("What psalm would you like to see?", "Psalm Lookup", null, null, JOptionPane.INFORMATION_MESSAGE);

				// exit on user input
				if (number == null)
					return -1;

				psalmNumber = Integer.parseInt(number);
				bInputSucceeded = true;
			}
			catch (NumberFormatException nfe)
			{
				createMessageBox("Please enter an integer to signify what psalm you want to see.", "Psalm Lookup", JOptionPane.ERROR_MESSAGE);
			}
		} while (!bInputSucceeded);
		
		return psalmNumber;
	}
	
	/**
	 * Main Function
	 * 
	 * @param args
	 *            command line arguments (if any)
	 */
	public static void main(String[] args)
	{
		Buffer memory = new Buffer();
		Pair<Integer, String>[] psalms = null;
		int psalmNumber = 0;
		
		int status = readFile("Psalms.txt", memory);

		if (status != 0)
			return;
		
		psalms = extractPsalmsFromFile(memory);

		createMessageBox("This program searches the psalms to find the one asked for. Valid Range is (" + psalms[0].first + " - " + psalms[psalms.length - 1].first + ").",
				"Psalm Lookup", JOptionPane.INFORMATION_MESSAGE);

		if ((psalmNumber = getPsalmNumber()) == -1)
			return;

		Pair<Integer, String> psalm = doBinarySearch(psalms, 0, psalms.length - 1, psalmNumber);

		if (psalm == null)
			createMessageBox("Sorry, but there is no such psalm denoted with " + psalmNumber + ".", "Psalm Lookup", JOptionPane.ERROR_MESSAGE);
		else
			createMessageBox("Psalm " + psalm.first + "\n" + psalm.second, "Psalm Lookup", JOptionPane.INFORMATION_MESSAGE);
	}
}

/**
 * 
 * @author Martin
 *
 *         Helper class to hold the file data
 *
 */
class Buffer
{
	/**
	 * Default Constructor
	 */
	public Buffer()
	{
		this.buffer = null;
	}

	/**
	 * Constructor
	 * 
	 * @param length
	 *            the length of the buffer in bytes
	 */
	public Buffer(int length)
	{
		allocate(length);
	}

	/**
	 * Allocates a memory pool the size of length in bytes
	 * 
	 * @param length
	 *            the length of the buffer in bytes
	 */
	public void allocate(int length)
	{
		this.buffer = new char[length & 0x7FFFFFFF];
	}

	/**
	 * Buffer variable (holds all file data)
	 */
	public char[] buffer;
}

/**
 * 
 * @author Martin
 *
 * @param <F>
 *            type of the first object
 * 
 * @param <S>
 *            type of the second object
 */
class Pair<F, S>
{
	/**
	 * First object in pair
	 */
	final F first;

	/**
	 * Second object in pair
	 */
	final S second;

	/**
	 * Constructor
	 * 
	 * @param first
	 *            First object in pair
	 * 
	 * @param second
	 *            Second object in pair
	 */
	public Pair(F first, S second)
	{
		this.first = first;
		this.second = second;
	}
}

/**
 * 
 * @author Martin
 *
 *         Helper class because
 *
 *         Pair<Integer, String>[] a = new Pair<Integer, String>[34];
 * 
 *         is not a correct statement for example
 *
 */
class IntStringPair extends Pair<Integer, String>
{
	/**
	 * Constructor
	 * 
	 * @param first
	 *            First object in pair type of int
	 * 
	 * @param second
	 *            Second object in pair type of string
	 */
	public IntStringPair(Integer first, String second)
	{
		super(first, second);
	}
}