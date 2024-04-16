package antlr4.demo.visitor;

import antlr4.demo.gen.CustomEventParser;
import antlr4.demo.gen.CustomEventVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor implements CustomEventVisitor {
    @Override
    public Object visitExpr(CustomEventParser.ExprContext ctx) {
        return null;
    }

    @Override
    public Object visitTerm(CustomEventParser.TermContext ctx) {
        return null;
    }

    @Override
    public Object visitFactor(CustomEventParser.FactorContext ctx) {
        return null;
    }

    @Override
    public Object visitOperand(CustomEventParser.OperandContext ctx) {
        return null;
    }

    @Override
    public Object visitEvent(CustomEventParser.EventContext ctx) {
        return null;
    }

    @Override
    public Object visit(ParseTree parseTree) {
        return null;
    }

    @Override
    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    @Override
    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    @Override
    public Object visitErrorNode(ErrorNode errorNode) {
        return null;
    }
}
