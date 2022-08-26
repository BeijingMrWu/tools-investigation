package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
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
@ZeebeDeployment(resources = "classpath:order-process.bpmn")
public class StarterOrderApplicationBak {

  private static Logger log = LoggerFactory.getLogger(StarterOrderApplicationBak.class);

  public static void main(final String... args) {
    SpringApplication.run(StarterOrderApplicationBak.class, args);
  }

  @Autowired
  private ZeebeClientLifecycle client;

  @Scheduled(fixedRate = 6000L)
  public void startProcesses() {
    if (!client.isRunning()) {
      return;
    }
    final ProcessInstanceEvent event =
//      client
//        .newCreateInstanceCommand()
//        .bpmnProcessId("order-process")
//        .latestVersion()
//        .variables("{\"orderId\": \"" + "1234" + "\",\"orderValue\":99}")
//        .send()
//        .join();
    client
      .newCreateInstanceCommand()
      .bpmnProcessId("order-process")
      .latestVersion()
      .variables("{\"orderId\": \"" + "2345" + "\",\"orderValue\":190}")
      .send()
      .join();

    client
      .newPublishMessageCommand()
      .messageName("payment-received")
      .correlationKey("2345")
      .timeToLive(Duration.ofMinutes(30))
      .send()
      .join();
    log.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
      event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
  }
}
