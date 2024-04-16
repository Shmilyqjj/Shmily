package antlr4.demo.error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.Collections;
import java.util.List;

public class ErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
                            RecognitionException e) {
        List<String> stacks = ((Parser) recognizer).getRuleInvocationStack();
        Collections.reverse(stacks);
        System.err.println("[语法错误] 规则栈: " + stacks);
        System.err.println("row" + line + " 列" + charPositionInLine + " 非法符号: " + offendingSymbol + ". 原始原因:" + msg + "Exception: " + e.toString());
    }


}
