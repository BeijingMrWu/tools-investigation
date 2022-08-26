package io.camunda.zeebe.spring.example;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(resources = "classpath:order-process.bpmn")
public class StarterSendOrderIdApplicationBak {

  private static Logger log = LoggerFactory.getLogger(StarterSendOrderIdApplicationBak.class);

  public static void main(final String... args) {
    SpringApplication.run(StarterSendOrderIdApplicationBak.class, args);
  }

  @Autowired
  private ZeebeClientLifecycle client;

  @Scheduled(fixedRate = 6000L)
  public void startProcesses() {
    if (!client.isRunning()) {
      return;
    }
    final PublishMessageResponse event =
      client
        .newPublishMessageCommand()
        .messageName("payment-received")
        .correlationKey("10")
        .timeToLive(Duration.ofMinutes(30))
        .send()
        .join();

    log.info("started instance for MessageKey='{}'",
      event.getMessageKey());
  }
}
