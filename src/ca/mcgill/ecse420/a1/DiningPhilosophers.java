package ca.mcgill.ecse420.a1;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];
		
		// Instantiate locks to represent the chop sticks.
		for(int i = 0; i < chopsticks.length; i++) {
			// Set to true so that a fair ordering policy is implemented on the locks. This
			// helps avoid starvation.
			chopsticks[i] = new ReentrantLock(true);
		}
		
		// Instantiate and run philosophers.
		for(int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(chopsticks, i);
			philosophers[i].start();
		}
	}

	public static class Philosopher extends Thread implements Runnable {
		// Fields.
		boolean isThinking; // If a philosopher is not thinking, they are eating.
		ReentrantLock[] chopsticks;
		int philosopherId;
		Random rand;
		int leftChopstickIndex;
		int rightChopstickIndex;

		// Constructor.
		public Philosopher(ReentrantLock[] chopsticks, int philosopherId) {
			if(Math.random() < 0.5) {
				isThinking = false;
			} else {
				isThinking = true;
			}

			this.chopsticks = chopsticks;
			this.philosopherId = philosopherId;
			this.rand = new Random();
			
			// For the "last" philosopher in the group, swap the positions of their chop sticks so that they
			// pick up their right chop stick first instead of their left. This solves the deadlock problem.
			if(philosopherId == chopsticks.length - 1) {
				this.leftChopstickIndex = (philosopherId + 1) % chopsticks.length;
				this.rightChopstickIndex =  philosopherId;
			} else {
				// For all other philosophers, we will say that the "left" chop stick is at the same
				// index as the philosopher ID and that the "right" chop stick is at the "next" index.
				this.leftChopstickIndex = philosopherId;
				this.rightChopstickIndex = (philosopherId + 1) % chopsticks.length;
			}
		}

		// Methods.
		@Override
		public void run() {
			while(true) {
				if(isThinking) {
					System.out.println("Philosopher " + philosopherId + " is thinking.");
					if(Math.random() < 0.5) { // Randomly decide when a philosopher has "thought long enough".
						isThinking = false; // Philosopher is ready to start eating.
					}

					continue;
				} else {
					// Try to pick up left chop stick.
					chopsticks[leftChopstickIndex].lock();
					System.out.println("Philosopher " + philosopherId + " picked up left chopstick.");

					// Sleep for some random amount of time between picking up chop sticks 
					// to help illustrate the deadlock more easily.
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) { /* TODO Auto-generated catch block. */ } 
					
					// Try to pick up right chop stick.
					chopsticks[rightChopstickIndex].lock();
					System.out.println("Philosopher " + philosopherId + " picked up right chopstick.");
					
					// Once both chop sticks are picked up, eat for random amount of time up to 3 seconds.
					try {
						System.out.println("Philosopher " + philosopherId + " is eating.");
						Thread.sleep(rand.nextInt(3000));
					} catch (InterruptedException e) { /* TODO Auto-generated catch block. */ }
					
					// Put both chop sticks down.
					chopsticks[leftChopstickIndex].unlock();
					chopsticks[rightChopstickIndex].unlock();
					System.out.println("Philosopher " + philosopherId + " is finished eating for now.");
					
					// Start thinking again.
					isThinking = true;
				}
			}
		}
	}
}
