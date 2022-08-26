package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@EnableZeebeClient
public class OrderProcessApplication {

  private static Logger log = LoggerFactory.getLogger(OrderProcessApplication.class);

  public static void main(final String... args) {
    SpringApplication.run(OrderProcessApplication.class, args);
  }

  private static void logJob(final ActivatedJob job, Object parameterValue) {
    log.info("\n\n\n ================================"+job.getType()+"示例开始================================================");
    log.info(
      "complete job\n>>> [类型key: {}, 关键字key: {}, 元素element: {}, 工作流实例workflow instance: {}]\n{截止时间deadline; {}]\n[请求头headers: {}]\n[变量variable parameter: {}\n[多个变量variables: {}]",
      job.getType(),
      job.getKey(),
      job.getElementId(),
      job.getProcessInstanceKey(),
      Instant.ofEpochMilli(job.getDeadline()),
      job.getCustomHeaders(),
      parameterValue,
      job.getVariables());

  }

 @ZeebeWorker(type = "initiate-payment", autoComplete = true)
  public void  handleExtractJob(final ActivatedJob job) {
    String system=String.valueOf(job.getVariablesAsMap().get("systemName"));
    log.info("我是initiate-payment，开始支付流程");
    logJob(job, system);
//    return Collections.singletonMap("extractResult", "开始支付流程");
  }

  @ZeebeWorker(type = "ship-without-insurance", autoComplete = true,forceFetchAllVariables = true)
  public Map<String, Object> handletransformJob(final ActivatedJob job) {
    log.info("我是ship-without-insurance任务，我出现因为商品小于100元");
//    log.info("extractResult参数："+extractResult);
    logJob(job,"");
    return Collections.singletonMap("transformResult", "小于100元");
  }

  @ZeebeWorker(type = "ship-with-insurance", autoComplete = true, forceFetchAllVariables = true)
  public void handleLoadJob(final JobClient client, final ActivatedJob job) {
    log.info("我是ship-with-insurance任务，我出现是因为商品大于100元");
    logJob(job, "");
//    throw new NullPointerException();
//    throw new ZeebeBpmnError("manualExceptionB", "This will actually never work :-)");
  }

}
