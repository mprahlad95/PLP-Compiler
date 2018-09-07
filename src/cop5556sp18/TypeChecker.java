package cop5556sp18;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Scanner.Token;
import cop5556sp18.Types.Type;

import java.util.ArrayList;
import java.util.List;

import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.ASTVisitor;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

public class TypeChecker implements ASTVisitor {

	TypeChecker() {
	}

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	SymbolTable ST = new SymbolTable();

	// Name is only used for naming the output file.
	// Visit the child block to type check program.
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		program.block.visit(this, arg);
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		ST.enterScope();
		for (int i = 0; i < block.decsOrStatements.size(); i++) {
			block.decsOrStatements.get(i).visit(this, null);
		}
		ST.leaveScope();
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception {
		if (declaration.height != null) {
			declaration.height.visit(this, null);
		}
		if (declaration.width != null) {
			declaration.width.visit(this, null);
		}
		Boolean check = ST.insert(declaration.name, declaration);
		Boolean flag = false;
		if (check == false) {
			throw new SemanticException(declaration.firstToken, "Lookup not null");
		}
		if (declaration.width == null && declaration.height == null) {
			flag = true;
		} else if ((declaration.width.type == Type.INTEGER && Types.getType(declaration.type) == Type.IMAGE)
				&& declaration.height.type == Type.INTEGER && Types.getType(declaration.type) == Type.IMAGE) {
			flag = true;
		}
		if (!flag) {
			throw new SemanticException(declaration.firstToken, "Mismatch in declaration of width & height");
		}
		ST.insert(declaration.name, declaration);
		return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		statementWrite.sourceDec = ST.lookup(statementWrite.sourceName);
		if (statementWrite.sourceDec == null) {
			throw new SemanticException(statementWrite.firstToken, "sourceDec is null");
		}
		statementWrite.destDec = ST.lookup(statementWrite.destName);
		if (statementWrite.destDec == null) {
			throw new SemanticException(statementWrite.firstToken, "destDec is null");
		}
		if (!(Types.getType(statementWrite.sourceDec.type).equals(Type.IMAGE))) {
			throw new SemanticException(statementWrite.firstToken, "sourceDec type is not image");
		}
		if (!(Types.getType(statementWrite.destDec.type).equals(Type.FILE))) {
			throw new SemanticException(statementWrite.firstToken, "destDec type is not file");
		}
		return null;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception {
		statementInput.e.visit(this, null);
		statementInput.dec = ST.lookup(statementInput.destName);
		if (statementInput.dec == null) {
			throw new SemanticException(statementInput.firstToken, "statementInput Declaration is null");
		}
		if (statementInput.e.type != Type.INTEGER) {
			throw new SemanticException(statementInput.firstToken, "statementInput Expression type is not integer");
		}
		return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		pixelSelector.ex.visit(this, null);
		pixelSelector.ey.visit(this, null);
		if (!(pixelSelector.ex.type == Type.INTEGER || pixelSelector.ex.type == Type.FLOAT)) {
			throw new SemanticException(pixelSelector.firstToken, "pixelSelector Expression0 is not integer or float");
		}
		if (pixelSelector.ex.type != pixelSelector.ey.type) {
			throw new SemanticException(pixelSelector.firstToken, "pixelSelector Expression types are not equal");
		}
		return null;
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		expressionConditional.guard.visit(this, null);
		expressionConditional.falseExpression.visit(this, null);
		expressionConditional.trueExpression.visit(this, null);
		if (!expressionConditional.guard.type.equals(Type.BOOLEAN)) {
			throw new SemanticException(expressionConditional.firstToken, "Guard is not type boolean");
		}
		if (!expressionConditional.trueExpression.type.equals(expressionConditional.falseExpression.type)) {
			throw new SemanticException(expressionConditional.firstToken, "Type mismatch between two expressions");
		}
		expressionConditional.type = expressionConditional.trueExpression.type;
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception { // inferred
		expressionBinary.leftExpression.visit(this, null);
		expressionBinary.rightExpression.visit(this, null);
		Type t1 = expressionBinary.leftExpression.type;
		Type t2 = expressionBinary.rightExpression.type;
		switch (expressionBinary.op) {
		case OP_PLUS:
		case OP_MINUS:
		case OP_TIMES:
		case OP_DIV:
		case OP_POWER:
			if (t1.equals(Type.INTEGER) && t2.equals(Type.INTEGER)) {
				expressionBinary.type = t1;
			} else if (t1.equals(Type.INTEGER) && t2.equals(Type.FLOAT)) {
				expressionBinary.type = t2;
			} else if (t1.equals(Type.FLOAT) && t2.equals(Type.INTEGER)) {
				expressionBinary.type = t1;
			} else if (t1.equals(Type.FLOAT) && t2.equals(Type.FLOAT)) {
				expressionBinary.type = t1;
			} else
				throw new SemanticException(expressionBinary.firstToken, "Illegal type in ExpressionBinary");
			break;
		case OP_MOD:
			if (t1.equals(Type.INTEGER) && t2.equals(Type.INTEGER)) {
				expressionBinary.type = t1;
			} else
				throw new SemanticException(expressionBinary.firstToken, "Illegal type in ExpressionBinary");
			break;
		case OP_AND:
		case OP_OR:
			if (t1.equals(Type.INTEGER) && t2.equals(Type.INTEGER)) {
				expressionBinary.type = t1;
			} else if (t1.equals(Type.BOOLEAN) && t2.equals(Type.BOOLEAN)) {
				expressionBinary.type = t1;
			} else
				throw new SemanticException(expressionBinary.firstToken, "Illegal type in ExpressionBinary");
			break;
		case OP_GT:
		case OP_EQ:
		case OP_NEQ:
		case OP_LE:
		case OP_LT:
		case OP_GE:
			if (t1.equals(Type.INTEGER) && t2.equals(Type.INTEGER)) {
				expressionBinary.type = Type.BOOLEAN;
			} else if (t1.equals(Type.BOOLEAN) && t2.equals(Type.BOOLEAN)) {
				expressionBinary.type = Type.BOOLEAN;
			} else if (t1.equals(Type.FLOAT) && t2.equals(Type.FLOAT)) {
				expressionBinary.type = Type.BOOLEAN;
			} else
				throw new SemanticException(expressionBinary.firstToken, "Illegal type in ExpressionBinary");
			break;
		default:
			throw new SemanticException(expressionBinary.firstToken, "Illegal type in ExpressionBinary");
		}
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		expressionUnary.expression.visit(this, null);
		expressionUnary.type = expressionUnary.expression.type;
		return null;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		expressionIntegerLiteral.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.type = Type.BOOLEAN;
		return null;
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		expressionPredefinedName.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		expressionFloatLiteral.type = Type.FLOAT;
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg) // inferred type
					throws Exception {
		expressionFunctionAppWithExpressionArg.e.visit(this, null);
		Type t1 = expressionFunctionAppWithExpressionArg.e.type;
		Kind fn = expressionFunctionAppWithExpressionArg.function;
		switch (fn) {
		case KW_red:
		case KW_blue:
		case KW_green:
		case KW_alpha:
			if (t1.equals(Type.INTEGER)) {
				expressionFunctionAppWithExpressionArg.type = t1;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;
		case KW_abs:
			if (t1.equals(Type.INTEGER)) {
				expressionFunctionAppWithExpressionArg.type = t1;
			} else if (t1.equals(Type.FLOAT)) {
				expressionFunctionAppWithExpressionArg.type = t1;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;
		case KW_sin:
		case KW_cos:
		case KW_atan:
		case KW_log:
			if (t1.equals(Type.FLOAT)) {
				expressionFunctionAppWithExpressionArg.type = t1;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;
		case KW_width:
		case KW_height:
			if (t1.equals(Type.IMAGE)) {
				expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;
		case KW_float:
			if (t1.equals(Type.FLOAT)) {
				expressionFunctionAppWithExpressionArg.type = Type.FLOAT;
			} else if (t1.equals(Type.INTEGER)) {
				expressionFunctionAppWithExpressionArg.type = Type.FLOAT;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;

		case KW_int:
			if (t1.equals(Type.INTEGER)) {
				expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
			} else if (t1.equals(Type.FLOAT)) {
				expressionFunctionAppWithExpressionArg.type = Type.INTEGER;
			} else
				throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
						"Illegal type in visitExpressionFunctionAppWithExpressionArg");
			break;
		default:
			throw new SemanticException(expressionFunctionAppWithExpressionArg.firstToken,
					"Illegal type in visitExpressionFunctionAppWithExpressionArg");
		}
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		expressionFunctionAppWithPixel.e0.visit(this, null);
		expressionFunctionAppWithPixel.e1.visit(this, null);
		if (expressionFunctionAppWithPixel.name.equals(Kind.KW_cart_x) // Check before submitting
				|| expressionFunctionAppWithPixel.name.equals(Kind.KW_cart_y)) { // Check before submitting
			if (expressionFunctionAppWithPixel.e0.type == Type.FLOAT
					&& expressionFunctionAppWithPixel.e1.type == Type.FLOAT) {
				expressionFunctionAppWithPixel.type = Type.INTEGER;
			} else {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,
						"expressionFunctionAppWithPixel - either function name is not cart_x or cart_y or expression type aren't float");
			}
		}
		if (expressionFunctionAppWithPixel.name.equals(Kind.KW_polar_a)
				|| expressionFunctionAppWithPixel.name.equals(Kind.KW_polar_r)) {
			if (expressionFunctionAppWithPixel.e0.type == Type.INTEGER
					&& expressionFunctionAppWithPixel.e1.type == Type.INTEGER) {
				expressionFunctionAppWithPixel.type = Type.FLOAT;
			} else {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken,
						"expressionFunctionAppWithPixel - either function name is not polar_a or polar_r or expression type aren't integer");
			}
		}
		return null;
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		expressionPixelConstructor.alpha.visit(this, null);
		expressionPixelConstructor.red.visit(this, null);
		expressionPixelConstructor.blue.visit(this, null);
		expressionPixelConstructor.green.visit(this, null);
		if (expressionPixelConstructor.alpha.type != Type.INTEGER || expressionPixelConstructor.red.type != Type.INTEGER
				|| expressionPixelConstructor.green.type != Type.INTEGER
				|| expressionPixelConstructor.blue.type != Type.INTEGER) {
			throw new SemanticException(expressionPixelConstructor.firstToken,
					" expressionPixelConstructor type not integer");
		}
		expressionPixelConstructor.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		statementAssign.e.visit(this, null);
		statementAssign.lhs.visit(this, null);
		if (statementAssign.lhs.type != statementAssign.e.type) {
			throw new SemanticException(statementAssign.firstToken, "StatementAssign Type mismatch");
		}
		return null;
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		statementShow.e.visit(this, null);
		Type valid[] = new Type[] { Type.INTEGER, Type.BOOLEAN, Type.FLOAT, Type.IMAGE };
		Boolean flag = false;

		for (Type temp : valid) {
			if (temp.equals(statementShow.e.type)) {
				flag = true;
			}
		}

		if (!flag) {
			throw new SemanticException(statementShow.firstToken, "Type invalid for statementShow");
		}

		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		expressionPixel.pixelSelector.visit(this, null);
		expressionPixel.dec = ST.lookup(expressionPixel.name);
		if (expressionPixel.dec == null) {
			throw new SemanticException(expressionPixel.firstToken, "expressionPixel declaration is null");
		}
		if (Types.getType(expressionPixel.dec.type) != Type.IMAGE) {
			throw new SemanticException(expressionPixel.firstToken, "expressionPixel declaration type is not image");
		}
		expressionPixel.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {
		expressionIdent.dec = ST.lookup(expressionIdent.name);
		if (expressionIdent.dec == null) {
			throw new SemanticException(expressionIdent.firstToken, "expressionIdent declaration is null");
		}
		expressionIdent.type = Types.getType(expressionIdent.dec.type);
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		lhsSample.pixelSelector.visit(this, null);
		lhsSample.dec = ST.lookup(lhsSample.name);
		if (lhsSample.dec == null) {
			throw new SemanticException(lhsSample.firstToken, "lhsSample declaration is null");
		}
		if (Types.getType(lhsSample.dec.type) != Type.IMAGE) {
			throw new SemanticException(lhsSample.firstToken, "lhsSample declaration type is not image");
		}
		lhsSample.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		lhsPixel.pixelSelector.visit(this, null);
		lhsPixel.dec = ST.lookup(lhsPixel.name);
		if (lhsPixel.dec == null) {
			throw new SemanticException(lhsPixel.firstToken, "lhsPixel declaration is null");
		}
		if (Types.getType(lhsPixel.dec.type) != Type.IMAGE) {
			throw new SemanticException(lhsPixel.firstToken, "lhsPixel declaration type is not image");
		}
		lhsPixel.type = Type.INTEGER;
		return null;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		lhsIdent.dec = ST.lookup(lhsIdent.name);
		if (lhsIdent.dec == null) {
			throw new SemanticException(lhsIdent.firstToken, "lhsIdent declaration is null");
		}
		lhsIdent.type = Types.getType(lhsIdent.dec.type);
		return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		statementIf.guard.visit(this, null);
		statementIf.b.visit(this, null);
		Boolean flag = false;
		if (statementIf.guard.type.equals(Type.BOOLEAN)) {
			flag = true;
		}
		if (!flag) {
			throw new SemanticException(statementIf.firstToken, "statementIf type not boolean");
		}
		return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		statementWhile.guard.visit(this, null);
		statementWhile.b.visit(this, null);
		Boolean flag = false;
		if (statementWhile.guard.type.equals(Type.BOOLEAN)) {
			flag = true;
		}
		if (!flag) {
			throw new SemanticException(statementWhile.firstToken, "statementWhile type not boolean");
		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		statementSleep.duration.visit(this, null);
		Boolean flag = false;
		if (statementSleep.duration.type.equals(Type.INTEGER)) {
			flag = true;
		}
		if (!flag) {
			throw new SemanticException(statementSleep.duration.firstToken,
					"statementSleep duration must be of type integer");
		}
		return null;
	}

}
