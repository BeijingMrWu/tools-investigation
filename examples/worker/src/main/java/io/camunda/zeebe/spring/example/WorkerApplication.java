package io.camunda.zeebe.spring.example;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class WorkerApplication {

  private static Logger log = LoggerFactory.getLogger(WorkerApplication.class);

  public static void main(final String... args) {
    SpringApplication.run(WorkerApplication.class, args);
  }

  private static void logJob(final ActivatedJob job, Object parameterValue) {
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
    log.info("================================================================================");
  }

  @ZeebeWorker(type = "foo", autoComplete = true)
  public void handleFooJob(final ActivatedJob job) {
    log.info("我是foo任务，第一个执行，被taskA触发");
    logJob(job, null);
  }

  @ZeebeWorker(type = "bar", autoComplete = true)
  public Map<String, Object> handleBarJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String a,@ZeebeVariable String test) {
    log.info("我是bar任务，第二个执行，被taskB触发");
    log.info("target参数："+test);
    logJob(job, a);
    return Collections.singletonMap("someResult", "99");
  }

  @ZeebeWorker(type = "fail", autoComplete = true, forceFetchAllVariables = true)
  public void handleFailingJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是fail任务，第三个执行，被taskC触发");
    logJob(job, someResult);
    throw new ZeebeBpmnError("DOESNT_WORK", "This will actually never work :-)");
  }

  @ZeebeWorker(type = "diy", autoComplete = true, forceFetchAllVariables = true)
  public Map handleDiyJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是diy任务，第四个执行，被fail触发");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是diy");
  }

  @ZeebeWorker(type = "copy", autoComplete = true, forceFetchAllVariables = true)
  public Map handleCopyJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是copy任务，第五个执行，被taskB触发");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是copy");
  }

}
