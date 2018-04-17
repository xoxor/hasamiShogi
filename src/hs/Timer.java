package hs;

import java.util.concurrent.Semaphore;

public class Timer {

	private Tempo t;
	private Semaphore s = new Semaphore(0);
	private volatile boolean isExpired;
	private long timeSleep;

	public Timer(long timeSleep) {
		t = new Tempo();
		t.start();
		this.isExpired = false;
		this.timeSleep = timeSleep;
		t.setPriority(Thread.MAX_PRIORITY);
	}

	public void start() {
		isExpired = false;
		s.release();
	}

	public void stop() {
		t.interrupt();
	}

	public boolean isExpired() {
		return this.isExpired;
	}

	class Tempo extends Thread {
		public void run() {
			while (true) {
				s.acquireUninterruptibly();
				try {
					sleep(timeSleep);
					isExpired = true;
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
