package com.example.demo.splunk;

import com.splunk.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.Optional;

@Component
public class SplunkHelper {

    private Service service;
    private String eventIndexName;

    public SplunkHelper(Optional<Service> service,
                        @Value("${splunk.event.index}") String eventIndexName) {
        this.service = service.orElse(null);
        this.eventIndexName = eventIndexName;
    }

    @Bean
    protected static Optional<Service> serviceProvider(ServiceArgs serviceArgs,
                                    @Value("${splunk.host}") String host,
                                    @Value("${splunk.port}") int port,
                                    @Value("${splunk.username}") String username,
                                    @Value("${splunk.password}") String password) {
        serviceArgs.setHost(host);
        serviceArgs.setPort(port);
        serviceArgs.setUsername(username);
        serviceArgs.setPassword(password);
        HttpService.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
       // Service.setSslSecurityProtocol( SSLSecurityProtocol.TLSv1_2 );
        try {
            return Optional.of(Service.connect(serviceArgs));
        } catch (RuntimeException e) {
            if(e.getCause() instanceof ConnectException) {
                LoggerFactory.getLogger(
                        SplunkHelper.class
                                .getName())
                        .error("Splunk is not available. Unable to connect");
            } else {
                throw e;
            }
        }
        return Optional.empty();
    }

    @Bean
    protected static ServiceArgs serviceArgsProvider() {
        return new ServiceArgs();
    }

    public void logEvent(String data) {
        service.getReceiver().log(eventIndexName, data);
    }

    public void logException(String data) {
        if(!isSplunkIsSetUp()) {
            return;
        }
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("sourcetype", "excetiprion");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }

    public void logRequestSuccess(String data) {
        if(!isSplunkIsSetUp()) {
            return;
        }
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("sourcetype", "request-success");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }

    public void logRequestFailure(String data) {
        if(!isSplunkIsSetUp()) {
            return;
        }
        Args serviceArgs = ServiceArgs.create();
        serviceArgs.put("sourcetype", "request-failure");
        service.getReceiver().log(eventIndexName, serviceArgs, data);
    }

    public boolean isSplunkIsSetUp() {
        return service != null;
    }
}
