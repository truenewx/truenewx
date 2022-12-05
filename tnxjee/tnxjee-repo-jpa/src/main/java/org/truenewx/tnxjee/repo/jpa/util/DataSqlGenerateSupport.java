package org.truenewx.tnxjee.repo.jpa.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 数据SQL生成支持
 */
public abstract class DataSqlGenerateSupport {

    protected void generateForeignKeyChecks(OutputStream out, boolean checks) {
        writeLine(out, "set foreign_key_checks = " + toSqlString(checks) + Strings.SEMICOLON);
    }

    protected void generate(OutputStream out, DataExportingTable table, Consumer<Integer> lengthConsumer) {
        generate(out, table, true, lengthConsumer);
    }

    protected void generate(OutputStream out, Function<Integer, DataExportingTable> function,
            Consumer<Integer> lengthConsumer) {
        int pageNo = 0;
        DataExportingTable table;
        do {
            table = function.apply(++pageNo);
            generate(out, table, pageNo == 1, lengthConsumer);
        } while (table.isMorePage());
    }

    private void generate(OutputStream out, DataExportingTable table, boolean clear, Consumer<Integer> lengthConsumer) {
        List<Object[]> records = table.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            if (clear) {
                writeLine(out);
                writeLine(out, "delete from " + table.getTableName() + Strings.SEMICOLON);
            }
            writeLine(out);
            writeLine(out, "insert into " + table.getTableName() + Strings.SPACE + Strings.LEFT_BRACKET +
                    StringUtils.join(table.getColumnNames(), ", ") + Strings.RIGHT_BRACKET);
            for (int i = 0; i < records.size(); i++) {
                StringBuilder line = new StringBuilder();
                if (i == 0) {
                    line.append("values ");
                } else {
                    line.append("       ");
                }
                line.append(Strings.LEFT_BRACKET);
                for (Object value : records.get(i)) {
                    line.append(toSqlString(value)).append(", ");
                }
                line.delete(line.length() - 2, line.length());
                line.append(Strings.RIGHT_BRACKET);
                if (i == records.size() - 1) {
                    line.append(Strings.SEMICOLON);
                } else {
                    line.append(Strings.COMMA);
                }
                writeLine(out, line.toString());
                if (lengthConsumer != null) {
                    lengthConsumer.accept(line.length());
                }
            }
            writeLine(out);
        } else {
            writeLine(out);
            writeLine(out, "-- Empty " + table.getTableName() + " --");
            writeLine(out);
        }
    }

    private String toSqlString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? "1" : "0";
        }
        if (value instanceof Temporal) {
            value = TemporalUtil.format((Temporal) value);
        }
        if (value instanceof Date) {
            value = DateUtil.formatLong((Date) value);
        }
        return Strings.SINGLE_QUOTES + value + Strings.SINGLE_QUOTES;
    }

    protected void writeLine(OutputStream out, String data) {
        try {
            if (data == null) {
                data = Strings.EMPTY;
            }
            IOUtils.write(data + Strings.ENTER, out, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeLine(OutputStream out) {
        writeLine(out, null);
    }

}
