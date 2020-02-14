package com.wanda.credit.ds.client.policeAuthV2.newThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.wanda.credit.ds.BaseRequestor;

public class MasterNew<T> extends BaseRequestor implements CallBack<T>{
	private List<Worker<T>> workers=new ArrayList<Worker<T>>();
	private List<T> results=new ArrayList<T>();//
	public MasterNew() {
	}
	public void excute() throws InterruptedException, ExecutionException{
		for (Worker<T> worker : workers) {
			asynOperate(worker);
		}
	}
	public synchronized boolean addWorker(Worker<T> worker) {
		return workers.add(worker);
	}
	@Override
	public synchronized boolean call(T result) {
		return results.add(result);
	}
	public  List<T> getResults() {
		return results;
	}
/*	public void setResults(List<T> results) {
		this.results = results;
	}*/
	
}
