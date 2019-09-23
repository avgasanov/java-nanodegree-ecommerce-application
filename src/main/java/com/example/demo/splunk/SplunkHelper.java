package com.example.demo.splunk;

import com.splunk.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SplunkHelper {

    private Service service;
    private String eventIndexName;

    public SplunkHelper(Service service,
                        @Value("${splunk.event.index}") String eventIndexName) {
        this.service = service;
        this.eventIndexName = eventIndexName;
        service.getReceiver().log("commerceapp-events", "hello splunk");
    }

    @Bean
    protected static Service serviceProvider(ServiceArgs serviceArgs,
                                    @Value("${splunk.host}") String host,
                                    @Value("${splunk.port}") int port,
                                    @Value("${splunk.username}") String username,
                                    @Value("${splunk.password}") String password) {
        serviceArgs.setHost(host);
        serviceArgs.setPort(port);
        serviceArgs.setUsername(username);
        serviceArgs.setPassword(password);
        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        Service.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        return Service.connect(serviceArgs);
    }

    @Bean
    protected static ServiceArgs serviceArgsProvider() {
        return new ServiceArgs();
    }

    public void logEvent(String data) {
        service.getReceiver().log(eventIndexName, data);
    }

    public void logException(String data) {
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("event-type", "excetiprion");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }

    public void logRequestSuccess(String data) {
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("event-type", "request-success");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }

    public void logRequestFailure(String data) {
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("event-type", "request-failure");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }
}
