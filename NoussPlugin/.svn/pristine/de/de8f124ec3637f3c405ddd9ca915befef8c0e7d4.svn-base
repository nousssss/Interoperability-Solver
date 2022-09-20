package org.processmining.models.workshop.sjjleemans.ProcessTree.mining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	private ExecutorService pool;
	private int numberOfThreads;
	private List<Future<?>> jobs;
	
	//constructor, makes an estimate of the number of threads.
	public ThreadPool() {
		numberOfThreads = Runtime.getRuntime().availableProcessors();
		init();
	}
	
	//constructor, takes a number of threads. Provide 0 to execute synchronously.
	public ThreadPool(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		init();
	}
	
	//add a job to be executed. Will block if executed synchronously
	public synchronized void addJob(Runnable job) {
		if (numberOfThreads > 0) {
			Future<?> x = pool.submit(job);
			jobs.add(x);
		} else {
			job.run();
		}
	}
	
	//wait till all jobs have finished execution. While waiting, new jobs can still be added and will be executed.
	//Hence, will block until the thread pool is idle
	public void join() throws ExecutionException {
		if (numberOfThreads > 0) {
			ExecutionException error = null;
			
			//wait for all jobs to finish
			while (jobs.size() > 0) {
				Future<?> job = jobs.remove(0);
				try {
					job.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
					error = e;
				}
			}
			
			//all jobs are done
			
			pool.shutdown();
			
			 if (error != null) {
				throw new ExecutionException(error);
			}
		}
	}
	
	private void init() {
		if (numberOfThreads > 0) {
			pool = Executors.newFixedThreadPool(numberOfThreads);
			jobs = Collections.synchronizedList(new ArrayList<Future<?>>());
		}
	}
}
