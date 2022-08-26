package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
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
import java.util.Date;

@SpringBootApplication
@EnableZeebeClient
@EnableScheduling
@ZeebeDeployment(resources = "classpath:etl.bpmn")
public class StarterSendMsgApplicationBak {

  private static Logger log = LoggerFactory.getLogger(StarterSendMsgApplicationBak.class);

  public static void main(final String... args) {
    SpringApplication.run(StarterSendMsgApplicationBak.class, args);
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
        .messageName("msgTest")
        .correlationKey("msgId-123")
        .timeToLive(Duration.ofMinutes(30))
        .send()
        .join();

    log.info("started instance for MessageKey='{}'",
      event.getMessageKey());
  }
}
