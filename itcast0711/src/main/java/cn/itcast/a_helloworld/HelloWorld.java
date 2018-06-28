package cn.itcast.a_helloworld;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class HelloWorld {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	/**1. 部署流程定义 */
	@Test
	public void deploymentProcessDefinition() {
		
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的service
					.createDeployment()//创建一个部署对象
					.name("helloworld入门程序")
					.addClasspathResource("diagrams/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
					.addClasspathResource("diagrams/helloworld.png")//从classpath的资源中加载，一次只能加载一个文件
					.deploy();//完成部署
		System.out.println("部署Id："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	/**2. 启动流程实例*/
	@Test
	public void startProcessInstance() {
		//流程定义的key
		String processDefinitionKey = "helloworld";
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println(pi.getId());
		System.out.println(pi.getProcessDefinitionId());
	}
	/**3. 查询当前人的个人任务*/
	@Test
	public void findMyPersonalTask() {
		String assignee = "王五";
		List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的service
					.createTaskQuery()//创建任务查询对象
					.taskAssignee(assignee)//指定任务管理，指定办理人
					.list();	
		if (list!=null && list.size()>0) {
			for(Task task : list) {
				System.out.println("任务Id："+task.getId());
				System.out.println("任务名称："+task.getName());
				System.out.println("任务的创建时间："+task.getCreateTime());
				System.out.println("任务的办理人："+task.getAssignee());
				System.out.println("流程定义ID："+task.getProcessDefinitionId());
				System.out.println("流程实例ID："+task.getProcessInstanceId());
				System.out.println("执行对象ID："+task.getExecutionId());
			}
		}
		
	}
	/**4. 完成任务 */
	@Test
	public void completeMyPersonalTask() {
		String taskId = "302";
		processEngine.getTaskService()
					.complete(taskId);
		System.out.println("完成任务成功："+taskId);
		
	}
	
	
}	
