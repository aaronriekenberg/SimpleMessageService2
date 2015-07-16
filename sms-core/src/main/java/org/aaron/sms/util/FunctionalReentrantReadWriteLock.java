package org.aaron.sms.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FunctionalReentrantReadWriteLock extends ReentrantReadWriteLock {

	private static final long serialVersionUID = 1L;

	public FunctionalReentrantReadWriteLock() {
		super();
	}

	public FunctionalReentrantReadWriteLock(boolean fair) {
		super(fair);
	}

	private void doInLock(Lock lock, Runnable r) {
		lock.lock();
		try {
			r.run();
		} finally {
			lock.unlock();
		}
	}

	public void doInReadLock(Runnable r) {
		checkNotNull(r, "r is null");

		doInLock(readLock(), r);
	}

	public void doInWriteLock(Runnable r) {
		checkNotNull(r, "r is null");

		doInLock(writeLock(), r);
	}

}
