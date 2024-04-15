// Generated from C:/Programing/Projects/Shmily/Antlr4/src/main/java/antlr4/demo\CustomEvent.g4 by ANTLR 4.12.0
package antlr4.demo.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CustomEventParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CustomEventVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CustomEventParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(CustomEventParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CustomEventParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(CustomEventParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link CustomEventParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(CustomEventParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CustomEventParser#operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand(CustomEventParser.OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link CustomEventParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent(CustomEventParser.EventContext ctx);
}