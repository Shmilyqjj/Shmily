// Generated from C:/Programing/Projects/Shmily/Antlr4/src/main/java/antlr4/demo\CustomEvent.g4 by ANTLR 4.12.0
package antlr4.demo.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CustomEventParser}.
 */
public interface CustomEventListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CustomEventParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(CustomEventParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CustomEventParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(CustomEventParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link CustomEventParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(CustomEventParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link CustomEventParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(CustomEventParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link CustomEventParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(CustomEventParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CustomEventParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(CustomEventParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CustomEventParser#operand}.
	 * @param ctx the parse tree
	 */
	void enterOperand(CustomEventParser.OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link CustomEventParser#operand}.
	 * @param ctx the parse tree
	 */
	void exitOperand(CustomEventParser.OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link CustomEventParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEvent(CustomEventParser.EventContext ctx);
	/**
	 * Exit a parse tree produced by {@link CustomEventParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEvent(CustomEventParser.EventContext ctx);
}