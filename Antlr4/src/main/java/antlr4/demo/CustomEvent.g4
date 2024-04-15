grammar CustomEvent;

// 表达式规则
expr
    : term EOF
    ;

// 项规则
term
    : factor ((PLUS | MINUS | MUL | DIV) factor)*
    ;

// 因子规则
factor
    : NUMBER
    | event
    | LPAREN term RPAREN
    ;

// 操作数规则
operand
    : event
    | NUMBER
    ;

// 事件规则
event
    : EVENT_NAME DELIMITER AFUNCS
    ;

// A方法
AFUNCS
    : 'A' [0-9]+
    ;

// 事件名称
EVENT_NAME
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

// 数字规则
NUMBER
    : [0-9]+
    ;

// 定义算术运算符
PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
// 定义括号
LPAREN : '(';
RPAREN : ')';
// 事件属性分隔符
DELIMITER: '.';

// 忽略空白字符
WS : [ \t\r\n]+ -> skip;