/* Copyright 2009 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics.jdbc.decorator;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class AbstractXAResourceDecorator implements XAResource {

    private final XAResource delegate;

    public AbstractXAResourceDecorator(final XAResource delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    protected final XAResource delegate() {
        return delegate;
    }

    public void commit(Xid arg0, boolean arg1) throws XAException {
        delegate().commit(arg0, arg1);
    }

    public void end(Xid arg0, int arg1) throws XAException {
        delegate().end(arg0, arg1);
    }

    public void forget(Xid arg0) throws XAException {
        delegate().forget(arg0);
    }

    public int getTransactionTimeout() throws XAException {
        return delegate().getTransactionTimeout();
    }

    public boolean isSameRM(XAResource arg0) throws XAException {
        return delegate().isSameRM(arg0);
    }

    public int prepare(Xid arg0) throws XAException {
        return delegate().prepare(arg0);
    }

    public Xid[] recover(int arg0) throws XAException {
        return delegate().recover(arg0);
    }

    public void rollback(Xid arg0) throws XAException {
        delegate().rollback(arg0);
    }

    public boolean setTransactionTimeout(int arg0) throws XAException {
        return delegate().setTransactionTimeout(arg0);
    }

    public void start(Xid arg0, int arg1) throws XAException {
        delegate().start(arg0, arg1);
    }
   
}
