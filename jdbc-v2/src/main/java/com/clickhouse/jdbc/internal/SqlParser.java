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

    private static final Logger LOG = LoggerFactory.getLogger(SqlParser.class);
    private final static Pattern UNQUOTE_IDENTIFIER = Pattern.compile("^[\"`]?(.+?)[\"`]?$");

    public ParsedStatement parsedStatement(String sql) {
        ParsedStatement parserListener = new ParsedStatement();
        walkSql(sql,  parserListener);
        return parserListener;
    }

    public ParsedPreparedStatement parsePreparedStatement(String sql) {
        ParsedPreparedStatement parserListener = new ParsedPreparedStatement();
        walkSql(sql,  parserListener);
        return parserListener;
    }

    private ClickHouseParser walkSql(String sql, ClickHouseParserBaseListener listener ) {
        CharStream charStream = CharStreams.fromString(sql);
        ClickHouseLexer lexer = new ClickHouseLexer(charStream);
        ClickHouseParser parser = new ClickHouseParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener());

        ClickHouseParser.QueryStmtContext parseTree = parser.queryStmt();
        IterativeParseTreeWalker.DEFAULT.walk(listener, parseTree);

        return parser;
    }

    public static String unquoteIdentifier(String str) {
        Matcher matcher = UNQUOTE_IDENTIFIER.matcher(str.trim());
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

    private static class ParserErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            LOG.warn("SQL syntax error at line: " + line + ", pos: " + charPositionInLine + ", " + msg);
        }
    }
}
