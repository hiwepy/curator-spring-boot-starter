package org.apache.curator.spring.boot;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.DefaultZookeeperFactory;
import org.apache.curator.utils.ZookeeperFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper.States;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

@Configuration
@EnableConfigurationProperties(CuratorZookeeperProperties.class)
public class CuratorZookeeperAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RetryPolicy retryPolicy() {
		// 1 重试策略：初试时间为1s 重试10次
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
		return retryPolicy;
	}

	@Bean
	@ConditionalOnMissingBean
	public CuratorFramework curatorClient(CuratorZookeeperProperties properties, RetryPolicy retryPolicy) {
		
		// 1、创建连接实例
		CuratorFramework curatorClient = CuratorFrameworkFactory.builder()
				.connectString(properties.getConnectString())
				.connectionTimeoutMs(properties.getConnectionTimeoutMs())
				.sessionTimeoutMs(properties.getSessionTimeoutMs())
				.authorization(CollectionUtils.isEmpty(properties.getAuthInfo()) ? new ArrayList<AuthInfo>(): properties.getAuthInfo().stream().map(info -> {
					return new AuthInfo(info.getAuth(), info.getAuth().getBytes());
				}).collect(Collectors.toList()))
				.canBeReadOnly(properties.isCanBeReadOnly())
				.ensembleTracker(properties.isWithEnsembleTracker())
				.retryPolicy(retryPolicy)
				.namespace(properties.getNamespace())
				.build();
		// 2、开启连接
		curatorClient.start();

		System.out.println(States.CONNECTED);
		System.out.println(curatorClient.getState());

		return curatorClient;
	}

	@Bean
	@ConditionalOnMissingBean
	public ZookeeperFactory zookeeperFactory() {
		return new DefaultZookeeperFactory();
	}

	@Bean
	@ConditionalOnMissingBean
	public EnsembleProvider ensembleProvider(CuratorZookeeperProperties properties) {
		return new FixedEnsembleProvider(properties.getConnectString());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public CuratorZookeeperClient curatorZkClient(CuratorZookeeperProperties properties,
			ZookeeperFactory zookeeperFactory, EnsembleProvider ensembleProvider, Watcher watcher,
			RetryPolicy retryPolicy) {
		CuratorZookeeperClient curatorClient = new CuratorZookeeperClient(zookeeperFactory, ensembleProvider,
				properties.getSessionTimeoutMs(), properties.getConnectionTimeoutMs(), watcher, retryPolicy,
				properties.isCanBeReadOnly());
		return curatorClient;
	}

	@Bean
	public CuratorZookeeperTemplate CuratorZkTemplate(CuratorZookeeperProperties properties,
			CuratorFramework curatorClient,
			CuratorZookeeperClient curatorZkClient) {
		return new CuratorZookeeperTemplate(properties, curatorZkClient);
	}

}
