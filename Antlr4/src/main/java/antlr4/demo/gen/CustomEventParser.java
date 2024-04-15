// Generated from C:/Programing/Projects/Shmily/Antlr4/src/main/java/antlr4/demo\CustomEvent.g4 by ANTLR 4.12.0
package antlr4.demo.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CustomEventParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AFUNCS=1, EVENT_NAME=2, NUMBER=3, PLUS=4, MINUS=5, MUL=6, DIV=7, LPAREN=8, 
		RPAREN=9, DELIMITER=10, WS=11;
	public static final int
		RULE_expr = 0, RULE_term = 1, RULE_factor = 2, RULE_operand = 3, RULE_event = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "term", "factor", "operand", "event"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'+'", "'-'", "'*'", "'/'", "'('", "')'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AFUNCS", "EVENT_NAME", "NUMBER", "PLUS", "MINUS", "MUL", "DIV", 
			"LPAREN", "RPAREN", "DELIMITER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "CustomEvent.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CustomEventParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TerminalNode EOF() { return getToken(CustomEventParser.EOF, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CustomEventVisitor ) return ((CustomEventVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			term();
			setState(11);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TermContext extends ParserRuleContext {
		public List<FactorContext> factor() {
			return getRuleContexts(FactorContext.class);
		}
		public FactorContext factor(int i) {
			return getRuleContext(FactorContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(CustomEventParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(CustomEventParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(CustomEventParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(CustomEventParser.MINUS, i);
		}
		public List<TerminalNode> MUL() { return getTokens(CustomEventParser.MUL); }
		public TerminalNode MUL(int i) {
			return getToken(CustomEventParser.MUL, i);
		}
		public List<TerminalNode> DIV() { return getTokens(CustomEventParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(CustomEventParser.DIV, i);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CustomEventVisitor ) return ((CustomEventVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(13);
			factor();
			setState(18);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 240L) != 0)) {
				{
				{
				setState(14);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 240L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(15);
				factor();
				}
				}
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FactorContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(CustomEventParser.NUMBER, 0); }
		public EventContext event() {
			return getRuleContext(EventContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(CustomEventParser.LPAREN, 0); }
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(CustomEventParser.RPAREN, 0); }
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).exitFactor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CustomEventVisitor ) return ((CustomEventVisitor<? extends T>)visitor).visitFactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_factor);
		try {
			setState(27);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(21);
				match(NUMBER);
				}
				break;
			case EVENT_NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(22);
				event();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
				match(LPAREN);
				setState(24);
				term();
				setState(25);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperandContext extends ParserRuleContext {
		public EventContext event() {
			return getRuleContext(EventContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(CustomEventParser.NUMBER, 0); }
		public OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).enterOperand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).exitOperand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CustomEventVisitor ) return ((CustomEventVisitor<? extends T>)visitor).visitOperand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperandContext operand() throws RecognitionException {
		OperandContext _localctx = new OperandContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_operand);
		try {
			setState(31);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EVENT_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(29);
				event();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(30);
				match(NUMBER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EventContext extends ParserRuleContext {
		public TerminalNode EVENT_NAME() { return getToken(CustomEventParser.EVENT_NAME, 0); }
		public TerminalNode DELIMITER() { return getToken(CustomEventParser.DELIMITER, 0); }
		public TerminalNode AFUNCS() { return getToken(CustomEventParser.AFUNCS, 0); }
		public EventContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_event; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).enterEvent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CustomEventListener ) ((CustomEventListener)listener).exitEvent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CustomEventVisitor ) return ((CustomEventVisitor<? extends T>)visitor).visitEvent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventContext event() throws RecognitionException {
		EventContext _localctx = new EventContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_event);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			match(EVENT_NAME);
			setState(34);
			match(DELIMITER);
			setState(35);
			match(AFUNCS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u000b&\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0005"+
		"\u0001\u0011\b\u0001\n\u0001\f\u0001\u0014\t\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u001c\b\u0002"+
		"\u0001\u0003\u0001\u0003\u0003\u0003 \b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004"+
		"\u0006\b\u0000\u0001\u0001\u0000\u0004\u0007$\u0000\n\u0001\u0000\u0000"+
		"\u0000\u0002\r\u0001\u0000\u0000\u0000\u0004\u001b\u0001\u0000\u0000\u0000"+
		"\u0006\u001f\u0001\u0000\u0000\u0000\b!\u0001\u0000\u0000\u0000\n\u000b"+
		"\u0003\u0002\u0001\u0000\u000b\f\u0005\u0000\u0000\u0001\f\u0001\u0001"+
		"\u0000\u0000\u0000\r\u0012\u0003\u0004\u0002\u0000\u000e\u000f\u0007\u0000"+
		"\u0000\u0000\u000f\u0011\u0003\u0004\u0002\u0000\u0010\u000e\u0001\u0000"+
		"\u0000\u0000\u0011\u0014\u0001\u0000\u0000\u0000\u0012\u0010\u0001\u0000"+
		"\u0000\u0000\u0012\u0013\u0001\u0000\u0000\u0000\u0013\u0003\u0001\u0000"+
		"\u0000\u0000\u0014\u0012\u0001\u0000\u0000\u0000\u0015\u001c\u0005\u0003"+
		"\u0000\u0000\u0016\u001c\u0003\b\u0004\u0000\u0017\u0018\u0005\b\u0000"+
		"\u0000\u0018\u0019\u0003\u0002\u0001\u0000\u0019\u001a\u0005\t\u0000\u0000"+
		"\u001a\u001c\u0001\u0000\u0000\u0000\u001b\u0015\u0001\u0000\u0000\u0000"+
		"\u001b\u0016\u0001\u0000\u0000\u0000\u001b\u0017\u0001\u0000\u0000\u0000"+
		"\u001c\u0005\u0001\u0000\u0000\u0000\u001d \u0003\b\u0004\u0000\u001e"+
		" \u0005\u0003\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f\u001e"+
		"\u0001\u0000\u0000\u0000 \u0007\u0001\u0000\u0000\u0000!\"\u0005\u0002"+
		"\u0000\u0000\"#\u0005\n\u0000\u0000#$\u0005\u0001\u0000\u0000$\t\u0001"+
		"\u0000\u0000\u0000\u0003\u0012\u001b\u001f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}