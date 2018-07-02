package cn.itcast.d_processVarialbes;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
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
	
	/**设置流程变量*/
	@Test
	public void setVariables() {
		/**与任务（正在执行）*/
		TaskService taskService = processEngine.getTaskService();
		String taskId="1204";
		/** 一：设置流程变量，使用基本数据类型 */
		/*taskService.setVariableLocal(taskId, "请假天数", 5);//与任务ID绑定
		taskService.setVariable(taskId, "请假日期", new Date());
		taskService.setVariable(taskId, "请假原因", "回家探亲aaa");
		System.out.println("设置流程变量成功");*/
		
		/** 二、设置流程变量，使用javaBean */
		/**
		 * 	当一个javaBea（实现序列号）防止到流程变量中，要求javabean的属性不能发生变化
		 * 	*如果发生变化，再次获取的时候，就出异常
		 * 	
		 * 解决方案：
		 * */
		Person p = new Person();
		p.setId("00001");
		p.setName("翠花");
		taskService.setVariable(taskId, "人员信息", p);
		
	}
	
	/**获取流程变量*/
	@Test
	public void getVariables() {
		/**与任务（正在执行）*/
		TaskService taskService = processEngine.getTaskService();
		//任务ID
		String taskId = "1204";
		/**一：获取流程变量，使用极本数据类型*/
		/*Integer days = (Integer) taskService.getVariable(taskId, "请假天数");
		Date date = (Date)taskService.getVariable(taskId, "请假日期");
		String reason = (String) taskService.getVariable(taskId, "请假原因");
		System.out.println("请假天数："+days);
		System.out.println("请假日期："+date);
		System.out.println("请假原因："+reason);*/
		/** 二：获取流程变量，使用javaBean类型 */
		Person p = (Person) taskService.getVariable(taskId, "人员信息");
		System.out.println(p.getId()+"              "+ p.getName());
		
		
	}
	
	/** 模拟设置和获取流程变量的场景 */
	public void setAndGetVariables() {
		/**与流程实例，执行对象(正在执行)*/
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		/**与任务（正在执行）*/
		TaskService taskService = processEngine.getTaskService();
		
		/**设置流程变量*/
//		runtimeService.setVariable(executionId, variableName, value);//表示使用执行对象ID，和流程变量的名称，设置流程变量的值（一次只能设置一个值）
//		runtimeService.setVariables(exectuionId, variables);//表示使用执行对象ID，和map集合设置流程变量，map集合的key就是流程变量的名称，map集合的value就是流程变量的值（一次设置多个值）
		
//		taskService.setVariable(executionId, variableName, value);//表示使用任务ID，和流程变量的名称，设置流程变量的值（一次只能设置一个值）
//		taskService.setVariables(exectuionId, variables);//表示使用任务ID，和map集合设置流程变量，map集合的key就是流程变量的名称，map集合的value就是流程变量的值（一次设置多个值）
		
//		runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);//启动流程实例的同时，可以设置流程变量，用map集合
//		taskService.complete(taskId, varibles);//完成任务的同时，设置流程变量，使用map集合
		
		/**获取流程变量*/
//		runtimeService.getVariable(executionId, varibleName);//使用执行对象ID和流程变量的名称，获取流程变量的值
//		runtimeService.getVariables(executionId);//使用执行对象Id，获取所有的流程变量，将流程变量放置到Map集合中,map集合的key就是流程变量的名称，map集合的value就是流程变量的值
//		runtimeService.getVariables(executionId, varibleNames);//使用执行对象id
		
		
		
	}
	
	/** 完成任务 */
	@Test
	public void completeMyPersonalTask() {
		//任务ID
		String taskId = "3002";
		processEngine.getTaskService()//与正在执行任务相关的Service
					.complete(taskId);
		System.out.println("任务完成："+taskId);
		
		
	}
	/** 查询历史流程变量 */
	@Test
	public void findHistoryProcessVaribles() {
		List<HistoricVariableInstance> list = processEngine.getHistoryService()
					.createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询对象
					.variableName("请假天数")
					.list();
		if(list !=null && list.size()>0) {
			for(HistoricVariableInstance hvi:list) {
				System.out.println(hvi.getId()+"   "+hvi.getProcessInstanceId()+"   "+hvi.getVariableName()+"  "+hvi.getVariableTypeName());
				System.out.println("###########################");
			}
		}
	}
}
