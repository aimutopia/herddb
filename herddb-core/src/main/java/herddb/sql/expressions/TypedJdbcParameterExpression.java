/*
 * Licensed to Diennea S.r.l. under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Diennea S.r.l. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package herddb.sql.expressions;

import herddb.model.StatementEvaluationContext;
import herddb.model.StatementExecutionException;
import herddb.sql.SQLRecordPredicate;

public class TypedJdbcParameterExpression implements CompiledSQLExpression {

    private final int index;
    private final int type;

    public TypedJdbcParameterExpression(int index, int type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public Object evaluate(herddb.utils.DataAccessor bean, StatementEvaluationContext context) throws StatementExecutionException {
        Object value = context.getJdbcParameter(index);
        try {
            return SQLRecordPredicate.cast(value, type);
        } catch (IllegalArgumentException err) {
            throw new StatementExecutionException("Unexpected cast to type " + type + " for value " + value + " "
                    + "while accessing JDBC paramter #" + index, err);
        }
    }

    @Override
    public void validate(StatementEvaluationContext context) throws StatementExecutionException {
        context.getJdbcParameter(index);
    }

    @Override
    public String toString() {
        return System.identityHashCode(this) + " TypedJdbcParameterExpression{type=" + type + ", index=" + index + '}';
    }

    @Override
    public CompiledSQLExpression cast(int type) {
        return new TypedJdbcParameterExpression(index, type);
    }

    @Override
    public CompiledSQLExpression remapPositionalAccessToToPrimaryKeyAccessor(int[] projection) {
        return this;
    }
}
