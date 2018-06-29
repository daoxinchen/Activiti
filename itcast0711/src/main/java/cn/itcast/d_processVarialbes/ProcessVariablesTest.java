package cn.itcast.d_processVarialbes;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class ProcessVariablesTest {
	//获取流程引擎
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	/**部署流程定义（从classpath）*/
	@Test
	public void deploymentProcessDefinition_classPath() {
		
		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.name("流程变量")
				.addClasspathResource("diagrams/processVariables.bpmn")
				.addClasspathResource("diagrams/processVariables.png")
				.deploy();
		System.out.println("部署名称："+deployment.getName());
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署时间："+deployment.getDeploymentTime());
	}
	/**部署流程定义（从zip）*/
	@Test
	public void deploymentProcessDefinition_zip() {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariables.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()
							.createDeployment()
							.name("流程变量")
							.addZipInputStream(zipInputStream)
							.deploy();
		System.out.println("部署名称："+deployment.getName());
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署时间："+deployment.getDeploymentTime());
		
	}
	/**部署流程定义（从InputStream）*/
	@Test
	public void deploymentProcessDefinition_inputStream() {
//		InputStream inputStreambpmn = this.getClass().getResourceAsStream("/diagrams/processVariables.bpmn");
//		InputStream inputStreampng = this.getClass().getResourceAsStream("/diagrams/processVariables.png");
		
		InputStream inputStreambpmn = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariables.bpmn");
		InputStream inputStreampng = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariables.png");
		Deployment deployment = processEngine.getRepositoryService()
								.createDeployment()
								.name("流程变量")
								.addInputStream("processVariables.bpmn", inputStreambpmn)//使用资源的名称(要求：与资源的名称要一致)和输入流完成部署
								.addInputStream("processVariables.png", inputStreampng)//使用资源的名称(要求：与资源的名称要一致)和输入流完成部署
								.deploy();
		System.out.println("部署名称："+deployment.getName());
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署时间："+deployment.getDeploymentTime());
	}
	
	/** 启动流程实例 */
	@Test
	public void startProcessInstance() {
		//流程定义的key
		String processDefinitionKey = "processVariables";
		
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的service
										.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID：" + pi.getId());
		System.out.println("流程定义ID："+pi.getProcessDefinitionId());
					
	}
}
