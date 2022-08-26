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
public class ETLApplication {

  private static Logger log = LoggerFactory.getLogger(ETLApplication.class);

  public static void main(final String... args) {
    SpringApplication.run(ETLApplication.class, args);
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

 @ZeebeWorker(type = "extract", autoComplete = true)
  public Map<String, Object>  handleExtractJob(final ActivatedJob job) {
    String system=String.valueOf(job.getVariablesAsMap().get("systemName"));
    log.info("我是extract任务22222，负责从"+system+"系统抽取数据");
    logJob(job, system);
    return Collections.singletonMap("extractResult", "我是一百万条数据");
  }

  @ZeebeWorker(type = "transform", autoComplete = true)
  public Map<String, Object> handletransformJob(final ActivatedJob job) {
//    log.info("我是transform任务，待转换的数据为"+extractResult);
//    log.info("extractResult参数："+extractResult);
    logJob(job,"");
    return Collections.singletonMap("transformResult", "我是加工处理后的80万条数据");
  }
  /*forceFetchAllVariables注解和加载全部流程的变量，不添加则只能获取前面步骤传递的变量。注意*，如果不希望获取全部还需要配合@ZeebeVariable选择取用变量*/
  @ZeebeWorker(type = "load", autoComplete = true, forceFetchAllVariables = true)
  public void handleLoadob(final JobClient client, final ActivatedJob job,@ZeebeVariable String loadInput) {
    log.info("我是load任务，待装填的loadInput数据为"+loadInput);
    logJob(job, "");
//    throw new NullPointerException();
    throw new ZeebeBpmnError("manualExceptionA", "This will actually never work :-)");
  }

  @ZeebeWorker(type = "customization", autoComplete = true, forceFetchAllVariables = true)
  public void handleCustomizationob(final JobClient client, final ActivatedJob job) {
    log.info("我是customization任务，如果我执行说明前置任务无异常");
    logJob(job, "");
//    throw new NullPointerException();
  }

  @ZeebeWorker(type = "throwException", autoComplete = true, forceFetchAllVariables = true)
  public Map handleDiyJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是throwExceptionA任务，有manualExceptionA异常");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是manualExceptionA");
  }
  @ZeebeWorker(type = "throwExceptionB", autoComplete = true, forceFetchAllVariables = true)
  public Map handleExceptionBJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是throwExceptionB任务，有manualExceptionB异常");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是manualExceptionB");
  }

  @ZeebeWorker(type = "subprocess", autoComplete = true, forceFetchAllVariables = true)
  public Map handleSubprocessJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是subprocessJob任务，说明子流程触发了");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是subprocessJob");
  }

  @ZeebeWorker(type = "receiveMsg", autoComplete = true, forceFetchAllVariables = true)
  public Map handleReceiveMsgJob(final JobClient client, final ActivatedJob job, @ZeebeVariable String someResult) {
    log.info("我是receiveMsg任务，当消息传入时我会触发");
    logJob(job, someResult);
    return Collections.singletonMap("someResult", "我是receiveMsg");
  }
}
