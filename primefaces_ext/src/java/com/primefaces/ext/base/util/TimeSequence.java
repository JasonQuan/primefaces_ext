package com.primefaces.ext.base.util;

import java.util.Vector;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

/** 
 * @GeneratedValue(generator = "time")
 * @author Jason
 */
@SuppressWarnings("serial")
public class TimeSequence extends Sequence implements SessionCustomizer {

    public TimeSequence() {
        super();
    }

    public TimeSequence(String name) {
        super(name);
    }

    @Override
    public String getGeneratedValue(Accessor accessor,
            AbstractSession writeSession, String seqName) {
        return String.valueOf(System.currentTimeMillis());
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Vector getGeneratedVector(Accessor accessor,
            AbstractSession writeSession, String seqName, int size) {
        return null;
    }

    @Override
    public boolean shouldAcquireValueAfterInsert() {
        return false;
    }

    @Override
    public boolean shouldUseTransaction() {
        return false;
    }

    @Override
    public boolean shouldUsePreallocation() {
        return false;
    }

    @Override
    public void customize(Session session) throws Exception {
        TimeSequence sequence = new TimeSequence("time");

        session.getLogin().addSequence(sequence);
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }
}
