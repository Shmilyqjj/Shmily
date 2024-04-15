package antlr4.demo;

import antlr4.demo.gen.CustomEventLexer;
import antlr4.demo.gen.CustomEventParser;
import antlr4.demo.listener.ErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Antlr demo测试类
 * 定义一段表达式格式 检测语句合法性
 */

public class Demo {
    public static void main(String[] args) {
        String customEvent = "6*dl_create_result.A100+((2*android_xlpan_file_consumption.A101)+(dl_create_result.A100+2-(3*1)))*dl_create_result.A100";
        ANTLRInputStream inputStream = new ANTLRInputStream(customEvent);
        CustomEventLexer lexer = new CustomEventLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CustomEventParser parser = new CustomEventParser(tokens);

        // 错误监听器 用于返回错误信息
        ErrorListener errorListener = new ErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        // 调用解析器
        CustomEventParser.ExprContext context = parser.expr();
        int syntaxErrors = parser.getNumberOfSyntaxErrors();
        if (syntaxErrors == 0) {
            System.out.println("表达式符合语法规则");
        } else {
            System.out.println("表达式不符合语法规则，错误数: " + syntaxErrors);
        }

    }
}
