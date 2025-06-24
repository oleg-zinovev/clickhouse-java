package com.clickhouse.jdbc.internal;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.IterativeParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {

    public ParsedStatement parsedStatement(String sql) {
        ClickHouseParser.QueryStmtContext parseTree = parseQueryStmt(sql);
        ParsedStatement parserListener = new ParsedStatement();
        IterativeParseTreeWalker.DEFAULT.walk(parserListener, parseTree);
        return parserListener;
    }

    public ParsedPreparedStatement parsePreparedStatement(String sql) {
        ClickHouseParser.QueryStmtContext parseTree = parseQueryStmt(sql);
        ParsedPreparedStatement parserListener = new ParsedPreparedStatement();
        IterativeParseTreeWalker.DEFAULT.walk(parserListener, parseTree);
        return parserListener;
    }

    private ClickHouseParser.QueryStmtContext parseQueryStmt(String sql) {
        CharStream charStream = CharStreams.fromString(sql);
        ClickHouseLexer lexer = new ClickHouseLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(SqlErrorListener.INSTANCE);
        ClickHouseParser parser = new ClickHouseParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(SqlErrorListener.INSTANCE);
        return parser.queryStmt();
    }

    private final static Pattern UNQUOTE_INDENTIFIER = Pattern.compile(
            "^[\\\"`]?(.+?)[\\\"`]?$"
    );

    public static String unquoteIdentifier(String str) {
        Matcher matcher = UNQUOTE_INDENTIFIER.matcher(str.trim());
        if (matcher.find()) {
            return matcher.group(1).replace("\\\\", "\\");
        } else {
            return str;
        }
    }

    public static String escapeQuotes(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
    }

    private static final Logger LOG = LoggerFactory.getLogger(SqlParser.class);

    private static class SqlErrorListener extends BaseErrorListener {
        private static final SqlErrorListener INSTANCE = new SqlErrorListener();
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            LOG.debug("SQL syntax error at line: {}, pos: {}, {}", line, charPositionInLine, msg);
        }
    }
}
