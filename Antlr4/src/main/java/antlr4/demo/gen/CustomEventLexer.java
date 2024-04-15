// Generated from C:/Programing/Projects/Shmily/Antlr4/src/main/java/antlr4/demo\CustomEvent.g4 by ANTLR 4.12.0
package antlr4.demo.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CustomEventLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AFUNCS=1, EVENT_NAME=2, NUMBER=3, PLUS=4, MINUS=5, MUL=6, DIV=7, LPAREN=8, 
		RPAREN=9, DELIMITER=10, WS=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"AFUNCS", "EVENT_NAME", "NUMBER", "PLUS", "MINUS", "MUL", "DIV", "LPAREN", 
			"RPAREN", "DELIMITER", "WS"
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


	public CustomEventLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CustomEvent.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u000b>\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000"+
		"\u0001\u0000\u0004\u0000\u001a\b\u0000\u000b\u0000\f\u0000\u001b\u0001"+
		"\u0001\u0001\u0001\u0005\u0001 \b\u0001\n\u0001\f\u0001#\t\u0001\u0001"+
		"\u0002\u0004\u0002&\b\u0002\u000b\u0002\f\u0002\'\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0004"+
		"\n9\b\n\u000b\n\f\n:\u0001\n\u0001\n\u0000\u0000\u000b\u0001\u0001\u0003"+
		"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011"+
		"\t\u0013\n\u0015\u000b\u0001\u0000\u0004\u0001\u000009\u0003\u0000AZ_"+
		"_az\u0004\u000009AZ__az\u0003\u0000\t\n\r\r  A\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000"+
		"\u0001\u0017\u0001\u0000\u0000\u0000\u0003\u001d\u0001\u0000\u0000\u0000"+
		"\u0005%\u0001\u0000\u0000\u0000\u0007)\u0001\u0000\u0000\u0000\t+\u0001"+
		"\u0000\u0000\u0000\u000b-\u0001\u0000\u0000\u0000\r/\u0001\u0000\u0000"+
		"\u0000\u000f1\u0001\u0000\u0000\u0000\u00113\u0001\u0000\u0000\u0000\u0013"+
		"5\u0001\u0000\u0000\u0000\u00158\u0001\u0000\u0000\u0000\u0017\u0019\u0005"+
		"A\u0000\u0000\u0018\u001a\u0007\u0000\u0000\u0000\u0019\u0018\u0001\u0000"+
		"\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000"+
		"\u0000\u0000\u001b\u001c\u0001\u0000\u0000\u0000\u001c\u0002\u0001\u0000"+
		"\u0000\u0000\u001d!\u0007\u0001\u0000\u0000\u001e \u0007\u0002\u0000\u0000"+
		"\u001f\u001e\u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f"+
		"\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\"\u0004\u0001\u0000"+
		"\u0000\u0000#!\u0001\u0000\u0000\u0000$&\u0007\u0000\u0000\u0000%$\u0001"+
		"\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000"+
		"\'(\u0001\u0000\u0000\u0000(\u0006\u0001\u0000\u0000\u0000)*\u0005+\u0000"+
		"\u0000*\b\u0001\u0000\u0000\u0000+,\u0005-\u0000\u0000,\n\u0001\u0000"+
		"\u0000\u0000-.\u0005*\u0000\u0000.\f\u0001\u0000\u0000\u0000/0\u0005/"+
		"\u0000\u00000\u000e\u0001\u0000\u0000\u000012\u0005(\u0000\u00002\u0010"+
		"\u0001\u0000\u0000\u000034\u0005)\u0000\u00004\u0012\u0001\u0000\u0000"+
		"\u000056\u0005.\u0000\u00006\u0014\u0001\u0000\u0000\u000079\u0007\u0003"+
		"\u0000\u000087\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:8\u0001"+
		"\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000"+
		"<=\u0006\n\u0000\u0000=\u0016\u0001\u0000\u0000\u0000\u0005\u0000\u001b"+
		"!\':\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}