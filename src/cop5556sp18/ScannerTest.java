 /**
 * JUnit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}
	


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testAddition() throws LexicalException {
		String input = "++\n++";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_PLUS, 0, 1, 1, 1);
		checkNext(scanner, OP_PLUS, 1, 1, 1, 2);
		checkNext(scanner, OP_PLUS, 3, 1, 2, 1);
		checkNext(scanner, OP_PLUS, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testComma() throws LexicalException {
		String input = ",,\n,,";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, COMMA, 0, 1, 1, 1);
		checkNext(scanner, COMMA, 1, 1, 1, 2);
		checkNext(scanner, COMMA, 3, 1, 2, 1);
		checkNext(scanner, COMMA, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}

// Compare Junit results with console. Add to scannertest before submitting.
	@Test
	public void testSeparators() throws LexicalException {
		String input = "([{;,.}])";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1); //checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line)
		checkNext(scanner, LSQUARE, 1, 1, 1, 2);
		checkNext(scanner, LBRACE, 2, 1, 1, 3);
		checkNext(scanner, SEMI, 3, 1, 1, 4);
		checkNext(scanner, COMMA, 4, 1, 1, 5);
		checkNext(scanner, DOT, 5, 1, 1, 6); // Make function for DOT as it'll be used as a float too
		checkNext(scanner, RBRACE, 6, 1, 1, 7);
		checkNext(scanner, RSQUARE, 7, 1, 1, 8);
		checkNext(scanner, RPAREN, 8, 1, 1, 9);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIdentifiers() throws LexicalException {
		String input = ".falsetruee5false@legend_$1.25.0.truebling%true default_width _";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, DOT,0,1,1,1); //checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line)
		checkNext(scanner, IDENTIFIER,1,16,1,2);
		checkNext(scanner, OP_AT,17,1,1,18);
		checkNext(scanner, IDENTIFIER,18,9,1,19);
		checkNext(scanner, FLOAT_LITERAL,27,3,1,28);
		checkNext(scanner, FLOAT_LITERAL,30,2,1,31);
		checkNext(scanner, DOT,32,1,1,33);
		checkNext(scanner, IDENTIFIER,33,9,1,34);
		checkNext(scanner, OP_MOD,42,1,1,43);
		checkNext(scanner, BOOLEAN_LITERAL,43,4,1,44);
		checkNext(scanner, KW_default_width,48,13,1,49);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIntegerLiterals() throws LexicalException {
		String input = "=";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(0,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}
	
	@Test
	public void kkkIllegalChar() throws LexicalException {
		String input = "/*n*";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(4,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}
	
	@Test
	public void testkeywords() throws LexicalException { 
		String input = "** * *while !!= !=== ==== < << <<>><>< > <= >= :<> :===";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_POWER,0,2,1,1);
		checkNext(scanner, OP_TIMES,3,1,1,4);
		checkNext(scanner, OP_TIMES,5,1,1,6);
		checkNext(scanner, KW_while,6,5,1,7);
		checkNext(scanner, OP_EXCLAMATION,12,1,1,13);
		checkNext(scanner, OP_NEQ,13,2,1,14);
		checkNext(scanner, OP_NEQ,16,2,1,17);
		checkNext(scanner, OP_EQ,18,2,1,19);
		checkNext(scanner, OP_EQ,21,2,1,22);
		checkNext(scanner, OP_EQ,23,2,1,24);
		checkNext(scanner, OP_LT,26,1,1,27);
		checkNext(scanner, LPIXEL,28,2,1,29);
		checkNext(scanner, LPIXEL,31,2,1,32);
		checkNext(scanner, RPIXEL,33,2,1,34);
		checkNext(scanner, OP_LT,35,1,1,36);
		checkNext(scanner, OP_GT,36,1,1,37);
		checkNext(scanner, OP_LT,37,1,1,38);
		checkNext(scanner, OP_GT,39,1,1,40);
		checkNext(scanner, OP_LE,41,2,1,42);
		checkNext(scanner, OP_GE,44,2,1,45);
		checkNext(scanner, OP_COLON,47,1,1,48);
		checkNext(scanner, OP_LT,48,1,1,49);
		checkNext(scanner, OP_GT,49,1,1,50);
		checkNext(scanner, OP_ASSIGN,51,2,1,52);
		checkNext(scanner, OP_EQ,53,2,1,54);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIdentifiers_blabla() throws LexicalException {
		String input = "012345kkk*misra_$ Z/*\n*/@";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, INTEGER_LITERAL,0,1,1,1);
		checkNext(scanner, INTEGER_LITERAL,1,5,1,2);
		checkNext(scanner, IDENTIFIER,6,3,1,7);
		checkNext(scanner, OP_TIMES,9,1,1,10);
		checkNext(scanner, IDENTIFIER,10,7,1,11);
		checkNext(scanner, KW_Z,18,1,1,19);
		checkNext(scanner, OP_AT,24,1,2,3);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testOperators() throws LexicalException {
		String input = "\" h e l l o \"123\"456\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_PLUS, 0, 1, 1, 1);
		checkNext(scanner, OP_MINUS, 1, 1, 1, 2);
		checkNext(scanner, OP_TIMES, 2, 1, 1, 3);
		checkNext(scanner, OP_DIV, 3, 1, 1, 4);
		checkNext(scanner, OP_MOD, 4, 1, 1, 5);
		checkNextIsEOF(scanner);
	}
}
	

