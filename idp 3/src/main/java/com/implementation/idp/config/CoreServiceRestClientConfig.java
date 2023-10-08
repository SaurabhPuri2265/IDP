/**
 * 
 */
package com.implementation.idp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author YogeshSharma on Oct 1, 2018
 *
 */
@Configuration
public class CoreServiceRestClientConfig {

	
	@Value(value="${coreService.connection.timeout.in.ms:30000}")
	private int connectTimeout = 30000;
	
	@Value(value="${coreService.request.timeout.in.ms:30000}")
	private int requestTimeout = 30000;
	
	@Value(value="${coreService.socket.timeout.in.ms:30000}")
	private int socketTimeout = 30000;
	
	@Value("${coreService.max.total.connection:100}")
	private int maxConnections = 100;
	
	@Value("${coreService.max.per.route:100}")
	private int maxPerRoute = 100;

	
	private  static  final  int  IDLE_CONNECTION_WAIT_TIME = 60;
	
	@Bean("coreService")
	public RestTemplate coreService(){
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient()));
		for(HttpMessageConverter<?> converter : template.getMessageConverters()){
			if(converter instanceof MappingJackson2HttpMessageConverter){
				MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter)converter;
				jsonConverter.setObjectMapper(objectMapper());
			}
		}
		return template;
	}

	private ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	private CloseableHttpClient httpClient() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(requestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(maxConnections);
		connManager.setDefaultMaxPerRoute(maxPerRoute);
		connManager.setValidateAfterInactivity(300);
		return HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).setConnectionManager(connManager)
				.evictIdleConnections(IDLE_CONNECTION_WAIT_TIME, TimeUnit.SECONDS).build();
	}


}
