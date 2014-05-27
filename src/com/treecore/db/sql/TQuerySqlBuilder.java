package com.treecore.db.sql;

import android.text.TextUtils;
import com.treecore.db.exception.TDBException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TQuerySqlBuilder extends TSqlBuilder {
	protected Pattern sLimitPattern = Pattern
			.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

	public String buildSql() throws TDBException, IllegalArgumentException,
			IllegalAccessException {
		return buildQueryString();
	}

	public String buildQueryString() {
		if ((TextUtils.isEmpty(this.groupBy))
				&& (!TextUtils.isEmpty(this.having))) {
			throw new IllegalArgumentException(
					"HAVING clauses are only permitted when using a groupBy clause");
		}
		if ((!TextUtils.isEmpty(this.limit))
				&& (!this.sLimitPattern.matcher(this.limit).matches())) {
			throw new IllegalArgumentException("invalid LIMIT clauses:"
					+ this.limit);
		}

		StringBuilder query = new StringBuilder(120);
		query.append("SELECT ");
		if (this.distinct.booleanValue()) {
			query.append("DISTINCT ");
		}
		query.append("* ");
		query.append("FROM ");
		query.append(this.tableName);
		appendClause(query, " WHERE ", this.where);
		appendClause(query, " GROUP BY ", this.groupBy);
		appendClause(query, " HAVING ", this.having);
		appendClause(query, " ORDER BY ", this.orderBy);
		appendClause(query, " LIMIT ", this.limit);
		return query.toString();
	}
}