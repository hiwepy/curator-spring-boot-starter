# curator-spring-boot-starter

#### 组件简介

> 基于 [Apache Curator ](http://curator.apache.org/) 整合的 Starter


#### 使用说明

##### 1、Spring Boot 项目添加 Maven 依赖

``` xml
<dependency>
	<groupId>com.github.hiwepy</groupId>
	<artifactId>curator-spring-boot-starter</artifactId>
	<version>${project.version}</version>
</dependency>
```

##### 2、在`application.yml`文件中增加如下配置

```yaml
#################################################################################################
### Apache Curator 配置：
#################################################################################################
curator:
  connect-string: 192.168.1.1:2100,192.168.1.1:2101,192.168.1.:2102
  connection-timeout-ms: 3000
  session-timeout-ms: 30000
  max-close-wait-ms: 2000
  with-ensemble-tracker: true
```

##### 3、使用示例

 
```java

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CuratorApplication_Test {
	
	@Autowired
	private CuratorZookeeperTemplate template;
	
	@PostConstruct
	public void testText() {
		

	}
	
	@PostConstruct
	public void testImage() {

		
		 
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(CuratorApplication_Test.class, args);
	}

}

```

## Jeebiz 技术社区

Jeebiz 技术社区 **微信公共号**、**小程序**，欢迎关注反馈意见和一起交流，关注公众号回复「Jeebiz」拉你入群。

|公共号|小程序|
|---|---|
| ![](https://raw.githubusercontent.com/hiwepy/static/main/images/qrcode_for_gh_1d965ea2dfd1_344.jpg)| ![](https://raw.githubusercontent.com/hiwepy/static/main/images/gh_09d7d00da63e_344.jpg)|