package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
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

import cop5556sp18.Scanner.*;
import static cop5556sp18.Scanner.Kind.*;
import java.util.*;
import cop5556sp18.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	Program program() throws SyntaxException {
		Token first = t;
		Token progName = match(IDENTIFIER);
		Block block = block();
		return new Program(first, progName, block);
	}

	/*
	 * Block ::= { ( (Declaration | Statement) ; )* }
	 */

	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = { KW_input, KW_write, IDENTIFIER, KW_red, KW_green, KW_blue, KW_alpha, KW_while, KW_if,
			KW_show, KW_sleep };
	Kind[] firstType = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatementInput = { KW_input };
	Kind[] firstStatementWrite = { KW_write };
	Kind[] firstStatementAssignment = { KW_red, KW_green, KW_blue, KW_alpha, IDENTIFIER };
	Kind[] firstStatementWhile = { KW_while };
	Kind[] firstStatementIf = { KW_if };
	Kind[] firstStatementShow = { KW_show };
	Kind[] firstStatementSleep = { KW_sleep };
	Kind[] firstColor = { KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] firstLHS = { IDENTIFIER, KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] firstPixelSelector = { LSQUARE };
	Kind[] firstExpression = { OP_PLUS, OP_MINUS, OP_EXCLAMATION, INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL,
			LPAREN, KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int,
			KW_float, KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha, IDENTIFIER, KW_Z, KW_default_height,
			KW_default_width, LPIXEL };
	Kind[] firstOrExpression = firstExpression;
	Kind[] firstAndExpression = firstExpression;
	Kind[] firstEqExpression = firstExpression;
	Kind[] firstRelExpression = firstExpression;
	Kind[] firstAddExpression = firstExpression;
	Kind[] firstMultExpression = firstExpression;
	Kind[] firstPowerExpression = firstExpression;
	Kind[] firstUnaryExpression = firstExpression;
	Kind[] firstUnaryExpressionNotPlusMinus = { OP_EXCLAMATION, INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL, LPAREN,
			KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float,
			KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha, IDENTIFIER, KW_Z, KW_default_height,
			KW_default_width, LPIXEL };
	Kind[] firstPrimary = { INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL, LPAREN, KW_sin, KW_cos, KW_atan, KW_abs,
			KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red,
			KW_green, KW_blue, KW_alpha, IDENTIFIER, KW_Z, KW_default_height, KW_default_width, LPIXEL };
	Kind[] firstPixelConstructor = { LPIXEL };
	Kind[] firstPixelExpression = { IDENTIFIER };
	Kind[] firstFunctionApplication = { KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a,
			KW_polar_r, KW_int, KW_float, KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] firstFunctionName = firstFunctionApplication;
	Kind[] firstPredefinedName = { KW_Z, KW_default_height, KW_default_width };
	Kind[] functionName = { KW_sin, KW_cos, KW_log, KW_atan, KW_abs, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r,
			KW_width, KW_height, KW_red, KW_green, KW_blue, KW_alpha, KW_int, KW_float };

	Block block() throws SyntaxException {
		Token first = t;
		match(LBRACE);
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		while (isKind(firstDec) | isKind(firstStatement)) {
			if (isKind(firstDec)) {
				Declaration dec = declaration();
				decsAndStatements.add(dec);
			} else if (isKind(firstStatement)) {
				Statement s = statement();
				decsAndStatements.add(s);
			}
			match(SEMI);
		}
		match(RBRACE);
		return new Block(first, decsAndStatements);
	}

	Declaration declaration() throws SyntaxException {
		Token first = t;
		Token type = consume();
		Token name = match(IDENTIFIER);
		Expression w = null;
		Expression h = null;
		if (type.kind == KW_image) {
			// match(KW_image);
			// match(IDENTIFIER);
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				w = expression();
				match(COMMA);
				h = expression();
				match(RSQUARE);
			}
		}
		// } else if (isKind(firstType)) {
		// type();
		// match(IDENTIFIER);
		// }
		return new Declaration(first, type, name, w, h);
	}

	public void type() throws SyntaxException {
		if (isKind(KW_int)) {
			match(KW_int);
		} else if (isKind(KW_float)) {
			match(KW_float);
		} else if (isKind(KW_boolean)) {
			match(KW_boolean);
		} else if (isKind(KW_image)) {
			match(KW_image);
		} else if (isKind(KW_filename)) {
			match(KW_filename);
		}
	}

	Statement statement() throws SyntaxException {
		Token first = t;
		if (isKind(firstStatement)) {
			if (isKind(firstStatementInput)) {
				consume();
				Token destName = match(IDENTIFIER);
				match(KW_from);
				match(OP_AT);
				Expression e = expression();
				return new StatementInput(first, destName, e);
			} else if (isKind(firstStatementWrite)) {
				consume();
				Token sourceName = match(IDENTIFIER);
				match(KW_to);
				Token destName = match(IDENTIFIER);
				return new StatementWrite(first, sourceName, destName);
			} else if (isKind(firstLHS)) { // StatemnentAssign
				LHS lhs = LHS();
				match(OP_ASSIGN);
				Expression e = expression();
				return new StatementAssign(first, lhs, e);
			} else if (isKind(firstStatementWhile)) {
				match(KW_while);
				match(LPAREN);
				Expression e = expression();
				match(RPAREN);
				Block b = block();
				return new StatementWhile(first, e, b);
			} else if (isKind(firstStatementIf)) {
				match(KW_if);
				match(LPAREN);
				Expression e = expression();
				match(RPAREN);
				Block b = block();
				return new StatementIf(first, e, b);
			} else if (isKind(firstStatementShow)) {
				consume();
				Expression e = expression();
				return new StatementShow(first, e);
			} else if (isKind(firstStatementSleep)) {
				consume();
				Expression e = expression();
				return new StatementSleep(first, e);
			}
		}
		throw new SyntaxException(t, " Error in Statement");
	}

	LHS LHS() throws SyntaxException {
		Token first = t;
		if (isKind(IDENTIFIER)) {
			Token name = match(IDENTIFIER);
			if (isKind(firstPixelSelector)) {
				PixelSelector pixel = pixelSelector();
				return new LHSPixel(first, name, pixel);
			}
			return new LHSIdent(first, name);
		}
		Token color = consume();
		match(LPAREN);
		Token name = match(IDENTIFIER);
		PixelSelector selector = pixelSelector();
		match(RPAREN);
		return new LHSSample(first, name, selector, color);
	}

	PixelSelector pixelSelector() throws SyntaxException {
		Token first = t;
		match(LSQUARE);
		Expression e0 = expression();
		match(COMMA);
		Expression e1 = expression();
		match(RSQUARE);
		PixelSelector pixel = new PixelSelector(first, e0, e1);
		return pixel;
	}

	Expression expression() throws SyntaxException {
		Token first = t;
		Expression e0 = orExpression();
		if (isKind(OP_QUESTION)) {
			match(OP_QUESTION);
			Expression e1 = expression();
			match(OP_COLON);
			Expression e2 = expression();
			e0 = new ExpressionConditional(first, e0, e1, e2);
		}
		return e0;
	}

	Expression orExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = andExpression();
		while (isKind(OP_OR)) {
			Token op = match(OP_OR);
			Expression e1 = andExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression andExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = eqExpression();
		while (isKind(OP_AND)) {
			Token op = match(OP_AND);
			Expression e1 = eqExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression eqExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = relExpression();
		Token op = null;
		while (isKind(OP_EQ) | isKind(OP_NEQ)) {
			if (isKind(OP_EQ)) {
				op = match(OP_EQ);
			} else if (isKind(OP_NEQ)) {
				op = match(OP_NEQ);
			}
			Expression e1 = relExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression relExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = addExpression();
		Token op = null;
		while (isKind(OP_LT) | isKind(OP_GT) | isKind(OP_LE) | isKind(OP_GE)) {
			if (isKind(OP_LT)) {
				op = match(OP_LT);
			} else if (isKind(OP_GT)) {
				op = match(OP_GT);
			} else if (isKind(OP_LE)) {
				op = match(OP_LE);
			} else if (isKind(OP_GE)) {
				op = match(OP_GE);
			}
			Expression e1 = addExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression addExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = multExpression();
		Token op = null;
		while (isKind(OP_PLUS) | isKind(OP_MINUS)) {
			if (isKind(OP_PLUS)) {
				op = match(OP_PLUS);
			} else if (isKind(OP_MINUS)) {
				op = match(OP_MINUS);
			}
			Expression e1 = multExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression multExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = powerExpression();
		Token op = null;
		while (isKind(OP_TIMES) | isKind(OP_DIV) | isKind(OP_MOD)) {
			if (isKind(OP_TIMES)) {
				op = match(OP_TIMES);
			} else if (isKind(OP_DIV)) {
				op = match(OP_DIV);
			} else if (isKind(OP_MOD)) {
				op = match(OP_MOD);
			}
			Expression e1 = powerExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression powerExpression() throws SyntaxException {
		Token first = t;
		Expression e0 = unaryExpression();
		if (isKind(OP_POWER)) {
			Token op = match(OP_POWER);
			Expression e1 = powerExpression();
			return new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	}

	Expression unaryExpression() throws SyntaxException {
		Token first = t;
		if (isKind(OP_PLUS)) {
			Token op = match(OP_PLUS);
			Expression e = unaryExpression();
			return new ExpressionUnary(first, op, e);
		} else if (isKind(OP_MINUS)) {
			Token op = match(OP_MINUS);
			Expression e = unaryExpression();
			return new ExpressionUnary(first, op, e);
		} else {
			return unaryExpressionNotPlusMinus();
		}
	}

	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token first = t;
		if (isKind(OP_EXCLAMATION)) {
			Token op = match(OP_EXCLAMATION);
			Expression e = unaryExpression();
			return new ExpressionUnary(first, op, e);
		} else if (isKind(firstPrimary)) {
			return primary();
		} else {
			throw new SyntaxException(t, "Possibly Incomplete   " + t.kind.toString() + " at line " + t.line()
					+ " and position " + t.posInLine());
		}

	}

	Expression primary() throws SyntaxException {
		Token first = t;
		if (isKind(INTEGER_LITERAL)) {
			Token intLit = match(INTEGER_LITERAL);
			return new ExpressionIntegerLiteral(first, intLit);
		} else if (isKind(BOOLEAN_LITERAL)) {
			Token booleanLit = match(BOOLEAN_LITERAL);
			return new ExpressionBooleanLiteral(first, booleanLit);
		} else if (isKind(FLOAT_LITERAL)) {
			Token floatLit = match(FLOAT_LITERAL);
			return new ExpressionFloatLiteral(first, floatLit);
		} else if (isKind(LPAREN)) {
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			return e;
		} else if (isKind(firstFunctionApplication)) {
			Expression e = functionApplication();
			return e;
		} else if (isKind(IDENTIFIER)) {
			Token name = match(IDENTIFIER);
			if (isKind(firstPixelSelector)) {
				PixelSelector pixelSelector = pixelSelector();
				return new ExpressionPixel(first, name, pixelSelector);
			}
			return new ExpressionIdent(first, name);
		} else if (isKind(firstPredefinedName)) {
			Token t = consume();
			return new ExpressionPredefinedName(first, t);
		} else if (isKind(firstPixelConstructor)) {
			// pixelConstructor(first);
			match(LPIXEL);
			Expression alpha = expression();
			match(COMMA);
			Expression red = expression();
			match(COMMA);
			Expression green = expression();
			match(COMMA);
			Expression blue = expression();
			match(RPIXEL);
			return new ExpressionPixelConstructor(first, alpha, red, green, blue);
		}
//		throw new SyntaxException(t, "Error in Primary");
		return null;
	}

	Expression functionApplication() throws SyntaxException {
		Token first = t;
		Token name = match(firstFunctionName);
		if (isKind(LPAREN)) {
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			return new ExpressionFunctionAppWithExpressionArg(first, name, e);
		} else if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expression e0 = expression();
			match(COMMA);
			Expression e1 = expression();
			match(RSQUARE);
			return new ExpressionFunctionAppWithPixel(first, name, e0, e1);
		}
		else throw new SyntaxException(t, "Error in Function Application");
	}

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		// throw new SyntaxException(t, "Syntax Error");
		throw new SyntaxException(t, "Illegal token " + t.kind.toString() + " at line " + t.line() + " and position "
				+ t.posInLine() + " Expected " + kind + " but received " + t.kind);
	}

	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind(EOF)) {
			throw new SyntaxException(t, "Trying to consume EOF");
		}
		t = scanner.nextToken();
		return tmp;
	}

	Token match(Kind... kinds) throws SyntaxException {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		throw new SyntaxException(t, "Error in matching kinds");
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t, "Syntax Error"); // TODO give a better error message!
	}

}
