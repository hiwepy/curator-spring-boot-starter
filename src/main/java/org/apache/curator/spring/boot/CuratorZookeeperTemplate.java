package org.apache.curator.spring.boot;

import org.apache.curator.CuratorZookeeperClient;

public class CuratorZookeeperTemplate {
	
	private CuratorZookeeperClient curatorZkClient;
	private final CuratorZookeeperProperties properties;
	
	public CuratorZookeeperTemplate(CuratorZookeeperProperties properties, CuratorZookeeperClient curatorZkClient) {
		this.curatorZkClient = curatorZkClient;
		this.properties = properties;
	}
	 
	
	
	public CuratorZookeeperProperties getProperties() {
		return properties;
	}

	public CuratorZookeeperClient getCuratorZkClient() {
		return curatorZkClient;
	}
	
}
