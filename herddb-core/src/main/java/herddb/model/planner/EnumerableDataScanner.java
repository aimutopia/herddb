/*
 Licensed to Diennea S.r.l. under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. Diennea S.r.l. licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

 */
package herddb.model.planner;

import herddb.model.Column;
import herddb.model.DataScanner;
import herddb.model.DataScannerException;
import herddb.utils.DataAccessor;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;

/**
 * Adapter from Enumerable to DataScanner
 *
 * @author eolivelli
 */
public class EnumerableDataScanner extends DataScanner {

    private final Enumerator<DataAccessor> wrapped;
    private DataAccessor next;

    public EnumerableDataScanner(
            long transactionId, String[] fieldNames, Column[] schema,
            Enumerable<DataAccessor> wrapped) {
        super(transactionId, fieldNames, schema);
        this.wrapped = wrapped.enumerator();
        fetchNext();
    }

    @Override
    public boolean hasNext() throws DataScannerException {
        return next != null;
    }

    private void fetchNext() {
        System.out.println("fetchnext");
        if (wrapped.moveNext()) {
            next = wrapped.current();
            System.out.println("new cuurent:" + next);
        } else {
            next = null;
            System.out.println("end");
        }
    }

    @Override
    public DataAccessor next() throws DataScannerException {
        DataAccessor current = next;
        fetchNext();
        return current;
    }

    @Override
    public void close() throws DataScannerException {
        try {
            wrapped.close();
        } catch (RuntimeException err) {
            throw new DataScannerException(err);
        }
    }

}