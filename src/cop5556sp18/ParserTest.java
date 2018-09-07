/**
 * JUunit tests for the Parser for the class project in COP5556 Programming Language Principles 
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

import static cop5556sp18.Scanner.Kind.OP_PLUS;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.*;
import cop5556sp18.Parser.SyntaxException;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	// creates and returns a parser for the given input.
	private Parser makeParser(String input) throws LexicalException {
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner);
		return parser;
	}

	/**
	 * Simple test case with an empty program. This throws an exception because it
	 * lacks an identifier and a block. The test case passes because it expects an
	 * exception
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string.
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	/**
	 * Smallest legal program.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";
		Parser parser = makeParser(input);
		parser.parse();
	}

	// This test should pass in your complete parser. It will fail in the starter
	// code.
	// Of course, you would want a better error message.
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c;}";
		Parser parser = makeParser(input);
		parser.parse();
	}

	@Test
	public void testFactor0() throws LexicalException, SyntaxException {
		// String input = "abc{blue(bird2[x,y]):=red(bird[x,y]);}";
		String input = "abc{red(bird[1,2]) := red(bird[1,2]);}";
		Parser parser = makeParser(input);
		parser.parse();
	}

	@Test
	public void testArg() throws LexicalException, SyntaxException {
		String input = "prog{if(a & b){};}";
		Parser parser = makeParser(input);
		parser.parse();
	}

	//// Failed Test Cases:
	// "prog{if(a & ){};}"
	@Test
	public void testArg1() throws LexicalException, SyntaxException {
		String input = "prog{if(a & ){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if(a!=){};}"
	@Test
	public void testArg2() throws LexicalException, SyntaxException {
		String input = "prog{if(a!=){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{image var [,]; }"
	@Test
	public void testArg3() throws LexicalException, SyntaxException {
		String input = "prog{image var [,]; }";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{show ;}"
	@Test
	public void testArg4() throws LexicalException, SyntaxException {
		String input = "prog{show ;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if(a | b |){};}"
	@Test
	public void testArg5() throws LexicalException, SyntaxException {
		String input = "prog{if(a | b |){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{input var from @; }"
	@Test
	public void testArg6() throws LexicalException, SyntaxException {
		String input = "prog{input var from @; }";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{sleep ;}"
	@Test
	public void testArg7() throws LexicalException, SyntaxException {
		String input = "prog{sleep ;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{var := ;}"
	@Test
	public void testArg8() throws LexicalException, SyntaxException {
		String input = "prog{var := ;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{while (){};}"
	@Test
	public void testArg9() throws LexicalException, SyntaxException {
		String input = "prog{while (){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{int a;a := (2+3)==(3+2)?1:;}"
	@Test
	public void testArg10() throws LexicalException, SyntaxException {
		String input = "prog{int a;a := (2+3)==(3+2)?1:;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{int a;a := (2+3)==(3+2)?:5;}"
	@Test
	public void testArg11() throws LexicalException, SyntaxException {
		String input = "prog{int a;a := (2+3)==(3+2)?:5;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{ var [,] := 25;}"
	@Test
	public void testArg12() throws LexicalException, SyntaxException {
		String input = "prog{ var [,] := 25;}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if(a | b || c){};}"
	@Test
	public void testArg13() throws LexicalException, SyntaxException {
		String input = "prog{if(a | b || c){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if(a==){};}"
	@Test
	public void testArg14() throws LexicalException, SyntaxException {
		String input = "prog{if(a==){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if (){};}"
	@Test
	public void testArg15() throws LexicalException, SyntaxException {
		String input = "prog{if (){};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{if(a && b){};}"
	@Test
	public void testArg16() throws LexicalException, SyntaxException {
		String input = "if 1+2 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}

	// "prog{show int();}"
	@Test
	public void testArg17() throws LexicalException, SyntaxException {
		String input = "prog{show int();}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.parse();
	}
	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "b{ " + "show +true;" + "red ( IDENTIFIER  [ 3+5+2-1-5 , 9**9/3%2 ] ):= 45 != 9;"
				+ "sleep true  ?  123.56  :  1;" + "while(default_height){}; }";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
		assertEquals(p.toString(),
				"Program [progName=b, block=Block [decsOrStatements=[ShowStatement [e=ExpressionUnary [op=OP_PLUS, expression=ExpressionBooleanLiteral [value=true]]], StatementAssign [lhs=LHSSample [name=IDENTIFIER, pixelSelector=PixelSelector [ex=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionIntegerLiteral [value=3], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=5]], op=OP_PLUS, rightExpression=ExpressionIntegerLiteral [value=2]], op=OP_MINUS, rightExpression=ExpressionIntegerLiteral [value=1]], op=OP_MINUS, rightExpression=ExpressionIntegerLiteral [value=5]], ey=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionBinary [leftExpression=ExpressionIntegerLiteral [value=9], op=OP_POWER, rightExpression=ExpressionIntegerLiteral [value=9]], op=OP_DIV, rightExpression=ExpressionIntegerLiteral [value=3]], op=OP_MOD, rightExpression=ExpressionIntegerLiteral [value=2]]], color=KW_red], e=ExpressionBinary [leftExpression=ExpressionIntegerLiteral [value=45], op=OP_NEQ, rightExpression=ExpressionIntegerLiteral [value=9]]], StatementSleep [duration=ExpressionConditional [guard=ExpressionBooleanLiteral [value=true], trueExpression=ExpressionFloatLiteral [value=123.56], falseExpression=ExpressionIntegerLiteral [value=1]]], StatementWhile [guard=ExpressionPredefinedName [name=KW_default_height], b=Block [decsOrStatements=[]]]]]]");
	}

	// "prog{sleep ;}"
	@Test
	public void testArg71() throws LexicalException, SyntaxException {
		String input = "if 1+2 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{var := ;}"
	@Test
	public void testArg81() throws LexicalException, SyntaxException {
		String input = "if 8&2 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{while (){};}"
	@Test
	public void testArg91() throws LexicalException, SyntaxException {
		String input = "if Z {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{int a;a := (2+3)==(3+2)?1:;}"
	@Test
	public void testArg101() throws LexicalException, SyntaxException {
		String input = "if 1.0 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	// "prog{int a;a := (2+3)==(3+2)?:5;}"
	@Test
	public void testArg111() throws LexicalException, SyntaxException {
		String input = "if x {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{ var [,] := 25;}"
	@Test
	public void testArg121() throws LexicalException, SyntaxException {
		String input = "if 1 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{if(a | b || c){};}"
	@Test
	public void testArg131() throws LexicalException, SyntaxException {
		String input = "while 1+2 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{if(a==){};}"
	@Test
	public void testArg141() throws LexicalException, SyntaxException {
		String input = "while Z {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{if (){};}"
	@Test
	public void testArg151() throws LexicalException, SyntaxException {
		String input = "while 2.0 {}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{if(a && b){};}"
	@Test
	public void testArg161() throws LexicalException, SyntaxException {
		String input = "while 1 {}";
		Parser parser = makeParser(input);
		 thrown.expect(SyntaxException.class);
		parser.statement();
	}

	// "prog{show int();}"
	@Test
	public void testArg171() throws LexicalException, SyntaxException {
		String input = "p{while 1+2{};}";
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	@Test
	public void testArg1712() throws LexicalException, SyntaxException {
		String input = "p { show sin x; }" ;
		Parser parser = makeParser(input);
		thrown.expect(SyntaxException.class);
		parser.expression();
	}
	
}
