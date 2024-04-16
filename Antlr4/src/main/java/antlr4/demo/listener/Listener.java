package antlr4.demo.listener;

import antlr4.demo.gen.CustomEventListener;
import antlr4.demo.gen.CustomEventParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Listener implements CustomEventListener {
    @Override
    public void enterExpr(CustomEventParser.ExprContext ctx) {

    }

    @Override
    public void exitExpr(CustomEventParser.ExprContext ctx) {

    }

    @Override
    public void enterTerm(CustomEventParser.TermContext ctx) {

    }

    @Override
    public void exitTerm(CustomEventParser.TermContext ctx) {

    }

    @Override
    public void enterFactor(CustomEventParser.FactorContext ctx) {

    }

    @Override
    public void exitFactor(CustomEventParser.FactorContext ctx) {

    }

    @Override
    public void enterOperand(CustomEventParser.OperandContext ctx) {

    }

    @Override
    public void exitOperand(CustomEventParser.OperandContext ctx) {

    }

    @Override
    public void enterEvent(CustomEventParser.EventContext ctx) {

    }

    @Override
    public void exitEvent(CustomEventParser.EventContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}


